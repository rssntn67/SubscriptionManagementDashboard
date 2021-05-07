package it.arsinfo.smd.service.dao;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import it.arsinfo.smd.service.api.PubblicazioneService;
import it.arsinfo.smd.dao.PubblicazioneDao;
import it.arsinfo.smd.data.TipoPubblicazione;
import it.arsinfo.smd.entity.Pubblicazione;

@Service
public class PubblicazioneServiceDaoImpl implements PubblicazioneService {

    @Autowired
    private PubblicazioneDao repository;

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

	@Override
	public List<Pubblicazione> searchByDefault() {
		return repository.findAll();
	}

	@Override
	public Pubblicazione add() {
		return null;
	}

	public PubblicazioneDao getRepository() {
		return repository;
	}
	
	public List<Pubblicazione> searchBy(String nome, TipoPubblicazione tipo) {
        if (!StringUtils.hasLength(nome) && tipo == null) {
            return findAll();
        }

        if (tipo == null) {
            return repository.findByNomeStartsWithIgnoreCase(nome);
        }
        if (!StringUtils.hasLength(nome)) {
            return repository.findByTipo(tipo);
        }
        return repository.findByNomeStartsWithIgnoreCase(nome).stream().filter(p -> p.getTipo().equals(tipo)).collect(Collectors.toList());

	}
}
