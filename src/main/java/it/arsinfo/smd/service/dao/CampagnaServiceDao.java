package it.arsinfo.smd.service.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.arsinfo.smd.dao.SmdServiceDao;
import it.arsinfo.smd.dao.repository.AbbonamentoDao;
import it.arsinfo.smd.dao.repository.CampagnaDao;
import it.arsinfo.smd.dao.repository.CampagnaItemDao;
import it.arsinfo.smd.dao.repository.PubblicazioneDao;
import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.Incassato;
import it.arsinfo.smd.data.StatoAbbonamento;
import it.arsinfo.smd.data.StatoCampagna;
import it.arsinfo.smd.data.TipoPubblicazione;
import it.arsinfo.smd.dto.AbbonamentoConRiviste;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Campagna;
import it.arsinfo.smd.entity.CampagnaItem;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.service.Smd;
import it.arsinfo.smd.service.SmdService;

@Service
public class CampagnaServiceDao implements SmdServiceDao<Campagna> {

	@Autowired
	private CampagnaDao repository;

	@Autowired
	private CampagnaItemDao campagnaItemDao;

	@Autowired
	private AbbonamentoDao abbonamentoDao;

	@Autowired
	private PubblicazioneDao pubblicazioneDao;

	@Autowired
	private SmdService smdService;

	private static final Logger log = LoggerFactory.getLogger(CampagnaServiceDao.class);

	@Override
	@Transactional
	public Campagna save(Campagna entity) throws Exception {
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
			log.warn("Impossibile rigenerare campagna per {}: una campagna esiste", entity.getAnno().getAnnoAsString());
			throw new UnsupportedOperationException(
					"Impossibile generare campagna per anno " + entity.getAnno().getAnno() + ". La campagna esiste");
		}

		log.info("save: Campagna start {}", entity);
		repository.save(entity);
		for (CampagnaItem item : entity.getCampagnaItems()) {
			campagnaItemDao.save(item);
			log.info("save: {}", item);
		}
		smdService.genera(entity);
		log.info("save: Campagna end {}", entity);
		return entity;
	}

	@Override
	@Transactional
	public void delete(Campagna entity) throws Exception {
		log.info("delete: Campagna start {}", entity);
		if (entity.getStatoCampagna() != StatoCampagna.Generata) {
			log.warn("delete: Impossibile delete campagna {}, lo stato campagna non 'Generata'", entity);
			throw new UnsupportedOperationException("Impossibile eseguire delete campagna, "
					+ entity.getAnno().getAnno() + ". La campagna non è nello stato 'Generata'");
		}
		for (Abbonamento abbonamento : abbonamentoDao.findByCampagna(entity)) {
			abbonamentoDao.delete(abbonamento);
		}
		entity.getCampagnaItems().stream().forEach(item -> campagnaItemDao.delete(item));
		repository.deleteById(entity.getId());
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

	public List<AbbonamentoConRiviste> findAbbonamentoConRivisteAnnullati(Campagna entity) {
		return smdService.get(findAnnullatiByCampagna(entity));
	}
	
	public List<Abbonamento> findInviatiByCampagna(Campagna entity) {
		return abbonamentoDao.findByCampagna(entity).stream().filter(a -> a.getTotale().signum() > 0)
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

	
	public void invia(Campagna campagna) throws Exception {
        log.info("invia Campagna start {}", campagna);
    	if (campagna.getStatoCampagna() != StatoCampagna.Generata ) {
        	log.warn("invia: Impossibile invia campagna {}, lo stato campagna non 'Generata'", campagna);
        	throw new UnsupportedOperationException("Impossibile eseguire invia campagna, " + campagna.getAnno().getAnno() +". La campagna non è nello stato 'Generata'");

    	}
        for (Abbonamento abb: abbonamentoDao.findByCampagna(campagna)) {
            if (abb.getTotale().signum() == 0) {
                abb.setStatoAbbonamento(StatoAbbonamento.Valido);
            } else {
                abb.setStatoAbbonamento(StatoAbbonamento.Proposto);
            }
            abbonamentoDao.save(abb);
        }
        campagna.setStatoCampagna(StatoCampagna.Inviata);
        repository.save(campagna);
        log.info("invia Campagna end {}", campagna);
	}

	public void estratto(Campagna campagna) throws Exception{
        log.info("estratto Campagna start {}", campagna);
    	if (campagna.getStatoCampagna() != StatoCampagna.Inviata ) {
        	log.warn("estratto: Impossibile estratto campagna {}, lo stato campagna non 'Inviata'", campagna);
        	throw new UnsupportedOperationException("Impossibile eseguire estratto campagna, " + campagna.getAnno().getAnno() +". La campagna non è nello stato 'Inviata'");

    	}
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
        log.info("chiudi Campagna start {}", campagna);
    	if (campagna.getStatoCampagna() != StatoCampagna.InviatoEC ) {
        	log.warn("chiudi: Impossibile chiudi campagna {}, lo stato campagna non 'InviatoEC'", campagna);
        	throw new UnsupportedOperationException("Impossibile eseguire chiudi campagna, " + campagna.getAnno().getAnno() +". La campagna non è nello stato 'InviatoEC'");

    	}
        for (Abbonamento abbonamento :abbonamentoDao.findByCampagna(campagna)) {
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
            default:
            	smdService.sospendiStorico(abbonamento);
                break;
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
}
