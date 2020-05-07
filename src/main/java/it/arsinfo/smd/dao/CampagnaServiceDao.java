package it.arsinfo.smd.dao;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.arsinfo.smd.dao.repository.AbbonamentoDao;
import it.arsinfo.smd.dao.repository.CampagnaDao;
import it.arsinfo.smd.dao.repository.CampagnaItemDao;
import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.StatoCampagna;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Campagna;
import it.arsinfo.smd.entity.CampagnaItem;
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
        	throw new UnsupportedOperationException("Impossibile generare campagna per anno " + entity.getAnno().getAnno() +". La campagna esiste");
        }

        log.info("save: Campagna start {}", entity);
        repository.save(entity);
        for (CampagnaItem item:entity.getCampagnaItems() ) {
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
    	if (entity.getStatoCampagna() != StatoCampagna.Generata ) {
        	log.warn("delete: Impossibile delete campagna {}, lo stato campagna non 'Generata'", entity);
        	throw new UnsupportedOperationException("Impossibile eseguire delete campagna, " + entity.getAnno().getAnno() +". La campagna non è nello stato 'Generata'");
    	}
    	for (Abbonamento abbonamento: abbonamentoDao.findByCampagna(entity)) {
    		smdService.cancella(abbonamento);
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
	
}
