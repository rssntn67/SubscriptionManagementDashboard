package it.arsinfo.smd.woocommerce.api;

import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.DistintaVersamento;
import it.arsinfo.smd.entity.WooCommerceOrder;

import java.util.List;

public interface WooCommerceService {
    WooCommerceOrder create(Abbonamento abb, int progressivo);
    List<WooCommerceOrder> update(List<WooCommerceOrder> wooCommerceProducts);
 }
