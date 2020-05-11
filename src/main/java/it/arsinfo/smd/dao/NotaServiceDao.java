package it.arsinfo.smd.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.arsinfo.smd.dao.repository.NotaDao;
import it.arsinfo.smd.entity.Nota;

@Service
public class NotaServiceDao implements SmdServiceDao<Nota> {

	@Autowired
	private NotaDao repository;

	@Override
	public Nota save(Nota entity) throws Exception {
		return repository.save(entity);
	}

	@Override
	public void delete(Nota entity) throws Exception {
		repository.delete(entity);
	}

	@Override
	public Nota findById(Long id) {
		return repository.findById(id).get();
	}

	@Override
	public List<Nota> findAll() {
		return repository.findAll();
	}

	public NotaDao getRepository() {
		return repository;
	}

}
