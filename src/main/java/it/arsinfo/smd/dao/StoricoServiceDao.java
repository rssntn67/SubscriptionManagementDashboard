package it.arsinfo.smd.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.arsinfo.smd.dao.repository.StoricoDao;
import it.arsinfo.smd.entity.Storico;

@Service
public class StoricoServiceDao implements SmdServiceDao<Storico> {

    @Autowired
    private StoricoDao repository;

	@Override
	public Storico save(Storico entity) throws Exception {
		return repository.save(entity);
	}

	@Override
	public void delete(Storico entity) throws Exception {
		repository.delete(entity);
	}

	@Override
	public Storico findById(Long id) {
		return repository.findById(id).get();
	}

	@Override
	public List<Storico> findAll() {
		return repository.findAll();
	}

	public StoricoDao getRepository() {
		return repository;
	}
	
}
