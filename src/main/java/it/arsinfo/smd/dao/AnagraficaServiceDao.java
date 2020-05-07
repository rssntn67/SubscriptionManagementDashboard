package it.arsinfo.smd.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.arsinfo.smd.dao.repository.AnagraficaDao;
import it.arsinfo.smd.entity.Anagrafica;

@Service
public class AnagraficaServiceDao implements SmdServiceDao<Anagrafica> {

    @Autowired
    AnagraficaDao anagraficaDao;

	@Override
	public Anagrafica save(Anagrafica entity) throws Exception {
		return anagraficaDao.save(entity);
	}

	@Override
	public void delete(Anagrafica entity) throws Exception {
		anagraficaDao.delete(entity);
	}

	@Override
	public Anagrafica findById(Long id) {
		return anagraficaDao.findById(id).get();
	}

	
}
