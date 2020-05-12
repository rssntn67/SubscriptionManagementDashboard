package it.arsinfo.smd.service.dao;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import it.arsinfo.smd.dao.SmdServiceDao;
import it.arsinfo.smd.dao.repository.PubblicazioneDao;
import it.arsinfo.smd.data.TipoPubblicazione;
import it.arsinfo.smd.entity.Pubblicazione;

@Service
public class PubblicazioneServiceDaoImpl implements SmdServiceDao<Pubblicazione> {

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

	public PubblicazioneDao getRepository() {
		return repository;
	}
	
	public List<Pubblicazione> searchBy(String nome, TipoPubblicazione tipo) {
        if (StringUtils.isEmpty(nome) && tipo == null) {
            return findAll();
        }

        if (tipo == null) {
            return repository.findByNomeStartsWithIgnoreCase(nome);
        }
        if (StringUtils.isEmpty(nome)) {
            return repository.findByTipo(tipo);
        }
        return repository.findByNomeStartsWithIgnoreCase(nome).stream().filter(p -> p.getTipo().equals(tipo)).collect(Collectors.toList());

	}
}
