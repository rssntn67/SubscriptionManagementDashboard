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
            log.info("catalog_visibility: {}",p.get("catalog_visibility"));
            log.info("purchasable: {}",p.get("purchasable"));
            log.info("total_sales {}",p.get("total_sales"));

        });
    }

    @Test
    public void updateTest() {
        wooCommerce.update(EndpointBaseType.PRODUCTS.getValue(),11300,WooCommerceServiceImpl.getUpdateMap());
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


        Map result = wooCommerce.create(EndpointBaseType.PRODUCTS.getValue(),WooCommerceServiceImpl.getCreateMapFromAbbonamento(abb,prefix));
        log.info("{}",result);
        Assertions.assertEquals("true",result.get("virtual").toString());
        Assertions.assertEquals("false",result.get("reviews_allowed").toString());
        Assertions.assertEquals("false",result.get("shipping_required").toString());
        Assertions.assertEquals("false",result.get("shipping_taxable").toString());

        Product created = WooCommerceServiceImpl.getProduct(result);
        log.info("{}",created);
        products = wooCommerce.getAll(EndpointBaseType.PRODUCTS.getValue(), params);
        Assertions.assertEquals(1,products.size());
        products.forEach(p -> {
            Product product = WooCommerceServiceImpl.getProduct(p);
            Assertions.assertNotNull(product);
            Assertions.assertNotNull(product.getName());
            Assertions.assertNotNull(product.getSlug());
            Assertions.assertNotNull(product.getPermalink());
            Assertions.assertNotNull(product.getDescription());
            Assertions.assertNotNull(product.getShortDescription());
            Assertions.assertNotNull(product.getRegularPrice());
            Assertions.assertNotNull(product.getTotalSales());
            Assertions.assertNotNull(product.isPurchasable());
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
            Product prod = WooCommerceServiceImpl.getProduct(p);
            wooCommerce.delete(EndpointBaseType.PRODUCTS.getValue(),prod.getId());
        }

        Assertions.assertEquals(0, wooCommerce.getAll(EndpointBaseType.PRODUCTS.getValue(), params).size());
    }

    @Test
    public void getProductTest() {
        Map map = wooCommerce.get(EndpointBaseType.PRODUCTS.getValue(),9043);
        log.info("{}", map);
        log.info("{}",WooCommerceServiceImpl.getProduct(map));
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
    public void getAndUpdateOrdersTest() {
        // Get all with request parameters
        Map<String, String> params = new HashMap<>();
        params.put("per_page","10");
        params.put("offset","0");
        params.put("search","TestAbbonamento-227434336667029313");

        List<Map> orders = wooCommerce.getAll(EndpointBaseType.ORDERS.getValue(), params);
        log.info("---------> order size {}" , orders.size());
        orders.forEach(p -> {
            log.info("id {}",p.get("id"));
            log.info("number {}",p.get("number"));
            log.info("status {}",p.get("status")); //processing/completed
            log.info("payment_method {}",p.get("payment_method"));
            log.info("transaction_id {}",p.get("transaction_id"));
            log.info("date_completed {}",p.get("date_completed"));
            log.info("date_paid {}",p.get("date_paid"));
            log.info("cart_hash {}",p.get("cart_hash"));
            log.info("total_tax {}",p.get("total_tax"));
            log.info("total {}",p.get("total"));
            List<Map> lineItems = (List) p.get("line_items");
            for (Map lineItem: lineItems) {
                log.info("line_item product_id {}",lineItem.get("product_id"));
                log.info("line_item name {}",lineItem.get("name"));
                log.info("line_item product_id {}",lineItem.get("product_id"));
                log.info("line_item total {}",lineItem.get("total"));
                log.info("line_item total_tax {}",lineItem.get("total_tax"));
            }
            log.info("billing {}",p.get("billing"));

            int id = Integer.parseInt(p.get("id").toString());
            Map<String,Object> updateMap = new HashMap<>();
            updateMap.put("status","processing");
            Map updated = wooCommerce.update(EndpointBaseType.ORDERS.getValue(),id,updateMap);
            log.info("updated id {}", updated.get("id"));
            log.info("updated number {}", updated.get("number"));
            log.info("updated status {}", updated.get("status")); //processing/completed

            Map<String,Object> update = new HashMap<>();
            update.put("status","completed");
            Map completed = wooCommerce.update(EndpointBaseType.ORDERS.getValue(),id,update);
            log.info("completed id {}", completed.get("id"));
            log.info("completed number {}", completed.get("number"));
            log.info("completed status {}", completed.get("status")); //processing/completed

        });
    }

}
