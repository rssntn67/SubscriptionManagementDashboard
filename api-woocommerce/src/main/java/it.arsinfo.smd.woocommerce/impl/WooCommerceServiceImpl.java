package it.arsinfo.smd.woocommerce.impl;


import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.DistintaVersamento;
import it.arsinfo.smd.woocommerce.api.WooCommerceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;


@Service
public class WooCommerceServiceImpl implements WooCommerceService {

    private static final Logger log = LoggerFactory.getLogger(WooCommerceService.class);


    @Override
    public void paga(Abbonamento abb) {

    }

    @Override
    public List<DistintaVersamento> getAll(Date date) {
        return null;
    }
}
