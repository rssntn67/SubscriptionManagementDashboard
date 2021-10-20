package it.arsinfo.smd.service.impl;

import it.arsinfo.smd.dao.*;
import it.arsinfo.smd.dto.AbbonamentoConRiviste;
import it.arsinfo.smd.dto.RivistaAbbonamentoAggiorna;
import it.arsinfo.smd.dto.SpedizioneDto;
import it.arsinfo.smd.dto.SpedizioneWithItems;
import it.arsinfo.smd.entity.*;
import it.arsinfo.smd.service.Smd;
import it.arsinfo.smd.service.api.SmdService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class SmdServiceImpl implements SmdService {

    @Autowired
    private SpesaSpedizioneDao spesaSpedizioneDao;
    
    @Autowired
    private AbbonamentoDao abbonamentoDao;

    @Autowired
    private CampagnaDao campagnaDao;

    @Autowired
    private OperazioneSospendiDao operazioneSospendiDao;

    @Autowired
    private RivistaAbbonamentoDao rivistaAbbonamentoDao;
    
    @Autowired
    private SpedizioneDao spedizioneDao;

    @Autowired
    private SpedizioneItemDao spedizioneItemDao;

    @Autowired
    private OperazioneDao operazioneDao;

    @Autowired
    private OperazioneIncassoDao operazioneIncassoDao;

    @Autowired
    private VersamentoDao versamentoDao;
    
    @Autowired
    private DistintaVersamentoDao incassoDao;

    @Autowired
    private OffertaDao offertaDao;

    @Autowired
    private OfferteCumulateDao offerteDao;

    @Autowired
    private DocumentoTrasportoDao ddtDao;

    @Autowired
    private DocumentiTrasportoCumulatiDao ddtCumulatiDao;

    private static final Logger log = LoggerFactory.getLogger(SmdService.class);


    @Override
    public List<AbbonamentoConRiviste> get(List<Abbonamento> abbonamenti) {
    	List<AbbonamentoConRiviste> list = new ArrayList<>();
    	for (Abbonamento abbonamento: abbonamenti) {
    	    List<RivistaAbbonamento> ralist =  rivistaAbbonamentoDao.findByAbbonamento(abbonamento);
                list.add(new
                        AbbonamentoConRiviste(abbonamento,
                        ralist,
                        abbonamento.getIntestatario()));
    	}
    	return list;
    }

    @Override
    public void rimuovi(Abbonamento abbonamento) {
    	if (abbonamento.getIncassato().signum() > 0) {
        	log.warn("rimuovi: {} , Non si può cancellare un abbonamento incassato.", abbonamento);
            throw new UnsupportedOperationException("Non si può cancellare un abbonamento con incasso: "+abbonamento.getIncassato());    		
    	}

    	if (abbonamento.getCampagna() != null) {
    	    Campagna campagna= campagnaDao.findById(abbonamento.getCampagna().getId()).orElse(null);
    	    if (campagna == null ) {
                log.error("rimuovi: {} , Non si può cancellare un abbonamento con una campagna inesistente.", abbonamento);
                throw new UnsupportedOperationException("Non si può cancellare un abbonamento una campagna inesistente: "+abbonamento.getCampagna().getId());
            }
            if (campagna.getStatoCampagna() != StatoCampagna.Generata ) {
                log.error("rimuovi: {} , Non si può cancellare un abbonamento con una campagna nello stato {}.", abbonamento, campagna.getStatoCampagna());
                throw new UnsupportedOperationException("Non si può cancellare l'abbonamento di una campagna: "+campagna.getStatoCampagna());
            }
        }

        spedizioneDao
        .findByAbbonamento(abbonamento)
        .forEach(sped -> 
            {
                spedizioneItemDao.findBySpedizione(sped).forEach(item -> spedizioneItemDao.deleteById(item.getId()));
                spedizioneDao.deleteById(sped.getId());
            }
        );
        rivistaAbbonamentoDao.findByAbbonamento(abbonamento).forEach(ec -> rivistaAbbonamentoDao.deleteById(ec.getId()));
        abbonamentoDao.delete(abbonamento);
    }

    @Override
    public void genera(Abbonamento abbonamento) {
        List<SpedizioneWithItems> spedizioni = findByAbbonamento(abbonamento);
        for (RivistaAbbonamento ec: abbonamento.getItems()) {
            spedizioni = Abbonamento.genera(abbonamento, ec, spedizioni,spesaSpedizioneDao.findAll());
        }
        abbonamentoDao.save(abbonamento);
        for (RivistaAbbonamento ec: abbonamento.getItems()) {
            rivistaAbbonamentoDao.save(ec);
        }
        spedizioni.forEach(sped -> {
            spedizioneDao.save(sped.getSpedizione());
            sped.getSpedizioneItems().forEach(item -> spedizioneItemDao.save(item));
        });
    }

    @Override
    @Transactional
    public void aggiornaRivistaAbbonamento(RivistaAbbonamento rivistaAbbonamento, int numero, TipoAbbonamentoRivista tipo) throws UnsupportedOperationException {
        Abbonamento abbonamento = abbonamentoDao.findById(rivistaAbbonamento.getAbbonamento().getId()).orElse(null);
        if (abbonamento == null) throw new UnsupportedOperationException("Abbonamento not found");
        log.info("aggiorna: {} -> numero -> {},  tipo -> {} ", rivistaAbbonamento, numero, tipo );
        
        if (numero == rivistaAbbonamento.getNumero() && tipo == rivistaAbbonamento.getTipoAbbonamentoRivista()) {
            log.info("aggiorna: updated equals to persisted: {}", rivistaAbbonamento);        	
        	return;
        }

        RivistaAbbonamentoAggiorna aggiorna =
        		Smd.aggiorna(
        				abbonamento,
        				findByAbbonamento(abbonamento),
                        spesaSpedizioneDao.findAll(),
                        rivistaAbbonamento,
                        numero,
                        tipo
                );
        
        aggiorna.getRivisteToSave().forEach(r -> {
            log.info("aggiorna: {} save ", r);
        	rivistaAbbonamentoDao.save(r);
        	});
        
        if (aggiorna.getAbbonamentoToSave() != null) {
            log.info("aggiorna: {} save ", aggiorna.getAbbonamentoToSave());
        	aggiornaStato(aggiorna.getAbbonamentoToSave());
        }

        aggiorna.getSpedizioniToSave().forEach(sped -> {
        	log.info("aggiorna: {}, save {}", rivistaAbbonamento, sped.getSpedizione());
            spedizioneDao.save(sped.getSpedizione());
            sped.getSpedizioneItems().forEach(item -> spedizioneItemDao.save(item));
        });
        
        aggiorna.getItemsToDelete().forEach(rimitem -> {
        	log.info("aggiorna: {}, del {}", rivistaAbbonamento, rimitem);
        	spedizioneItemDao.deleteById(rimitem.getId());
        	});
        
        for (SpedizioneWithItems sped:aggiorna.getSpedizioniToSave()) {
            if (sped.getSpedizioneItems().isEmpty()) {
            	log.info("aggiorna: {}, del {}",rivistaAbbonamento, sped);
                spedizioneDao.deleteById(sped.getSpedizione().getId());
            }
        }
        
    }

    @Override
    @Transactional
    public void rimuovi(Abbonamento abbonamento, RivistaAbbonamento rivistaAbbonamento) throws Exception {
        if (rivistaAbbonamento == null || abbonamento == null)
            return;
        List<SpedizioneWithItems> spedizioni = findByAbbonamento(abbonamento);

        RivistaAbbonamentoAggiorna aggiorna = Smd.rimuovi(abbonamento,
                                                     rivistaAbbonamento, 
                                                     spedizioni,
                                                    spesaSpedizioneDao.findAll());  
        
        aggiorna.getSpedizioniToSave().forEach(sped -> {
            spedizioneDao.save(sped.getSpedizione());
            sped.getSpedizioneItems().forEach(item -> spedizioneItemDao.save(item));
        });
        aggiorna.getItemsToDelete().forEach(item -> spedizioneItemDao.deleteById(item.getId()));
        
        for (SpedizioneWithItems sped:spedizioni) {
            if (sped.getSpedizioneItems().isEmpty()) {
                spedizioneDao.deleteById(sped.getSpedizione().getId());
            }
        }
        
        aggiorna.getRivisteToDelete().forEach(r ->rivistaAbbonamentoDao.deleteById(r.getId()));
        aggiorna.getRivisteToSave().forEach(r->rivistaAbbonamentoDao.save(r));
    	abbonamentoDao.save(aggiorna.getAbbonamentoToSave());        
    }

    @Override
    public void generaStatisticheTipografia(Anno anno, Mese mese, Pubblicazione p) {
    	log.info("generaStatisticheTipografia: {}, {}, {}", mese,anno,p.getNome());
        Operazione saved = operazioneDao.findByAnnoAndMeseAndPubblicazione(anno, mese,p);
        if (saved != null && saved.getStatoOperazione() != StatoOperazione.Programmata) {
        	log.info("generaStatisticheTipografia: already done {}", saved);
            return;
        }
        if (saved != null) {
            operazioneDao.deleteById(saved.getId());
        }
        Operazione op = Operazione.generaOperazione(p,
        		findByMeseSpedizioneAndAnnoSpedizione(mese, anno,p), 
                                             mese, 
                                             anno);
		log.info("generaStatisticheTipografia {}", op);
    	if (op.getStimatoSped() > 0 || op.getStimatoSede() >0) {
                operazioneDao.save(op);                               
        }
        
    }

    @Override
    @Transactional
    public void inviaSpedizionere(Operazione operazione) {
    	log.info("inviaSpedizionere: {} - start", operazione);
        if ( operazione == null || operazione.getStatoOperazione() != StatoOperazione.Inviata) {
        	log.error("inviaSpedizionere: {} : Operazione non valida",operazione);
        	throw new UnsupportedOperationException("Operazione non valida");
        }
        inviaSpedizioni(operazione.getMese(), operazione.getAnno(), operazione.getPubblicazione(), InvioSpedizione.Spedizioniere);
        operazione.setStatoOperazione(StatoOperazione.Spedita);
        operazioneDao.save(operazione);
    	log.info("inviaSpedizionere: {} - end",operazione);

    }
    
    @Override
    @Transactional
    public void inviaAdpSede(Mese meseSpedizione, Anno annoSpedizione, InvioSpedizione invio) {
        final List<Long> spedizioniIds = 
    		spedizioneDao.findByMeseSpedizioneAndAnnoSpedizioneAndInvioSpedizione(
    				meseSpedizione, 
    				annoSpedizione, 
    				invio)
    		.stream()
    		.map(Spedizione::getId).collect(Collectors.toList());
        
        spedizioneItemDao.findByStatoSpedizione(StatoSpedizione.PROGRAMMATA)
        .stream()
        .filter(item -> spedizioniIds.contains(item.getSpedizione().getId()))
        .forEach(item -> 
        	{
    			item.setStatoSpedizione(StatoSpedizione.INVIATA);
    			spedizioneItemDao.save(item);
    		}
    	);
            	
    }

    @Override
    @Transactional
    public void annullaAdpSede(Mese meseSpedizione, Anno annoSpedizione, InvioSpedizione invio) {
        final List<Long> spedizioniIds = 
    		spedizioneDao.findByMeseSpedizioneAndAnnoSpedizioneAndInvioSpedizione(
    				meseSpedizione, 
    				annoSpedizione, 
    				invio)
    		.stream()
    		.map(Spedizione::getId).collect(Collectors.toList());
        
        spedizioneItemDao.findByStatoSpedizione(StatoSpedizione.SOSPESA)
        .stream()
        .filter(item -> spedizioniIds.contains(item.getSpedizione().getId()))
        .forEach(item -> 
        	{
    			item.setStatoSpedizione(StatoSpedizione.ANNULLATA);
    			spedizioneItemDao.save(item);
    		}
    	);
            	
    }

    @Override
    @Transactional
    public void inviaSpedizioni(Mese meseSpedizione, Anno annoSpedizione, Pubblicazione p, InvioSpedizione invio) {
        final List<Long> spedizioniIds = 
    		spedizioneDao.findByMeseSpedizioneAndAnnoSpedizioneAndInvioSpedizione(
    				meseSpedizione, 
    				annoSpedizione, 
    				invio)
    		.stream()
    		.map(Spedizione::getId).collect(Collectors.toList());
        
        spedizioneItemDao.findByPubblicazioneAndStatoSpedizione(p, StatoSpedizione.PROGRAMMATA)
        .stream()
        .filter(item -> spedizioniIds.contains(item.getSpedizione().getId()))
        .forEach(item -> 
        	{
    			item.setStatoSpedizione(StatoSpedizione.INVIATA);
    			spedizioneItemDao.save(item);
    		}
    	);
        
        spedizioneItemDao.findByPubblicazioneAndStatoSpedizione(p, StatoSpedizione.SOSPESA)
        .stream()
        .filter(item -> spedizioniIds.contains(item.getSpedizione().getId()))
        .forEach(item -> 
        	{
    			item.setStatoSpedizione(StatoSpedizione.ANNULLATA);
    			spedizioneItemDao.save(item);
    		}
    	);

    }
    
    @Override
    public List<SpedizioneDto> listBy(Pubblicazione pubblicazione, Mese meseSpedizione, Anno annoSpedizione, StatoSpedizione statoSpedizione, InvioSpedizione invio) {
        final List<SpedizioneItem> items = new ArrayList<>();
        final Set<Long> rivistaAbbonamentoIdSet =  new HashSet<>();
        final Map<Long,Spedizione> approved = 
        		spedizioneDao
        		.findByMeseSpedizioneAndAnnoSpedizioneAndInvioSpedizione(
        				meseSpedizione, 
        				annoSpedizione, 
        				invio)
        		.stream()
        		.collect(Collectors.toMap(Spedizione::getId, Function.identity()));
    	
        spedizioneItemDao
        	.findByPubblicazioneAndStatoSpedizione(pubblicazione,statoSpedizione)
        	.stream()
        	.filter(spedItem -> approved.containsKey(spedItem.getSpedizione().getId()))
        	.forEach(spedItem -> {
        		rivistaAbbonamentoIdSet.add(spedItem.getRivistaAbbonamento().getId());
        		items.add(spedItem);
			});
    	List<Long> omaggi = 
			rivistaAbbonamentoDao.findAllById(rivistaAbbonamentoIdSet)
                    .stream()
                    .filter(SmdServiceImpl::isOmaggio)
                    .map(RivistaAbbonamento::getId).collect(Collectors.toList());
    	List<SpedizioneDto> dtos = new ArrayList<>();
    	for (SpedizioneItem item: items) {
    		Spedizione sped = approved.get(item.getSpedizione().getId());
    		Anagrafica destinatario =  sped.getDestinatario();
    		SpedizioneDto dto = SpedizioneDto.getSpedizioneDto(sped,item, destinatario);
    		if (omaggi.contains(item.getRivistaAbbonamento().getId())) {
    			dto.setOmaggio();
    		}
    		dtos.add(dto);
    	}
    	return dtos;
    }
    
    @Override
    public List<SpedizioneDto> listBy(Mese meseSpedizione, Anno annoSpedizione, StatoSpedizione statoSpedizione, InvioSpedizione invio) {
        final List<SpedizioneItem> items = new ArrayList<>();
        final Set<Long> rivistaAbbonamentoIdSet =  new HashSet<>();
        final Map<Long,Spedizione> approved = 
        		spedizioneDao
        		.findByMeseSpedizioneAndAnnoSpedizioneAndInvioSpedizione(
        				meseSpedizione, 
        				annoSpedizione, 
        				invio)
        		.stream()
        		.collect(Collectors.toMap(Spedizione::getId, Function.identity()));
    	
        spedizioneItemDao
        	.findByStatoSpedizione(statoSpedizione)
        	.stream()
        	.filter(spedItem -> approved.containsKey(spedItem.getSpedizione().getId()))
        	.forEach(spedItem -> {
        		rivistaAbbonamentoIdSet.add(spedItem.getRivistaAbbonamento().getId());
        		items.add(spedItem);
			});
    	List<Long> omaggi = 
			rivistaAbbonamentoDao.findAllById(rivistaAbbonamentoIdSet)
                    .stream()
                    .filter(SmdServiceImpl::isOmaggio)
                    .map(RivistaAbbonamento::getId)
                    .collect(Collectors.toList());
    	List<SpedizioneDto> dtos = new ArrayList<>();
    	for (SpedizioneItem item: items) {
    		Spedizione sped = approved.get(item.getSpedizione().getId());
    		Anagrafica destinatario =  sped.getDestinatario();
    		SpedizioneDto dto = SpedizioneDto.getSpedizioneDto(sped,item, destinatario);
    		if (omaggi.contains(item.getRivistaAbbonamento().getId())) {
    			dto.setOmaggio();
    		}
    		dtos.add(dto);
    	}
    	return dtos;
    }


    @Override
    public void incassa(Abbonamento abbonamento, Versamento versamento, UserInfo user, String description) throws Exception {
        log.info("incassa: {}, {}, {}", user, abbonamento,versamento);
        if (versamento.getResiduo().signum() == 0) {
            log.warn("incassa: Versamento con residuo 0, non incassabile {} {} {}", abbonamento,versamento,user);
            throw new UnsupportedOperationException("incassa: Versamento con residuo 0, abbonamento non incassato");            
        }
        
        DistintaVersamento incasso = versamento.getDistintaVersamento();
        BigDecimal incassato = DistintaVersamento.incassa(incasso,versamento, abbonamento);
        if (versamento.getCommittente() == null) {
        	versamento.setCommittente(abbonamento.getIntestatario());
        }
        versamentoDao.save(versamento);
        incassoDao.save(incasso);
        aggiornaStato(abbonamento);

        OperazioneIncasso operIncasso = new OperazioneIncasso();
        operIncasso.setAbbonamento(abbonamento);
        operIncasso.setVersamento(versamento);
        operIncasso.setStatoOperazioneIncasso(StatoOperazioneIncasso.Incasso);
        operIncasso.setDescription(description);
        operIncasso.setOperatore(user.getUsername());
        operIncasso.setImporto(incassato);
        operazioneIncassoDao.save(operIncasso);
            
        log.info("incassa: {}", operIncasso);
    }

	@Override
	public void incassa(String ddtid, BigDecimal importo, DocumentiTrasportoCumulati ddtAnno, Versamento versamento,
			UserInfo user, Anagrafica committente) {
        log.info("incassa: {} {}, {}, {}",importo, user, ddtAnno,versamento);
        if (importo == null) {
            log.warn("incassa: Importo null, non incassabile {} {} {}", ddtAnno,versamento,user);
            throw new UnsupportedOperationException("incassa: I mporto null, offerta non incassata");            
        }
        if (versamento.getResiduo().signum() == 0) {
            log.warn("incassa: Versamento con residuo 0, non incassabile {} {} {} {}",importo, ddtAnno,versamento,user);
            throw new UnsupportedOperationException("incassa: Versamento con residuo 0, offerta non incassata");            
        }
        if (versamento.getResiduo().compareTo(importo) < 0) {
            log.warn("incassa: Versamento con residuo minore di importo, non incassabile {} {} {} {}", importo,ddtAnno,versamento,user);
            throw new UnsupportedOperationException("incassa: Versamento con residuo minore di importo, offerta non incassata");            
        }
        
        DistintaVersamento incasso = versamento.getDistintaVersamento();
        BigDecimal incassato = DistintaVersamento.incassa(incasso,versamento, ddtAnno,importo);
        if (versamento.getCommittente() == null) {
        	versamento.setCommittente(committente);
        }
        versamentoDao.save(versamento);
        incassoDao.save(incasso);
        ddtCumulatiDao.save(ddtAnno);
        DocumentoTrasporto ddt = new DocumentoTrasporto();
        ddt.setDdt(ddtid);
        ddt.setDocumentiTrasportoCumulati(ddtAnno);
        ddt.setVersamento(versamento);
        ddt.setStatoOperazioneIncasso(StatoOperazioneIncasso.Incasso);
        ddt.setOperatore(user.getUsername());
        ddt.setCommittente(committente);
        ddt.setImporto(incassato);
        ddtDao.save(ddt);
            
        log.info("incassa: {}", ddt);
		
	}

    @Override
    public void incassa(BigDecimal importo,OfferteCumulate offerte, Versamento versamento, UserInfo user, Anagrafica committente) throws Exception {
        log.info("incassa: {} {}, {}, {}",importo, user, offerte,versamento);
        if (importo == null) {
            log.warn("incassa: Importo null, non incassabile {} {} {}", offerte,versamento,user);
            throw new UnsupportedOperationException("incassa: I mporto null, offerta non incassata");            
        }
        if (versamento.getResiduo().signum() == 0) {
            log.warn("incassa: Versamento con residuo 0, non incassabile {} {} {} {}",importo, offerte,versamento,user);
            throw new UnsupportedOperationException("incassa: Versamento con residuo 0, offerta non incassata");            
        }
        if (versamento.getResiduo().compareTo(importo) < 0) {
            log.warn("incassa: Versamento con residuo minore di importo, non incassabile {} {} {} {}", importo,offerte,versamento,user);
            throw new UnsupportedOperationException("incassa: Versamento con residuo minore di importo, offerta non incassata");            
        }
        
        DistintaVersamento incasso = versamento.getDistintaVersamento();
        BigDecimal incassato = DistintaVersamento.incassa(incasso,versamento, offerte,importo);
        if (versamento.getCommittente() == null) {
        	versamento.setCommittente(committente);
        }
        versamentoDao.save(versamento);
        incassoDao.save(incasso);
        offerteDao.save(offerte);        
        Offerta offerta = new Offerta();
        offerta.setOfferteCumulate(offerte);
        offerta.setVersamento(versamento);
        offerta.setStatoOperazioneIncasso(StatoOperazioneIncasso.Incasso);
        offerta.setOperatore(user.getUsername());
        offerta.setCommittente(committente);
        offerta.setImporto(incassato);
        offertaDao.save(offerta);
            
        log.info("incassa: {}", offerta);
    }

	@Override
	public void storna(DocumentoTrasporto ddt, UserInfo user) {
    	if (ddt.getStatoOperazioneIncasso() == StatoOperazioneIncasso.Storno) {
            log.warn("storna: tipo Storno, non dissociabile {}", ddt);
            throw new UnsupportedOperationException("dissocia: Operazione tipo Storno, non dissociabile, non dissociabile");                		
    	}
    	Versamento versamento = ddt.getVersamento();
    	DocumentiTrasportoCumulati cumulati = ddt.getDocumentiTrasportoCumulati();
        if (versamento.getIncassato().signum() == 0) {
            log.warn("storna: Versamento con Incasso 0, non dissociabile {}", ddt);
            throw new UnsupportedOperationException("dissocia: Versamento con Incasso 0, non dissociabile");            
        }
        DistintaVersamento distinta = versamento.getDistintaVersamento();
        DistintaVersamento.storna(distinta, versamento, cumulati, ddt.getImporto());
        ddt.setStatoOperazioneIncasso(StatoOperazioneIncasso.IncassoStornato);
        ddtDao.save(ddt);

        if (ddt.getCommittente().equals(versamento.getCommittente()) && versamento.getIncassato().signum() == 0) {
        	versamento.setCommittente(null);
        }
        versamentoDao.save(versamento);
        incassoDao.save(distinta);        
        ddtCumulatiDao.save(cumulati); 
        
        DocumentoTrasporto operStorno = new DocumentoTrasporto();
        operStorno.setDdt(ddt.getDdt());
        operStorno.setDocumentiTrasportoCumulati(cumulati);
        operStorno.setVersamento(versamento);
        operStorno.setStatoOperazioneIncasso(StatoOperazioneIncasso.Storno);
        operStorno.setCommittente(ddt.getCommittente());
        operStorno.setOperatore(user.getUsername());
        operStorno.setImporto(ddt.getImporto());
        ddtDao.save(operStorno);
        log.info("storna: {}",operStorno );
		
	}


    @Override
    public void storna(Offerta offerta, UserInfo user) {
    	if (offerta.getStatoOperazioneIncasso() == StatoOperazioneIncasso.Storno) {
            log.warn("storna: tipo Storno, non dissociabile {}", offerta);
            throw new UnsupportedOperationException("dissocia: Operazione tipo Storno, non dissociabile, non dissociabile");                		
    	}
    	Versamento versamento = offerta.getVersamento();
    	OfferteCumulate offerte = offerta.getOfferteCumulate();
        if (versamento.getIncassato().signum() == 0) {
            log.warn("storna: Versamento con Incasso 0, non dissociabile {}", offerta);
            throw new UnsupportedOperationException("dissocia: Versamento con Incasso 0, non dissociabile");            
        }
        DistintaVersamento distinta = versamento.getDistintaVersamento();
        DistintaVersamento.storna(distinta, versamento, offerte, offerta.getImporto());
        offerta.setStatoOperazioneIncasso(StatoOperazioneIncasso.IncassoStornato);
        offertaDao.save(offerta);

        if (offerta.getCommittente().equals(versamento.getCommittente()) && versamento.getIncassato().signum() == 0) {
        	versamento.setCommittente(null);
        }
        versamentoDao.save(versamento);
        incassoDao.save(distinta);        
        offerteDao.save(offerte); 
        
        Offerta operStorno = new Offerta();
        operStorno.setOfferteCumulate(offerte);
        operStorno.setVersamento(versamento);
        operStorno.setStatoOperazioneIncasso(StatoOperazioneIncasso.Storno);
        operStorno.setCommittente(offerta.getCommittente());
        operStorno.setOperatore(user.getUsername());
        operStorno.setImporto(offerta.getImporto());
        offertaDao.save(operStorno);
        log.info("storna: {}",operStorno );
    }

    @Override
    public void storna(OperazioneIncasso operazioneIncasso, UserInfo user, String description) {
    	if (operazioneIncasso.getStatoOperazioneIncasso() == StatoOperazioneIncasso.Storno) {
            log.warn("storna: tipo Storno, non dissociabile {}", operazioneIncasso);
            throw new UnsupportedOperationException("dissocia: Operazione tipo Storno, non dissociabile, non dissociabile");                		
    	}
    	Versamento versamento = operazioneIncasso.getVersamento();
    	Abbonamento abbonamento = operazioneIncasso.getAbbonamento();
        if (versamento.getIncassato().signum() == 0) {
            log.warn("storna: Versamento con Incasso 0, non dissociabile {}", operazioneIncasso);
            throw new UnsupportedOperationException("dissocia: Versamento con Incasso 0, non dissociabile");            
        }
        DistintaVersamento incasso = versamento.getDistintaVersamento();
        DistintaVersamento.storna(incasso, versamento, abbonamento, operazioneIncasso.getImporto());
        operazioneIncasso.setStatoOperazioneIncasso(StatoOperazioneIncasso.IncassoStornato);
        operazioneIncassoDao.save(operazioneIncasso);
        versamentoDao.save(versamento);
        incassoDao.save(incasso);        
        abbonamentoDao.save(abbonamento); 
        
        OperazioneIncasso operStorno = new OperazioneIncasso();
        operStorno.setAbbonamento(abbonamento);
        operStorno.setVersamento(versamento);
        operStorno.setStatoOperazioneIncasso(StatoOperazioneIncasso.Storno);
        operStorno.setDescription(description);
        operStorno.setOperatore(user.getUsername());
        operStorno.setImporto(operazioneIncasso.getImporto());
        operazioneIncassoDao.save(operStorno);
        log.info("storna: {}",operStorno );

    }

    @Override
    public void sospendiSpedizioniProgrammate(Abbonamento abbonamento, RivistaAbbonamento rivista) {
        spedizioneDao.findByAbbonamento(abbonamento)
        .forEach(sped -> spedizioneItemDao
        .findBySpedizioneAndStatoSpedizioneAndRivistaAbbonamento(sped, StatoSpedizione.PROGRAMMATA,rivista)
        .forEach( item ->
        {
            switch (rivista.getStatoRivista()) {
                case Sospesa:
                item.setStatoSpedizione(StatoSpedizione.SOSPESA);
                spedizioneItemDao.save(item);
                break;
                case Attiva:
                default:
                break;
            }
        }));

    }

    @Override
    public void programmaSpedizioniSospese(Abbonamento abbonamento, RivistaAbbonamento rivista) {
        spedizioneDao.findByAbbonamento(abbonamento)
        .forEach(sped -> spedizioneItemDao
        .findBySpedizioneAndStatoSpedizioneAndRivistaAbbonamento(
                sped, StatoSpedizione.SOSPESA,rivista)
.forEach( item ->
{
if (sped.getInvioSpedizione() == InvioSpedizione.Spedizioniere ) {
Operazione operazione =
                        operazioneDao.findByAnnoAndMeseAndPubblicazione(sped.getAnnoSpedizione(), sped.getMeseSpedizione(),item.getPubblicazione());
if (operazione != null && operazione.getStatoOperazione() == StatoOperazione.Spedita) {
                    return;
}
}
switch (rivista.getStatoRivista()) {
            case Attiva:
                item.setStatoSpedizione(StatoSpedizione.PROGRAMMATA);
                spedizioneItemDao.save(item);
                break;
    case Sospesa:
    default:
                break;
            }
}));
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

    public List<SpedizioneWithItems> findByMeseSpedizioneAndAnnoSpedizione(Mese meseSpedizione, Anno annoSpedizione, Pubblicazione p) {
        Map<Long,SpedizioneWithItems> spedizioni = new HashMap<>();
        final Map<Long,Spedizione> approved = 
        		spedizioneDao
        		.findByMeseSpedizioneAndAnnoSpedizione(
        				meseSpedizione, 
        				annoSpedizione)
        		.stream()
        		.collect(Collectors.toMap(Spedizione::getId, Function.identity()));

        for (SpedizioneItem item: spedizioneItemDao.findByPubblicazione(p)) {
        	if (approved.containsKey(item.getSpedizione().getId())) {
            	Spedizione sped = approved.get(item.getSpedizione().getId());
        		if (!spedizioni.containsKey(sped.getId())) 
        			spedizioni.put(sped.getId(),new SpedizioneWithItems(sped));
        		spedizioni.get(sped.getId()).addSpedizioneItem(item);
        	}
        }
        return new ArrayList<>(spedizioni.values());
    }

	@Override
	public void inviaDuplicato(Spedizione spedizione, InvioSpedizione invio) {
		Abbonamento abbonamento = abbonamentoDao.findById(spedizione.getAbbonamento().getId()).orElse(null);
        assert abbonamento != null;
        List<SpedizioneItem> items = spedizioneItemDao.findBySpedizione(spedizione);
		for (SpedizioneItem item: items) {
			RivistaAbbonamento ra = new RivistaAbbonamento();
			ra.setAbbonamento(abbonamento);
			ra.setTipoAbbonamentoRivista(TipoAbbonamentoRivista.Duplicato);
			ra.setAnnoInizio(item.getAnnoPubblicazione());
			ra.setAnnoFine(item.getAnnoPubblicazione());
			ra.setMeseInizio(item.getMesePubblicazione());
			ra.setMeseFine(item.getMesePubblicazione());
			ra.setPubblicazione(item.getPubblicazione());
			ra.setDestinatario(spedizione.getDestinatario());
			ra.setNumero(item.getNumero());
			ra.setInvioSpedizione(invio);
            abbonamento.addItem(ra);
        }

		genera(abbonamento);
		
	}

	@Override
	public void aggiornaStato(Abbonamento abbonamento)  {
		Campagna campagna = campagnaDao.findByAnno(abbonamento.getAnno());
		if (campagna == null) {
		    return;
		}
        switch (campagna.getStatoCampagna()) {
            case InviatoSospeso:
                aggiornaCampagnaInviatoSospeso(abbonamento,campagna);
                break;
            case InviatoEC:
                aggiornaCampagnaInviatoEC(abbonamento,campagna);
                break;
            case Generata:
            case Inviata:
            case InviatoSollecito:
            case Chiusa:
            default:
                break;
		}
        abbonamentoDao.save(abbonamento);
	}

	private void aggiornaCampagnaInviatoEC(Abbonamento abbonamento, Campagna campagna) {
    	for (RivistaAbbonamento ra: rivistaAbbonamentoDao.
    			findByAbbonamento(abbonamento)) {
    		StatoRivista stato = getStatoRivista(campagna,abbonamento, ra);
        	if (stato != StatoRivista.Attiva) {
				ra.setStatoRivista(StatoRivista.Sospesa);
				rivistaAbbonamentoDao.save(ra);
				sospendiSpedizioniProgrammate(abbonamento,ra);
        	} else {
				ra.setStatoRivista(StatoRivista.Attiva);
				rivistaAbbonamentoDao.save(ra);
				programmaSpedizioniSospese(abbonamento,ra);
        	}
        }
	}

	private void aggiornaCampagnaInviatoSospeso(Abbonamento abbonamento, Campagna campagna) {
        List<Long> rivisteSospese = 
        		operazioneSospendiDao
        		.findByCampagna(campagna)
        		.stream()
        		.map(opsos -> opsos.getPubblicazione().getId())
        		.collect(Collectors.toList());
    	for (RivistaAbbonamento ra: rivistaAbbonamentoDao.
    			findByAbbonamento(abbonamento)) {
    		StatoRivista stato = getStatoRivista(campagna,abbonamento, ra);
        	boolean sospesa = rivisteSospese.contains(ra.getPubblicazione().getId());
        	if (stato != StatoRivista.Attiva && sospesa) {
				ra.setStatoRivista(StatoRivista.Sospesa);
				rivistaAbbonamentoDao.save(ra);
				sospendiSpedizioniProgrammate(abbonamento,ra);
        	} else {
				ra.setStatoRivista(StatoRivista.Attiva);
				rivistaAbbonamentoDao.save(ra);
				programmaSpedizioniSospese(abbonamento,ra);
        	}
        }
	}
	
	@Override
	public List<RivistaAbbonamento> getRivisteNotValid(Abbonamento abbonamento,Campagna campagna) {
        final List<Pubblicazione> rivisteSospeseList = new ArrayList<>();
        if (campagna.getStatoCampagna() == StatoCampagna.InviatoSospeso) {
            for (OperazioneSospendi op: operazioneSospendiDao.findByCampagna(campagna)) {
                rivisteSospeseList.add(op.getPubblicazione());
            }
        }
		return rivistaAbbonamentoDao
				.findByAbbonamento(abbonamento)
				.stream()
				.filter(ra ->
                        isRivistaValid(campagna, abbonamento, ra,rivisteSospeseList.contains(ra.getPubblicazione())))
				.collect(Collectors.toList());
	}


    @Override
    public StatoRivista getStatoRivista(Campagna campagna, Abbonamento abbonamento, RivistaAbbonamento rivista) {

        if (isOmaggio(rivista)) {
            return StatoRivista.Attiva;
        }

        StatoRivista stato = StatoRivista.Sospesa;
        switch (abbonamento.getStatoIncasso(campagna)) {
            case Si:
            case SiConDebito:
                stato = StatoRivista.Attiva;
                break;
            default:
                break;
        }

        return stato;
    }

    public static boolean isOmaggio(RivistaAbbonamento rivistaAbbonamento) {
        boolean isOmaggio=false;
        switch (rivistaAbbonamento.getTipoAbbonamentoRivista()) {
            case Duplicato:
            case OmaggioCuriaDiocesiana:
            case OmaggioCuriaGeneralizia:
            case OmaggioDirettoreAdp:
            case OmaggioEditore:
            case OmaggioGesuiti:
                isOmaggio=true;
                break;
            default:
                break;
        }
        return isOmaggio;
    }

    private boolean isRivistaValid(Campagna campagna, Abbonamento abbonamento, RivistaAbbonamento rivista, boolean isRivistaSospesa) {
        if (isOmaggio(rivista)) {
            return true;
        }
        if (campagna == null) {
            return true;
        }
        boolean value=true;
        switch (campagna.getStatoCampagna()) {
            case InviatoSospeso:
                if (isRivistaSospesa) {
                    value = abbonamento.isAbbonamentoValid(campagna);
                }
                break;
            case InviatoEC:
                value = abbonamento.isAbbonamentoValid(campagna);
            case Inviata:
            case InviatoSollecito:
            case Chiusa:
            default:
                break;
        }
        return value;
    }
}
