package it.arsinfo.smd.service.dao;

import it.arsinfo.smd.dao.SpesaSpedizioneDao;
import it.arsinfo.smd.data.AreaSpedizione;
import it.arsinfo.smd.data.RangeSpeseSpedizione;
import it.arsinfo.smd.entity.SpesaSpedizione;
import it.arsinfo.smd.service.api.SpesaSpedizioneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class SpesaSpedizioneServiceDaoImpl implements SpesaSpedizioneService {

    @Autowired
    private SpesaSpedizioneDao repository;

	@Override
	public SpesaSpedizione save(SpesaSpedizione entity) throws Exception {
		return repository.save(entity);
	}

	@Override
	public void delete(SpesaSpedizione entity) throws Exception {
		repository.delete(entity);
	}

	@Override
	public SpesaSpedizione findById(Long id) {
		return repository.findById(id).orElse(null);
	}

	@Override
	public List<SpesaSpedizione> findAll() {
		return repository.findAll();
	}

	@Override
	public List<SpesaSpedizione> searchByDefault() {
		return repository.findAll();
	}

	@Override
	public SpesaSpedizione add() {
		return new SpesaSpedizione();
	}

	public SpesaSpedizioneDao getRepository() {
		return repository;
	}
	
	public List<SpesaSpedizione> searchBy(AreaSpedizione area, RangeSpeseSpedizione range) {
        if (area == null && range == null) {
            return findAll();
        }
        if (range == null ) {
        	return repository.findByAreaSpedizione(area);
        } 
        if (area == null ) {
        	return repository.findByRangeSpeseSpedizione(range);
        }
        return Collections.singletonList(repository.findByAreaSpedizioneAndRangeSpeseSpedizione(area, range));
	}
	
}
