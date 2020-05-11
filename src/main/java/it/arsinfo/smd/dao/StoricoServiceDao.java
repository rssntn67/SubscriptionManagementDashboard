package it.arsinfo.smd.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.arsinfo.smd.dao.repository.StoricoDao;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Pubblicazione;
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
	
	public List<Storico> searchBy(Anagrafica intestatario, Anagrafica destinatario, Pubblicazione pubblicazione) {
        if (destinatario == null && intestatario == null && pubblicazione == null) {
            return findAll();            
        }
        if (destinatario == null && intestatario == null) {
            return repository.findByPubblicazione(pubblicazione);
        }
        if (destinatario == null && pubblicazione == null) {
            return repository.findByIntestatario(intestatario);
        }
        if (intestatario == null && pubblicazione == null) {
            return repository.findByDestinatario(destinatario);
        }
        if (pubblicazione == null) {
            return repository.findByIntestatarioAndDestinatario(intestatario,destinatario);
        }
        if (intestatario == null) {
            return repository.findByDestinatarioAndPubblicazione(destinatario,pubblicazione);
        }
        if (destinatario == null ) {
            return repository.findByIntestatarioAndPubblicazione(intestatario, pubblicazione);
        }
        return repository.findByIntestatarioAndDestinatarioAndPubblicazione(intestatario, destinatario, pubblicazione);

	}
	
}
