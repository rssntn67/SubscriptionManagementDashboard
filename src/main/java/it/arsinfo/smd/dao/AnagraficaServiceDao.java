package it.arsinfo.smd.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.arsinfo.smd.dao.repository.AnagraficaDao;
import it.arsinfo.smd.entity.Anagrafica;

@Service
public class AnagraficaServiceDao implements SmdServiceDao<Anagrafica> {

    @Autowired
    AnagraficaDao repository;

	@Override
	public Anagrafica save(Anagrafica entity) throws Exception {
		return repository.save(entity);
	}

	@Override
	public void delete(Anagrafica entity) throws Exception {
		repository.delete(entity);
	}

	@Override
	public Anagrafica findById(Long id) {
		return repository.findById(id).get();
	}

	@Override
	public List<Anagrafica> findAll() {
		return repository.findAll();
	}

	public AnagraficaDao getRepository() {
		return repository;
	}
	
}
