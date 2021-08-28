package it.arsinfo.smd.woocommerce.api;

import com.icoderman.woocommerce.WooCommerce;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.DistintaVersamento;
import it.arsinfo.smd.entity.WooCommerceProduct;

import java.util.Date;
import java.util.List;

public interface WooCommerceService {
    WooCommerceProduct create(Abbonamento abb);
    List<DistintaVersamento> getAll(List<WooCommerceProduct> wooCommerceProducts);
 }
