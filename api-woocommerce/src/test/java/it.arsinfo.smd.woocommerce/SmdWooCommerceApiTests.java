package it.arsinfo.smd.woocommerce;


import com.google.common.base.Splitter;
import com.google.gson.Gson;
import com.icoderman.woocommerce.ApiVersionType;
import com.icoderman.woocommerce.EndpointBaseType;
import com.icoderman.woocommerce.WooCommerce;
import com.icoderman.woocommerce.WooCommerceAPI;
import com.icoderman.woocommerce.oauth.OAuthConfig;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.util.*;

public class SmdWooCommerceApiTests {

    @Test
    public void parseProductObjectStringTest() {
        String prodstring= "{id:11034, " +
                "name:\"AlfaBetaGamma0\", " +
                "slug:\"alfabetagamma0-6\", " +
                "permalink:\"https://www.retepreghierapapa.it/prodotto/alfabetagamma0-6/\" } " ;

        Product p = new Gson().fromJson(prodstring,Product.class);


        System.out.println(p.getId()
        );

        String prodstring2= "id=11034," +
                "name=AlfaBetaGamma0," +
                "slug=alfabetagamma0-6," +
                "permalink=https://www.retepreghierapapa.it/prodotto/alfabetagamma0-6/" ;

        System.out.println(prodstring2);
        Map<String,String> map = Splitter.on(",").withKeyValueSeparator("=").split(prodstring2.trim());
        System.out.println(map);
        System.out.println(map.get("slug"));

        Map<String,String> testMap = new HashMap<>();
        testMap.put("id","prova");
        testMap.put("name","nomeprova");
        System.out.println(testMap);
        System.out.println(testMap.get("id"));
        System.out.println(testMap.get("name"));


    }
    @Test
    public void getProductsTest() {

        OAuthConfig config = new OAuthConfig("http://www.retepreghierapapa.it", "ck_f70f8d7811e6a176cf58da451df59960d68244b0", "cs_1bacae59ede6f326690e2855f108d844a397327b");
        WooCommerce wooCommerce = new WooCommerceAPI(config, ApiVersionType.V3);

        // Get all with request parameters
        Map<String, String> params = new HashMap<>();
        params.put("per_page","1");
        params.put("offset","0");
        List products = wooCommerce.getAll(EndpointBaseType.PRODUCTS.getValue(), params);
        products.forEach(p -> {
            String value = p.toString().replaceAll(" ","");
            System.out.println(value);
            value=value.substring(1,value.indexOf(",downloadable")).replace(" ","" );
            System.out.println(value);
            Map<String,String> map = Splitter.on(",").withKeyValueSeparator("=").split(value);
            System.out.println(map.get("id"));
            System.out.println(map.get("permalink"));
        });
        System.out.println(products.size());
    }

    @Test
    public void createProductTest() {
        OAuthConfig config = new OAuthConfig("http://www.retepreghierapapa.it", "ck_f70f8d7811e6a176cf58da451df59960d68244b0", "cs_1bacae59ede6f326690e2855f108d844a397327b");
        WooCommerce wooCommerce = new WooCommerceAPI(config, ApiVersionType.V3);
        Map<String,Object> product = new HashMap<>();
        product.put("name","AlfaBetaGamma0");
        product.put("regular_price","6.00");
        product.put("description", "Antonio Russo");
        product.put("short_description","ADP");

        Map<String,Object> result = wooCommerce.create(EndpointBaseType.PRODUCTS.getValue(),product);
//        System.out.println(result);
        System.out.println(result.get("id"));
        System.out.println(result.get("name"));
        System.out.println(result.get("slug"));
        System.out.println(result.get("permalink"));

        Map<String, String> params = new HashMap<>();
        params.put("per_page","100");
        params.put("search","AlfaBetaGamma0");

        for (Object p : wooCommerce.getAll(EndpointBaseType.PRODUCTS.getValue(), params)) {
            //wooCommerce.delete(EndpointBaseType.PRODUCTS.getValue(),)
            System.out.println(p);
        }

    }

    @Test
    public void getCustomersTest() {

        OAuthConfig config = new OAuthConfig("http://www.retepreghierapapa.it", "ck_f70f8d7811e6a176cf58da451df59960d68244b0", "cs_1bacae59ede6f326690e2855f108d844a397327b");
        WooCommerce wooCommerce = new WooCommerceAPI(config, ApiVersionType.V3);

        // Get all with request parameters
        Map<String, String> params = new HashMap<>();
        params.put("per_page","100");
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
        params.put("per_page","100");
        params.put("offset","57");
        List orders = wooCommerce.getAll(EndpointBaseType.ORDERS.getValue(), params);
        orders.forEach(p -> System.out.println(p));
        System.out.println(orders.size());
    }

}
