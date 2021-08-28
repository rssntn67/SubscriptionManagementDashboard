package it.arsinfo.smd.woocommerce.impl;


import com.icoderman.woocommerce.ApiVersionType;
import com.icoderman.woocommerce.EndpointBaseType;
import com.icoderman.woocommerce.WooCommerce;
import com.icoderman.woocommerce.WooCommerceAPI;
import com.icoderman.woocommerce.oauth.OAuthConfig;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.DistintaVersamento;
import it.arsinfo.smd.entity.WooCommerceProduct;
import it.arsinfo.smd.woocommerce.Product;
import it.arsinfo.smd.woocommerce.api.WooCommerceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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

    public static Map<String,Object> getCreateMapFromAbbonamento(Abbonamento abb) {
        Map<String,Object> create = new HashMap<>();
        create.put("name","Abbonamento-"+ abb.getCodeLine());
        create.put("regular_price",abb.getResiduo().toString());
        create.put("description", "Importo Abbonamento Riviste ADP anno "+abb.getAnno().getAnnoAsString()+ "intestatario " +abb.getIntestatario().getDenominazione());
        create.put("short_description","Abbonamento ADP");
        return create;
    }

    public static Product getProductFromMap(Map map) {
        Product product = new Product();
        log.debug("getProductFromMap: {}", map);
        Object id = map.get("id");
        if (id instanceof Integer) {
            product.setId((Integer) id);
        }
        product.setName(map.get("name").toString());
        product.setSlug(map.get("slug").toString());
        product.setPermalink((map.get("permalink")).toString());
        product.setDescription(map.get("description").toString());
        product.setShortDescription(map.get("short_description").toString());
        product.setRegularPrice(new BigDecimal(map.get("regular_price").toString()));
        return product;
    }

    public static Product getProduct(Map map) {
        int id = Integer.parseInt(map.get("id").toString());
        Product product = new Product();
        product.setId(id);
        product.setName(map.get("name").toString());
        product.setSlug(map.get("slug").toString());
        product.setPermalink((map.get("permalink")).toString());
        product.setDescription(map.get("description").toString());
        product.setShortDescription(map.get("short_description").toString());
        product.setRegularPrice(new BigDecimal(map.get("regular_price").toString()));
        return product;
    }

    @Override
    public WooCommerceProduct creaProdotto(Abbonamento abb) {
        Map result = wooCommerce.create(EndpointBaseType.PRODUCTS.getValue(),WooCommerceServiceImpl.getCreateMapFromAbbonamento(abb));
        Product created = getProductFromMap(result);
        return Product.createFromProduct(created,abb);
    }

    @Override
    public List<DistintaVersamento> getFromProduct(List<WooCommerceProduct> wooCommerceProducts) {
        return null;
    }

}
