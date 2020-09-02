package it.arsinfo.smd.service.dao;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.arsinfo.smd.dao.SpedizioneServiceDao;
import it.arsinfo.smd.dao.repository.SpedizioneDao;
import it.arsinfo.smd.dao.repository.SpedizioneItemDao;
import it.arsinfo.smd.data.StatoSpedizione;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.entity.Spedizione;

@Service
public class SpedizioneServiceDaoImpl implements SpedizioneServiceDao {

    @Autowired
    private SpedizioneDao repository;
    
    
    @Autowired
    private SpedizioneItemDao itemRepository;

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
	
	public List<Spedizione> searchBy(Pubblicazione p, Anagrafica a,StatoSpedizione s) {
        if (a == null && p == null && s == null) {
            return findAll();            
        } 
        if (a == null && s == null) {
            return itemRepository
            		.findByPubblicazione(p)
            		.stream()
            		.map(si -> si.getSpedizione())
            		.collect(Collectors.toList());
        }
        if (p == null && s == null ) {
        	return repository
        			.findByDestinatario(a);
        } 
        if (a == null && p == null) {
            return itemRepository
            		.findByStatoSpedizione(s)
            		.stream()
            		.map(si -> si.getSpedizione()).collect(Collectors.toList());
        }
        if (s == null) {
         return itemRepository
        		 .findByPubblicazione(p)
         		 .stream()
         		 .map(si -> si.getSpedizione())
         		 .filter( sp -> sp.getDestinatario().equals(a))
         		 .collect(Collectors.toList());
        }
        if (p == null) {
            return itemRepository.findByStatoSpedizione(s)
            		.stream()
            		.map(si -> si.getSpedizione())
            		.filter( sp -> sp.getDestinatario().equals(a))
            		.collect(Collectors.toList());
        }
        if (a == null) {
        	return itemRepository
        			.findByPubblicazioneAndStatoSpedizione(p, s)
        			.stream()
            		.map(si -> si.getSpedizione())
            		.collect(Collectors.toList());
        }
        return itemRepository.findByPubblicazioneAndStatoSpedizione(p, s).stream()
        		.map(si -> si.getSpedizione())
        		.filter( sp -> sp.getDestinatario().equals(a))
        		.collect(Collectors.toList());

	}
		
}
