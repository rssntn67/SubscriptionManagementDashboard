package it.arsinfo.smd.service.api;

import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.WooCommerceOrder;

import java.util.List;

public interface WooCommerceOrderService extends SmdServiceBase<WooCommerceOrder> {
    List<WooCommerceOrder> findByAbbonamento(Abbonamento abbonamento);

}
