package it.arsinfo.smd.service.impl;

import it.arsinfo.smd.dao.*;
import it.arsinfo.smd.entity.*;
import it.arsinfo.smd.service.Smd;
import it.arsinfo.smd.service.api.AnagraficaService;
import it.arsinfo.smd.service.api.CampagnaService;
import it.arsinfo.smd.service.api.SmdService;
import it.arsinfo.smd.service.dto.AbbonamentoConRiviste;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CampagnaServiceDaoImpl implements CampagnaService {

	@Autowired
	private CampagnaDao repository;

	@Autowired
	private CampagnaItemDao campagnaItemDao;

	@Autowired
	private AbbonamentoDao abbonamentoDao;

	@Autowired
	private RivistaAbbonamentoDao rivistaAbbonamentoDao;

	@Autowired
	private AnagraficaService anagraficaService;

	@Autowired
	private StoricoDao storicoDao;

	@Autowired
	private NotaDao notaDao;

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
		for (Storico storico: storicoDao.findByStatoStorico(StatoStorico.Sospeso)) {
			storico.setNumero(0);
			storico.setStatoStorico(StatoStorico.Annullato);
			Nota nota = new Nota();
			nota.setStorico(storico);
			nota.setOperatore("admin");
			nota.setDescription("stato storico: Sospeso -> Annullato :: set numero 0");
			storicoDao.save(storico);
			notaDao.save(nota);
		}
		for (Storico storico: storicoDao.findByNumero(0)) {
			storico.setStatoStorico(StatoStorico.Annullato);
			Nota nota = new Nota();
			nota.setStorico(storico);
			nota.setOperatore("admin");
			nota.setDescription("numero 0 -> stato_storico: Annullato");
			storicoDao.save(storico);
			notaDao.save(nota);
		}
		for (Storico storico: storicoDao.findByStatoStorico(StatoStorico.Nuovo)) {
			storico.setStatoStorico(StatoStorico.Valido);
			Nota nota = new Nota();
			nota.setStorico(storico);
			nota.setOperatore("admin");
			nota.setDescription("stato_storico: Nuovo -> Valido");
			storicoDao.save(storico);
			notaDao.save(nota);
		}

        for (Abbonamento abb: Smd.genera(entity, anagraficaService.findAll(), storicoDao.findByStatoStoricoNotAndNumeroGreaterThan(StatoStorico.Annullato,0))) {
            storicoDao.findByIntestatarioAndContrassegnoAndStatoStorico(abb.getIntestatario(),abb.isContrassegno(), StatoStorico.Valido)
                .forEach(storico -> Smd.genera(abb, storico));
            if (abb.getItems().size() >= 1) {
                smdService.genera(abb);
            }
        }
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
		entity.getCampagnaItems().forEach(item -> campagnaItemDao.delete(item));
		operazioneCampagnaDao.findByCampagna(entity).forEach(op -> operazioneCampagnaDao.delete(op));
		log.info("delete: Campagna end {}", entity);
	}

	@Override
	public Campagna findById(Long id) {
		return repository.findById(id).orElse(null);
	}

	@Override
	public List<Campagna> findAll() {
		return repository.findAll();
	}

	@Override
	public List<Campagna> searchByDefault() {
		return repository.findAll();
	}

	@Override
	public Campagna add() {
		return new Campagna();
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
		return smdService.get(
				abbonamentoDao.findByCampagna(entity)
					.stream()
					.filter(a -> 
					a.getImporto().signum() > 0 
					|| a.getSpese().signum() > 0 
					|| a.getPregresso().signum() > 0 
					|| a.getSpeseEstero().signum() > 0)
					.collect(Collectors.toList()
							)
					);
	}
	
	@Override
	public List<AbbonamentoConRiviste> findAbbonamentoConRivisteSollecito(Campagna entity) {
		return smdService.get( abbonamentoDao.findByCampagnaAndSollecitato(entity, true).stream().filter( abb -> abb.getStatoAbbonamento() == StatoAbbonamento.Sospeso).collect(Collectors.toList())
		);
	}

	@Override
	public List<AbbonamentoConRiviste> findAbbonamentoConRivisteEstrattoConto(Campagna entity) {
		return smdService.get( abbonamentoDao.findByCampagnaAndInviatoEC(entity, true));
	}

	@Override
	public List<AbbonamentoConRiviste> findAbbonamentoConDebito(Campagna entity) {
		return smdService
				.get(abbonamentoDao.findByCampagna(entity)
		                .stream()
		                .filter(a -> a.getResiduo().compareTo(entity.getResiduo()) > 0)
		                .collect(Collectors.toList()));
	}
				
	@Override
	public void invia(Campagna campagna, UserInfo user) {
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
        campagna.setStatoCampagna(StatoCampagna.Inviata);
        repository.save(campagna);
    	abbonamentoDao
		.findByCampagna(campagna)
		.forEach( abbonamento -> {
        	if (smdService.getNotValid(abbonamento, campagna).isEmpty()) {
        		abbonamento.setStatoAbbonamento(StatoAbbonamento.Valido);        		
        	} else {
        		abbonamento.setStatoAbbonamento(StatoAbbonamento.Proposto);
        	}
			abbonamentoDao.save(abbonamento);
    	});   
        log.info("invia Campagna end {}", campagna);
	}

	@Override
	public void sollecita(Campagna campagna, UserInfo user) {
    	if (campagna.getStatoCampagna() != StatoCampagna.Inviata ) {
        	log.warn("sollecita: Impossibile sollecita campagna {}, lo stato campagna non 'Inviata'", campagna);
        	throw new UnsupportedOperationException("Impossibile eseguire sollecito campagna, " + campagna.getAnno().getAnno() +". La campagna non è nello stato 'Inviata'");
    	}
    	lock(campagna);
		try {
			doSollecito(campagna);
			OperazioneCampagna op = new OperazioneCampagna();
			op.setCampagna(campagna);
			op.setStato(StatoCampagna.InviatoSollecito);
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
	private void doSollecito(Campagna campagna) {
        log.info("sollecito Campagna start {}", campagna);
        campagna.setStatoCampagna(StatoCampagna.InviatoSollecito);
        repository.save(campagna);  
        abbonamentoDao
    	.findByCampagna(campagna)
    	.forEach(abbonamento -> {
    		if ( smdService.getNotValid(abbonamento, campagna).isEmpty()) {
        		abbonamento.setStatoAbbonamento(StatoAbbonamento.Valido);        		
        	} else {
        		abbonamento.setStatoAbbonamento(StatoAbbonamento.Proposto);
        	}
    		if (sendSollecito(abbonamento,campagna)) {
    			abbonamento.setSpeseEstrattoConto(campagna.getSpeseSollecito());
    			abbonamento.setSollecitato(true);
    		}
			abbonamentoDao.save(abbonamento);    		
        });
        log.info("sollecito Campagna end {}", campagna);
	}

	@Override
	public void sospendi(Campagna campagna, Pubblicazione p, UserInfo user) {
        log.info("sospendi Campagna start {} {}", campagna, p);
    	if (campagna.getStatoCampagna() != StatoCampagna.InviatoSollecito && campagna.getStatoCampagna() != StatoCampagna.InviatoSospeso) {
        	log.warn("sospendi: Impossibile sospendere campagna {}, lo stato campagna non 'Inviato Sollecito'", campagna);
        	throw new UnsupportedOperationException("Impossibile sospendere campagna, " + campagna.getAnno().getAnno() +". La campagna non è nello stato 'InviatoSollecito'");

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
        log.info("sospendi Campagna end {} {}", campagna,p);
	}
	
	@Transactional
	private void doSospendi(Campagna campagna, Pubblicazione p) {
        campagna.setStatoCampagna(StatoCampagna.InviatoSospeso);
        repository.save(campagna);  
        abbonamentoDao.
        findByCampagna(campagna).
        forEach(abbonamento -> {
            boolean almenounarivistasospesa=false;
            boolean almenounarivistaattiva=false;
        	for (RivistaAbbonamento ra: rivistaAbbonamentoDao.
            findByAbbonamentoAndPubblicazione(abbonamento, p)) {
            	StatoRivista stato = Smd.getStatoRivista(abbonamento, ra);
            	if (stato != StatoRivista.Attiva) {
            		almenounarivistasospesa=true;
					ra.setStatoRivista(StatoRivista.Sospesa);
					rivistaAbbonamentoDao.save(ra);
					smdService.sospendiSpedizioniProgrammate(abbonamento,ra);
            	} else {
            		almenounarivistaattiva=true;
            	}
            }
        	abbonamento.setStatoAbbonamento(Smd.getStatoAbbonamento(almenounarivistaattiva, almenounarivistasospesa, abbonamento.getStatoIncasso(),StatoCampagna.InviatoSospeso));
        	abbonamentoDao.save(abbonamento);
        });

	}
	
	public static boolean sendEC(Abbonamento abb, Campagna campagna) {
		return abb.getStatoAbbonamento() != StatoAbbonamento.Valido && 
				abb.getResiduo().subtract(campagna.getLimiteInvioEstratto()).signum() >= 0;
	}

	public static boolean sendSollecito(Abbonamento abb, Campagna campagna) {
		return abb.getStatoAbbonamento() != StatoAbbonamento.Valido && 
				abb.getResiduo().subtract(campagna.getLimiteInvioSollecito()).signum() >= 0;
	}

	@Override
	public void estratto(Campagna campagna, UserInfo user) {
		switch (campagna.getStatoCampagna()) {
			case InviatoSospeso:
			case InviatoSollecito:
				break;
			default:
				log.warn("estratto: Impossibile EC campagna {}, stato campagna non è 'InviatoSospeso' o 'InviatoSollecito", campagna);
				throw new UnsupportedOperationException("Impossibile eseguire estratto conto campagna, " + campagna.getAnno().getAnno() +". La campagna non è nello stato 'InviatoSospeso' o 'InviatoSollecito'");
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
	private void doEstratto(Campagna campagna) {
        log.info("estratto Campagna start {}", campagna);
        campagna.setStatoCampagna(StatoCampagna.InviatoEC);
        repository.save(campagna);  
        abbonamentoDao.findByCampagna(campagna).forEach(abbonamento -> {
            boolean almenounarivistasospesa=false;
            boolean almenounarivistaattiva=false;
        	for (RivistaAbbonamento ra: rivistaAbbonamentoDao.findByAbbonamento(abbonamento)) {
            	StatoRivista stato = Smd.getStatoRivista(abbonamento, ra);
            	if (stato != StatoRivista.Attiva) {
            		almenounarivistasospesa=true;
					ra.setStatoRivista(StatoRivista.Sospesa);
					rivistaAbbonamentoDao.save(ra);
					smdService.sospendiSpedizioniProgrammate(abbonamento,ra);
            	} else {
            		almenounarivistaattiva=true;
            	}
            }
			if (sendEC(abbonamento,campagna) ) {
				abbonamento.setSpeseEstrattoConto(abbonamento.getSpeseEstrattoConto().add(campagna.getSpeseEstrattoConto()));
				abbonamento.setInviatoEC(true);
			}
        	abbonamento.setStatoAbbonamento(Smd.getStatoAbbonamento(almenounarivistaattiva, almenounarivistasospesa, abbonamento.getStatoIncasso(),StatoCampagna.InviatoSospeso));
        	abbonamentoDao.save(abbonamento);

        });
        log.info("estratto Campagna end {}", campagna);
	}
	
	@Override
	public void chiudi(Campagna campagna, UserInfo user) {
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
	
	@Transactional
	private void doChiudi(Campagna campagna) {
        log.info("chiudi Campagna start {}", campagna);
        storicoDao.findByNumero(0).forEach(s -> {
        	if (s.getStatoStorico() != StatoStorico.Annullato) {
        		s.setStatoStorico(StatoStorico.Annullato);
        		storicoDao.save(s);
        		log.info("chiudi: nr.0/Annullato: {} ", s);
        	}
        });
        Map<Long,Storico> storici = storicoDao.findByStatoStoricoNotAndNumeroGreaterThan(StatoStorico.Sospeso, 0).stream().collect(Collectors.toMap(Storico::getId, Function.identity()));
        for (Abbonamento abbonamento :abbonamentoDao.findByCampagna(campagna)) {
            for (RivistaAbbonamento rivista: rivistaAbbonamentoDao.findByAbbonamento(abbonamento)) {
            	if (rivista.getStorico() == null ) {
            		continue;
            	}
            	Storico storico = storici.get(rivista.getStorico().getId());
            	if (storico == null) {
            		continue;
            	}            	
            	switch (rivista.getStatoRivista()) {
		            case Attiva:
		            	storico.setStatoStorico(StatoStorico.Valido);
		            	break;
		            case Sospesa:
		            	if (storico.getNumero() <= campagna.getNumero()) {
		                	storico.setStatoStorico(StatoStorico.Sospeso);
		                } else {
		                	storico.setStatoStorico(StatoStorico.Valido);
		                }
		            	storicoDao.save(storico);
		            	log.info("chiudi: {}", storico);
		                break;
		            default:
		            	break;
            	}
                storicoDao.save(storico);

            }
        }

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
		repository.save(entity);
		return repository.findByAnno(entity.getAnno());
	}

	@Override
	public List<OperazioneCampagna> getOperazioni(Campagna campagna) {
		return operazioneCampagnaDao.findByCampagna(campagna);
	}

	@Override
	public List<OperazioneSospendi> getSospensioni(Campagna campagna) {
		return operazioneSospendiDao.findByCampagna(campagna);
	}

	@Override
	public List<AbbonamentoConRiviste> searchBy(
			Anno anno,
			Diocesi searchDiocesi,
			String searchNome,
			String searchDenominazione,
			String searchCitta,
			String searchCap,
			Paese paese,
			AreaSpedizione areaSped,
			Provincia provincia,
			TitoloAnagrafica titolo,
			Regione regioneVescovi,
			CentroDiocesano centroDiocesano,
			Regione regioneDirettoreDiocesano,
			boolean filterDirettoreDiocesano,
			boolean filterPresidenteDiocesano,
			Regione regionePresidenteDiocesano,
			boolean filterDirettoreZonaMilano,
			boolean filterConsiglioNazionaleADP,
			boolean filterPresidenzaADP,
			boolean filterDirezioneADP,
			boolean filterCaricheSocialiADP,
			boolean filterDelegatiRegionaliADP,
			boolean filterElencoMarisaBisi,
			boolean filterPromotoreRegionale)

	{
		List<Long> intestatarioId =
				anagraficaService.searchBy(searchDiocesi, searchNome, searchDenominazione, searchCitta, searchCap,
								paese, areaSped, provincia,
								titolo, regioneVescovi, centroDiocesano,
								regioneDirettoreDiocesano, filterDirettoreDiocesano,
								filterPresidenteDiocesano, regionePresidenteDiocesano,
								filterDirettoreZonaMilano, filterConsiglioNazionaleADP,
								filterPresidenzaADP, filterDirezioneADP, filterCaricheSocialiADP,
								filterDelegatiRegionaliADP, filterElencoMarisaBisi,
								filterPromotoreRegionale)
						.stream().map(Anagrafica::getId).collect(Collectors.toList());
		List<Abbonamento> abbonamenti = abbonamentoDao.findByAnno(anno).stream().filter(abb -> intestatarioId.contains(abb.getIntestatario().getId())).collect(Collectors.toList());
		return smdService.get(abbonamenti);
	}

}
