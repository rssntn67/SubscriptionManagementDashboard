package it.arsinfo.smd.service.dao;

import it.arsinfo.smd.dao.*;
import it.arsinfo.smd.data.*;
import it.arsinfo.smd.dto.AbbonamentoConRiviste;
import it.arsinfo.smd.dto.SpedizioneDto;
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
    private AnagraficaDao anagraficaDao;

    @Autowired
    private CampagnaDao campagnaDao;

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

    @Autowired
    private OperazioneSospendiDao operazioneSospendiDao;
    
    private static final Logger log = LoggerFactory.getLogger(SmdService.class);


    @Override
    public List<AbbonamentoConRiviste> get(List<Abbonamento> abbonamenti) {
    	List<AbbonamentoConRiviste> list = new ArrayList<>();
    	for (Abbonamento abbonamento: abbonamenti) {
    	    List<RivistaAbbonamento> ralist =  rivistaAbbonamentoDao.findByAbbonamento(abbonamento);
            Anagrafica co;
            co = null;
    	    if (abbonamento.getIntestatario().getCo() != null) {
                co = anagraficaDao.findById(abbonamento.getIntestatario().getCo().getId()).orElse(null);
            }
    	    if (co != null) {
                list.add(new
                        AbbonamentoConRiviste(abbonamento, ralist,abbonamento.getIntestatario(), co));
            } else {
                list.add(new
                        AbbonamentoConRiviste(abbonamento,
                        ralist,
                        abbonamento.getIntestatario()));
            }
    	}
    	return list;
    }


    @Override
    public void rimuovi(Abbonamento abbonamento) {
    	if (abbonamento.getIncassato().signum() > 0) {
        	log.warn("rimuovi: {} , Non si può cancellare un abbonamento incassato.", abbonamento);
            throw new UnsupportedOperationException("Non si può cancellare un abbonamento con incasso: "+abbonamento.getIncassato());    		
    	}
        if (abbonamento.getStatoAbbonamento() != StatoAbbonamento.Nuovo) {
        	log.warn("rimuovi: {} , Non si può cancellare un abbonamento con stato in uno stato diverso da Nuovo.", abbonamento);
            throw new UnsupportedOperationException("Non si può cancellare abbonamento con rivista nello stato: "+abbonamento.getStatoAbbonamento());
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
            spedizioni = Smd.genera(abbonamento, ec, spedizioni,spesaSpedizioneDao.findAll());
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
        Operazione op = Smd.generaOperazione(p,
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
                    .filter(Smd::isOmaggio)
                    .map(RivistaAbbonamento::getId).collect(Collectors.toList());
    	List<SpedizioneDto> dtos = new ArrayList<>();
    	for (SpedizioneItem item: items) {
    		Spedizione sped = approved.get(item.getSpedizione().getId());
    		Anagrafica destinatario =  sped.getDestinatario();
    		Anagrafica co = destinatario.getCo();
    		SpedizioneDto dto;
            if (co == null) {
    			dto = SpedizioneDto.getSpedizioneDto(sped,item, destinatario);
    		} else {
        		co = anagraficaDao.findById(co.getId()).orElse(null);
        		dto=SpedizioneDto.getSpedizioneDto(sped,item, destinatario, co);
    		}
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
                    .filter(Smd::isOmaggio)
                    .map(RivistaAbbonamento::getId)
                    .collect(Collectors.toList());
    	List<SpedizioneDto> dtos = new ArrayList<>();
    	for (SpedizioneItem item: items) {
    		Spedizione sped = approved.get(item.getSpedizione().getId());
    		Anagrafica destinatario =  sped.getDestinatario();
    		Anagrafica co = destinatario.getCo();
    		SpedizioneDto dto;
    		if (co == null) {
    			dto = SpedizioneDto.getSpedizioneDto(sped,item, destinatario);
    		} else {
        		co = anagraficaDao.findById(co.getId()).orElse(null);
        		dto=SpedizioneDto.getSpedizioneDto(sped,item, destinatario, co);
    		}
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
        BigDecimal incassato = Smd.incassa(incasso,versamento, abbonamento);
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
        BigDecimal incassato = Smd.incassa(incasso,versamento, ddtAnno,importo);    
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
        BigDecimal incassato = Smd.incassa(incasso,versamento, offerte,importo);    
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
        Smd.storna(distinta, versamento, cumulati, ddt.getImporto());
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
        Smd.storna(distinta, versamento, offerte, offerta.getImporto());
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
        Smd.storna(incasso, versamento, abbonamento, operazioneIncasso.getImporto());
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
		List<SpedizioneItem> items = spedizioneItemDao.findBySpedizione(spedizione);
		for (SpedizioneItem item: items) {
			
			RivistaAbbonamento ra0= rivistaAbbonamentoDao.findById(item.getRivistaAbbonamento().getId()).orElse(null);
			if (ra0 == null) {
				log.error("inviaDuplicato: rivista abbonamento not trovata per item {}", item);
				throw new UnsupportedOperationException("Rivista Abbonamento non trovata");
			}
			RivistaAbbonamento ra = new RivistaAbbonamento();
			ra.setAbbonamento(abbonamento);
			ra.setTipoAbbonamentoRivista(TipoAbbonamentoRivista.Duplicato);
			ra.setAnnoInizio(item.getAnnoPubblicazione());
			ra.setAnnoFine(item.getAnnoPubblicazione());
			ra.setMeseInizio(item.getMesePubblicazione());
			ra.setMeseFine(item.getMesePubblicazione());
			ra.setPubblicazione(item.getPubblicazione());
			ra.setDestinatario(ra0.getDestinatario());
			ra.setInvioSpedizione(invio);
            assert abbonamento != null;
            abbonamento.addItem(ra);
        }

		genera(abbonamento);
		
	}

	@Override
	public void aggiornaStato(Abbonamento abbonamento)  {
		Campagna campagna = campagnaDao.findByAnno(abbonamento.getAnno());
		if (campagna == null) {
    		abbonamento.setStatoAbbonamento(StatoAbbonamento.Nuovo);
		} else {
	        switch (campagna.getStatoCampagna()) {
	        	case Generata:
	        		abbonamento.setStatoAbbonamento(StatoAbbonamento.Nuovo);
	        		break;
	        	
	        	case Inviata:

                case InviatoSollecito:
                    if (getNotValid(abbonamento, campagna).isEmpty()) {
	        			abbonamento.setStatoAbbonamento(StatoAbbonamento.Valido);
	        		} else {
	        			abbonamento.setStatoAbbonamento(StatoAbbonamento.Proposto);
	        		}
	        		break;

                case InviatoSospeso:
	        		abbonamento.setStatoAbbonamento(aggiornaCampagnaInviatoSospeso(abbonamento,campagna));
	        		break;
	        	
	        	case InviatoEC:	
	        		abbonamento.setStatoAbbonamento(aggiornaCampagnaInviatoEC(abbonamento));
	        		break;
	        	
	        	case Chiusa:
	    		default:
	    			break;
	        }
		}
        abbonamentoDao.save(abbonamento);
	}

	private StatoAbbonamento aggiornaCampagnaInviatoEC(Abbonamento abbonamento) {
    	boolean almenounarivistasospesa=false;
    	boolean almenounarivistaattiva=false;
    	for (RivistaAbbonamento ra: rivistaAbbonamentoDao.
    			findByAbbonamento(abbonamento)) {
    		StatoRivista stato = Smd.getStatoRivista(abbonamento, ra);
        	if (stato != StatoRivista.Attiva) {
        		almenounarivistasospesa=true;
				ra.setStatoRivista(StatoRivista.Sospesa);
				rivistaAbbonamentoDao.save(ra);
				sospendiSpedizioniProgrammate(abbonamento,ra);
        	} else {
        		almenounarivistaattiva=true;
				ra.setStatoRivista(StatoRivista.Attiva);
				rivistaAbbonamentoDao.save(ra);
				programmaSpedizioniSospese(abbonamento,ra);
        	}
        }
    	return Smd.getStatoAbbonamento(almenounarivistaattiva, almenounarivistasospesa, abbonamento.getStatoIncasso(),StatoCampagna.InviatoSospeso);
	}

	private StatoAbbonamento aggiornaCampagnaInviatoSospeso(Abbonamento abbonamento, Campagna campagna) {
        List<Long> rivisteSospese = 
        		operazioneSospendiDao
        		.findByCampagna(campagna)
        		.stream()
        		.map(opsos -> opsos.getPubblicazione().getId())
        		.collect(Collectors.toList());
    	boolean almenounarivistasospesa=false;
    	boolean almenounarivistaattiva=false;
    	for (RivistaAbbonamento ra: rivistaAbbonamentoDao.
    			findByAbbonamento(abbonamento)) {
    		StatoRivista stato = Smd.getStatoRivista(abbonamento, ra);
        	boolean sospesa = rivisteSospese.contains(ra.getPubblicazione().getId());
        	if (stato != StatoRivista.Attiva && sospesa) {
        		almenounarivistasospesa=true;
				ra.setStatoRivista(StatoRivista.Sospesa);
				rivistaAbbonamentoDao.save(ra);
				sospendiSpedizioniProgrammate(abbonamento,ra);
        	} else {
        		almenounarivistaattiva=true;
				ra.setStatoRivista(StatoRivista.Attiva);
				rivistaAbbonamentoDao.save(ra);
				programmaSpedizioniSospese(abbonamento,ra);
        	}
        }
    	return Smd.getStatoAbbonamento(almenounarivistaattiva, almenounarivistasospesa, abbonamento.getStatoIncasso(),StatoCampagna.InviatoSospeso);		
	}
	
	@Override
	public List<RivistaAbbonamento> getNotValid(Abbonamento abbonamento,Campagna campagna) {
		return rivistaAbbonamentoDao
				.findByAbbonamento(abbonamento)
				.stream()
				.filter(ra ->  
					Smd.getStatoAbbonamento(abbonamento, ra, campagna,false) != StatoAbbonamento.Valido)
				.collect(Collectors.toList());
	}

	
}
