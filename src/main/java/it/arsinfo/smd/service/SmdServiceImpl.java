package it.arsinfo.smd.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.boot.actuate.audit.listener.AuditApplicationEvent;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.arsinfo.smd.dao.SmdService;
import it.arsinfo.smd.dao.repository.AbbonamentoDao;
import it.arsinfo.smd.dao.repository.DistintaVersamentoDao;
import it.arsinfo.smd.dao.repository.OffertaDao;
import it.arsinfo.smd.dao.repository.OfferteCumulateDao;
import it.arsinfo.smd.dao.repository.OperazioneDao;
import it.arsinfo.smd.dao.repository.OperazioneIncassoDao;
import it.arsinfo.smd.dao.repository.RivistaAbbonamentoDao;
import it.arsinfo.smd.dao.repository.SpedizioneDao;
import it.arsinfo.smd.dao.repository.SpedizioneItemDao;
import it.arsinfo.smd.dao.repository.SpesaSpedizioneDao;
import it.arsinfo.smd.dao.repository.UserInfoDao;
import it.arsinfo.smd.dao.repository.VersamentoDao;
import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.Incassato;
import it.arsinfo.smd.data.InvioSpedizione;
import it.arsinfo.smd.data.Mese;
import it.arsinfo.smd.data.RivistaAbbonamentoAggiorna;
import it.arsinfo.smd.data.SpedizioneWithItems;
import it.arsinfo.smd.data.StatoAbbonamento;
import it.arsinfo.smd.data.StatoOperazione;
import it.arsinfo.smd.data.StatoOperazioneIncasso;
import it.arsinfo.smd.data.StatoSpedizione;
import it.arsinfo.smd.data.TipoAbbonamentoRivista;
import it.arsinfo.smd.dto.AbbonamentoConRiviste;
import it.arsinfo.smd.dto.Indirizzo;
import it.arsinfo.smd.dto.SpedizioneDto;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.DistintaVersamento;
import it.arsinfo.smd.entity.Offerta;
import it.arsinfo.smd.entity.OfferteCumulate;
import it.arsinfo.smd.entity.Operazione;
import it.arsinfo.smd.entity.OperazioneIncasso;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.entity.RivistaAbbonamento;
import it.arsinfo.smd.entity.Spedizione;
import it.arsinfo.smd.entity.SpedizioneItem;
import it.arsinfo.smd.entity.UserInfo;
import it.arsinfo.smd.entity.Versamento;
import it.arsinfo.smd.ui.SmdUI;

@Service
public class SmdServiceImpl implements SmdService {

    @Autowired
    private SpesaSpedizioneDao spesaSpedizioneDao;
    
    @Autowired
    private AbbonamentoDao abbonamentoDao;
    
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
    private UserInfoDao userInfoDao;

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
    	if (abbonamento.getIncassato().signum() > 0) {
        	log.warn("rimuovi: {} , Non si può cancellare un abbonamento incassato.", abbonamento);
            throw new UnsupportedOperationException("Non si può cancellare un abbonamento con incasso: "+abbonamento.getIncassato());    		
    	}
    	List<RivistaAbbonamento> riviste = rivistaAbbonamentoDao.findByAbbonamento(abbonamento);
    	for (RivistaAbbonamento rivista:riviste) {
	        if (rivista.getStatoAbbonamento() != StatoAbbonamento.Nuovo) {
	        	log.warn("rimuovi: {} , Non si può cancellare un abbonamento con rivista {} in uno stato diverso da Nuovo.", abbonamento,rivista);
	            throw new UnsupportedOperationException("Non si può cancellare abbonamento con rivista nello stato:"+rivista.getStatoAbbonamento());
	        }
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
        riviste.forEach(ec -> rivistaAbbonamentoDao.deleteById(ec.getId()));
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
    @Transactional
    public void aggiorna(RivistaAbbonamento rivistaAbbonamento, int numero, TipoAbbonamentoRivista tipo) throws Exception {
        Abbonamento abbonamento = abbonamentoDao.findById(rivistaAbbonamento.getAbbonamento().getId()).get();
        if (abbonamento == null) throw new UnsupportedOperationException("Abbonamento not found");
        List<SpedizioneWithItems> spedizioni = findByAbbonamento(abbonamento);
        RivistaAbbonamentoAggiorna aggiorna = 
        		Smd.aggiorna(
        				abbonamento,
        				spedizioni,
                        spesaSpedizioneDao.findAll(),
                        rivistaAbbonamento,
                        numero,
                        tipo
                );          
        aggiorna.getSpedizioniToSave().stream().forEach(sped -> {
            spedizioneDao.save(sped.getSpedizione());
            sped.getSpedizioneItems().stream().forEach(item -> spedizioneItemDao.save(item));
        });
        
        aggiorna.getItemsToDelete().forEach(rimitem -> spedizioneItemDao.deleteById(rimitem.getId()));
        
        for (SpedizioneWithItems sped:aggiorna.getSpedizioniToSave()) {
            if (sped.getSpedizioneItems().isEmpty()) {
                spedizioneDao.deleteById(sped.getSpedizione().getId());
            }
        }
        aggiorna.getRivisteToSave().forEach(r -> rivistaAbbonamentoDao.save(r));
        
        if (aggiorna.getAbbonamentoToSave() != null)
        	abbonamentoDao.save(aggiorna.getAbbonamentoToSave());
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
        
        aggiorna.getSpedizioniToSave().stream().forEach(sped -> {
            spedizioneDao.save(sped.getSpedizione());
            sped.getSpedizioneItems().stream().forEach(item -> {
                spedizioneItemDao.save(item);
               });
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
    public void generaStatisticheTipografia(Anno anno, Pubblicazione p) {
        EnumSet.allOf(Mese.class).forEach(mese -> generaStatisticheTipografia(anno, mese,p));
    }

    @Override
    @Transactional
    public void inviaSpedizionere(Mese meseSpedizione, Anno annoSpedizione, Pubblicazione p) throws Exception{
    	log.info("inviaSpedizionere: {} {} {} - start", meseSpedizione,annoSpedizione,p.getNome());
        Operazione operazione = operazioneDao
        .findByAnnoAndMeseAndPubblicazione(annoSpedizione, meseSpedizione,p);
        if ( operazione == null || operazione.getStatoOperazione() != StatoOperazione.Inviata) {
        	log.error("inviaSpedizionere: {} {} {}: Operazione non valida",meseSpedizione,annoSpedizione,p.getNome());
        	throw new UnsupportedOperationException("Operazione non valida");
        }
        
        spedizioneItemDao.findByPubblicazioneAndStatoSpedizione(p, StatoSpedizione.PROGRAMMATA)
        .stream()
        .filter(item -> item.getSpedizione().getMeseSpedizione() == meseSpedizione
        			 && item.getSpedizione().getAnnoSpedizione() == annoSpedizione
        			 && item.getSpedizione().getInvioSpedizione() == InvioSpedizione.Spedizioniere
        )
        .forEach(item -> 
        	{
    			item.setStatoSpedizione(StatoSpedizione.INVIATA);
    			spedizioneItemDao.save(item);
    		}
    	);
        operazione.setStatoOperazione(StatoOperazione.Spedita);
        operazioneDao.save(operazione);
    	log.info("inviaSpedizionere: {} {} {} - end",meseSpedizione,annoSpedizione,p.getNome());

    }
    
    @Override
    @Transactional
    public void spedisciAdpSede(Mese meseSpedizione, Anno annoSpedizione, Pubblicazione p, InvioSpedizione invio) throws Exception{
        Operazione operazione = operazioneDao
        .findByAnnoAndMeseAndPubblicazione(annoSpedizione, meseSpedizione,p);
        if ( operazione == null || operazione.getStatoOperazione() == StatoOperazione.Programmata) {
        	throw new UnsupportedOperationException("Operazione non valida");
        }

        spedizioneItemDao.findByPubblicazioneAndStatoSpedizione(p, StatoSpedizione.PROGRAMMATA)
        .stream()
        .filter(item -> item.getSpedizione().getMeseSpedizione() == meseSpedizione
        			 && item.getSpedizione().getAnnoSpedizione() == annoSpedizione
        			 && item.getSpedizione().getInvioSpedizione() == invio
        )
        .forEach(item -> 
        	{
    			item.setStatoSpedizione(StatoSpedizione.INVIATA);
    			spedizioneItemDao.save(item);
    		}
    	);
    }


    @Override
    public List<SpedizioneDto> listBy(Pubblicazione pubblicazione, Mese meseSpedizione, Anno annoSpedizione, StatoSpedizione statoSpedizione, InvioSpedizione invio) {
        final List<SpedizioneDto> items = new ArrayList<>();
    	spedizioneItemDao
        	.findByPubblicazioneAndStatoSpedizione(pubblicazione,statoSpedizione)
        	.stream()
        	.filter(spedItem -> spedItem.getSpedizione().getMeseSpedizione() == meseSpedizione 
        						&& spedItem.getSpedizione().getAnnoSpedizione()== annoSpedizione
        						&& spedItem.getSpedizione().getInvioSpedizione() == invio
        						)
        	.forEach(spedItem -> {
        		items.add(genera(spedItem));
			});
    	return items;
    }

    @Override
    public SpedizioneDto genera(SpedizioneItem spedItem) {
		return new SpedizioneDto(spedItem, spedItem.getSpedizione().getDestinatario(), spedItem.getSpedizione().getDestinatario().getCo());	
    }

    @Override
    public Indirizzo genera(Spedizione spedizione) {
		return new Indirizzo(spedizione.getDestinatario(), spedizione.getDestinatario().getCo());	
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
        boolean valido = false;
    	switch (abbonamento.getStatoIncasso()) {
			case Si:
				valido = true;
				break;
			case SiConDebito:
				valido = true;
			default:
				break;
        }
        abbonamentoDao.save(abbonamento);        
    	if (valido) {
			rivistaAbbonamentoDao.findByAbbonamento(abbonamento)
			.stream()
			.filter(riv -> !Smd.isOmaggio(riv))
			.forEach(riv -> {
				if (riv.getStatoAbbonamento() == StatoAbbonamento.SospesoInviatoEC) {
					riv.setStatoAbbonamento(StatoAbbonamento.ValidoInviatoEC);
				} else if (abbonamento.getStatoIncasso() == Incassato.Si) {
					riv.setStatoAbbonamento(StatoAbbonamento.Valido);
				} else if (abbonamento.getStatoIncasso() == Incassato.SiConDebito) {
					riv.setStatoAbbonamento(StatoAbbonamento.ValidoConResiduo);
				}
				rivistaAbbonamentoDao.save(riv);
			});
			aggiornaSpedizioni(abbonamento);
    	}
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
    public void storna(Offerta offerta, UserInfo user) throws Exception {
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
    public void storna(OperazioneIncasso operazioneIncasso, UserInfo user, String description) throws Exception {
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
    public void aggiornaSpedizioni(Abbonamento abbonamento) throws Exception {
        spedizioneDao.findByAbbonamento(abbonamento)
        .forEach(sped -> {
        	spedizioneItemDao.findBySpedizioneAndStatoSpedizione(sped, StatoSpedizione.PROGRAMMATA)
        	.forEach( item -> 
        	{
        		RivistaAbbonamento rivista = rivistaAbbonamentoDao.findById(item.getRivistaAbbonamento().getId()).get();
        		switch (rivista.getStatoAbbonamento()) {
				case Sospeso:
		        	item.setStatoSpedizione(StatoSpedizione.SOSPESA);
					break;
				case SospesoInviatoEC:
		        	item.setStatoSpedizione(StatoSpedizione.SOSPESA);
					break;
				default:
					break;
				}
            	spedizioneItemDao.save(item);
        	});
        	
        	spedizioneItemDao.findBySpedizioneAndStatoSpedizione(sped, StatoSpedizione.SOSPESA)
        	.forEach( item -> 
        	{
        		RivistaAbbonamento rivista = rivistaAbbonamentoDao.findById(item.getRivistaAbbonamento().getId()).get();
        		switch (rivista.getStatoAbbonamento()) {
				case Valido:
		        	item.setStatoSpedizione(StatoSpedizione.PROGRAMMATA);
					break;
				case ValidoConResiduo:
		        	item.setStatoSpedizione(StatoSpedizione.PROGRAMMATA);
					break;
				case ValidoInviatoEC:
		        	item.setStatoSpedizione(StatoSpedizione.PROGRAMMATA);
					break;
				default:
					break;
				}
            	spedizioneItemDao.save(item);
        	});
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

    public List<SpedizioneWithItems> findByMeseSpedizioneAndAnnoSpedizione(Mese mese, Anno anno, Pubblicazione p) {
        Map<Long,SpedizioneWithItems> spedizioni = new HashMap<>();
        for (SpedizioneItem item: spedizioneItemDao.findByPubblicazione(p)) {
        	Spedizione sped = item.getSpedizione();
        	if (sped.getMeseSpedizione() == mese && sped.getAnnoSpedizione() == anno) {
        		if (!spedizioni.containsKey(sped.getId())) 
        			spedizioni.put(sped.getId(),new SpedizioneWithItems(sped));
        		spedizioni.get(sped.getId()).addSpedizioneItem(item);
        	}
        }
        return spedizioni.values().stream().collect(Collectors.toList());
    }

	@Override
	public void inviaDuplicato(Spedizione spedizione, InvioSpedizione invio) throws Exception {
		Abbonamento abbonamento = abbonamentoDao.findById(spedizione.getAbbonamento().getId()).get();
		List<SpedizioneItem> items = spedizioneItemDao.findBySpedizione(spedizione);
		for (SpedizioneItem item: items) {
			
			RivistaAbbonamento ra0= rivistaAbbonamentoDao.findById(item.getRivistaAbbonamento().getId()).get();
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
			ra.setStatoAbbonamento(StatoAbbonamento.Proposto);
			abbonamento.addItem(ra);
		}

		genera(abbonamento);
		
	}

}
