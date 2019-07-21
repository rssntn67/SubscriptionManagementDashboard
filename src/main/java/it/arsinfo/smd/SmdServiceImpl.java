package it.arsinfo.smd;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Campagna;
import it.arsinfo.smd.entity.EstrattoConto;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.entity.Spedizione;
import it.arsinfo.smd.entity.SpedizioneItem;
import it.arsinfo.smd.entity.SpesaSpedizione;
import it.arsinfo.smd.entity.Storico;
import it.arsinfo.smd.repository.AbbonamentoDao;
import it.arsinfo.smd.repository.AnagraficaDao;
import it.arsinfo.smd.repository.CampagnaDao;
import it.arsinfo.smd.repository.CampagnaItemDao;
import it.arsinfo.smd.repository.EstrattoContoDao;
import it.arsinfo.smd.repository.SpedizioneDao;
import it.arsinfo.smd.repository.SpedizioneItemDao;
import it.arsinfo.smd.repository.SpesaSpedizioneDao;
import it.arsinfo.smd.repository.StoricoDao;

@Service
public class SmdServiceImpl implements SmdService {
    @Autowired
    private CampagnaDao campagnaDao;

    @Autowired
    private CampagnaItemDao campagnaItemDao;

    @Autowired
    private SpesaSpedizioneDao spesaSpedizioneDao;
    
    @Autowired
    AnagraficaDao anagraficaDao;

    @Autowired
    StoricoDao storicoDao;

    @Autowired
    AbbonamentoDao abbonamentoDao;
    
    @Autowired
    EstrattoContoDao estrattoContoDao;
    
    @Autowired
    SpedizioneDao spedizioneDao;

    @Autowired
    SpedizioneItemDao spedizioneItemDao;

    @Override
    public void generaCampagnaAbbonamenti(Campagna campagna, List<Pubblicazione> attivi) {
        List<Abbonamento> abbonamentiCampagna = Smd.generaAbbonamentiCampagna(campagna, anagraficaDao.findAll(),
                                                                              storicoDao.findAll(),
                                                                              attivi);
                                                           
        campagnaDao.save(campagna);
        campagna.getCampagnaItems().stream().forEach(ci -> campagnaItemDao.save(ci));

        for (Abbonamento abb:abbonamentiCampagna) {
            List<EstrattoConto> ecs = new ArrayList<>();
            for (Storico storico: storicoDao.findByIntestatario(abb.getIntestatario()).stream().filter(s -> s.getCassa() == abb.getCassa()).collect(Collectors.toList())) {
               ecs.add(Smd.generaECDaStorico(abb, storico));
            }
            if (ecs.isEmpty()) {
                continue;
            }
            genera(abb, ecs.toArray(new EstrattoConto[ecs.size()]));
        }
        
    }

    @Override
    public void deleteCampagnaAbbonamenti(Campagna campagna) {
        abbonamentoDao.findByCampagna(campagna).stream().forEach(abb -> deleteAbbonamento(abb));
        campagna.getCampagnaItems().stream().forEach(item -> campagnaItemDao.delete(item));
        campagnaDao.deleteById(campagna.getId());
        
    }

    @Override
    public void deleteAbbonamento(Abbonamento abbonamento) {
        spedizioneDao
        .findByAbbonamento(abbonamento)
        .forEach(sped -> 
            {
                sped.getSpedizioneItems().forEach(item -> {
                    spedizioneItemDao.deleteById(item.getId());
                });
                spedizioneDao.deleteById(sped.getId());
            }
        );
        estrattoContoDao.findByAbbonamento(abbonamento).forEach(ec -> estrattoContoDao.deleteById(ec.getId()));
        abbonamentoDao.delete(abbonamento);
    }

    @Override
    public void aggiornaAbbonamentoDaStorico(Storico storico) {
        // TODO Auto-generated method stub
        //first check stato, se annullato allora devi rimuovere EC
        // secondo trova ec ed abbonamento -> ec ed abbonamento sono quelli aspettati?
        // se diversi, allora bisogna rimuovere da uno ed aggiungere ad altro.
        // se corrispondono allora bisogna fare aggiornamento EC.
        List<SpesaSpedizione> spese = spesaSpedizioneDao.findAll();

        List<EstrattoConto> ecs = estrattoContoDao.findByStorico(storico);
        ecs.stream().filter(ec -> ec.getAbbonamento() != null && ec.getAbbonamento().getAnno() == Anno.getAnnoProssimo()).forEach( ec ->{
            ec.setNumero(storico.getNumero());
            ec.setTipoEstrattoConto(storico.getTipoEstrattoConto());
            ec.setPubblicazione(storico.getPubblicazione());
            Abbonamento abb = ec.getAbbonamento();
            List<Spedizione> spedizioni = spedizioneDao.findByAbbonamento(abb);
            Smd.aggiornaEC(abb, ec,spedizioni,spese);
            abbonamentoDao.save(abb);
            estrattoContoDao.save(ec);
        });
    }

    @Override
    public void generaAbbonamento(Abbonamento abbonamento,
            EstrattoConto estrattoConto) {
        genera(abbonamento, estrattoConto);
    }

    @Override
    public void generaAbbonamento(Abbonamento abbonamento,
            List<EstrattoConto> estrattiConto) {
        genera(abbonamento, estrattiConto.toArray(new EstrattoConto[estrattiConto.size()]));
    }    
    
    private void genera(Abbonamento abbonamento, EstrattoConto...contos) {
        List<Spedizione> spedizioni = new ArrayList<>();
        List<SpesaSpedizione> spese = spesaSpedizioneDao.findAll();
        for (EstrattoConto ec: contos) {
            spedizioni = Smd.generaSpedizioni(abbonamento, ec, spedizioni,spese);
        }
        abbonamentoDao.save(abbonamento);
        for (EstrattoConto ec: contos) {
            estrattoContoDao.save(ec);
        }
        spedizioni.stream().forEach(sped -> {
            spedizioneDao.save(sped);
            sped.getSpedizioneItems().stream().forEach(item -> spedizioneItemDao.save(item));
        });
    }

    @Override
    public void aggiornaECAbbonamento(Abbonamento abbonamento,
            EstrattoConto estrattoConto) {
        List<Spedizione> spedizioni = spedizioneDao.findByAbbonamento(abbonamento);
        List<SpedizioneItem> deleted = Smd.aggiornaEC(abbonamento,
                                                     estrattoConto, 
                                                     spedizioni,
                                                    spesaSpedizioneDao.findAll());  
        
        spedizioni.stream().forEach(sped -> {
            spedizioneDao.save(sped);
            sped.getSpedizioneItems().stream().forEach(item -> spedizioneItemDao.save(item));
        });
        deleted.forEach(rimitem -> spedizioneItemDao.deleteById(rimitem.getId()));
        estrattoContoDao.save(estrattoConto);
        abbonamentoDao.save(abbonamento);

    }

    @Override
    public void cancellaECAbbonamento(Abbonamento abbonamento,
            EstrattoConto estrattoConto) {
        List<Spedizione> spedizioni = spedizioneDao.findByAbbonamento(abbonamento);

        List<SpedizioneItem> deleted = Smd.rimuoviEC(abbonamento,
                                                     estrattoConto, 
                                                     spedizioni,
                                                    spesaSpedizioneDao.findAll());  
        
        spedizioni.stream().forEach(sped -> {
            spedizioneDao.save(sped);
            sped.getSpedizioneItems().stream().forEach(item -> {
                spedizioneItemDao.save(item);
               });
        });
        for (SpedizioneItem delitem: deleted ) {
            spedizioneItemDao.deleteById(delitem.getId());
        }
        
        for (Spedizione sped:spedizioni) {
            if (sped.getSpedizioneItems().isEmpty()) {
                spedizioneDao.deleteById(sped.getId());
            }
        }
        
        if (estrattoConto.getNumeroTotaleRiviste() == 0) { 
            estrattoContoDao.deleteById(estrattoConto.getId());
        } else {
            estrattoContoDao.save(estrattoConto);
        }
        abbonamentoDao.save(abbonamento);
    }
}
