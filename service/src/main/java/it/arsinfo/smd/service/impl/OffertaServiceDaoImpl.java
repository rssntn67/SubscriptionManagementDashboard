package it.arsinfo.smd.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.arsinfo.smd.service.api.OffertaService;
import it.arsinfo.smd.dao.OffertaDao;
import it.arsinfo.smd.entity.Anno;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Offerta;

@Service
public class OffertaServiceDaoImpl implements OffertaService {

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
		return repository.findById(id).orElse(null);
	}

	@Override
	public List<Offerta> findAll() {
		return repository.findAll();
	}

	@Override
	public List<Offerta> searchByDefault() {
		return repository.findAll();
	}

	@Override
	public Offerta add() {
		return new Offerta();
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
