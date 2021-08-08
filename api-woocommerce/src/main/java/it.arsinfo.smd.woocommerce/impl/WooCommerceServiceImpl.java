package it.arsinfo.smd.woocommerce.impl;


import com.google.common.base.Splitter;
import com.icoderman.woocommerce.ApiVersionType;
import com.icoderman.woocommerce.WooCommerce;
import com.icoderman.woocommerce.WooCommerceAPI;
import com.icoderman.woocommerce.oauth.OAuthConfig;
import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.DistintaVersamento;
import it.arsinfo.smd.woocommerce.Product;
import it.arsinfo.smd.woocommerce.api.WooCommerceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class WooCommerceServiceImpl implements WooCommerceService {

    private static final Logger log = LoggerFactory.getLogger(WooCommerceService.class);
    private final OAuthConfig config = new OAuthConfig("http://www.retepreghierapapa.it", "ck_f70f8d7811e6a176cf58da451df59960d68244b0", "cs_1bacae59ede6f326690e2855f108d844a397327b");
    private final WooCommerce wooCommerce = new WooCommerceAPI(config, ApiVersionType.V3);

    public static Map<String,Object> getCreateMapFromAbbonamento(Abbonamento abb) {
        Map<String,Object> create = new HashMap<>();
        create.put("name","AbbonamentiAdp"+ abb.getCodeLine());
        create.put("regular_price",abb.getResiduo().toString());
        create.put("description", "ImportoAbbonamentoRivisteADP"+abb.getAnno().getAnnoAsString()+abb.getIntestatario().getDenominazione().trim());
        create.put("short_description","AbbonamentoADP");
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

    public static Product getProduct(Object p) {
        String value = p.toString().replaceAll(" ","").replaceAll("<[^>]*>","");
        value=value.substring(1,value.indexOf(",downloadable")).replace(" ","" );
        log.debug("getProduct: {}", value);
        Map<String,String> map = Splitter.on(",").withKeyValueSeparator("=").split(value);
        int id = Integer.parseInt(map.get("id"));
        Product product = new Product();
        product.setId(id);
        product.setName(map.get("name"));
        product.setSlug(map.get("slug"));
        product.setPermalink((map.get("permalink")));
        product.setDescription(map.get("description"));
        product.setShortDescription(map.get("short_description"));
        product.setRegularPrice(new BigDecimal(map.get("regular_price")));
        return product;
    }

    @Override
    public void creaOrdine(Abbonamento abb) {

    }

    @Override
    public List<DistintaVersamento> getAll(Date date) {
        return null;
    }
}
