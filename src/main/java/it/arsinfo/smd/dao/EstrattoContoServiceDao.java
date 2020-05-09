package it.arsinfo.smd.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.arsinfo.smd.dao.repository.EstrattoContoDao;
import it.arsinfo.smd.entity.EstrattoConto;

@Service
public class EstrattoContoServiceDao implements SmdServiceDao<EstrattoConto> {

    @Autowired
    EstrattoContoDao repository;

	@Override
	public EstrattoConto save(EstrattoConto entity) throws Exception {
		return repository.save(entity);
	}

	@Override
	public void delete(EstrattoConto entity) throws Exception {
		repository.delete(entity);
	}

	@Override
	public EstrattoConto findById(Long id) {
		return repository.findById(id).get();
	}

	@Override
	public List<EstrattoConto> findAll() {
		return repository.findAll();
	}

	public EstrattoContoDao getRepository() {
		return repository;
	}
	
}
