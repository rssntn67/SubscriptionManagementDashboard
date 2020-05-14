package it.arsinfo.smd.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.boot.actuate.audit.listener.AuditApplicationEvent;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Service;

import it.arsinfo.smd.dao.repository.AbbonamentoDao;
import it.arsinfo.smd.dao.repository.AnagraficaDao;
import it.arsinfo.smd.dao.repository.IncassoDao;
import it.arsinfo.smd.dao.repository.NotaDao;
import it.arsinfo.smd.dao.repository.OperazioneDao;
import it.arsinfo.smd.dao.repository.OperazioneIncassoDao;
import it.arsinfo.smd.dao.repository.PubblicazioneDao;
import it.arsinfo.smd.dao.repository.RivistaAbbonamentoDao;
import it.arsinfo.smd.dao.repository.SpedizioneDao;
import it.arsinfo.smd.dao.repository.SpedizioneItemDao;
import it.arsinfo.smd.dao.repository.SpesaSpedizioneDao;
import it.arsinfo.smd.dao.repository.StoricoDao;
import it.arsinfo.smd.dao.repository.UserInfoDao;
import it.arsinfo.smd.dao.repository.VersamentoDao;
import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.Invio;
import it.arsinfo.smd.data.InvioSpedizione;
import it.arsinfo.smd.data.Mese;
import it.arsinfo.smd.data.SpedizioneWithItems;
import it.arsinfo.smd.data.StatoAbbonamento;
import it.arsinfo.smd.data.StatoOperazione;
import it.arsinfo.smd.data.StatoOperazioneIncasso;
import it.arsinfo.smd.data.StatoSpedizione;
import it.arsinfo.smd.data.StatoStorico;
import it.arsinfo.smd.dto.AbbonamentoConRiviste;
import it.arsinfo.smd.dto.Indirizzo;
import it.arsinfo.smd.dto.SpedizioniereItem;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Incasso;
import it.arsinfo.smd.entity.Operazione;
import it.arsinfo.smd.entity.OperazioneIncasso;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.entity.RivistaAbbonamento;
import it.arsinfo.smd.entity.Spedizione;
import it.arsinfo.smd.entity.SpedizioneItem;
import it.arsinfo.smd.entity.Storico;
import it.arsinfo.smd.entity.UserInfo;
import it.arsinfo.smd.entity.Versamento;
import it.arsinfo.smd.ui.SmdUI;

@Service
public class SmdServiceImpl implements SmdService {

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
    RivistaAbbonamentoDao rivistaAbbonamentoDao;
    
    @Autowired
    SpedizioneDao spedizioneDao;

    @Autowired
    SpedizioneItemDao spedizioneItemDao;

    @Autowired
    OperazioneDao operazioneDao;

    @Autowired
    OperazioneIncassoDao operazioneIncassoDao;

    @Autowired
    PubblicazioneDao pubblicazioneDao;

    @Autowired
    VersamentoDao versamentoDao;
    
    @Autowired
    IncassoDao incassoDao;

    @Autowired
    UserInfoDao userInfoDao;

    private static final Logger log = LoggerFactory.getLogger(SmdService.class);

    @Override
    public void logout(String userName) {
        log.info("logout: {}",userInfoDao.findByUsername(userName));
    }

    @Override
    public UserInfo login(String userName) throws UsernameNotFoundException {
        UserInfo user = userInfoDao.findByUsername(userName);
        if (null == user) {
        	log.info("login: '{}' not found, access is denied.", userName);
            throw new UsernameNotFoundException("No user found with username: "
                + userName);
        }
        log.info("login: {}",user);
        return user;
    }

    @Override
    public void auditlog(AuditApplicationEvent auditApplicationEvent) {
        
    	AuditEvent auditEvent = auditApplicationEvent.getAuditEvent();
        
        WebAuthenticationDetails details
          = (WebAuthenticationDetails) auditEvent.getData().get("details");
        String requestUrl = (String)auditEvent.getData().get("requestUrl"); 
        if (requestUrl == null && auditEvent.getType().equals("AUTHENTICATION_SUCCESS")) {
        	requestUrl = SmdUI.URL_LOGIN;
        } else if (requestUrl == null && auditEvent.getType().equals("AUTHENTICATION_FAILURE")) {
        	requestUrl = SmdUI.URL_LOGIN_FAILURE;        	
        } else if (requestUrl == null) {
        	requestUrl="NA";
        }
        String message = (String)auditEvent.getData().get("message");        
        String remoteAddress=null;
        String sessionId = null;
        if (details != null) {
            remoteAddress = details.getRemoteAddress();
            if (remoteAddress == null ) {
            	remoteAddress = "NA";
            }
            sessionId = details.getSessionId();
            if (sessionId == null) {
            	sessionId="NA";
            }
        }
        log.info("auditlog: {} '{} from {}'   URL {}, SessionId {}: {}" ,
	                 auditEvent.getType(),
	                 auditEvent.getPrincipal() ,
	                 remoteAddress,
	                 requestUrl,
	                 sessionId,
	                 message
	                );   	
    }

    @Override
    public List<AbbonamentoConRiviste> get(List<Abbonamento> abbonamenti) {
    	List<AbbonamentoConRiviste> list = new ArrayList<>();
    	for (Abbonamento abbonamento: abbonamenti) {
    		list.add(new 
				AbbonamentoConRiviste(abbonamento, 
						rivistaAbbonamentoDao.findByAbbonamento(abbonamento),
    					abbonamento.getIntestatario(),abbonamento.getIntestatario().getCo()));
    	}
    	return list;
    }


    @Override
    public void rimuovi(Abbonamento abbonamento) {
        if (abbonamento.getStatoAbbonamento() != StatoAbbonamento.Nuovo) {
        	log.warn("rimuovi: {} , Non si può cancellare un abbonamento in uno stato diverso da Nuovo.", abbonamento);
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
        spedizioni.stream().forEach(sped -> {
            spedizioneDao.save(sped.getSpedizione());
            sped.getSpedizioneItems().stream().forEach(item -> spedizioneItemDao.save(item));
        });
    }

    @Override
    public void aggiorna(RivistaAbbonamento rivistaAbbonamento) throws Exception {
        // quantita -> spedizioneItem Importo Abbonamento
        // Tipo -> ordinario -> Importo Abbonamento
        Abbonamento abbonamento = abbonamentoDao.findById(rivistaAbbonamento.getAbbonamento().getId()).get();
        if (abbonamento == null) return;
        List<SpedizioneWithItems> spedizioni = findByAbbonamento(abbonamento);
        List<SpedizioneItem> deleted = Smd.aggiornaEC(abbonamento,
                                                     rivistaAbbonamento, 
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
        rivistaAbbonamentoDao.save(rivistaAbbonamento);
        
        abbonamentoDao
        .findByIntestatarioAndAnnoAndCassa(
                   abbonamento.getIntestatario(), 
                   Anno.getAnnoPrecedente(abbonamento.getAnno()), 
                   abbonamento.getCassa()
               )
        .forEach(abb -> 
        {
        	abbonamento.setPregresso(
			abbonamento.getPregresso().add(abb.getResiduo()));
        	abb.getPregresso().subtract(abb.getResiduo());
        	abbonamentoDao.save(abb);
        });
        abbonamentoDao.save(abbonamento);
    }

    @Override
    public void rimuovi(Abbonamento abbonamento, RivistaAbbonamento rivistaAbbonamento) throws Exception {
        if (rivistaAbbonamento == null || abbonamento == null)
            return;
        List<SpedizioneWithItems> spedizioni = findByAbbonamento(abbonamento);

        List<SpedizioneItem> deleted = Smd.rimuoviEC(abbonamento,
                                                     rivistaAbbonamento, 
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
        
        if (rivistaAbbonamento.getNumeroTotaleRiviste() == 0 && spedizioneItemDao.findByRivistaAbbonamento(rivistaAbbonamento).isEmpty()) { 
            rivistaAbbonamentoDao.deleteById(rivistaAbbonamento.getId());
        }
        if (spedizioneDao.findByAbbonamento(abbonamento).isEmpty() && rivistaAbbonamentoDao.findByAbbonamento(abbonamento).isEmpty()) {
        	abbonamentoDao.delete(abbonamento);
        } else {
        	abbonamentoDao.save(abbonamento);
        }
    }

    @Override
    public void generaStatisticheTipografia(Anno anno, Mese mese) {
    	log.info("generaStatisticheTipografia {}, {}", mese,anno);
    	List<SpedizioneWithItems> speditems = findByMeseSpedizioneAndAnnoSpedizione(mese, anno);
        pubblicazioneDao.findAll().forEach(p -> {
            Operazione saved = operazioneDao.findByAnnoAndMeseAndPubblicazione(anno, mese,p);
            if (saved != null && saved.getStatoOperazione() != StatoOperazione.Programmata) {
                return;
            }
            if (saved != null) {
                operazioneDao.deleteById(saved.getId());
            }
            Operazione op = Smd.generaOperazione(p,
            								speditems, 
                                             mese, 
                                             anno);
    	log.info("generaStatisticheTipografia {}", op);
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
        operazioneDao
        .findByAnnoAndMese(annoSpedizione, meseSpedizione)
        .stream()
        .filter(operazione -> operazione.getStatoOperazione() == StatoOperazione.Inviata)
        .forEach( operazione -> {
            operazione.setStatoOperazione(StatoOperazione.Spedita);
            operazioneDao.save(operazione);
        });

        spedizioneDao
        .findByMeseSpedizioneAndAnnoSpedizione(meseSpedizione,annoSpedizione)
        .stream()
        .filter(sped -> 
        	sped.getInvioSpedizione() == InvioSpedizione.Spedizioniere && 
        	sped.getStatoSpedizione() == StatoSpedizione.PROGRAMMATA)
        .forEach(sped -> {
            sped.setStatoSpedizione(StatoSpedizione.INVIATA);
            spedizioneDao.save(sped);
        });
    }

    @Override
    public List<SpedizioniereItem> listItems(Pubblicazione pubblicazione, Mese meseSpedizione, Anno annoSpedizione, InvioSpedizione inviosped, StatoSpedizione statoSpedizione) {
        final List<SpedizioniereItem> items = new ArrayList<>();
    	spedizioneItemDao
        	.findByPubblicazione(pubblicazione)
        	.stream()
        	.filter(spedItem -> spedItem.getSpedizione().getMeseSpedizione() == meseSpedizione 
        						&& spedItem.getSpedizione().getAnnoSpedizione()== annoSpedizione
        						&& spedItem.getSpedizione().getInvioSpedizione() == inviosped
        						&& spedItem.getSpedizione().getStatoSpedizione() == statoSpedizione
        						)
        	.forEach(spedItem -> {
        		items.add(genera(spedItem));
			});
    	return items;
    }

    @Override
    public SpedizioniereItem genera(SpedizioneItem spedItem) {
		if (spedItem.getSpedizione().getInvio() == Invio.Destinatario) {
			return new SpedizioniereItem(spedItem, spedItem.getSpedizione().getDestinatario(), spedItem.getSpedizione().getDestinatario().getCo());
		} 
		Anagrafica intestatario = abbonamentoDao.findById(spedItem.getSpedizione().getAbbonamento().getId()).get().getIntestatario();
		return new SpedizioniereItem(spedItem, spedItem.getSpedizione().getDestinatario(), intestatario, intestatario.getCo());
	
    }

    @Override
    public Indirizzo genera(Spedizione spedizione) {
		if (spedizione.getInvio() == Invio.Destinatario) {
			return new Indirizzo(spedizione.getDestinatario(), spedizione.getDestinatario().getCo());
		} 
		Anagrafica intestatario = abbonamentoDao.findById(spedizione.getAbbonamento().getId()).get().getIntestatario();
		return new Indirizzo(spedizione.getDestinatario(), intestatario, intestatario.getCo());
	
    }

    @Override
    public void incassa(Abbonamento abbonamento, Versamento versamento, UserInfo user, String description) throws Exception {
        log.info("incassa: {}", user);
        log.info("incassa: {}", abbonamento);
        log.info("incassa: {}", versamento);
        if (versamento.getResiduo().signum() == 0) {
            log.warn("incassa: Versamento con residuo 0, non incassabile {} {} {}", abbonamento,versamento,user);
            throw new UnsupportedOperationException("incassa: Versamento con residuo 0, abbonamento non incassato");            
        }
        
        Incasso incasso = versamento.getIncasso();
        BigDecimal incassato = Smd.incassa(incasso,versamento, abbonamento);        
        versamentoDao.save(versamento);
        incassoDao.save(incasso);
        if (abbonamento.getStatoAbbonamento() == StatoAbbonamento.SospesoInviatoEC) {
        	switch (abbonamento.getStatoIncasso()) {
			case Si:
				riattivaSpedizioni(abbonamento);
				abbonamento.setStatoAbbonamento(StatoAbbonamento.ValidoInviatoEC);
				break;
			case SiConDebito:
				riattivaSpedizioni(abbonamento);
				abbonamento.setStatoAbbonamento(StatoAbbonamento.ValidoInviatoEC);
			default:
				break;
			}
        } else if (abbonamento.getStatoAbbonamento() == StatoAbbonamento.ValidoConResiduo) {
        	switch (abbonamento.getStatoIncasso()) {
			case Si:
				abbonamento.setStatoAbbonamento(StatoAbbonamento.Valido);
				break;
			case SiConDebito:
				riattivaSpedizioni(abbonamento);
				abbonamento.setStatoAbbonamento(StatoAbbonamento.Valido);
			default:
				break;
			}
        }
        abbonamentoDao.save(abbonamento);        
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
    public void storna(OperazioneIncasso operazioneIncasso, UserInfo user, String description) throws Exception {
    	if (operazioneIncasso.getStatoOperazioneIncasso() == StatoOperazioneIncasso.Storno) {
            log.warn("dissocia: tipo Storno, non dissociabile {}", operazioneIncasso);
            throw new UnsupportedOperationException("dissocia: Operazione tipo Storno, non dissociabile, non dissociabile");                		
    	}
    	Versamento versamento = operazioneIncasso.getVersamento();
    	Abbonamento abbonamento = operazioneIncasso.getAbbonamento();
        if (versamento.getIncassato().signum() == 0) {
            log.warn("dissocia: Versamento con Incasso 0, non dissociabile {}", operazioneIncasso);
            throw new UnsupportedOperationException("dissocia: Versamento con Incasso 0, non dissociabile");            
        }
        Incasso incasso = versamento.getIncasso();
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
        log.info("dissocia: {}",operStorno );

    }

    @Override
    public List<OperazioneIncasso> getAssociati(Versamento versamento) {
    	return operazioneIncassoDao.findByVersamento(versamento);
    }

    @Override
    public List<Abbonamento> getAssociabili(Versamento versamento) {
        if (versamento == null || versamento.getResiduo().signum() == 0) {
        	return new ArrayList<>();
        }

        return abbonamentoDao
        .findAll()
        .stream()
        .filter(abb -> 
            abb.getResiduo().signum() > 0 
            ).collect(Collectors.toList());       
    }

    @Override
    public void sospendiSpedizioni(Abbonamento abbonamento) throws Exception {
        spedizioneDao.findByAbbonamentoAndStatoSpedizione(abbonamento, StatoSpedizione.PROGRAMMATA)
        .forEach(sped -> {
            sped.setStatoSpedizione(StatoSpedizione.SOSPESA);
            spedizioneDao.save(sped);
        });
    }

    @Override
    public void riattivaSpedizioni(Abbonamento abbonamento) throws Exception {
        spedizioneDao.findByAbbonamentoAndStatoSpedizione(abbonamento,StatoSpedizione.SOSPESA)
        .forEach(sped -> {
            sped.setStatoSpedizione(StatoSpedizione.PROGRAMMATA);
            spedizioneDao.save(sped);
        });
    }

    @Override
    public void sospendiStorico(Abbonamento abbonamento) throws Exception {
        rivistaAbbonamentoDao.findByAbbonamento(abbonamento)
        .stream()
        .filter(ec -> ec.getStorico() != null)
        .forEach(ec -> {
            Storico storico = storicoDao.findById(ec.getStorico().getId()).get();
            storico.setStatoStorico(StatoStorico.Sospeso);
            storicoDao.save(storico);
        });
    }

    @Override
    public void riattivaStorico(Abbonamento abbonamento) throws Exception {
        rivistaAbbonamentoDao.findByAbbonamento(abbonamento)
        .stream()
        .filter(ec -> ec.getStorico() != null)
        .forEach(ec -> {
            Storico storico = storicoDao.findById(ec.getStorico().getId()).get();
            storico.setStatoStorico(StatoStorico.Valido);
            storicoDao.save(storico);
        });
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
        boolean alreadyPersisted = incasso.getId() != null;
    	List<Incasso> found = incassoDao
        .findByDataContabile(incasso.getDataContabile())
        .stream()
        .filter(inc -> 
           inc.getCassa() == incasso.getCassa() 
        && inc.getCuas() == incasso.getCuas() 
        && inc.getCcp() == incasso.getCcp())
        .collect(Collectors.toList());
        if (!found.isEmpty()) {
        	log.warn("save: Incasso esistente, {} ", incasso);
            throw new UnsupportedOperationException("save: Non posso Salvare: Incasso esistente ");
        }
        incassoDao.save(incasso);
        log.info("save: {}", incasso);
        if (alreadyPersisted) {
        	return;
        }
        incasso.getVersamenti().forEach(vers -> {
        	versamentoDao.save(vers);
            log.info("save: {}", vers);
        	
        });
    }

    @Override
    public void save(Versamento versamento) throws Exception {
        log.info("save: {}", versamento);
        if (versamento.getIncasso() == null || versamento.getIncasso().getId() == null ) {
        	log.warn("save: Versamento: incasso non esistente, {} ", versamento);
            throw new UnsupportedOperationException("save: Versamento: incasso non esistente" );
        	
    	}
    	if (versamento.getIncassato().signum() != 0) {
        	log.warn("save: Versamento: non posso aggiornare un versamento incassato, {} ", versamento);
            throw new UnsupportedOperationException("save: Versamento: non posso aggiornare un versamento incassato");
        }
        versamentoDao.save(versamento);
        Incasso incasso = incassoDao.findById(versamento.getIncasso().getId()).get();
        Smd.calcoloImportoIncasso(incasso, versamentoDao.findByIncasso(incasso));
        incassoDao.save(incasso);        
    }

    @Override
    public void delete(Versamento versamento) throws Exception{
        log.info("delete: {}", versamento);
        if (versamento.getIncasso() == null || versamento.getIncasso().getId() == null ) {
        	log.warn("delete: Versamento: incasso non esistente, {} ", versamento);
            throw new UnsupportedOperationException("delete: Versamento: incasso non esistente" );
        	
    	}
    	if (versamento.getIncassato().signum() != 0) {
        	log.warn("delete: Versamento: non posso cancellare un versamento incassato, {} ", versamento);
            throw new UnsupportedOperationException("delete: Versamento: non posso calcellare un versamento incassato");
        }
        versamentoDao.delete(versamento);
    	Incasso incasso = incassoDao.findById(versamento.getIncasso().getId()).get();
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
        versamento.setProgressivo(abbonamento.getProgressivo());
        versamento.setDataPagamento(abbonamento.getDataPagamento());
        versamentoDao.save(versamento);
        Smd.calcoloImportoIncasso(incasso,
                                  versamentoDao.findByIncasso(incasso));
        incassoDao.save(incasso);
        incassa(abbonamento, versamento,user,"Incassato da Abbonamento");
    }
    
    @Override
    public void incassaCodeLine(List<Incasso> incassi,UserInfo user) throws Exception {
    	for (Incasso incasso:incassi) {
    		if (incasso.getResiduo().signum() == 0) {
    			continue;
    		}
    		for (Versamento v: versamentoDao.findByIncasso(incasso)) {
    			if (v.getResiduo().signum() == 0) {
    				continue;
    			}
    			final Abbonamento abbonamento = abbonamentoDao.findByCodeLine(v.getCodeLine());
    			if (abbonamento != null && abbonamento.getResiduo().signum() > 0 ) {
    				incassa(abbonamento,v,user,"Incassato con CodeLine");
    			}
    		}
		}

    }

	@Override
	public void associaCommittente(Anagrafica committente, Versamento versamento) {
		versamento.setCommittente(committente);
		versamentoDao.save(versamento);
	}

	@Override
	public void rimuoviCommittente(Versamento versamento) {
		if (versamento.getCommittente() == null) {
			return;
		}
		versamento.setCommittente(null);
		versamentoDao.save(versamento);		
	}

}
