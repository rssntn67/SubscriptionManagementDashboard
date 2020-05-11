package it.arsinfo.smd.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.arsinfo.smd.dao.repository.IncassoDao;
import it.arsinfo.smd.entity.Incasso;

@Service
public class IncassoServiceDao implements SmdServiceDao<Incasso> {

    @Autowired
    private IncassoDao repository;

	@Override
	public Incasso save(Incasso entity) throws Exception {
		return repository.save(entity);
	}

	@Override
	public void delete(Incasso entity) throws Exception {
		repository.delete(entity);
	}

	@Override
	public Incasso findById(Long id) {
		return repository.findById(id).get();
	}

	@Override
	public List<Incasso> findAll() {
		return repository.findAll();
	}

	public IncassoDao getRepository() {
		return repository;
	}
	
}
