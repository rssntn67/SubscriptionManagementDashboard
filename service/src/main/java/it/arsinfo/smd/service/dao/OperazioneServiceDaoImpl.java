package it.arsinfo.smd.service.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.arsinfo.smd.service.api.OperazioneService;
import it.arsinfo.smd.dao.OperazioneDao;
import it.arsinfo.smd.entity.Operazione;
import it.arsinfo.smd.entity.Pubblicazione;

@Service
public class OperazioneServiceDaoImpl implements OperazioneService {

    @Autowired
    private OperazioneDao repository;

	@Override
	public Operazione save(Operazione entity) throws Exception {
		return repository.save(entity);
	}

	@Override
	public void delete(Operazione entity) throws Exception {
		repository.delete(entity);
	}

	@Override
	public Operazione findById(Long id) {
		return repository.findById(id).get();
	}

	@Override
	public List<Operazione> findAll() {
		return repository.findAll();
	}

	@Override
	public List<Operazione> searchByDefault() {
		return new ArrayList<>();
	}

	public OperazioneDao getRepository() {
		return repository;
	}

	public List<Operazione> searchBy(Pubblicazione p) {
       if (p == null) {
            return findAll();
        }
        return repository.findByPubblicazione(p);	 
	}
	
}
