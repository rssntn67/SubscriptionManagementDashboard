package it.arsinfo.smd;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.Cassa;
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
import it.arsinfo.smd.entity.Storico;
import it.arsinfo.smd.entity.UserInfo;
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
    public void genera(Campagna campagna, List<Pubblicazione> attivi) {
        
        log.info("genera Campagna start {}", campagna);
        List<Abbonamento> 
            abbonamenti = 
              Smd.genera(campagna, 
                  anagraficaDao.findAll(),
                  storicoDao.findAll(),
                  attivi
        );
                                                           
        campagnaDao.save(campagna);
        campagna.getCampagnaItems().stream().forEach(ci -> campagnaItemDao.save(ci));

        abbonamenti.forEach(abb -> {
            final List<EstrattoConto> estrattiConto = new ArrayList<>(); 
            storicoDao.findByIntestatarioAndCassa(
                  abb.getIntestatario(),abb.getCassa())
                .stream()
                .filter(s -> s.attivo())
                .forEach(storico -> {
                    estrattiConto.add(Smd.genera(abb, storico));
                   storico.setStatoStorico(StatoStorico.Valido);
                   storicoDao.save(storico);
            });
            if (estrattiConto.size() >= 1) {
                genera(abb, estrattiConto.toArray(new EstrattoConto[estrattiConto.size()]));
            }
        });
        log.info("genera Campagna end");

    }

    @Override
    public void invia(Campagna campagna) throws Exception {
    	if (campagna.getStatoCampagna() != StatoCampagna.Generata )
    		return;
        for (Abbonamento abb: abbonamentoDao.findByCampagna(campagna)) {
            if (Smd.getStatoIncasso(abb) ==  Incassato.Omaggio) {
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
    public void estratto(Campagna campagna) throws Exception {
    	if (campagna.getStatoCampagna() != StatoCampagna.Inviata )
    		return;
        for (Abbonamento abbonamento :abbonamentoDao.findByCampagna(campagna)) {
            if (abbonamento.getStatoAbbonamento() == StatoAbbonamento.Proposto ) {
            	aggiornaStatoAbbonamento(abbonamento);
                abbonamentoDao.save(abbonamento);
            }
        }
        campagna.setStatoCampagna(StatoCampagna.InviatoEC);
        campagnaDao.save(campagna);
        
        
    }

    @Override
    public void chiudi(Campagna campagna) throws Exception {
    	if (campagna.getStatoCampagna() != StatoCampagna.Inviata )
    		return;
        for (Abbonamento abbonamento :abbonamentoDao.findByCampagna(campagna)) {
            switch (Smd.getStatoIncasso(abbonamento)) {
            case Si:
            	attiva(abbonamento);
                break;
            case No:
                abbonamento.setStatoAbbonamento(StatoAbbonamento.Annullato);
                annulla(abbonamento);
                break;
            case Omaggio:
                abbonamento.setStatoAbbonamento(StatoAbbonamento.Valido);
            	attiva(abbonamento);
                break;
            case Parzialmente:
                abbonamento.setStatoAbbonamento(StatoAbbonamento.Sospeso);
                sospendi(abbonamento);
                break;
            case SiConDebito:
                abbonamento.setStatoAbbonamento(StatoAbbonamento.Valido);
                break;
            default:
            	attiva(abbonamento);
                break;
            }
            abbonamentoDao.save(abbonamento);
        }
        campagna.setStatoCampagna(StatoCampagna.Chiusa);
        campagnaDao.save(campagna);
        
    }

    @Override
    public void delete(Campagna campagna) {
        abbonamentoDao.findByCampagna(campagna).stream().forEach(abb -> delete(abb));
        campagna.getCampagnaItems().stream().forEach(item -> campagnaItemDao.delete(item));
        campagnaDao.deleteById(campagna.getId());
        
    }

    @Override
    public void delete(Abbonamento abbonamento) {
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

    private EstrattoConto getByStorico(Campagna campagna,Storico storico) throws Exception{
        List<EstrattoConto> ecs = 
                estrattoContoDao
                .findByStorico(storico)
                .stream()
                .filter(ec -> {
                    Abbonamento abb = abbonamentoDao.findById(ec.getAbbonamento().getId()).get();
                    return abb.getAnno() == campagna.getAnno();
                })
                .collect(Collectors.toList());

        if (ecs.size() > 1 ) {
            throw new Exception("Un solo Estratto Conto per storico ogni anno");
        }        
        if (ecs.size()  == 0) {
        	return null;
        }        
        return ecs.iterator().next();
        
    }
    @Override
    public void rimuovi(Campagna campagna, Storico storico, Nota...note) throws Exception {
        if (campagna == null || storico == null 
             || campagna.getStatoCampagna() == StatoCampagna.Chiusa 
                ) {
            return;
        }
        storico.setStatoStorico(StatoStorico.Sospeso);
        save(storico, note);
        rimuovi(getByStorico(campagna, storico));
    }

    @Override
    public void genera(Campagna campagna, Storico storico, Nota...note) throws Exception {
        if (campagna == null  || storico == null
                || campagna.getStatoCampagna() == StatoCampagna.Chiusa 
                ) {
            return;
        }
        storico.setStatoStorico(StatoStorico.Valido);
        save(storico, note);        
        Abbonamento abbonamento =
                abbonamentoDao.findByIntestatarioAndCampagnaAndCassa(storico.getIntestatario(), campagna, storico.getCassa()); 
        if (abbonamento == null) {
            Anagrafica a = anagraficaDao.findById(storico.getIntestatario().getId()).get();
            abbonamento = Smd.genera(campagna, a, storico);
        }
        EstrattoConto estrattoConto = Smd.genera(abbonamento, storico);
        genera(abbonamento, estrattoConto);
        
    }

    @Override
    public void aggiorna(Campagna campagna, Storico storico, Nota...note) throws Exception {
        if (campagna == null  || storico == null
                || campagna.getStatoCampagna() == StatoCampagna.Chiusa 
                ) {
            return;
        }
        EstrattoConto ec = getByStorico(campagna, storico);
        
        if (ec == null) {
            genera(campagna, storico, note);
            return;
        }
        //Only updates are Numero and EstrattoConto other changes
        ec.setNumero(storico.getNumero());
        ec.setTipoEstrattoConto(storico.getTipoEstrattoConto());
        save(storico, note);
        aggiorna(ec);
    }

    @Override
    public void genera(Abbonamento abbonamento,
            EstrattoConto... contos) {
        List<SpedizioneWithItems> spedizioni = findByAbbonamento(abbonamento);
        for (EstrattoConto ec: contos) {
            spedizioni = Smd.genera(abbonamento, ec, spedizioni,spesaSpedizioneDao.findAll());
        }
        abbonamentoDao
            .findByIntestatarioAndAnnoAndCassa(
                       abbonamento.getIntestatario(), 
                       Anno.getAnnoPrecedente(abbonamento.getAnno()), 
                       abbonamento.getCassa()
                   )
                .forEach(abb -> 
                abbonamento.setPregresso(
                 abbonamento.getPregresso().add(abb.getResiduo())));
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
    public void aggiorna(EstrattoConto estrattoConto) throws Exception {
        // quantita -> spedizioneItem Importo Abbonamento
        // Tipo -> ordinario -> Importo Abbonamento
        Abbonamento abbonamento = abbonamentoDao.findById(estrattoConto.getAbbonamento().getId()).get();
        if (abbonamento == null) return;
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
        aggiornaStatoAbbonamento(abbonamento);
        abbonamentoDao.save(abbonamento);
    }

    @Override
    public void rimuovi(Abbonamento abbonamento) throws Exception {
        if (abbonamento == null) return;
        for (EstrattoConto ec: estrattoContoDao.findByAbbonamento(abbonamento)) {
            rimuovi(abbonamento,ec);
        }
    }

    @Override
    public void rimuovi(EstrattoConto estrattoConto) throws Exception {
        if (estrattoConto == null)
            return;
        Abbonamento abbonamento = abbonamentoDao.findById(estrattoConto.getAbbonamento().getId()).get();
        if (abbonamento == null) return;
        rimuovi(abbonamento,estrattoConto);
    }
    
    private void rimuovi(Abbonamento abbonamento, EstrattoConto estrattoConto) throws Exception {
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
        aggiornaStatoAbbonamento(abbonamento);
        if (spedizioneDao.findByAbbonamento(abbonamento).isEmpty() && estrattoContoDao.findByAbbonamento(abbonamento).isEmpty()) {
        	abbonamentoDao.delete(abbonamento);
        } else {
        	abbonamentoDao.save(abbonamento);
        }
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

    private void ricondiziona(Abbonamento abbonamento) throws Exception {
    	if (abbonamento.getCampagna() == null) {
    		return;
    	}
    	for (EstrattoConto estrattoConto : estrattoContoDao.findByAbbonamento(abbonamento)){
    		if (estrattoConto.getNumero()< 16 && estrattoConto.getNumero() > 0) {
    			continue;
    		}
			if (estrattoConto.getStorico() != null) {
				continue;
			}
			double costoUno = estrattoConto.getImporto().doubleValue() / (estrattoConto.getNumero());
			for (int i = 1; i < 6; i++) {
				if (i * costoUno == abbonamento.getResiduo().doubleValue()) {
					Storico storico = storicoDao.findById(estrattoConto.getStorico().getId()).get();
					Campagna campagna = campagnaDao.findById(abbonamento.getCampagna().getId()).get();
					int numero = estrattoConto.getNumero() - i;
					storico.setNumero(numero);
					if (numero == 0) {
						Nota nota = new Nota(storico);
						nota.setOperatore("Sistema:Incasso");
						nota.setDescription("Rimosso da Incasso: " + storico);
						rimuovi(campagna, storico, nota);
					} else {
						Nota nota = new Nota(storico);
						nota.setOperatore("Sistema:Incasso");
						nota.setDescription("Aggiorna da Incasso: " + storico);
						aggiorna(campagna, storico, nota);								
					}
					abbonamento.setStatoAbbonamento(StatoAbbonamento.Valido);
					return;
				}					
    		}
    	}
    }
    
    private void aggiornaStatoAbbonamento(Abbonamento abbonamento) throws Exception {
        switch (Smd.getStatoIncasso(abbonamento)) {
        case No:
            abbonamento.setStatoAbbonamento(StatoAbbonamento.Sospeso);
            break;
        case Omaggio:
            abbonamento.setStatoAbbonamento(StatoAbbonamento.Valido);
            break;
        case Parzialmente:
    		abbonamento.setStatoAbbonamento(StatoAbbonamento.Sospeso);
        	ricondiziona(abbonamento);
            break;
        case Si:
            abbonamento.setStatoAbbonamento(StatoAbbonamento.Valido);
            break;
        case SiConDebito:
            abbonamento.setStatoAbbonamento(StatoAbbonamento.Valido);
            break;
        default:
            break;
        
        }
    	
    }
    @Override
    public void incassa(Abbonamento abbonamento, Versamento versamento, UserInfo user) throws Exception {
    	boolean identifiedByCodeline = abbonamento.getCodeLine().equals(versamento.getCodeLine());
    	if (!identifiedByCodeline
			&& abbonamentoDao.findByCodeLine(versamento.getCodeLine()) != null ) {
            log.warn("incassa: Abbonamento e Versamento non associabili, esiste un abbonamento con la codeLine del Versamento {} {}", abbonamento,versamento);
    		throw new UnsupportedOperationException("Abbonamento e Versamento non associabili, esiste un abbonamento con la codeLine del Versamento");
    	}
        if (versamento.getResiduo().signum() == 0) {
            log.warn("incassa: Versamento con residuo 0, non incassabile {} {}", abbonamento,versamento);
            throw new UnsupportedOperationException("incassa: Versamento con residuo 0, abbonamento non incassato");            
        }
        if (   !identifiedByCodeline 
    		&& abbonamento.getVersamento() != null 
    		&&  abbonamento.getVersamento().getId().longValue() != versamento.getId()) {
            log.warn("incassa: Abbonamento già incassato da versamento senza matching Codeline, {} {}", abbonamento,versamento);
            throw new UnsupportedOperationException("incassa: Abbonamento già incassato da versamento senza matching Codeline");            
        }
        
        Incasso incasso = versamento.getIncasso();
        BigDecimal incassato = Smd.incassa(incasso,versamento, abbonamento);
        if (incassato.signum() <= 0) {
        	return;
        }
        versamento.setOperazione(versamento.getOperazione() +"incasso : operatore='"+user.getUsername()+"', " + new Date());
        versamentoDao.save(versamento);
        incassoDao.save(incasso);
            
        aggiornaStatoAbbonamento(abbonamento);
        abbonamento.setCassa(incasso.getCassa());
        abbonamento.setCcp(incasso.getCcp());
        abbonamento.setCuas(incasso.getCuas());
        if (abbonamento.getVersamento() == null && !identifiedByCodeline) {
            abbonamento.setVersamento(versamento);
        }
        abbonamentoDao.save(abbonamento);        
        log.info("incassato: {} by {} {} {}", incassato,user.getUsername(),abbonamento,versamento);
    }

    @Override
    public void dissocia(Abbonamento abbonamento, Versamento versamento, UserInfo user) throws Exception {
    	boolean identifiedByCodeline = abbonamento.getCodeLine().equals(versamento.getCodeLine());
    	if (!identifiedByCodeline
			&& abbonamentoDao.findByCodeLine(versamento.getCodeLine()) != null ) {
            log.warn("dissocia: Abbonamento e Versamento non associabili, esiste un abbonamento con la codeLine del Versamento {} {}", abbonamento,versamento);
    		throw new UnsupportedOperationException("Abbonamento e Versamento non associabili, esiste un abbonamento con la codeLine del Versamento");
    	}
        if (versamento.getIncassato().signum() == 0) {
            log.warn("dissocia: Versamento con Incasso 0, non dissociabile {} {}", abbonamento,versamento);
            throw new UnsupportedOperationException("dissocia: Versamento con Incasso 0, non dissociabile");            
        }
        if (   !identifiedByCodeline 
    		&& abbonamento.getVersamento() == null ) {
            log.warn("dissocia: Abbonamento non associato a versamento, {} {}", abbonamento,versamento);
            throw new UnsupportedOperationException("dissocia: Abbonamento non associato a Versamento");            
        }
        Incasso incasso = versamento.getIncasso();
        BigDecimal dissociato = Smd.dissocia(incasso, versamento, abbonamento);
        if (dissociato.signum() <= 0) {
        	return;
        }
        versamento.setOperazione(versamento.getOperazione() +"dissocia incasso : operatore='"+user.getUsername()+"', " + new Date());
        versamentoDao.save(versamento);
        incassoDao.save(incasso);
        
        aggiornaStatoAbbonamento(abbonamento);
        if (!identifiedByCodeline) {
        	abbonamento.setVersamento(null);
        }
        abbonamentoDao.save(abbonamento);        
        log.info("dissociato: {} by {} {} {}", dissociato,user.getUsername(),abbonamento,versamento);

    }

    @Override
    public List<Versamento> getAssociati(Abbonamento abbonamento) {
    	Map<Long,Versamento> associati = new HashMap<Long,Versamento>();
    	if (abbonamento.getVersamento() != null ) {
    		Versamento versamento = versamentoDao.findById(abbonamento.getVersamento().getId()).get();
    		associati.put(versamento.getId(),versamento);
    	}
    	for (Versamento versamento: versamentoDao.findByCodeLineContainingIgnoreCase(abbonamento.getCodeLine())) {
    		if (versamento.getIncassato().signum() > 0 ) {
    			associati.put(versamento.getId(),versamento);
    		}
    	}
    	return associati.values().stream().collect(Collectors.toList());
    }

    @Override
    public List<Abbonamento> getAssociati(Versamento versamento) {
        if (versamento == null || versamento.getIncassato().signum() == 0) {
            return new ArrayList<>();
        }        
        Map<Long,Abbonamento> associati = new HashMap<>();
        for (Abbonamento abbonamento: abbonamentoDao.findByVersamento(versamento)) {
        	associati.put(abbonamento.getId(), abbonamento);
        }

        if (versamento.getCodeLine() != null) {
            Abbonamento associato = abbonamentoDao.findByCodeLine(versamento.getCodeLine());
            if (associato != null ) {
            	associati.put(associato.getId(),associato);
            }
        }
        return associati.values().stream().collect(Collectors.toList());
    }

    @Override
    public List<Abbonamento> getAssociabili(Versamento versamento) {
        if (versamento == null || versamento.getResiduo().signum() == 0) {
        	return new ArrayList<>();
        }
        Map<Long,Abbonamento> associabili = new HashMap<>();
        Abbonamento associabile = abbonamentoDao.findByCodeLine(versamento.getCodeLine());
        if (associabile != null && versamento.getResiduo().signum() > 0) {
        	associabili.put(associabile.getId(),associabile);
        }

        abbonamentoDao
        .findByVersamento(null)
        .stream()
        .filter(abb -> 
            abb.getResiduo().signum() > 0 
            ).forEach(abb -> associabili.put(abb.getId(), abb));
       
        return associabili.values().stream().collect(Collectors.toList());
    }

    public void sospendiSpedizioni(Abbonamento abbonamento) throws Exception {
        spedizioneDao.findByAbbonamento(abbonamento)
        .stream()
        .filter(sped -> sped.getStatoSpedizione() == StatoSpedizione.PROGRAMMATA)
        .forEach(sped -> {
            sped.setStatoSpedizione(StatoSpedizione.SOSPESA);
            spedizioneDao.save(sped);
        });
    }

    public void riattivaSpedizioni(Abbonamento abbonamento) throws Exception {
        spedizioneDao.findByAbbonamento(abbonamento)
        .stream()
        .filter(sped -> sped.getStatoSpedizione() == StatoSpedizione.SOSPESA)
        .forEach(sped -> {
            sped.setStatoSpedizione(StatoSpedizione.PROGRAMMATA);
            spedizioneDao.save(sped);
        });
    }

    public void sospendiStorico(Abbonamento abbonamento) throws Exception {
        estrattoContoDao.findByAbbonamento(abbonamento)
        .stream()
        .filter(ec -> ec.getStorico() != null)
        .forEach(ec -> {
            Storico storico = storicoDao.findById(ec.getStorico().getId()).get();
            storico.setStatoStorico(StatoStorico.Sospeso);
            storicoDao.save(storico);
        });
    }

    public void riattivaStorico(Abbonamento abbonamento) throws Exception {
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
    public void save(Storico storico, Nota...note) {
        if (storico.getNumero() <= 0) {
            storico.setNumero(0);
            storico.setStatoStorico(StatoStorico.Sospeso);
        }
        storicoDao.save(storico);
        for (Nota nota:note)
            notaDao.save(nota);
    }

    @Override
    public List<SpedizioneWithItems> findByAbbonamento(Abbonamento abb) {
        List<SpedizioneWithItems> spedizioni = new ArrayList<>();
        if (abb.getId() == null) {
            return spedizioni;
        }
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

    @Override
    public void save(Incasso incasso) {
        List<Incasso> found = incassoDao
        .findByDataContabile(incasso.getDataContabile())
        .stream()
        .filter(inc -> 
           inc.getCassa() == Cassa.Ccp 
        && inc.getCuas() == incasso.getCuas() 
        && inc.getImporto().compareTo(incasso.getImporto()) == 0)
        .collect(Collectors.toList());
        if (found.isEmpty()) {
            incassoDao.save(incasso);
            incasso.getVersamenti().forEach(vers -> versamentoDao.save(vers));
        }
    }

    @Override
    public void save(Versamento versamento, UserInfo user) throws Exception {
        if (versamento.getIncasso() == null || versamento.getIncasso().getId() == null) {
            return;
        }
        versamentoDao.save(versamento);
        Incasso incasso = incassoDao.findById(versamento.getIncasso().getId()).get();
        Smd.calcoloImportoIncasso(incasso, versamentoDao.findByIncasso(incasso));
        incassoDao.save(incasso);
        for (Abbonamento abbonamento: getAssociati(versamento)) {
        	incassa(abbonamento, versamento, user);
        }
        
    }

    @Override
    public void delete(Versamento versamento, UserInfo user) throws Exception{
        if (versamento.getIncasso() == null || versamento.getIncasso().getId() == null) {
            return;
        }
        Incasso incasso = incassoDao.findById(versamento.getIncasso().getId()).get();
        for (Abbonamento abbonamento: getAssociati(versamento)) {
        	dissocia(abbonamento, versamento, user);
        }
        versamentoDao.delete(versamento);
        List<Versamento> versamenti = versamentoDao.findByIncasso(incasso);
        if (versamenti.size() == 0) {
            incassoDao.delete(incasso);
        } else {
            Smd.calcoloImportoIncasso(incasso,versamenti);
            incassoDao.save(incasso);
        }
        
        
    }

    @Override
    public void incassa(Abbonamento abbonamento, BigDecimal incassato, UserInfo user) throws Exception {

        Incasso incasso = 
                incassoDao
                    .findByDataContabileAndCassaAndCcpAndCuas(
                                abbonamento.getDataContabile(),
                                abbonamento.getCassa(),
                                abbonamento.getCcp(),
                                abbonamento.getCuas()
                            );
        if (incasso == null) {
            incasso = new Incasso();
            incasso.setDataContabile(abbonamento.getDataContabile());
            incasso.setCassa(abbonamento.getCassa());
            incasso.setCcp(abbonamento.getCcp());
            incasso.setCuas(abbonamento.getCuas());
            incassoDao.save(incasso);
        }
        Versamento versamento = new Versamento(incasso,incassato);
        versamento.setCodeLine(abbonamento.getCodeLine());
        versamento.setOperazione("Incassato da Abbonamento");
        versamento.setProgressivo(abbonamento.getProgressivo());
        versamento.setDataPagamento(abbonamento.getDataPagamento());
        versamentoDao.save(versamento);
        Smd.calcoloImportoIncasso(incasso,
                                  versamentoDao.findByIncasso(incasso));
        incassoDao.save(incasso);
        incassa(abbonamento, versamento,user);
    }
    
    @Override
    public void incassaCodeLine(List<Incasso> incassi,UserInfo user) throws Exception {
    	for (Incasso incasso:incassi) {
    		if (incasso.getResiduo().signum() == 0) {
    			continue;
    		}
    		for (Versamento v: versamentoDao.findByIncasso(incasso)) {
    			if (v.getIncassato().signum() != 0 || v.getCodeLine() == null) {
    				continue;
    			}
    			final Abbonamento abbonamento = abbonamentoDao.findByCodeLine(v.getCodeLine());
    			if (abbonamento != null && abbonamento.getVersamento() == null) {
    				v.setOperazione("Incassato con CodeLine");
    				incassa(abbonamento,v,user);
    			}
    		}
		}

    }

	@Override
	public void sospendi(Abbonamento abbonamento) throws Exception{
        sospendiSpedizioni(abbonamento);
        sospendiStorico(abbonamento);		
	}

	@Override
	public void attiva(Abbonamento abbonamento) throws Exception{
		riattivaSpedizioni(abbonamento);
		riattivaStorico(abbonamento);		
	}

	@Override
	public void annulla(Abbonamento abbonamento) throws Exception{
        sospendiStorico(abbonamento);
        rimuovi(abbonamento);		
	}

}
