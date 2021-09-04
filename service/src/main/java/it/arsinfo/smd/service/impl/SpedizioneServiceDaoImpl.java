package it.arsinfo.smd.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.arsinfo.smd.service.api.SmdService;
import it.arsinfo.smd.service.api.SpedizioneService;
import it.arsinfo.smd.dao.AnagraficaDao;
import it.arsinfo.smd.dao.PubblicazioneDao;
import it.arsinfo.smd.dao.SpedizioneDao;
import it.arsinfo.smd.dao.SpedizioneItemDao;
import it.arsinfo.smd.entity.Anno;
import it.arsinfo.smd.entity.InvioSpedizione;
import it.arsinfo.smd.entity.Mese;
import it.arsinfo.smd.entity.StatoSpedizione;
import it.arsinfo.smd.service.dto.Indirizzo;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.entity.Spedizione;
import it.arsinfo.smd.entity.SpedizioneItem;

@Service
public class SpedizioneServiceDaoImpl implements SpedizioneService {

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
		return repository.findById(id).orElse(null);
	}

	@Override
	public List<Spedizione> findAll() {
        return repository.findAll();
	}

	@Override
	public List<Spedizione> searchByDefault() {
		return new ArrayList<>();
	}

	@Override
	public Spedizione add() {
		return new Spedizione();
	}

	public SpedizioneDao getRepository() {
		return repository;
	}

	@Override
	public List<Spedizione> searchBy(
			Anagrafica destinatario,
			Anno anno,
			Mese mese
	) {
		return repository.findByDestinatarioAndMeseSpedizioneAndAnnoSpedizione(destinatario,mese,anno);
	}

	@Override
	public List<Spedizione> searchBy(
			Anagrafica destinatario,
			Anno anno, 
			Mese mese,
			InvioSpedizione invio,
			Pubblicazione pubblicazione, 
			StatoSpedizione stato
			) {
        List<Spedizione> spedizioni;

		if (destinatario == null && anno == null && mese == null && invio == null)
            spedizioni = findAll();
        else if (anno == null && mese == null && invio == null) 
        	spedizioni = repository.findByDestinatario(destinatario);
        else if (destinatario == null && mese == null && invio == null) 
        	spedizioni = repository.findByAnnoSpedizione(anno);
        else if (destinatario == null && anno == null && invio == null) 
        	spedizioni = repository.findByMeseSpedizione(mese);
        else if (destinatario == null && mese == null && anno == null) 
        	spedizioni = repository.findByInvioSpedizione(invio);
        else if (mese == null && anno == null)
        	spedizioni = repository.findByDestinatarioAndInvioSpedizione(destinatario, invio);
        else if (mese == null && invio == null)
        	spedizioni = repository.findByDestinatarioAndAnnoSpedizione(destinatario, anno);
        else if (anno == null && invio == null)
        	spedizioni = repository.findByDestinatarioAndMeseSpedizione(destinatario, mese);
        else if (destinatario == null && anno == null)
        	spedizioni = repository.findByMeseSpedizioneAndInvioSpedizione(mese, invio);
        else if (destinatario == null && invio == null)
        	spedizioni = repository.findByMeseSpedizioneAndAnnoSpedizione(mese, anno);
        else if (destinatario == null && mese == null)
        	spedizioni = repository.findByAnnoSpedizioneAndInvioSpedizione(anno,invio);
        else if (mese == null)
        	spedizioni = repository.findByDestinatarioAndAnnoSpedizioneAndInvioSpedizione(destinatario,anno,invio);
        else if (anno == null)
        	spedizioni = repository.findByDestinatarioAndMeseSpedizioneAndInvioSpedizione(destinatario,mese,invio);
        else if (invio == null)
        	spedizioni = repository.findByDestinatarioAndMeseSpedizioneAndAnnoSpedizione(destinatario,mese,anno);
        else if (destinatario == null)
        	spedizioni = repository.findByMeseSpedizioneAndAnnoSpedizioneAndInvioSpedizione(mese,anno,invio);
        else
        	spedizioni = repository.findByDestinatarioAndMeseSpedizioneAndAnnoSpedizioneAndInvioSpedizione(destinatario,mese, anno, invio);
        return filterBy(pubblicazione, stato, spedizioni);

	}
	
	private List<Spedizione> filterBy(Pubblicazione pubblicazione, StatoSpedizione stato, List<Spedizione> spedizioni) {
		if (spedizioni.size() == 0) {
			return spedizioni;
		}
		if (pubblicazione == null && stato == null)
			return spedizioni;
		List<Long> approved;
		if (pubblicazione == null) {
			approved = itemRepository
					.findByStatoSpedizione(stato)
					.stream()
					.map(item -> item.getSpedizione().getId())
					.collect(Collectors.toList());
		} else if (stato == null) {
			approved = itemRepository
					.findByPubblicazione(pubblicazione)
					.stream()
					.map(item -> item.getSpedizione().getId())
					.collect(Collectors.toList());
		} else {
			approved = itemRepository
					.findByPubblicazioneAndStatoSpedizione(pubblicazione, stato)
					.stream()
					.map(item -> item.getSpedizione().getId())
					.collect(Collectors.toList());		
		}
        return spedizioni.stream().filter(sped -> approved.contains(sped.getId())).collect(Collectors.toList());

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
	public SpedizioneItem addItem(Spedizione spedizione) {
		SpedizioneItem item = new SpedizioneItem();
		item.setSpedizione(spedizione);
		return item;
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
	public Indirizzo getIndirizzo(Spedizione sped) {
		Anagrafica co = sped.getDestinatario().getCo();
		if (co == null)
			return Indirizzo.getIndirizzo(sped.getDestinatario());
		co = anagraficaDao.findById(co.getId()).orElse(null);
		if (co == null)
			return Indirizzo.getIndirizzo(sped.getDestinatario());
		return Indirizzo.getIndirizzo(sped.getDestinatario(),co);
	}

	@Override
	public void spedisci(Spedizione sped) {
		itemRepository.findBySpedizioneAndStatoSpedizione(sped, StatoSpedizione.PROGRAMMATA).forEach(item -> {
			item.setStatoSpedizione(StatoSpedizione.INVIATA);
			itemRepository.save(item);
		});
		itemRepository.findBySpedizioneAndStatoSpedizione(sped, StatoSpedizione.SOSPESA).forEach(item -> {
			item.setStatoSpedizione(StatoSpedizione.ANNULLATA);
			itemRepository.save(item);
		});
	}		
}
