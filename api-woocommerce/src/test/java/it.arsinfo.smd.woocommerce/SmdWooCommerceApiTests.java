package it.arsinfo.smd.woocommerce;


import com.icoderman.woocommerce.EndpointBaseType;
import com.icoderman.woocommerce.WooCommerce;
import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.Diocesi;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.woocommerce.api.WooCommerceService;
import it.arsinfo.smd.woocommerce.impl.WooCommerceServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SmdWooCommerceApiTests {

    private WooCommerce wooCommerce;

    private static final Logger log = LoggerFactory.getLogger(SmdWooCommerceApiTests.class);

    @BeforeEach
    public void onSetUp() {
        WooCommerceServiceImpl impl = new WooCommerceServiceImpl();
        wooCommerce= impl.getWooCommerce();
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
        params.put("search","Abbonamento-"+abb.getCodeLine());
        List<Map> products = wooCommerce.getAll(EndpointBaseType.PRODUCTS.getValue(), params);
        Assertions.assertEquals(0,products.size());


        Map result = wooCommerce.create(EndpointBaseType.PRODUCTS.getValue(),WooCommerceServiceImpl.getCreateMapFromAbbonamento(abb));
        Product created = WooCommerceServiceImpl.getProductFromMap(result);
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
            log.info("{}",product);
        });

        for (Map p : products) {
            Product prod = WooCommerceServiceImpl.getProduct(p);
            wooCommerce.delete(EndpointBaseType.PRODUCTS.getValue(),prod.getId());
        }

        Assertions.assertEquals(0, wooCommerce.getAll(EndpointBaseType.PRODUCTS.getValue(), params).size());
    }

    @Test
    public void getProductTest() {
        Map product = wooCommerce.get(EndpointBaseType.PRODUCTS.getValue(),9043);
        log.info("{}",WooCommerceServiceImpl.getProductFromMap(product));
    }

    @Test
    public void getCustomersTest() {

        // Get all with request parameters
        Map<String, String> params = new HashMap<>();
        params.put("per_page","10");
        params.put("offset","0");
        List customers = wooCommerce.getAll(EndpointBaseType.CUSTOMERS.getValue(), params);
        customers.forEach(p -> System.out.println(p));
        System.out.println(customers.size());
    }

    @Test
    public void getOrdersTest() {
        // Get all with request parameters
        Map<String, String> params = new HashMap<>();
        params.put("per_page","10");
        params.put("offset","0");
        params.put("search","puddu.serenella@yahoo.it");

        List orders = wooCommerce.getAll(EndpointBaseType.ORDERS.getValue(), params);
        orders.forEach(p -> System.out.println(p));
        System.out.println(orders.size());

    }

}
