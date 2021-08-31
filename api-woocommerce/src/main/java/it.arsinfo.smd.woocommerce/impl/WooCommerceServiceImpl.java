package it.arsinfo.smd.woocommerce.impl;


import com.icoderman.woocommerce.ApiVersionType;
import com.icoderman.woocommerce.EndpointBaseType;
import com.icoderman.woocommerce.WooCommerce;
import com.icoderman.woocommerce.WooCommerceAPI;
import com.icoderman.woocommerce.oauth.OAuthConfig;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.DistintaVersamento;
import it.arsinfo.smd.entity.WooCommerceOrder;
import it.arsinfo.smd.woocommerce.Product;
import it.arsinfo.smd.woocommerce.api.WooCommerceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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
    public WooCommerceOrder create(Abbonamento abb) {
        Map result = wooCommerce.create(EndpointBaseType.PRODUCTS.getValue(), Product.getCreateMapFromAbbonamento(abb,"Abbonamento"));
        Product created = Product.getFromMap(result);
        return Product.createFromProduct(created, abb);
    }

    @Override
    public List<DistintaVersamento> getAll(List<WooCommerceOrder> wooCommerceProducts) {
        //se ordine pagato:
        Product.getUpdateProcessingMap();
        return null;
    }

}
