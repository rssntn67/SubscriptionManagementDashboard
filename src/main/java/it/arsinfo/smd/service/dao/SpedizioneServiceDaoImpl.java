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
import it.arsinfo.smd.entity.SpedizioneItem;

@Service
public class SpedizioneServiceDaoImpl implements SpedizioneServiceDao {

    @Autowired
    private SpedizioneDao repository;
    
    
    @Autowired
    private SpedizioneItemDao itemRepository;

	@Override
	public Spedizione save(Spedizione entity) throws Exception {
		throw new UnsupportedOperationException("Non posso salvare da UI");
	}

	@Override
	public void delete(Spedizione entity) throws Exception {
		throw new UnsupportedOperationException("Non posso delete da UI");
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

	@Override
	public List<SpedizioneItem> getItems(Spedizione t) {
		return itemRepository.findBySpedizione(t);
	}

	@Override
	public Spedizione deleteItem(Spedizione t, SpedizioneItem item) throws Exception {
		throw new UnsupportedOperationException("deleteItem non supportato");
	}

	@Override
	public Spedizione saveItem(Spedizione t, SpedizioneItem item) throws Exception {
		if (t.getId() == null) {
			throw new UnsupportedOperationException("saveItem: Spedizione non salvata: non supportato");			
		}
		if (item == null || item.getSpedizione() == null || item.getId() == null) {
			throw new UnsupportedOperationException("saveItem: SpedizioneItem non salvata: non supportato");						
		}
		if (item.getSpedizione().getId().longValue() != t.getId().longValue()) {
			throw new UnsupportedOperationException("saveItem: Spedizione non salvata: non corrispondono");
		}
		itemRepository.save(item);
		return t;
	}

	@Override
	public List<SpedizioneItem> findAllItems() {
		return itemRepository.findAll();
	}
		
}
