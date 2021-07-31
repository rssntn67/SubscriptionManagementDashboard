package it.arsinfo.smd.woocommerce;


import com.icoderman.woocommerce.ApiVersionType;
import com.icoderman.woocommerce.EndpointBaseType;
import com.icoderman.woocommerce.WooCommerce;
import com.icoderman.woocommerce.WooCommerceAPI;
import com.icoderman.woocommerce.oauth.OAuthConfig;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SmdWooCommerceApiTests {

    @Test
    public void getProductsTest() {

        OAuthConfig config = new OAuthConfig("http://www.retepreghierapapa.it", "ck_f70f8d7811e6a176cf58da451df59960d68244b0", "cs_1bacae59ede6f326690e2855f108d844a397327b");
        WooCommerce wooCommerce = new WooCommerceAPI(config, ApiVersionType.V3);

        // Get all with request parameters
        Map<String, String> params = new HashMap<>();
        params.put("per_page","100");
        params.put("offset","0");
        List products = wooCommerce.getAll(EndpointBaseType.PRODUCTS.getValue(), params);
        products.forEach(p -> System.out.println(p));
        System.out.println(products.size());
    }

}
