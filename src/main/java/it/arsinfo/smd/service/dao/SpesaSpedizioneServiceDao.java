package it.arsinfo.smd.service.dao;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.arsinfo.smd.dao.SmdServiceDao;
import it.arsinfo.smd.dao.repository.SpesaSpedizioneDao;
import it.arsinfo.smd.data.AreaSpedizione;
import it.arsinfo.smd.data.RangeSpeseSpedizione;
import it.arsinfo.smd.entity.SpesaSpedizione;

@Service
public class SpesaSpedizioneServiceDao implements SmdServiceDao<SpesaSpedizione> {

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
		return repository.findById(id).get();
	}

	@Override
	public List<SpesaSpedizione> findAll() {
		return repository.findAll();
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
        return Arrays.asList(repository.findByAreaSpedizioneAndRangeSpeseSpedizione(area, range));
	}
	
}
