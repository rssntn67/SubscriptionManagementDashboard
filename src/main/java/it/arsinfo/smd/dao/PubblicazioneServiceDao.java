package it.arsinfo.smd.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.arsinfo.smd.dao.repository.PubblicazioneDao;
import it.arsinfo.smd.entity.Pubblicazione;

@Service
public class PubblicazioneServiceDao implements SmdServiceDao<Pubblicazione> {

    @Autowired
    PubblicazioneDao repository;

	@Override
	public Pubblicazione save(Pubblicazione entity) throws Exception {
		return repository.save(entity);
	}

	@Override
	public void delete(Pubblicazione entity) throws Exception {
		repository.delete(entity);
	}

	@Override
	public Pubblicazione findById(Long id) {
		return repository.findById(id).get();
	}

	@Override
	public List<Pubblicazione> findAll() {
		return repository.findAll();
	}

	public PubblicazioneDao getRepository() {
		return repository;
	}
	
}
