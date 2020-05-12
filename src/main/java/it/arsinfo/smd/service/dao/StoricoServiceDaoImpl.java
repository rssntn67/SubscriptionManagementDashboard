package it.arsinfo.smd.service.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.arsinfo.smd.dao.SmdServiceItemDao;
import it.arsinfo.smd.dao.repository.AnagraficaDao;
import it.arsinfo.smd.dao.repository.CampagnaDao;
import it.arsinfo.smd.dao.repository.NotaDao;
import it.arsinfo.smd.dao.repository.PubblicazioneDao;
import it.arsinfo.smd.dao.repository.StoricoDao;
import it.arsinfo.smd.data.StatoCampagna;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Campagna;
import it.arsinfo.smd.entity.Nota;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.entity.Storico;
import it.arsinfo.smd.service.SmdService;

@Service
public class StoricoServiceDaoImpl implements SmdServiceItemDao<Storico, Nota> {

    @Autowired
    private StoricoDao repository;
    
    @Autowired
    private NotaDao itemRepository;
    
    @Autowired
    private SmdService smdService;
    
    @Autowired
    private PubblicazioneDao pubblicazioneDao;

    @Autowired
    private CampagnaDao campagnaDao;
    
    @Autowired
    private AnagraficaDao anagraficaDao;

	@Override
	public Storico save(Storico entity) throws Exception {
        if (entity.getIntestatario() == null) {
        	throw new UnsupportedOperationException("Intestatario deve essere valorizzato");
        }
        if (entity.getDestinatario() == null) {
        	throw new UnsupportedOperationException("Destinatario deve essere valorizzato");
        }
        if (entity.getPubblicazione() == null) {
        	throw new UnsupportedOperationException("Pubblicazione deve essere valorizzata");
        }        
        smdService.save(entity, entity.getItems().stream().toArray(Nota[]::new));
		return entity;
	}

	@Override
	public void delete(Storico entity) throws Exception {
		throw new UnsupportedOperationException("Delete Storico non supportato");
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

	@Override
	public List<Nota> getItems(Storico t) {
		return itemRepository.findByStorico(t);
	}

	@Override
	public Storico deleteItem(Storico t, Nota item) throws Exception {
		t.removeItem(item);
		return t;
	}

	@Override
	public Storico saveItem(Storico t, Nota item) throws Exception {
		t.addItem(item);
		return t;
	}
	
	public List<Pubblicazione> findPubblicazioni() {
		return pubblicazioneDao.findAll();
	}

	public List<Anagrafica> findAnagrafica() {
		return anagraficaDao.findAll();
	}
	
	public List<Campagna> findCampagne() {
		return campagnaDao.findByStatoCampagnaNot(StatoCampagna.Chiusa);
	}

	public void aggiornaCampagna(Campagna campagna, Storico storico, String username) throws Exception {
        if (campagna == null) {
            throw new UnsupportedOperationException("La Campagna da aggiornare deve essere valorizzato");                 
        }
        if (storico.getNumero() <= 0) {
            Nota nota = getNotaOnUpdate(storico, campagna, "rimuovi",username);
    		smdService.rimuovi(campagna, storico, nota);
        } else {
            Nota nota = getNotaOnUpdate(storico, campagna, "aggiorna",username);
    		smdService.aggiorna(campagna, storico, nota);		
        }
	}
	
    private  Nota getNotaOnUpdate(Storico storico,Campagna campagna, String action, String username) {
        Nota unota = new Nota(storico);
        unota.setOperatore(username);
        unota.setDescription(action+": " + campagna.getCaption());
        return unota;
    }

	@Override
	public List<Nota> findAllItems() {
		return itemRepository.findAll();
	}

}
