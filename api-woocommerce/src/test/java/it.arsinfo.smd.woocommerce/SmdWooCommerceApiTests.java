package it.arsinfo.smd.woocommerce;


import com.icoderman.woocommerce.ApiVersionType;
import com.icoderman.woocommerce.EndpointBaseType;
import com.icoderman.woocommerce.WooCommerce;
import com.icoderman.woocommerce.WooCommerceAPI;
import com.icoderman.woocommerce.oauth.OAuthConfig;
import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.Diocesi;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.woocommerce.impl.WooCommerceServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SmdWooCommerceApiTests {

    @Test
    public void productsTest() {

        OAuthConfig config = new OAuthConfig("http://www.retepreghierapapa.it", "ck_f70f8d7811e6a176cf58da451df59960d68244b0", "cs_1bacae59ede6f326690e2855f108d844a397327b");
        WooCommerce wooCommerce = new WooCommerceAPI(config, ApiVersionType.V3);

        // Get all with request parameters
        Map<String, String> params = new HashMap<>();
        params.put("per_page","10");
        params.put("offset","0");
        params.put("search","AbbonamentiAdp");
        List products = wooCommerce.getAll(EndpointBaseType.PRODUCTS.getValue(), params);

        Abbonamento abb = new Abbonamento();
        Anagrafica intestatario = new Anagrafica();
        intestatario.setDenominazione("Antonio Russo");
        intestatario.setDiocesi(Diocesi.DIOCESI000);
        abb.setAnno(Anno.ANNO2022);
        abb.setCodeLine(Abbonamento.generaCodeLine(Anno.ANNO2022));
        abb.setIntestatario(intestatario);
        abb.setImporto(new BigDecimal("6.00"));

        Map result = wooCommerce.create(EndpointBaseType.PRODUCTS.getValue(),WooCommerceServiceImpl.getCreateMapFromAbbonamento(abb));
        Product created = WooCommerceServiceImpl.getProductFromMap(result);
        System.out.println(created);
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
            System.out.println(product);
        });

        for (Object p : products) {
            Product prod = WooCommerceServiceImpl.getProduct(p);
            wooCommerce.delete(EndpointBaseType.PRODUCTS.getValue(),prod.getId());
        }

        Assertions.assertEquals(0, wooCommerce.getAll(EndpointBaseType.PRODUCTS.getValue(), params).size());


    }

    @Test
    public void getCustomersTest() {

        OAuthConfig config = new OAuthConfig("http://www.retepreghierapapa.it", "ck_f70f8d7811e6a176cf58da451df59960d68244b0", "cs_1bacae59ede6f326690e2855f108d844a397327b");
        WooCommerce wooCommerce = new WooCommerceAPI(config, ApiVersionType.V3);

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

        OAuthConfig config = new OAuthConfig("http://www.retepreghierapapa.it", "ck_f70f8d7811e6a176cf58da451df59960d68244b0", "cs_1bacae59ede6f326690e2855f108d844a397327b");
        WooCommerce wooCommerce = new WooCommerceAPI(config, ApiVersionType.V3);

        // Get all with request parameters
        Map<String, String> params = new HashMap<>();
        params.put("per_page","10");
        params.put("offset","0");
        List orders = wooCommerce.getAll(EndpointBaseType.ORDERS.getValue(), params);
        orders.forEach(p -> System.out.println(p));
        System.out.println(orders.size());
    }

}
