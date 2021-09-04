package it.arsinfo.smd.woocommerce.impl;


import com.icoderman.woocommerce.ApiVersionType;
import com.icoderman.woocommerce.EndpointBaseType;
import com.icoderman.woocommerce.WooCommerce;
import com.icoderman.woocommerce.WooCommerceAPI;
import com.icoderman.woocommerce.oauth.OAuthConfig;
import it.arsinfo.smd.entity.Cassa;
import it.arsinfo.smd.entity.StatoWooCommerceOrder;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.WooCommerceOrder;
import it.arsinfo.smd.woocommerce.Order;
import it.arsinfo.smd.woocommerce.OrderItem;
import it.arsinfo.smd.woocommerce.Product;
import it.arsinfo.smd.woocommerce.api.WooCommerceApiService;
import it.arsinfo.smd.woocommerce.config.WooCommerceApiConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
public class WooCommerceApiServiceImpl implements WooCommerceApiService {


    private static final Logger log = LoggerFactory.getLogger(WooCommerceApiService.class);
    @Autowired
    WooCommerceApiConfig config;

    private final OAuthConfig oAuthConfigconfig = new OAuthConfig(config.getUrl(), config.getConsumerKey(), config.getConsumerSecret());
    private final WooCommerce wooCommerce = new WooCommerceAPI(oAuthConfigconfig, ApiVersionType.V3);

    public WooCommerce getWooCommerce() {
        return wooCommerce;
    }

    @Override
    public WooCommerceOrder create(Abbonamento abb, int id) {
        Map result = wooCommerce.create(EndpointBaseType.PRODUCTS.getValue(), Product.getCreateMapFromAbbonamento(abb,"Abbonamento-"+abb.getCodeLine()+"-"+id));
        Product created = Product.getFromMap(result);
        return Product.createFromProduct(created, abb);
    }

    @Override
    public List<WooCommerceOrder> update(List<WooCommerceOrder> worders) {
        final List<WooCommerceOrder> updates = new ArrayList<>();
        final Map<String,Integer> nameToOrderMap = new HashMap<>();
        final Map<String, WooCommerceOrder> valid =
            worders
            .stream()
            .filter(wo -> wo.getStatus() == StatoWooCommerceOrder.Generated)
            .collect(Collectors.toMap(WooCommerceOrder::getName, Function.identity()));

        final Set<String> names = valid.values().stream().map(WooCommerceOrder::getName).collect(Collectors.toSet());
        names.forEach(name -> {
            if (nameToOrderMap.containsKey(name)) {
                return;
            }
            Map<String, String> params = new HashMap<>();
            params.put("per_page","10");
            params.put("offset","0");
            params.put("search",name);
            for (Map orderM:  (List<Map>)wooCommerce.getAll(EndpointBaseType.ORDERS.getValue(), params) ) {
                Order o = Order.getFromMap(orderM);
                Cassa cassa= Cassa.Carte;
                if (o.getPaymentMethod().equals("paypal")) {
                    cassa=Cassa.Paypal;
                }
                log.info("update: parsing {}",o);
                if (o.getStatus() != Order.OrderStatus.Processing)
                    continue;
                boolean wooOrderCompleted = true;
                for (OrderItem item : o.getOrderItems()) {
                    if (!valid.containsKey(item.getName())) {
                        wooOrderCompleted = false;
                        continue;
                    }
                    WooCommerceOrder wo = valid.get(item.getName());
                    if (wo.getPrice().subtract(item.getTotal()).signum() != 0 || item.getTotalTax().signum() != 0) {
                        wooOrderCompleted = false;
                        continue;
                    }
                    wo.setOrderId(o.getId());
                    wo.setCassa(cassa);
                    wo.setStatus(StatoWooCommerceOrder.Completed);
                    updates.add(wo);
                    nameToOrderMap.put(item.getName(), o.getId());
                    Product product = Product.getFromMap(wooCommerce.get(EndpointBaseType.PRODUCTS.getValue(),item.getProductId()));
                    if (product != null) {
                        log.info("update: hiding {}",product);
                        wooCommerce.update(EndpointBaseType.PRODUCTS.getValue(), product.getId(),Product.getHideMap());
                    }
                }
                if (wooOrderCompleted) {
                    Map updatetordermap = wooCommerce.update(EndpointBaseType.ORDERS.getValue(), o.getId(),Order.getStatusCompletedMap());
                    log.info("update: completed {}",Order.getFromMap(updatetordermap));
                }
            }

        });
        return updates;
    }

    @Override
    public void delete(WooCommerceOrder wo) {
        Product product = Product.getFromMap(wooCommerce.get(EndpointBaseType.PRODUCTS.getValue(),wo.getProductId()));
        if (product != null) {
            log.info("update: hiding {}", product);
            wooCommerce.update(EndpointBaseType.PRODUCTS.getValue(), product.getId(), Product.getHideMap());
        }
    }
}
