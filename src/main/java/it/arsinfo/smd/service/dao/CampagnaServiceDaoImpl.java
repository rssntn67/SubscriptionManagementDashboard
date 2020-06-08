package it.arsinfo.smd.service.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
import it.arsinfo.smd.dao.repository.PubblicazioneDao;
import it.arsinfo.smd.dao.repository.StoricoDao;
import it.arsinfo.smd.dao.repository.UserInfoDao;
import it.arsinfo.smd.dao.repository.VersamentoDao;
import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.Incassato;
import it.arsinfo.smd.data.StatoAbbonamento;
import it.arsinfo.smd.data.StatoCampagna;
import it.arsinfo.smd.data.StatoStorico;
import it.arsinfo.smd.data.TipoPubblicazione;
import it.arsinfo.smd.dto.AbbonamentoConRiviste;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Campagna;
import it.arsinfo.smd.entity.CampagnaItem;
import it.arsinfo.smd.entity.Pubblicazione;
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
	private AnagraficaDao anagraficaDao;

	@Autowired
	private VersamentoDao versamentoDao;

	@Autowired
	private StoricoDao storicoDao;

	@Autowired
	private PubblicazioneDao pubblicazioneDao;

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
	public void genera(Campagna entity) throws Exception {
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

	public List<Pubblicazione> findPubblicazioni() {
		return pubblicazioneDao.findAll();
	}

	public List<Pubblicazione> findPubblicazioniValide() {
		return pubblicazioneDao.findByTipoNotAndActive(TipoPubblicazione.UNICO, true);
	}

	public List<AbbonamentoConRiviste> findAbbonamentoConRivisteGenerati(Campagna entity) {
		return smdService.get(abbonamentoDao.findByCampagna(entity));
	}

	public List<AbbonamentoConRiviste> findAbbonamentoConRivisteInviati(Campagna entity) {
		return smdService.get(findInviatiByCampagna(entity));
	}

	public List<AbbonamentoConRiviste> findAbbonamentoConRivisteEstrattoConto(Campagna entity) {
		return smdService
				.get(findEstrattoContoByCampagna(entity));
	}

	public List<AbbonamentoConRiviste> findAbbonamentoConDebito(Campagna entity) {
		return smdService
				.get(findConDebitoByCampagna(entity));
	}

	public List<AbbonamentoConRiviste> findAbbonamentoConRivisteAnnullati(Campagna entity) {
		return smdService.get(findAnnullatiByCampagna(entity));
	}
	
	public List<Abbonamento> findInviatiByCampagna(Campagna entity) {
		return abbonamentoDao.findByCampagna(entity).stream().filter(a -> a.getImporto().signum() > 0 || a.getSpese().signum() > 0 || a.getPregresso().signum() > 0 || a.getSpeseEstero().signum() > 0)
			.collect(Collectors.toList());
	}
	
	public List<Abbonamento> findEstrattoContoByCampagna(Campagna entity) {
		return Stream
		.of(abbonamentoDao.findByCampagnaAndStatoAbbonamento(entity, StatoAbbonamento.ValidoInviatoEC),
				abbonamentoDao.findByCampagnaAndStatoAbbonamento(entity,
						StatoAbbonamento.SospesoInviatoEC))
		.flatMap(Collection::stream).collect(Collectors.toList());
	}
	
	public List<Abbonamento> findAnnullatiByCampagna(Campagna entity) {
		return 
				abbonamentoDao.findByCampagna(entity)
                .stream()
                .filter(a -> a.getStatoAbbonamento() == StatoAbbonamento.Annullato)
                .collect(Collectors.toList());
	}
	
	public List<Abbonamento> findConDebitoByCampagna(Campagna entity) {
		return 
				abbonamentoDao.findByCampagna(entity)
                .stream()
                .filter(a -> a.getResiduo().signum() > 0)
                .collect(Collectors.toList());
	}

	
	@Transactional
	public void invia(Campagna campagna) throws Exception {
    	if (campagna.getStatoCampagna() != StatoCampagna.Generata ) {
        	log.warn("invia: Impossibile invia campagna {}, lo stato campagna non 'Generata'", campagna);
        	throw new UnsupportedOperationException("Impossibile eseguire invia campagna, " + campagna.getAnno().getAnno() +". La campagna non è nello stato 'Generata'");

    	}
    	lock(campagna);
		try {
			doInvia(campagna);
		} catch (Exception e) {
			log.error("invia: {}",e.getMessage(),e);
			unlock(campagna);
			throw e;
		} 
    	unlock(campagna);
	}
	
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
            if (ca.getTotale().signum() == 0) {
                ca.setStatoAbbonamento(StatoAbbonamento.Valido);
            } else {
                ca.setStatoAbbonamento(StatoAbbonamento.Proposto);
            }
            abbonamentoDao.save(ca);
    	});        
        campagna.setStatoCampagna(StatoCampagna.Inviata);
        repository.save(campagna);
        log.info("invia Campagna end {}", campagna);
	}

	public void estratto(Campagna campagna) throws Exception{
    	if (campagna.getStatoCampagna() != StatoCampagna.Inviata ) {
        	log.warn("estratto: Impossibile estratto campagna {}, lo stato campagna non 'Inviata'", campagna);
        	throw new UnsupportedOperationException("Impossibile eseguire estratto campagna, " + campagna.getAnno().getAnno() +". La campagna non è nello stato 'Inviata'");

    	}
    	lock(campagna);
		try {
			doEstratto(campagna);
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
        	Incassato inc = Smd.getStatoIncasso(abbonamento);
            switch (inc) {
            case No:
				if (abbonamento.getImporto().subtract(new BigDecimal("7.00")).signum() >=0) {
					abbonamento.setStatoAbbonamento(StatoAbbonamento.SospesoInviatoEC);
					abbonamento.setSpeseEstrattoConto(new BigDecimal("2.00"));
					log.info("estratto: EC {} inc.{}", abbonamento,inc);
				} else {
					abbonamento.setStatoAbbonamento(StatoAbbonamento.Sospeso);
					log.info("estratto: sospeso {} inc.", abbonamento,inc);
				}
				smdService.sospendiSpedizioni(abbonamento);
                break;
            case Parzialmente:
				if (abbonamento.getResiduo().subtract(new BigDecimal("7.00")).signum() >=0) {
					abbonamento.setStatoAbbonamento(StatoAbbonamento.SospesoInviatoEC);
					abbonamento.setSpeseEstrattoConto(new BigDecimal("2.00"));
					log.info("estratto: EC {} inc.{}", abbonamento,Incassato.Parzialmente);
					smdService.sospendiSpedizioni(abbonamento);
    			} else {
					abbonamento.setStatoAbbonamento(StatoAbbonamento.ValidoConResiduo);
					log.info("estratto: ValidoConResiduo {} inc.{}", abbonamento,inc);
					smdService.riattivaSpedizioni(abbonamento);
				}
	            break;
            case Zero:
                abbonamento.setStatoAbbonamento(StatoAbbonamento.Annullato);
				log.info("estratto: Annullato {} inc.{}", abbonamento,inc);
				smdService.sospendiSpedizioni(abbonamento);
                break;
            default:
				abbonamento.setStatoAbbonamento(StatoAbbonamento.Valido);
				log.info("estratto: Valido {} inc.{}", abbonamento,inc);
				smdService.riattivaSpedizioni(abbonamento);
                break;
            }        	
            abbonamentoDao.save(abbonamento);
        }
        campagna.setStatoCampagna(StatoCampagna.InviatoEC);
        repository.save(campagna);  
        log.info("estratto Campagna end {}", campagna);
	}
	
	public void chiudi(Campagna campagna) throws Exception{
    	if (campagna.getStatoCampagna() != StatoCampagna.InviatoEC ) {
        	log.warn("chiudi: Impossibile chiudi campagna {}, lo stato campagna non 'InviatoEC'", campagna);
        	throw new UnsupportedOperationException("Impossibile eseguire chiudi campagna, " + campagna.getAnno().getAnno() +". La campagna non è nello stato 'InviatoEC'");

    	}
    	lock(campagna);
		try {
			doChiudi(campagna);
		} catch (Exception e) {
			log.error("chiudi: {}",e.getMessage(),e);
			unlock(campagna);
			throw e;
		} 
    	unlock(campagna);
	}
	
	@Transactional
	private void doChiudi(Campagna campagna) throws Exception {
        log.info("chiudi Campagna start {}", campagna);
        
        for (Abbonamento abbonamento :abbonamentoDao.findByCampagna(campagna)) {
            log.info("chiudi: {}", abbonamento);
            switch (abbonamento.getStatoAbbonamento()) {
            case Valido:
            	smdService.riattivaStorico(abbonamento);
                break;
            case ValidoConResiduo:
            	smdService.riattivaStorico(abbonamento);
                break;
            case ValidoInviatoEC:
            	smdService.riattivaStorico(abbonamento);
                break;
            case Annullato:
            	smdService.aggiornaStatoStorico(abbonamento,campagna.getNumero());
                break;
            case Sospeso:
            	smdService.aggiornaStatoStorico(abbonamento,campagna.getNumero());
                break;
            case SospesoInviatoEC:
            	smdService.aggiornaStatoStorico(abbonamento,campagna.getNumero());
                break;
            case Proposto:
            	smdService.riattivaStorico(abbonamento);
                break;
            case Nuovo:
            	smdService.riattivaStorico(abbonamento);
                break;
            default:
                break;
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
}
