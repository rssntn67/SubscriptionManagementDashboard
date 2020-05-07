package it.arsinfo.smd.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.arsinfo.smd.dao.repository.SpesaSpedizioneDao;
import it.arsinfo.smd.entity.SpesaSpedizione;

@Service
public class SpesaSpedizioneServiceDao implements SmdServiceDao<SpesaSpedizione> {

    @Autowired
    SpesaSpedizioneDao repository;

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
	
}
