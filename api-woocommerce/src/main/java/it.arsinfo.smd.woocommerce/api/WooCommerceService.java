package it.arsinfo.smd.woocommerce.api;

import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.DistintaVersamento;

import java.util.Date;
import java.util.List;

public interface WooCommerceService {

   void paga(Abbonamento abb);

   List<DistintaVersamento> getAll(Date date);
 }
