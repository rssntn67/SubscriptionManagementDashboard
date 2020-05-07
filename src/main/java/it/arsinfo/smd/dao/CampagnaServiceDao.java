package it.arsinfo.smd.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.arsinfo.smd.dao.repository.CampagnaDao;
import it.arsinfo.smd.dao.repository.CampagnaItemDao;
import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.entity.Campagna;
import it.arsinfo.smd.service.SmdService;

@Service
public class CampagnaServiceDao implements SmdServiceDao<Campagna> {
	
    @Autowired
    private CampagnaDao repository;
    
    @Autowired
    private CampagnaItemDao campagnaItemDao;

    @Autowired
    private SmdService smdService;

	@Override
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
        Campagna tobereturned=repository.save(entity);
        entity.getCampagnaItems().
        	stream().
        	forEach(ci -> campagnaItemDao.save(ci));
        smdService.genera(entity);
        return findById(tobereturned.getId());
	}

	@Override
	public void delete(Campagna entity) throws Exception {
		smdService.delete(entity);
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
