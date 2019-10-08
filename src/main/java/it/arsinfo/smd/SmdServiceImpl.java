package it.arsinfo.smd;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.Incassato;
import it.arsinfo.smd.data.InvioSpedizione;
import it.arsinfo.smd.data.Mese;
import it.arsinfo.smd.data.StatoAbbonamento;
import it.arsinfo.smd.data.StatoCampagna;
import it.arsinfo.smd.data.StatoOperazione;
import it.arsinfo.smd.data.StatoSpedizione;
import it.arsinfo.smd.data.StatoStorico;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Campagna;
import it.arsinfo.smd.entity.EstrattoConto;
import it.arsinfo.smd.entity.Incasso;
import it.arsinfo.smd.entity.Nota;
import it.arsinfo.smd.entity.Operazione;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.entity.Spedizione;
import it.arsinfo.smd.entity.SpedizioneItem;
import it.arsinfo.smd.entity.SpedizioneWithItems;
import it.arsinfo.smd.entity.SpesaSpedizione;
import it.arsinfo.smd.entity.Storico;
import it.arsinfo.smd.entity.Versamento;
import it.arsinfo.smd.repository.AbbonamentoDao;
import it.arsinfo.smd.repository.AnagraficaDao;
import it.arsinfo.smd.repository.CampagnaDao;
import it.arsinfo.smd.repository.CampagnaItemDao;
import it.arsinfo.smd.repository.EstrattoContoDao;
import it.arsinfo.smd.repository.IncassoDao;
import it.arsinfo.smd.repository.NotaDao;
import it.arsinfo.smd.repository.OperazioneDao;
import it.arsinfo.smd.repository.PubblicazioneDao;
import it.arsinfo.smd.repository.SpedizioneDao;
import it.arsinfo.smd.repository.SpedizioneItemDao;
import it.arsinfo.smd.repository.SpesaSpedizioneDao;
import it.arsinfo.smd.repository.StoricoDao;
import it.arsinfo.smd.repository.VersamentoDao;

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
    NotaDao notaDao;

    @Autowired
    AbbonamentoDao abbonamentoDao;
    
    @Autowired
    EstrattoContoDao estrattoContoDao;
    
    @Autowired
    SpedizioneDao spedizioneDao;

    @Autowired
    SpedizioneItemDao spedizioneItemDao;

    @Autowired
    OperazioneDao operazioneDao;

    @Autowired
    PubblicazioneDao pubblicazioneDao;

    @Autowired
    VersamentoDao versamentoDao;
    
    @Autowired
    IncassoDao incassoDao;

    private static final Logger log = LoggerFactory.getLogger(SmdService.class);

    @Override
    public void generaCampagnaAbbonamenti(Campagna campagna, List<Pubblicazione> attivi) {
        
        log.info("genera Campagna start {}", campagna);
        List<Abbonamento> abbonamentiCampagna = Smd.generaAbbonamentiCampagna(campagna, anagraficaDao.findAll(),
                                                                              storicoDao.findAll(),
                                                                              attivi);
                                                           
        campagnaDao.save(campagna);
        campagna.getCampagnaItems().stream().forEach(ci -> campagnaItemDao.save(ci));

        for (Abbonamento abb:abbonamentiCampagna) {
            List<EstrattoConto> ecs = new ArrayList<>();
            for (Storico storico: 
                storicoDao.findByIntestatario(
                  abb.getIntestatario())
                .stream()
                .filter(s -> 
                       s.attivo() 
                    && s.getNumero() > 0
                    && s.getCassa() == abb.getCassa() 
                 ).collect(Collectors.toList())) {
               ecs.add(Smd.generaECDaStorico(abb, storico));
               storico.setStatoStorico(StatoStorico.Valido);
               storicoDao.save(storico);
            }
            if (ecs.isEmpty()) {
                continue;
            }
            genera(abb, ecs.toArray(new EstrattoConto[ecs.size()]));
        }
        log.info("genera Campagna end");

    }

    @Override
    public void inviaCampagna(Campagna campagna) throws Exception {
        for (Abbonamento abb: abbonamentoDao.findByCampagna(campagna)) {
            if (abb.getStatoIncasso() ==  Incassato.Omaggio) {
                abb.setStatoAbbonamento(StatoAbbonamento.Valido);
            } else {
                abb.setStatoAbbonamento(StatoAbbonamento.Proposto);
            }
            abbonamentoDao.save(abb);
        }
        campagna.setStatoCampagna(StatoCampagna.Inviata);
        campagnaDao.save(campagna);
    }

    @Override
    public void inviaEstrattoConto(Campagna campagna) throws Exception {
        for (Abbonamento abbonamento :abbonamentoDao.findByAnno(campagna.getAnno())) {
            if (abbonamento.getStatoAbbonamento() == StatoAbbonamento.Proposto ) {
                switch (abbonamento.getStatoIncasso()) {
                case Si:
                    abbonamento.setStatoAbbonamento(StatoAbbonamento.Valido);
                    break;
                case No:
                    abbonamento.setStatoAbbonamento(StatoAbbonamento.Sospeso);
                    sospendiSpedizioniAbbonamento(abbonamento);
                    sospendiStoricoAbbonamento(abbonamento);
                    break;
                case Omaggio:
                    abbonamento.setStatoAbbonamento(StatoAbbonamento.Valido);
                    break;
                case Parzialmente:
                    abbonamento.setStatoAbbonamento(StatoAbbonamento.Sospeso);
                    sospendiSpedizioniAbbonamento(abbonamento);
                    sospendiStoricoAbbonamento(abbonamento);
                    break;
                case SiConDebito:
                    abbonamento.setStatoAbbonamento(StatoAbbonamento.Valido);
                    break;
                default:
                    break;
                }
            }
            abbonamentoDao.save(abbonamento);
        }
        campagna.setStatoCampagna(StatoCampagna.InviatoEC);
        campagnaDao.save(campagna);
        
        
    }

    @Override
    public void chiudiCampagna(Campagna campagna) throws Exception {
        abbonamentoDao.findByAnno(campagna.getAnno())
        .stream()
        .filter(abbonamento -> abbonamento.getStatoAbbonamento() == StatoAbbonamento.Sospeso)
        .forEach(abbonamento -> {
            try {
                annullaAbbonamento(abbonamento);
            } catch (Exception e) {
                log.error(e.getMessage(),e);
            }
        });
        campagna.setStatoCampagna(StatoCampagna.Chiusa);
        campagnaDao.save(campagna);
        
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
                spedizioneItemDao.findBySpedizione(sped).forEach(item -> {
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
        if (storico.getStatoStorico() == StatoStorico.Sospeso) {
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
            abbonamento.setCodeLine(Abbonamento.generaCodeLine(abbonamento.getAnno(),storico.getIntestatario()));
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
        List<SpedizioneWithItems> spedizioni = new ArrayList<>();
        List<SpesaSpedizione> spese = spesaSpedizioneDao.findAll();
        for (EstrattoConto ec: contos) {
            spedizioni = Smd.genera(abbonamento, ec, spedizioni,spese);
        }
        abbonamentoDao
            .findByIntestatarioAndAnnoAndCassa(
                           abbonamento.getIntestatario(), 
                           Anno.getAnnoPrecedente(abbonamento.getAnno()), 
                           abbonamento.getCassa()).forEach(abb -> abbonamento.setPregresso(abbonamento.getPregresso().add(abb.getResiduo())));
        abbonamentoDao.save(abbonamento);
        for (EstrattoConto ec: contos) {
            estrattoContoDao.save(ec);
        }
        spedizioni.stream().forEach(sped -> {
            spedizioneDao.save(sped.getSpedizione());
            sped.getSpedizioneItems().stream().forEach(item -> spedizioneItemDao.save(item));
        });
    }

    @Override
    public void aggiornaECAbbonamento(Abbonamento abbonamento,
            EstrattoConto estrattoConto) {
        List<SpedizioneWithItems> spedizioni = findByAbbonamento(abbonamento);
        List<SpedizioneItem> deleted = Smd.aggiornaEC(abbonamento,
                                                     estrattoConto, 
                                                     spedizioni,
                                                    spesaSpedizioneDao.findAll());  
        
        spedizioni.stream().forEach(sped -> {
            spedizioneDao.save(sped.getSpedizione());
            sped.getSpedizioneItems().stream().forEach(item -> spedizioneItemDao.save(item));
        });
        
        deleted.forEach(rimitem -> spedizioneItemDao.deleteById(rimitem.getId()));
        
        for (SpedizioneWithItems sped:spedizioni) {
            if (sped.getSpedizioneItems().isEmpty()) {
                spedizioneDao.deleteById(sped.getSpedizione().getId());
            }
        }
        estrattoContoDao.save(estrattoConto);        
        abbonamentoDao.save(abbonamento);

    }

    @Override
    public void cancellaECAbbonamento(Abbonamento abbonamento,
            EstrattoConto estrattoConto) {
        List<SpedizioneWithItems> spedizioni = findByAbbonamento(abbonamento);

        List<SpedizioneItem> deleted = Smd.rimuoviEC(abbonamento,
                                                     estrattoConto, 
                                                     spedizioni,
                                                    spesaSpedizioneDao.findAll());  
        
        spedizioni.stream().forEach(sped -> {
            spedizioneDao.save(sped.getSpedizione());
            sped.getSpedizioneItems().stream().forEach(item -> {
                spedizioneItemDao.save(item);
               });
        });
        for (SpedizioneItem delitem: deleted ) {
            spedizioneItemDao.deleteById(delitem.getId());
        }
        
        for (SpedizioneWithItems sped:spedizioni) {
            if (sped.getSpedizioneItems().isEmpty()) {
                spedizioneDao.deleteById(sped.getSpedizione().getId());
            }
        }
        
        if (estrattoConto.getNumeroTotaleRiviste() == 0 && estrattoConto.getStorico() == null) { 
            estrattoContoDao.deleteById(estrattoConto.getId());
        } else {
            estrattoContoDao.save(estrattoConto);
        }
        abbonamentoDao.save(abbonamento);
    }

    @Override
    public void rimuoviECDaStorico(Storico storico)
            throws Exception {
        if (storico.getStatoStorico() != StatoStorico.Sospeso) {
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
    public void generaStatisticheTipografia(Anno anno, Mese mese) {
        pubblicazioneDao.findAll().forEach(p -> {
            Operazione saved = operazioneDao.findByAnnoAndMeseAndPubblicazione(anno, mese,p);
            if (saved != null && saved.getStatoOperazione() != StatoOperazione.Programmata) {
                return;
            }
            if (saved != null) {
                operazioneDao.deleteById(saved.getId());
            }
            Operazione op = Smd.generaOperazione(p,
                                             findByMeseSpedizioneAndAnnoSpedizione(mese, anno), 
                                             mese, 
                                             anno);
        if (op.getStimatoSped() > 0 || op.getStimatoSede() >0) {
            operazioneDao.save(op);                               
        }
        });
        
    }

    @Override
    public void generaStatisticheTipografia(Anno anno) {
        EnumSet.allOf(Mese.class).forEach(mese -> generaStatisticheTipografia(anno, mese));
    }

    @Override
    public void inviaSpedizionere(Mese meseSpedizione, Anno annoSpedizione) {
        operazioneDao.findByAnnoAndMese(annoSpedizione, meseSpedizione).forEach( operazione -> {
            if (operazione.getStatoOperazione() != StatoOperazione.Inviata) {
                throw new UnsupportedOperationException("Bisogna inviare tutti le pubblicazione del mese");
            }
        });

        operazioneDao.findByAnnoAndMese(annoSpedizione, meseSpedizione).forEach( operazione -> {
            operazione.setStatoOperazione(StatoOperazione.Spedita);
            operazioneDao.save(operazione);
        });

        spedizioneDao
        .findByMeseSpedizioneAndAnnoSpedizione(meseSpedizione,annoSpedizione)
        .stream().filter(sped -> sped.getInvioSpedizione() == InvioSpedizione.Spedizioniere)
        .forEach(sped -> {
            sped.setStatoSpedizione(StatoSpedizione.INVIATA);
            spedizioneDao.save(sped);
        });
    }

    @Override
    public List<SpedizioneItem> listItems(Mese meseSpedizione, Anno annoSpedizione, InvioSpedizione inviosped) {
        final List<SpedizioneItem> items = new ArrayList<>();
        spedizioneDao
            .findByMeseSpedizioneAndAnnoSpedizione(meseSpedizione, annoSpedizione)
            .stream().filter(sped -> sped.getInvioSpedizione() == inviosped)
            .forEach(sped -> {
                Abbonamento abb = abbonamentoDao.findById(sped.getAbbonamento().getId()).get();
                sped.setAbbonamento(abb);
                items.addAll(spedizioneItemDao.findBySpedizione(sped));
            });
        return items;
    }

    @Override
    public void incassa(Abbonamento abbonamento, Versamento versamento) throws Exception {
        Incasso incasso = versamento.getIncasso();
        Smd.incassa(incasso,versamento, abbonamento);
        versamentoDao.save(versamento);
        incassoDao.save(incasso);
        Campagna campagna = campagnaDao.findByAnno(abbonamento.getAnno());
        if (campagna != null) {
            
        switch (campagna.getStatoCampagna()) {
            case Generata:
                break;
            case Chiusa:
                break;
            case Inviata:
                break;
            case InviatoEC:
                switch (abbonamento.getStatoIncasso()) {
                case No:
                    break;
                case Omaggio:
                    break;
                case Parzialmente:
                    break;
                case Si:
                    abbonamento.setStatoAbbonamento(StatoAbbonamento.Valido);
                    riattivaSpedizioniAbbonamento(abbonamento);
                    riattivaStoricoAbbonamento(abbonamento);
                    break;
                case SiConDebito:
                    abbonamento.setStatoAbbonamento(StatoAbbonamento.Valido);
                    riattivaSpedizioniAbbonamento(abbonamento);
                    riattivaStoricoAbbonamento(abbonamento);
                    break;
                default:
                    break;
                
                }
                break;
            default:
                break;                                
        }
        }

        abbonamentoDao.save(abbonamento);
        
    }

    @Override
    public void reverti(Abbonamento abbonamento, Versamento versamento) throws Exception {
        Incasso incasso = versamento.getIncasso();
        Smd.dissocia(incasso, versamento, abbonamento);
        versamentoDao.save(versamento);
        incassoDao.save(incasso);
        Campagna campagna = campagnaDao.findByAnno(abbonamento.getAnno());
        if (campagna == null) {
            abbonamento.setStatoAbbonamento(StatoAbbonamento.Nuovo);
        } else if (abbonamento.getStatoAbbonamento() != StatoAbbonamento.Annullato) {
            switch (campagna.getStatoCampagna()) {
            case Generata:
                abbonamento.setStatoAbbonamento(StatoAbbonamento.Nuovo);
                break;
            case Chiusa:
                break;
            case Inviata:
                abbonamento.setStatoAbbonamento(StatoAbbonamento.Proposto);
                break;
            case InviatoEC:
                abbonamento.setStatoAbbonamento(StatoAbbonamento.Sospeso);
                sospendiSpedizioniAbbonamento(abbonamento);
                sospendiStoricoAbbonamento(abbonamento);
                break;
            default:
                break;                                
            }
        }
        abbonamentoDao.save(abbonamento);        

    }

    @Override
    public List<Abbonamento> getAssociati(Versamento versamento) {
        if (versamento == null) {
            return new ArrayList<>();
        }
        return abbonamentoDao.findByVersamento(versamento);
    }

    @Override
    public List<Abbonamento> getAssociabili(Versamento versamento) {
        if (versamento == null || versamento.getResiduo().compareTo(BigDecimal.ZERO) <= 0) {
            return new ArrayList<>();
        }
        return abbonamentoDao
                .findByVersamento(null)
                .stream()
                .filter(abb -> 
                    abb.getStatoIncasso() == Incassato.No 
                    )
                .collect(Collectors.toList());
    }

    @Override
    public void sospendiSpedizioniAbbonamento(Abbonamento abbonamento) throws Exception {
        spedizioneDao.findByAbbonamento(abbonamento)
        .stream()
        .filter(sped -> sped.getStatoSpedizione() == StatoSpedizione.PROGRAMMATA)
        .forEach(sped -> {
            sped.setStatoSpedizione(StatoSpedizione.SOSPESA);
            spedizioneDao.save(sped);
        });
    }

    @Override
    public void riattivaSpedizioniAbbonamento(Abbonamento abbonamento) throws Exception {
        spedizioneDao.findByAbbonamento(abbonamento)
        .stream()
        .filter(sped -> sped.getStatoSpedizione() == StatoSpedizione.SOSPESA)
        .forEach(sped -> {
            sped.setStatoSpedizione(StatoSpedizione.PROGRAMMATA);
            spedizioneDao.save(sped);
        });
    }

    @Override
    public void sospendiStoricoAbbonamento(Abbonamento abbonamento) throws Exception {
        estrattoContoDao.findByAbbonamento(abbonamento)
        .stream()
        .filter(ec -> ec.getStorico() != null)
        .forEach(ec -> {
            Storico storico = storicoDao.findById(ec.getStorico().getId()).get();
            storico.setStatoStorico(StatoStorico.Sospeso);
            storicoDao.save(storico);
        });
    }

    @Override
    public void riattivaStoricoAbbonamento(Abbonamento abbonamento) throws Exception {
        estrattoContoDao.findByAbbonamento(abbonamento)
        .stream()
        .filter(ec -> ec.getStorico() != null)
        .forEach(ec -> {
            Storico storico = storicoDao.findById(ec.getStorico().getId()).get();
            storico.setStatoStorico(StatoStorico.Valido);
            storicoDao.save(storico);
        });
    }

    @Override
    public void annullaAbbonamento(Abbonamento abbonamento) throws Exception {
        sospendiStoricoAbbonamento(abbonamento);
        abbonamento.setStatoAbbonamento(StatoAbbonamento.Annullato);
        estrattoContoDao.findByAbbonamento(abbonamento).forEach(ec -> cancellaECAbbonamento(abbonamento, ec));
    }

    @Override
    public List<Spedizione> findSpedizioneByDestinatario(Anagrafica a) {
        log.info("Spedizioni By Destinatario fetch start");
        List<Spedizione> spedizioni = spedizioneDao.findByDestinatario(a);
        log.info("Spedizioni By Destinatario fetch end");
        return spedizioni;
    }

    @Override
    public List<Spedizione> findSpedizioneByPubblicazione(Pubblicazione p) {
        log.info("Spedizioni By Pubblicazione fetch start");
        final List<Spedizione> spedizioni = new ArrayList<Spedizione>();
        spedizioneItemDao.findByPubblicazione(p).forEach(si -> spedizioni.add(si.getSpedizione()));
        log.info("Spedizioni By Pubblicazione fetch end");
        return spedizioni;
    }


    @Override
    public List<Spedizione> findSpedizioneAll() {
        log.info("Spedizioni All fetch start");
        List<Spedizione> spedizioni = spedizioneDao.findAll();
        log.info("Spedizioni All fetch end");
        return spedizioni;
    }

    @Override
    public void delete(Storico storico) {
        notaDao.findByStorico(storico).forEach(nota->notaDao.deleteById(nota.getId()));
        storicoDao.delete(storico);
        
    }

    @Override
    public void save(Storico storico, Nota nota) {
        storicoDao.save(storico);
        notaDao.save(nota);
    }

    @Override
    public List<SpedizioneWithItems> findByAbbonamento(Abbonamento abb) {
        List<SpedizioneWithItems> spedizioni = new ArrayList<>();
        for (Spedizione sped: spedizioneDao.findByAbbonamento(abb)) {
            SpedizioneWithItems swit = new SpedizioneWithItems(sped);
            swit.setSpedizioneItems(spedizioneItemDao.findBySpedizione(sped));
            spedizioni.add(swit);
        }
        return spedizioni;
    }

    public List<SpedizioneWithItems> findByMeseSpedizioneAndAnnoSpedizione(Mese mese, Anno anno) {
        List<SpedizioneWithItems> spedizioni = new ArrayList<>();
        for (Spedizione sped: spedizioneDao.findByMeseSpedizioneAndAnnoSpedizione(mese, anno)) {
            SpedizioneWithItems swit = new SpedizioneWithItems(sped);
            swit.setSpedizioneItems(spedizioneItemDao.findBySpedizione(sped));
            spedizioni.add(swit);
        }
        return spedizioni;
    }

}
