package it.arsinfo.smd.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.arsinfo.smd.dao.repository.VersamentoDao;
import it.arsinfo.smd.entity.Versamento;

@Service
public class VersamentoServiceDao implements SmdServiceDao<Versamento> {

    @Autowired
    VersamentoDao repository;

	@Override
	public Versamento save(Versamento entity) throws Exception {
		return repository.save(entity);
	}

	@Override
	public void delete(Versamento entity) throws Exception {
		repository.delete(entity);
	}

	@Override
	public Versamento findById(Long id) {
		return repository.findById(id).get();
	}

	@Override
	public List<Versamento> findAll() {
		return repository.findAll();
	}

	public VersamentoDao getRepository() {
		return repository;
	}
	
}
