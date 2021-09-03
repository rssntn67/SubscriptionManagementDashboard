package it.arsinfo.smd;

import it.arsinfo.smd.data.Ccp;
import it.arsinfo.smd.entity.DistintaVersamento;
import it.arsinfo.smd.entity.Versamento;
import it.arsinfo.smd.entity.WooCommerceOrder;
import it.arsinfo.smd.service.api.DistintaVersamentoService;
import it.arsinfo.smd.service.api.SmdService;
import it.arsinfo.smd.service.api.WooCommerceOrderService;
import it.arsinfo.smd.woocommerce.api.WooCommerceApiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class ScheduleTask {
    private static final Logger log = LoggerFactory.getLogger(SmdService.class);
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    @Autowired
    private WooCommerceApiService api;
    @Autowired
    private DistintaVersamentoService dvservice;
    @Autowired
    private WooCommerceOrderService service;

    @Scheduled(fixedDelay = 60000)
    public void incassa() {
        log.info("incassa; Fixed Delay Task :: Execution Time - {}", dateTimeFormatter.format(LocalDateTime.now()));
        try {
            List<WooCommerceOrder> list= api.update(service.findGenerated());
            for (WooCommerceOrder wo:list) {
                log.info("incassa; {}", wo);
                DistintaVersamento dv = new DistintaVersamento();
                dv.setCassa(wo.getCassa());
                dv.setCcp(Ccp.QUATTRO);
                dv.setImporto(wo.getPrice());

                Versamento v = new Versamento();
                v.setCodeLine(wo.getAbbonamento().getCodeLine());
                v.setImporto(wo.getPrice());
                v.setDistintaVersamento(dv);
                v.setDataContabile(v.getDataPagamento());
                dv.addItem(v);
                List<DistintaVersamento> update= new ArrayList<>();
                update.add(dvservice.save(dv));
                dvservice.incassaCodeLine(update,wo.getUserInfo());
                service.save(wo);
            }
        } catch (Exception ex) {
            log.error("Ran into an error {}", ex.getMessage(),ex);
        }

    }
}
