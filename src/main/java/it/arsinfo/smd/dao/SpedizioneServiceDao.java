package it.arsinfo.smd.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.arsinfo.smd.dao.repository.SpedizioneDao;
import it.arsinfo.smd.entity.Spedizione;

@Service
public class SpedizioneServiceDao implements SmdServiceDao<Spedizione> {

    @Autowired
    private SpedizioneDao repository;

	@Override
	public Spedizione save(Spedizione entity) throws Exception {
		return repository.save(entity);
	}

	@Override
	public void delete(Spedizione entity) throws Exception {
		repository.delete(entity);
	}

	@Override
	public Spedizione findById(Long id) {
		return repository.findById(id).get();
	}

	@Override
	public List<Spedizione> findAll() {
		return repository.findAll();
	}

	public SpedizioneDao getRepository() {
		return repository;
	}
	
}
