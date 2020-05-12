package it.arsinfo.smd.service.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.arsinfo.smd.dao.SpedizioneServiceDao;
import it.arsinfo.smd.dao.repository.SpedizioneDao;
import it.arsinfo.smd.dao.repository.SpedizioneItemDao;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.entity.Spedizione;

@Service
public class SpedizioneServiceDaoImpl implements SpedizioneServiceDao {

    @Autowired
    private SpedizioneDao repository;
    
    
    @Autowired
    private SpedizioneItemDao itemRepository;

    private static final Logger log = LoggerFactory.getLogger(SpedizioneServiceDaoImpl.class);

	@Override
	public Spedizione save(Spedizione entity) throws Exception {
		return repository.save(entity);
	}

	@Override
	public void delete(Spedizione entity) throws Exception {
		repository.delete(entity);
	}

	@Override
	public Spedizione findById(Long id) {
		return repository.findById(id).get();
	}

	@Override
	public List<Spedizione> findAll() {
        return repository.findAll();
	}

	public SpedizioneDao getRepository() {
		return repository;
	}
	
	public List<Spedizione> searchBy(Pubblicazione p, Anagrafica a) {
        log.info("searchBy: start");
        List<Spedizione> spedizioni = new ArrayList<>();
        if (a == null && p == null) {
            spedizioni = findAll();            
        } else if (a == null) {
            spedizioni = findSpedizioneByPubblicazione(p);
        } else if (p == null) {
        	spedizioni = repository.findByDestinatario(a);
        } else {
            spedizioni = findSpedizioneByPubblicazione(p).stream().filter( s -> s.getDestinatario().equals(a)).collect(Collectors.toList());
        }
        log.info("searchBy: end");
        return spedizioni;

	}
	
    public List<Spedizione> findSpedizioneByPubblicazione(Pubblicazione p) {
        return itemRepository.findByPubblicazione(p)
        		.stream()
        		.map(si -> si.getSpedizione()).collect(Collectors.toList());
    }
	
}
