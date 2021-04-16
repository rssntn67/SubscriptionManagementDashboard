package it.arsinfo.smd.service.dao;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.arsinfo.smd.dao.OffertaServiceDao;
import it.arsinfo.smd.dao.repository.OffertaDao;
import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Offerta;

@Service
public class OffertaServiceDaoImpl implements OffertaServiceDao {

    @Autowired
    private OffertaDao repository;

	@Override
	public Offerta save(Offerta entity) throws Exception {
		return repository.save(entity);
	}

	@Override
	public void delete(Offerta entity) throws Exception {
		repository.delete(entity);
	}

	@Override
	public Offerta findById(Long id) {
		return repository.findById(id).get();
	}

	@Override
	public List<Offerta> findAll() {
		return repository.findAll();
	}

	public OffertaDao getRepository() {
		return repository;
	}

	@Override
	public List<Offerta> findByCommittente(Anagrafica committente, Anno anno) throws Exception {
    	if (committente == null) {
    		throw new UnsupportedOperationException("Anagrafica deve essere valorizzata");
    	}
    	if (anno == null) {
    		throw new UnsupportedOperationException("Anno deve essere valorizzato");
    	}
		return repository.findByCommittente(committente)
				.stream()
				.filter(o -> o.getOfferteCumulate().getAnno() == anno)
				.collect(Collectors.toList());
	}
	
}
