package it.arsinfo.smd.service.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.arsinfo.smd.dao.CampagnaServiceDao;
import it.arsinfo.smd.dao.SmdService;
import it.arsinfo.smd.dao.repository.AbbonamentoDao;
import it.arsinfo.smd.dao.repository.AnagraficaDao;
import it.arsinfo.smd.dao.repository.CampagnaDao;
import it.arsinfo.smd.dao.repository.CampagnaItemDao;
import it.arsinfo.smd.dao.repository.OperazioneCampagnaDao;
import it.arsinfo.smd.dao.repository.OperazioneSospendiDao;
import it.arsinfo.smd.dao.repository.PubblicazioneDao;
import it.arsinfo.smd.dao.repository.RivistaAbbonamentoDao;
import it.arsinfo.smd.dao.repository.StoricoDao;
import it.arsinfo.smd.dao.repository.UserInfoDao;
import it.arsinfo.smd.dao.repository.VersamentoDao;
import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.StatoAbbonamento;
import it.arsinfo.smd.data.StatoCampagna;
import it.arsinfo.smd.data.StatoStorico;
import it.arsinfo.smd.data.TipoAbbonamentoRivista;
import it.arsinfo.smd.data.TipoPubblicazione;
import it.arsinfo.smd.dto.AbbonamentoConRiviste;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Campagna;
import it.arsinfo.smd.entity.CampagnaItem;
import it.arsinfo.smd.entity.OperazioneCampagna;
import it.arsinfo.smd.entity.OperazioneSospendi;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.entity.RivistaAbbonamento;
import it.arsinfo.smd.entity.Storico;
import it.arsinfo.smd.entity.UserInfo;
import it.arsinfo.smd.entity.Versamento;
import it.arsinfo.smd.service.Smd;

@Service
public class CampagnaServiceDaoImpl implements CampagnaServiceDao {

	@Autowired
	private CampagnaDao repository;

	@Autowired
	private UserInfoDao userDao;

	@Autowired
	private CampagnaItemDao campagnaItemDao;

	@Autowired
	private AbbonamentoDao abbonamentoDao;

	@Autowired
	private RivistaAbbonamentoDao rivistaAbbonamentoDao;

	@Autowired
	private AnagraficaDao anagraficaDao;

	@Autowired
	private VersamentoDao versamentoDao;

	@Autowired
	private StoricoDao storicoDao;

	@Autowired
	private PubblicazioneDao pubblicazioneDao;

	@Autowired
	private OperazioneSospendiDao operazioneSospendiDao;

	@Autowired
	private OperazioneCampagnaDao operazioneCampagnaDao;

	@Autowired
	private SmdService smdService;

	private static final Logger log = LoggerFactory.getLogger(CampagnaServiceDaoImpl.class);

	public void lock(Campagna entity) {
		entity.setRunning(true);
		repository.save(entity);
		log.info("lock: Campagna locked {}", entity);
	}

	public void unlock(Campagna entity) {
		entity.setRunning(false);
		repository.save(entity);
		log.info("unlock: Campagna unlocked {}", entity);		
	}

	@Override
	public void genera(Campagna entity, UserInfo user) throws Exception {
		if (entity.getId() != null) {
			throw new UnsupportedOperationException("Impossibile Rigenerare Campagna");
		}
		if (entity.getAnno() == null) {
			throw new UnsupportedOperationException("Anno Campagna non definito");
		}
		if (entity.getAnno().getAnno() <= Anno.getAnnoCorrente().getAnno()) {
			throw new UnsupportedOperationException("Anno deve essere almeno anno successivo");
		}
		Campagna exists = repository.findByAnno(entity.getAnno());
		if (exists != null) {
			log.warn("genera: Impossibile rigenerare campagna per {}: una campagna esiste", entity.getAnno().getAnnoAsString());
			throw new UnsupportedOperationException(
					"Impossibile generare campagna per anno " + entity.getAnno().getAnno() + ". La campagna esiste");
		}
		lock(entity);
		try {
			doGenera(entity);
			OperazioneCampagna op = operazioneCampagnaDao.findUniqueByCampagnaAndStato(entity, StatoCampagna.Generata);
			if (op == null) {
				op = new OperazioneCampagna();
				op.setCampagna(entity);
				op.setStato(StatoCampagna.Generata);
			} else {
				op.setData(new Date());
			}
			op.setOperatore(user.getUsername());
			operazioneCampagnaDao.save(op);
		} catch (Exception e) {
			log.error("genera: {}",e.getMessage(),e);
			unlock(entity);
			throw e;
		} 
		unlock(entity);
	}
	
	@Transactional
	private void doGenera(Campagna entity) throws Exception {
		log.info("genera: Campagna start {}", entity);
		for (CampagnaItem item : entity.getCampagnaItems()) {
			campagnaItemDao.save(item);
		}
        List<Abbonamento> 
            abbonamenti = 
              Smd.genera(entity, 
                  anagraficaDao.findAll(),
                  storicoDao.findByStatoStoricoNotAndNumeroGreaterThan(StatoStorico.Sospeso, 0));
                                                           
        for (Abbonamento abb:abbonamenti) {
            storicoDao.findByIntestatarioAndContrassegno(
                  abb.getIntestatario(),abb.isContrassegno())
                .forEach(storico -> {
                   Smd.genera(abb, storico);
                   storico.setStatoStorico(StatoStorico.Valido);
                   storicoDao.save(storico);
            });
            if (abb.getItems().size() >= 1) {
                smdService.genera(abb);
            }
        }
        entity.setStatoCampagna(StatoCampagna.Generata);
		repository.save(entity);
		log.info("genera: Campagna end {}", entity);
	}

	@Override
	public void delete(Campagna entity) throws Exception {
		if (entity.getStatoCampagna() != StatoCampagna.Generata) {
			log.warn("delete: Impossibile delete campagna {}, lo stato campagna non 'Generata'", entity);
			throw new UnsupportedOperationException("Impossibile eseguire delete campagna, "
					+ entity.getAnno().getAnno() + ". La campagna non è nello stato 'Generata'");
		}
		lock(entity);
		try {
			doDelete(entity);
			repository.deleteById(entity.getId());
		} catch (Exception e) {
			log.error("delete: {}",e.getMessage(),e);
			unlock(entity);
			throw e;
		} 
	}
	
	@Transactional
	private void doDelete(Campagna entity) throws Exception {
		log.info("delete: Campagna start {}", entity);
		for (Abbonamento abbonamento : abbonamentoDao.findByCampagna(entity)) {
			smdService.rimuovi(abbonamento);
		}
		entity.getCampagnaItems().stream().forEach(item -> campagnaItemDao.delete(item));
		log.info("delete: Campagna end {}", entity);
	}

	@Override
	public Campagna findById(Long id) {
		return repository.findById(id).get();
	}

	@Override
	public List<Campagna> findAll() {
		return repository.findAll();
	}

	public CampagnaDao getRepository() {
		return repository;
	}

	@Override
	public List<Pubblicazione> findPubblicazioni() {
		return pubblicazioneDao.findAll();
	}

	@Override
	public List<Pubblicazione> findPubblicazioniValide() {
		return pubblicazioneDao.findByTipoNotAndActive(TipoPubblicazione.UNICO, true);
	}

	@Override
	public List<AbbonamentoConRiviste> findAbbonamentoConRivisteGenerati(Campagna entity) {
		return smdService.get(abbonamentoDao.findByCampagna(entity));
	}

	@Override
	public List<AbbonamentoConRiviste> findAbbonamentoConRivisteInviati(Campagna entity) {
		return smdService.get(findInviatiByCampagna(entity));
	}

	@Override
	public List<AbbonamentoConRiviste> findAbbonamentoConRivisteEstrattoConto(Campagna entity) {
		return smdService
				.get(findEstrattoContoByCampagna(entity));
	}

	@Override
	public List<AbbonamentoConRiviste> findAbbonamentoConDebito(Campagna entity) {
		return smdService
				.get(findConDebitoByCampagna(entity));
	}

	public List<AbbonamentoConRiviste> findAbbonamentoConRivisteAnnullati(Campagna entity) {
		return smdService.get(findAnnullatiByCampagna(entity));
	}
	
	@Override
	public List<Abbonamento> findInviatiByCampagna(Campagna entity) {
		return abbonamentoDao.findByCampagna(entity).stream().filter(a -> a.getImporto().signum() > 0 || a.getSpese().signum() > 0 || a.getPregresso().signum() > 0 || a.getSpeseEstero().signum() > 0)
			.collect(Collectors.toList());
	}
	
	@Override
	public List<Abbonamento> findEstrattoContoByCampagna(Campagna entity) {
		List<Long>abboIds = rivistaAbbonamentoDao
				.findByStatoAbbonamentoOrStatoAbbonamento(
				StatoAbbonamento.ValidoInviatoEC,StatoAbbonamento.SospesoInviatoEC)
				.stream()
				.map(r -> r.getAbbonamento().getId()).
				collect(Collectors.toList());
		return abbonamentoDao.findByCampagna(entity)
				.stream()
				.filter(a -> abboIds.contains(a.getId()))
				.collect(Collectors.toList());

	}
	
	@Override
	public List<Abbonamento> findAnnullatiByCampagna(Campagna entity) {
		List<Long>abboIds = rivistaAbbonamentoDao
				.findByStatoAbbonamento(
				StatoAbbonamento.Annullato)
				.stream()
				.map(r -> r.getAbbonamento().getId()).
				collect(Collectors.toList());
				return abbonamentoDao.findByCampagna(entity)
						.stream()
						.filter(a -> abboIds.contains(a.getId()))
						.collect(Collectors.toList());
	}
	
	public List<Abbonamento> findConDebitoByCampagna(Campagna entity) {
		log.info("findConDebitoByCampagna: {} {}", entity, entity.getResiduo());
		return 
				abbonamentoDao.findByCampagna(entity)
                .stream()
                .filter(a -> a.getResiduo().compareTo(entity.getResiduo()) > 0)
                .collect(Collectors.toList());
	}

	
	@Override
	public void invia(Campagna campagna, UserInfo user) throws Exception {
    	if (campagna.getStatoCampagna() != StatoCampagna.Generata ) {
        	log.warn("invia: Impossibile invia campagna {}, lo stato campagna non 'Generata'", campagna);
        	throw new UnsupportedOperationException("Impossibile eseguire invia campagna, " + campagna.getAnno().getAnno() +". La campagna non è nello stato 'Generata'");

    	}
    	lock(campagna);
		try {
			doInvia(campagna);
			OperazioneCampagna op = new OperazioneCampagna();
			op.setCampagna(campagna);
			op.setStato(StatoCampagna.Inviata);
			op.setOperatore(user.getUsername());
			operazioneCampagnaDao.save(op);
		} catch (Exception e) {
			log.error("invia: {}",e.getMessage(),e);
			unlock(campagna);
			throw e;
		} 
    	unlock(campagna);
	}
	
	@Transactional
	private void doInvia(Campagna campagna) {
        log.info("invia Campagna start {}", campagna);
    	List<Abbonamento> abbonamenti = abbonamentoDao.findByCampagna(campagna);
    	
    	List<Abbonamento> oldabbs= abbonamentoDao.findWithResiduoAndAnno(Anno.getAnnoPrecedente(campagna.getAnno()));
    	oldabbs.forEach( a->log.info("invia: residuo: {}",a));

    	
    	Map<Long,Abbonamento> intestatariNoContrassegnoNoCampagnaMap= 
    			oldabbs
			.stream().filter(a -> a.getCampagna() == null && a.isContrassegno() == false)
			.collect(Collectors.toMap(a -> a.getIntestatario().getId(), a -> a));

    	Map<Long,Abbonamento> intestatariContrassegnoNoCampagnaMap= 
    			oldabbs
			.stream().filter(a -> a.getCampagna() == null && a.isContrassegno() == true)
			.collect(Collectors.toMap(a -> a.getIntestatario().getId(), a -> a));

    	Map<Long,Abbonamento> intestatariNoContrassegnoCampagnaMap= 
    			oldabbs
			.stream().filter(a -> a.getCampagna() != null && a.isContrassegno() == false)
			.collect(Collectors.toMap(a -> a.getIntestatario().getId(), a -> a));

    	Map<Long,Abbonamento> intestatariContrassegnoCampagnaMap= 
    			oldabbs
			.stream().filter(a -> a.getCampagna() != null && a.isContrassegno() == true)
			.collect(Collectors.toMap(a -> a.getIntestatario().getId(), a -> a));

    	List<Versamento> committenti = versamentoDao.findWithResiduo();

    	abbonamenti.forEach( ca -> {
    		Anagrafica intestatario = ca.getIntestatario();
    		Abbonamento past = null;
    		if (!ca.isContrassegno() && intestatariNoContrassegnoNoCampagnaMap.containsKey(intestatario.getId())) {
    			past = intestatariNoContrassegnoNoCampagnaMap.get(intestatario.getId());
    		} else if (ca.isContrassegno() && intestatariContrassegnoNoCampagnaMap.containsKey(intestatario.getId())) {
    			past = intestatariContrassegnoNoCampagnaMap.get(intestatario.getId());
    		} else if (!ca.isContrassegno() && intestatariNoContrassegnoCampagnaMap.containsKey(intestatario.getId())) {
    			past = intestatariNoContrassegnoCampagnaMap.get(intestatario.getId());
    		} else if (ca.isContrassegno() && intestatariContrassegnoCampagnaMap.containsKey(intestatario.getId())) {
    			past = intestatariContrassegnoCampagnaMap.get(intestatario.getId());
    		}
    		if (past != null) {
    			log.info("invia: trovato Residuo: {}", past.getResiduo());
    			ca.setPregresso(past.getResiduo());
    			past.setPregresso(past.getPregresso().subtract(past.getResiduo()));
    			abbonamentoDao.save(ca);
    			abbonamentoDao.save(past);
    			log.info("invia: rimosso: {}", past);
    			log.info("invia: aggiunto: {}", ca);
    		}
    		committenti
    			.stream()
    			.filter(v -> v.getCommittente() != null && v.getCommittente().getId() == intestatario.getId())
    			.forEach(v -> {
					try {
						log.info("invia: incassa {} da {}", v.getResiduo(), v.getCommittente().getCaption());
						smdService.incassa(ca, v, userDao.findByUsernameContainingIgnoreCase("admin").iterator().next()
								, "Incassato da Committente ad Invio Campagna");
					} catch (Exception e) {
						log.error("invia: incassa {} da {}", v.getResiduo(), v.getCommittente().getCaption(), e.getMessage(),e);
					}
				});
    		rivistaAbbonamentoDao.findByAbbonamentoAndStatoAbbonamento(ca,StatoAbbonamento.Nuovo).forEach(ra -> {
    			if (ra.getNumeroTotaleRiviste() == 0) {
    				ra.setStatoAbbonamento(StatoAbbonamento.Annullato);
    			} else if (Smd.isOmaggio(ra)) {
    				ra.setStatoAbbonamento(StatoAbbonamento.Valido);
    			} else {
    				ra.setStatoAbbonamento(StatoAbbonamento.Proposto);
    			}
    			rivistaAbbonamentoDao.save(ra);
    		});
            abbonamentoDao.save(ca);
    	});        
        campagna.setStatoCampagna(StatoCampagna.Inviata);
        repository.save(campagna);
        log.info("invia Campagna end {}", campagna);
	}

	@Override
	public void estratto(Campagna campagna, UserInfo user) throws Exception{
    	if (campagna.getStatoCampagna() != StatoCampagna.Inviata ) {
        	log.warn("estratto: Impossibile estratto campagna {}, lo stato campagna non 'Inviata'", campagna);
        	throw new UnsupportedOperationException("Impossibile eseguire estratto campagna, " + campagna.getAnno().getAnno() +". La campagna non è nello stato 'Inviata'");

    	}
    	lock(campagna);
		try {
			doEstratto(campagna);
			OperazioneCampagna op = new OperazioneCampagna();
			op.setCampagna(campagna);
			op.setStato(StatoCampagna.InviatoEC);
			op.setOperatore(user.getUsername());
			operazioneCampagnaDao.save(op);

		} catch (Exception e) {
			log.error("estratto: {}",e.getMessage(),e);
			unlock(campagna);
			throw e;
		} 
    	unlock(campagna);

	}
	
	@Transactional
	private void doEstratto(Campagna campagna) throws Exception {
        log.info("estratto Campagna start {}", campagna);
        for (Abbonamento abbonamento :abbonamentoDao.findByCampagna(campagna)) {
            StatoAbbonamento stato = StatoAbbonamento.SospesoInviatoEC;
            switch (Smd.getStatoIncasso(abbonamento)) {
            case Si:
            	stato=StatoAbbonamento.Valido;
            	break;
            case SiConDebito:
            	stato=StatoAbbonamento.ValidoConResiduo;
            	break;
            case No:
				if (abbonamento.getImporto().subtract(new BigDecimal("7.00")).signum() >=0) {
					abbonamento.setSpeseEstrattoConto(new BigDecimal("2.00"));
				} else {
					stato=StatoAbbonamento.Sospeso;
				}
                break;
            case Parzialmente:
				if (abbonamento.getResiduo().subtract(new BigDecimal("7.00")).signum() >=0) {
					abbonamento.setSpeseEstrattoConto(new BigDecimal("2.00"));
    			} else {
					stato=StatoAbbonamento.Sospeso;
				}
	            break;
            case Zero:
            	stato = StatoAbbonamento.Valido;
                break;
            default:
                break;
            }
            abbonamentoDao.save(abbonamento);
			log.info("estratto: {}:  {}", stato, abbonamento);
            for (RivistaAbbonamento ra: rivistaAbbonamentoDao.findByAbbonamento(abbonamento)) {
                if (ra.getTipoAbbonamentoRivista() == TipoAbbonamentoRivista.Ordinario 
        		 || ra.getTipoAbbonamentoRivista() == TipoAbbonamentoRivista.Web
        		 || ra.getTipoAbbonamentoRivista() == TipoAbbonamentoRivista.Scontato ) {
            		ra.setStatoAbbonamento(stato);
        			rivistaAbbonamentoDao.save(ra);
        		}
            }
			smdService.aggiornaSpedizioni(abbonamento);
        }
        campagna.setStatoCampagna(StatoCampagna.InviatoEC);
        repository.save(campagna);  
        log.info("estratto Campagna end {}", campagna);
	}
	
	@Override
	public void chiudi(Campagna campagna, UserInfo user) throws Exception{
    	if (campagna.getStatoCampagna() != StatoCampagna.InviatoEC ) {
        	log.warn("chiudi: Impossibile chiudi campagna {}, lo stato campagna non 'InviatoEC'", campagna);
        	throw new UnsupportedOperationException("Impossibile eseguire chiudi campagna, " + campagna.getAnno().getAnno() +". La campagna non è nello stato 'InviatoEC'");

    	}
    	lock(campagna);
		try {
			doChiudi(campagna);
			OperazioneCampagna op = new OperazioneCampagna();
			op.setCampagna(campagna);
			op.setStato(StatoCampagna.Chiusa);
			op.setOperatore(user.getUsername());
			operazioneCampagnaDao.save(op);
		} catch (Exception e) {
			log.error("chiudi: {}",e.getMessage(),e);
			unlock(campagna);
			throw e;
		} 
    	unlock(campagna);
	}
	
	//FIXME what about storico?
	@Transactional
	private void doChiudi(Campagna campagna) throws Exception {
        log.info("chiudi Campagna start {}", campagna);
        Map<Long,Storico> storici = storicoDao.findAll().stream().collect(Collectors.toMap(Storico::getId, Function.identity()));
        for (Abbonamento abbonamento :abbonamentoDao.findByCampagna(campagna)) {
            log.info("chiudi: {}", abbonamento);
            for (RivistaAbbonamento rivista: rivistaAbbonamentoDao.findByAbbonamento(abbonamento)) {
            	if (rivista.getStorico() == null ) {
            		continue;
            	}
            	Storico storico = storici.get(rivista.getStorico().getId());
            	if (storico == null) {
            		continue;
            	}
            	switch (rivista.getStatoAbbonamento()) {
		            case Valido:
		            	break;
		            case ValidoConResiduo:
		            	break;
		            case ValidoInviatoEC:
		            	riattivaStorico(storico);
		                break;
		            case Annullato:
		            	aggiornaStatoStorico(storico,campagna.getNumero());
		                break;
		            case Sospeso:
		            	aggiornaStatoStorico(storico,campagna.getNumero());
		                break;
		            case SospesoInviatoEC:
		            	aggiornaStatoStorico(storico,campagna.getNumero());
		                break;
		            case Proposto:
		            	riattivaStorico(storico);
		                break;
		            case Nuovo:
		            	riattivaStorico(storico);
		                break;
		            default:
		                break;
            	}
            }
        }
        storicoDao.findByNumero(0).forEach(s -> {
        	if (s.getStatoStorico() != StatoStorico.Annullato) {
        		s.setStatoStorico(StatoStorico.Annullato);
        		storicoDao.save(s);
        		log.info("chiudi: nr.0/Annullato: {} ", s);
        	}
        });

        campagna.setStatoCampagna(StatoCampagna.Chiusa);
        repository.save(campagna); 
        log.info("chiudi Campagna end {}", campagna);
	}

	public List<Campagna> searchBy(Anno anno) {
        if (anno != null) {
            Campagna campagna = repository.findByAnno(anno);
            List<Campagna> campagne = new ArrayList<>();
            if (campagna != null) {
                campagne.add(campagna);
            }
            return campagne;
        }
        return findAll();

	}

	@Override
	public Campagna save(Campagna entity) throws Exception {
		throw new UnsupportedOperationException("Campagna non può essere salvata");
	}

    public void aggiornaStatoStorico(Storico storico, int numero) throws Exception {
        if (storico.getNumero() == 0) {
        	storico.setStatoStorico(StatoStorico.Annullato);            	
        } else if (storico.getNumero() <= numero) {
        	storico.setStatoStorico(StatoStorico.Sospeso);
        } else {
        	storico.setStatoStorico(StatoStorico.Valido);
        }
    	storicoDao.save(storico);
    	log.info("aggiornaStatoStorico: {}", storico);
    }

    public void riattivaStorico(Storico storico) throws Exception {
        storico.setStatoStorico(StatoStorico.Valido);
        storicoDao.save(storico);
    }

	@Override
	public void sospendi(Campagna campagna, Pubblicazione p, UserInfo user) throws Exception {
    	if (campagna.getStatoCampagna() != StatoCampagna.Inviata ) {
        	log.warn("sospendi: Impossibile sospendere campagna {}, lo stato campagna non 'Inviata'", campagna);
        	throw new UnsupportedOperationException("Impossibile eseguire estratto campagna, " + campagna.getAnno().getAnno() +". La campagna non è nello stato 'Inviata'");

    	}
    	lock(campagna);
		try {
	        OperazioneSospendi op = operazioneSospendiDao.findUniqueByCampagnaAndPubblicazione(campagna, p);
	        if (op != null) {
	        	throw new UnsupportedOperationException("Anno and Pubblicazione sospesi: " + campagna.getAnno() + ", " + p.getNome());
	        }
			doSospendi(campagna,p);
	        op = new OperazioneSospendi(p, campagna);
	        op.setOperatore(user.getUsername());
			operazioneSospendiDao.save(op);
		} catch (Exception e) {
			log.error("sospendi: {}",e.getMessage(),e);
			unlock(campagna);
			throw e;
		} 
    	unlock(campagna);		
	}
	
	@Transactional
	private void doSospendi(Campagna campagna, Pubblicazione p) throws Exception {
        log.info("sospendi Campagna start {} {}", campagna, p);
        for (Abbonamento abbonamento :abbonamentoDao.findByCampagna(campagna)) {
            StatoAbbonamento stato = StatoAbbonamento.Sospeso;
            switch (Smd.getStatoIncasso(abbonamento)) {
            case Si:
            	stato=StatoAbbonamento.Valido;
            	break;
            case SiConDebito:
            	stato=StatoAbbonamento.ValidoConResiduo;
            	break;
            case No:
                break;
            case Parzialmente:
	            break;
            case Zero:
            	stato = StatoAbbonamento.Valido;
                break;
            default:
                break;
            }
			log.info("sospendi: {}:  {}", stato, abbonamento);
            for (RivistaAbbonamento ra: rivistaAbbonamentoDao.findByAbbonamentoAndPubblicazione(abbonamento, p)) {
                if (ra.getTipoAbbonamentoRivista() == TipoAbbonamentoRivista.Ordinario 
        		 || ra.getTipoAbbonamentoRivista() == TipoAbbonamentoRivista.Web
        		 || ra.getTipoAbbonamentoRivista() == TipoAbbonamentoRivista.Scontato ) {
            		ra.setStatoAbbonamento(stato);
        			rivistaAbbonamentoDao.save(ra);
        		}
            }
			smdService.aggiornaSpedizioni(abbonamento);
        }
        log.info("sospendi Campagna end {} {}", campagna,p);
	}

	@Override
	public List<OperazioneCampagna> getOperazioni(Campagna campagna) {
		return operazioneCampagnaDao.findByCampagna(campagna);
	}

	@Override
	public List<OperazioneSospendi> getSospensioni(Campagna campagna) {
		return operazioneSospendiDao.findByCampagna(campagna);
	}

}
