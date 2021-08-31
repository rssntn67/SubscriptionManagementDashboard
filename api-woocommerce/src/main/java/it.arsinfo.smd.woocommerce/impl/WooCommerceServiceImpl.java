package it.arsinfo.smd.woocommerce.impl;


import com.icoderman.woocommerce.ApiVersionType;
import com.icoderman.woocommerce.EndpointBaseType;
import com.icoderman.woocommerce.WooCommerce;
import com.icoderman.woocommerce.WooCommerceAPI;
import com.icoderman.woocommerce.oauth.OAuthConfig;
import it.arsinfo.smd.data.StatoWooCommerceOrder;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.WooCommerceOrder;
import it.arsinfo.smd.woocommerce.Order;
import it.arsinfo.smd.woocommerce.OrderItem;
import it.arsinfo.smd.woocommerce.Product;
import it.arsinfo.smd.woocommerce.api.WooCommerceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class WooCommerceServiceImpl implements WooCommerceService {

    private static final Logger log = LoggerFactory.getLogger(WooCommerceService.class);
    private final OAuthConfig config = new OAuthConfig("http://www.retepreghierapapa.it", "ck_f70f8d7811e6a176cf58da451df59960d68244b0", "cs_1bacae59ede6f326690e2855f108d844a397327b");
    private final WooCommerce wooCommerce = new WooCommerceAPI(config, ApiVersionType.V3);

    public WooCommerce getWooCommerce() {
        return wooCommerce;
    }

    @Override
    public WooCommerceOrder create(Abbonamento abb, int id) {
        Map result = wooCommerce.create(EndpointBaseType.PRODUCTS.getValue(), Product.getCreateMapFromAbbonamento(abb,"Abbonamento-"+id));
        Product created = Product.getFromMap(result);
        return Product.createFromProduct(created, abb);
    }

    @Override
    public List<WooCommerceOrder> updateProcessing(List<WooCommerceOrder> worders) {
        final List<WooCommerceOrder> updates = new ArrayList<>();
        worders.stream().filter(wo -> wo.getStatus() == StatoWooCommerceOrder.Processing && wo.getOrderId() != null).forEach(wo -> {
            Map p = wooCommerce.get(EndpointBaseType.ORDERS.getValue(), wo.getOrderId());
            Order o = Order.getFromMap(p);
            if (o.getStatus() == Order.OrderStatus.Completed) {
                wo.setStatus(StatoWooCommerceOrder.Completed);
                updates.add(wo);
            }
        });
        return updates;
    }

    @Override
    public List<WooCommerceOrder> updateGenerated(List<WooCommerceOrder> worders) {
        //se ordine pagato:
        final List<WooCommerceOrder> updates = new ArrayList<>();
        worders.stream().filter(wo -> wo.getStatus() == StatoWooCommerceOrder.Generated).forEach(wo -> {
            Map<String, String> params = new HashMap<>();
            params.put("per_page","10");
            params.put("offset","0");
            params.put("search",wo.getName());
            List<Map> ordersof = wooCommerce.getAll(EndpointBaseType.ORDERS.getValue(), params);
            log.info("updateGenerated: order size {}" , ordersof.size());
            for (Map p:  ordersof )
            {
                Order o =Order.getFromMap(p);
                if (o.getStatus() != Order.OrderStatus.Processing)
                    continue;

                for (OrderItem item: o.getOrderItems()) {
                    if (!item.getName().equals(wo.getName()))
                            continue;
                    if (wo.getPrice().subtract(item.getTotal()).signum() != 0)
                        continue;
                    if (item.getTotalTax().signum() != 0 )
                        continue;

                    wo.setOrderId(o.getId());
                    if (o.getOrderItems().size() == 1) {
                        wo.setStatus(StatoWooCommerceOrder.Completed);
                        wooCommerce.update(EndpointBaseType.ORDERS.getValue(), o.getId(),Order.getStatusCompletedMap());
                    } else {
                        wo.setStatus(StatoWooCommerceOrder.Processing);
                    }
                    updates.add(wo);
                    Product product = Product.getFromMap(wooCommerce.get(EndpointBaseType.PRODUCTS.getValue(),item.getProductId()));
                    if (product != null) {
                        log.info("update: {}",product);
                        wooCommerce.update(EndpointBaseType.PRODUCTS.getValue(), product.getId(),Product.getUpdateProcessingMap());
                    }
                }
            }
        });
        return updates;
    }
}
