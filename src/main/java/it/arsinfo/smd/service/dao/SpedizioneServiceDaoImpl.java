package it.arsinfo.smd.service.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.arsinfo.smd.dao.SmdService;
import it.arsinfo.smd.dao.SpedizioneServiceDao;
import it.arsinfo.smd.dao.repository.AnagraficaDao;
import it.arsinfo.smd.dao.repository.PubblicazioneDao;
import it.arsinfo.smd.dao.repository.SpedizioneDao;
import it.arsinfo.smd.dao.repository.SpedizioneItemDao;
import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.InvioSpedizione;
import it.arsinfo.smd.data.Mese;
import it.arsinfo.smd.data.StatoSpedizione;
import it.arsinfo.smd.dto.Indirizzo;
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

    @Autowired
    AnagraficaDao anagraficaDao;

    @Autowired
    PubblicazioneDao pubblicazioneDao; 
    
    @Autowired
    SmdService smdService;

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
	
	@Override
	public List<Spedizione> searchBy(
			Pubblicazione pubblicazione, 
			Anagrafica destinatario,
			StatoSpedizione stato, 
			Anno anno, 
			Mese mese,
			InvioSpedizione invio) {

		if (destinatario == null && pubblicazione == null && stato == null) {
            return findAll();            
        } 
        if (destinatario == null && stato == null) {
            return itemRepository
            		.findByPubblicazione(pubblicazione)
            		.stream()
            		.map(si -> si.getSpedizione())
            		.collect(Collectors.toList());
        }
        if (pubblicazione == null && stato == null ) {
        	return repository
        			.findByDestinatario(destinatario);
        } 
        if (destinatario == null && pubblicazione == null) {
            return itemRepository
            		.findByStatoSpedizione(stato)
            		.stream()
            		.map(si -> si.getSpedizione()).collect(Collectors.toList());
        }
        if (stato == null) {
         return itemRepository
        		 .findByPubblicazione(pubblicazione)
         		 .stream()
         		 .map(si -> si.getSpedizione())
         		 .filter( sp -> sp.getDestinatario().equals(destinatario))
         		 .collect(Collectors.toList());
        }
        if (pubblicazione == null) {
            return itemRepository.findByStatoSpedizione(stato)
            		.stream()
            		.map(si -> si.getSpedizione())
            		.filter( sp -> sp.getDestinatario().equals(destinatario))
            		.collect(Collectors.toList());
        }
        if (destinatario == null) {
        	return itemRepository
        			.findByPubblicazioneAndStatoSpedizione(pubblicazione, stato)
        			.stream()
            		.map(si -> si.getSpedizione())
            		.collect(Collectors.toList());
        }
        List<Spedizione> spedizioni = new ArrayList<Spedizione>();
        if (anno != null) {
            spedizioni = spedizioni.stream().filter( s -> s.getAnnoSpedizione() == anno).collect(Collectors.toList());
        }
        if (mese != null) {
            spedizioni=spedizioni.stream().filter(s -> s.getMeseSpedizione() == mese).collect(Collectors.toList());      
        }
        if (invio != null) {
            spedizioni=spedizioni.stream().filter(s -> s.getInvioSpedizione() == invio).collect(Collectors.toList());      
        }
        return itemRepository.findByPubblicazioneAndStatoSpedizione(pubblicazione, stato).stream()
        		.map(si -> si.getSpedizione())
        		.filter( sp -> sp.getDestinatario().equals(destinatario))
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

	@Override
	public List<Pubblicazione> findPubblicazioni() {
		return pubblicazioneDao.findAll();
	}

	@Override
	public List<Anagrafica> findAnagrafica() {
		return anagraficaDao.findAll();
	}

	@Override
	public void inviaDuplicato(Spedizione sped, InvioSpedizione invio) throws Exception {
		smdService.inviaDuplicato(sped, invio);
	}

	@Override
	public Indirizzo stampa(Spedizione sped) {
		Anagrafica co = sped.getDestinatario().getCo();
		if (co == null)
			return Indirizzo.getIndirizzo(sped.getDestinatario());
		co = anagraficaDao.findById(co.getId()).get();
			return Indirizzo.getIndirizzo(sped.getDestinatario(),co);
	}
		
}
