package it.arsinfo.smd;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.Mese;
import it.arsinfo.smd.data.StatoAbbonamento;
import it.arsinfo.smd.data.StatoStorico;
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
        if (abbonamento.getStatoAbbonamento() != StatoAbbonamento.Nuovo) {
            throw new UnsupportedOperationException("Non si può cancellare un abbonamento nello stato:"+abbonamento.getStatoAbbonamento());
        }
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
    public void aggiornaAbbonamentoDaStorico(Storico storico) throws Exception {
        if (storico.getStatoStorico() == StatoStorico.SOSPESO) {
            rimuoviECDaStorico(storico);
            return;
        }
        Campagna campagna = campagnaDao.findByAnno(Anno.getAnnoProssimo());
        if (campagna == null) {
            return;
        }
        Abbonamento abbonamento = 
                abbonamentoDao.findByIntestatarioAndCampagnaAndCassa(storico.getIntestatario(), campagna, storico.getCassa());
        if (abbonamento == null) {
            abbonamento = new Abbonamento();
            abbonamento.setIntestatario(storico.getIntestatario());
            abbonamento.setCampagna(campagna);
            abbonamento.setAnno(campagna.getAnno());
            abbonamento.setCassa(storico.getCassa());
            abbonamento.setCampo(Abbonamento.generaCodeLine(abbonamento.getAnno(),storico.getIntestatario()));
            abbonamento.setStatoAbbonamento(StatoAbbonamento.Nuovo);   
        }
        
        List<EstrattoConto> ecs = 
                estrattoContoDao
                .findByStorico(storico)
                .stream()
                .filter(ec -> {
                    Abbonamento abb = abbonamentoDao.findById(ec.getAbbonamento().getId()).get();
                    if (abb != null && abb.getCampagna() != null && abb.getAnno() == campagna.getAnno()) {
                        return true;
                    }
                    return false;
                })
                .collect(Collectors.toList());
        
        if (ecs.size() > 1 ) {
            throw new Exception("Un solo Estratto Conto per storico ogni anno");
        }
        
        if (ecs.isEmpty()) {
            genera(abbonamento, Smd.generaECDaStorico(abbonamento, storico));
            return;
        } 
        EstrattoConto estrattoConto = ecs.iterator().next();                
        
        if (estrattoConto.getAbbonamento().getId() != abbonamento.getId()) {
            Abbonamento oldabb = abbonamentoDao.findById(estrattoConto.getAbbonamento().getId()).get();
            cancellaECAbbonamento(oldabb, estrattoConto);
        }
        
        if (abbonamento.getId() == null) {
            genera(abbonamento, Smd.generaECDaStorico(abbonamento, storico));
        } else {
            estrattoConto.setAbbonamento(abbonamento);
            estrattoConto.setPubblicazione(storico.getPubblicazione());
            estrattoConto.setNumero(storico.getNumero());
            estrattoConto.setTipoEstrattoConto(storico.getTipoEstrattoConto());
            estrattoConto.setMeseInizio(Mese.GENNAIO);
            estrattoConto.setAnnoInizio(abbonamento.getAnno());
            estrattoConto.setMeseFine(Mese.DICEMBRE);
            estrattoConto.setAnnoFine(abbonamento.getAnno());
            estrattoConto.setInvio(storico.getInvio());
            estrattoConto.setInvioSpedizione(storico.getInvioSpedizione());
            estrattoConto.setDestinatario(storico.getDestinatario());
            aggiornaECAbbonamento(abbonamento, estrattoConto);
        }        

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
        
        for (Spedizione sped:spedizioni) {
            if (sped.getSpedizioneItems().isEmpty()) {
                spedizioneDao.deleteById(sped.getId());
            }
        }
        estrattoContoDao.save(estrattoConto);

        if (spedizioneDao.findByAbbonamento(abbonamento).isEmpty()) {
            abbonamento.setStatoAbbonamento(StatoAbbonamento.Annullato);
        }
        
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
        
        if (estrattoConto.getNumeroTotaleRiviste() == 0 && estrattoConto.getStorico() == null) { 
            estrattoContoDao.deleteById(estrattoConto.getId());
        } else {
            estrattoContoDao.save(estrattoConto);
        }
        if (spedizioneDao.findByAbbonamento(abbonamento).isEmpty()) {
            abbonamento.setStatoAbbonamento(StatoAbbonamento.Annullato);
        }
        abbonamentoDao.save(abbonamento);
    }

    @Override
    public void annullaAbbonamento(Abbonamento abbonamento) throws Exception {
        abbonamento.setStatoAbbonamento(StatoAbbonamento.Annullato);
        estrattoContoDao.findByAbbonamento(abbonamento).forEach(ec -> cancellaECAbbonamento(abbonamento, ec));        
    }

    @Override
    public void rimuoviECDaStorico(Storico storico)
            throws Exception {
        if (storico.getStatoStorico() != StatoStorico.SOSPESO) {
            return;
        }
        Campagna campagna = campagnaDao.findByAnno(Anno.getAnnoProssimo());
        if (campagna == null) {
            return;
        }        List<EstrattoConto> ecs = 
                estrattoContoDao
                .findByStorico(storico)
                .stream()
                .filter(ec -> ec.getAbbonamento().getAnno() == Anno.getAnnoProssimo())
                .collect(Collectors.toList());

        if (ecs.size() > 1 ) {
            throw new Exception("Un solo Estratto Conto per storico ogni anno");
        }
        
        if (ecs.isEmpty()) {
            return;
        } 
        EstrattoConto estrattoConto = ecs.iterator().next();
        Abbonamento abbonamento = abbonamentoDao.findById(estrattoConto.getAbbonamento().getId()).get();
        estrattoConto.setAbbonamento(abbonamento);
        
        cancellaECAbbonamento(abbonamento, estrattoConto);
        
    }

    @Override
    public void inviaCampagna(Campagna campagna) throws Exception {
        for (Abbonamento abb: Smd.inviaPropostaAbbonamentoCampagna(campagna, abbonamentoDao.findByCampagna(campagna))) {
            abbonamentoDao.save(abb);
        }
        campagnaDao.save(campagna);
        
    }
}
