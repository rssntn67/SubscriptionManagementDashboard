package it.arsinfo.smd.woocommerce;


import com.icoderman.woocommerce.EndpointBaseType;
import com.icoderman.woocommerce.WooCommerce;
import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.Diocesi;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.woocommerce.impl.WooCommerceServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SmdWooCommerceApiTests {

    private WooCommerce wooCommerce;

    private static final Logger log = LoggerFactory.getLogger(SmdWooCommerceApiTests.class);

    private final static String prefix = "TestAbbonamento";
    @BeforeEach
    public void onSetUp() {
        WooCommerceServiceImpl impl = new WooCommerceServiceImpl();
        wooCommerce= impl.getWooCommerce();
    }

    @Test
    public void getAllProductTest() {
        Map<String, String> params = new HashMap<>();
        params.put("per_page","10");
        params.put("offset","0");
        params.put("search",prefix+"-");
        List<Map> products = wooCommerce.getAll(EndpointBaseType.PRODUCTS.getValue(), params);
        log.info(" find: {}", products.size());

        products.forEach(p -> {
            log.info("id: {}",p.get("id"));
            log.info("virtual: {}",p.get("virtual"));
            log.info("status: {}",p.get("status"));
            log.info("tax_class: {}",p.get("tax_class"));
            log.info("catalog_visibility: {}",p.get("catalog_visibility"));
            log.info("purchasable: {}",p.get("purchasable"));
            log.info("total_sales {}",p.get("total_sales"));

            log.info("{}",Product.getFromMap(p));
        });
    }

    @Test
    public void productsTest() {
        Abbonamento abb = new Abbonamento();
        Anagrafica intestatario = new Anagrafica();
        intestatario.setDenominazione("Antonio Russo");
        intestatario.setDiocesi(Diocesi.DIOCESI000);
        abb.setAnno(Anno.ANNO2022);
        abb.setCodeLine(Abbonamento.generaCodeLine(Anno.ANNO2022));
        abb.setIntestatario(intestatario);
        abb.setImporto(new BigDecimal("1.00"));

        // Get all with request parameters
        Map<String, String> params = new HashMap<>();
        params.put("per_page","10");
        params.put("offset","0");
        params.put("search",prefix+"-"+abb.getCodeLine());
        List<Map> products = wooCommerce.getAll(EndpointBaseType.PRODUCTS.getValue(), params);
        Assertions.assertEquals(0,products.size());

        Map result = wooCommerce.create(EndpointBaseType.PRODUCTS.getValue(),Product.getCreateMapFromAbbonamento(abb,prefix));
        log.info("{}",result);
        Assertions.assertEquals("true",result.get("virtual").toString());
        Assertions.assertEquals("true",result.get("virtual").toString());
        Assertions.assertEquals("false",result.get("reviews_allowed").toString());
        Assertions.assertEquals("false",result.get("shipping_required").toString());
        Assertions.assertEquals("nessuna-tariffa",result.get("tax_class").toString());

        Product created = Product.getFromMap(result);
        log.info("{}",created);
        products = wooCommerce.getAll(EndpointBaseType.PRODUCTS.getValue(), params);
        Assertions.assertEquals(1,products.size());
        products.forEach(p -> {
            Product product = Product.getFromMap(p);
            Assertions.assertNotNull(product);
            Assertions.assertNotNull(product.getName());
            Assertions.assertNotNull(product.getSlug());
            Assertions.assertNotNull(product.getPermalink());
            Assertions.assertNotNull(product.getDescription());
            Assertions.assertNotNull(product.getShortDescription());
            Assertions.assertEquals(BigDecimal.ONE.doubleValue(),product.getRegularPrice().doubleValue());
            Assertions.assertTrue(product.getTotalSales()>=0);
            Assertions.assertNotNull(product.isPurchasable());
            Assertions.assertNotNull(product.getTaxClass());
            Assertions.assertEquals("nessuna-tariffa", product.getTaxClass());
            wooCommerce.update(EndpointBaseType.PRODUCTS.getValue(),product.getId(),Product.getHideMap());
        });


    }

    @Test
    public void deleteAllTestProducts() {
        Map<String, String> params = new HashMap<>();
        params.put("per_page","10");
        params.put("offset","0");
        params.put("search",prefix+"-");
        List<Map> products = wooCommerce.getAll(EndpointBaseType.PRODUCTS.getValue(), params);
        for (Map p : products) {
            Product prod = Product.getFromMap(p);
            wooCommerce.delete(EndpointBaseType.PRODUCTS.getValue(),prod.getId());
        }

        Assertions.assertEquals(0, wooCommerce.getAll(EndpointBaseType.PRODUCTS.getValue(), params).size());
    }

    @Test
    public void getProductTest() {
        Map map = wooCommerce.get(EndpointBaseType.PRODUCTS.getValue(),7930);
        log.info("{}", map);
        log.info("{}",Product.getFromMap(map));
    }

    @Test
    public void getCustomersTest() {
        // Get all with request parameters
        Map<String, String> params = new HashMap<>();
        params.put("per_page","10");
        params.put("offset","0");
        List customers = wooCommerce.getAll(EndpointBaseType.CUSTOMERS.getValue(), params);
        customers.forEach(p -> log.info("{}", p));
        log.info("{}",customers.size());
    }

    @Test
    public void getOrdersTest() {
        // Get all with request parameters
        Map<String, String> params = new HashMap<>();
        params.put("per_page", "10");
        params.put("offset", "0");
        params.put("search","TestAbbonamento-");
        List<Map> orders =wooCommerce.getAll(EndpointBaseType.ORDERS.getValue(), params);
        orders.forEach(p -> {
            log.info("id {}", p.get("id"));
            log.info("number {}", p.get("number"));
            log.info("status {}", p.get("status")); //processing/completed
            log.info("payment_method {}",p.get("payment_method"));
            log.info("transaction_id {}",p.get("transaction_id"));
            log.info("date_completed {}",p.get("date_completed"));
            log.info("date_paid {}",p.get("date_paid"));
            log.info("cart_hash {}",p.get("cart_hash"));
            log.info("total_tax {}",p.get("total_tax"));
            log.info("total {}",p.get("total"));
            List<Map> lineItems = (List) p.get("line_items");
            for (Map lineItem: lineItems) {
                log.info("{}",lineItem);
                log.info("line_item product_id {}",lineItem.get("product_id"));
                log.info("line_item name {}",lineItem.get("name"));
                log.info("line_item product_id {}",lineItem.get("product_id"));
                log.info("line_item total {}",lineItem.get("total"));
                log.info("line_item total_tax {}",lineItem.get("total_tax"));
            }
            log.info("billing {}",p.get("billing"));
            Order o = Order.getFromMap(p);
            log.info("{}",o);
            Assertions.assertNotNull(o.getId());
            Assertions.assertNotNull(o.getOrderItems());
            Assertions.assertNotNull(o.getBilling());
            Assertions.assertNotNull(o.getCartHash());
            Assertions.assertNotNull(o.getNumber());
            if (o.getDateCompleted() != null)
                Assertions.assertNotNull(o.getDateCompleted());
            Assertions.assertNotNull(o.getDatePaid());
            Assertions.assertNotNull(o.getPaymentMethod());
            Assertions.assertNotNull(o.getStatus());
            Assertions.assertNotNull(o.getTotal());
            Assertions.assertNotNull(o.getTotalTax());
            Assertions.assertNotNull(o.getTransactionId());
        });

    }

    @Test
    public void getAndUpdateOrdersTest() {
        // Get all with request parameters
        Map<String, String> params = new HashMap<>();
        params.put("per_page","10");
        params.put("offset","0");
        params.put("search","TestAbbonamento-228334146025438973");

        List<Map> orders = wooCommerce.getAll(EndpointBaseType.ORDERS.getValue(), params);
        log.info("---------> order size {}" , orders.size());
        orders.forEach(p -> {
            Order o =Order.getFromMap(p);
            log.info("{}",o);
            if (o.getStatus() == Order.OrderStatus.Processing) {
                Assertions.assertEquals(Order.OrderStatus.Processing, o.getStatus());
                Map completed = wooCommerce.update(EndpointBaseType.ORDERS.getValue(), o.getId(), Order.getStatusCompletedMap());
                log.info("completed id {}", completed.get("id"));
                log.info("completed number {}", completed.get("number"));
                log.info("completed status {}", completed.get("status")); //processing/completed
                log.info("completed date_completed {}", completed.get("date_completed")); //processing/completed
            }
            for (OrderItem item: o.getOrderItems()) {
                if (item.getName().equals("TestAbbonamento-228334146025438973")) {
                    Product product = Product.getFromMap(wooCommerce.get(EndpointBaseType.PRODUCTS.getValue(),item.getProductId()));
                    log.info("{}",product);
                    Assertions.assertEquals(1,product.getTotalSales());
                }
            }
        });
    }

}
