package it.arsinfo.smd.service.dao;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.arsinfo.smd.dao.StoricoServiceDao;
import it.arsinfo.smd.dao.repository.AbbonamentoDao;
import it.arsinfo.smd.dao.repository.AnagraficaDao;
import it.arsinfo.smd.dao.repository.CampagnaDao;
import it.arsinfo.smd.dao.repository.NotaDao;
import it.arsinfo.smd.dao.repository.PubblicazioneDao;
import it.arsinfo.smd.dao.repository.RivistaAbbonamentoDao;
import it.arsinfo.smd.dao.repository.StoricoDao;
import it.arsinfo.smd.data.StatoCampagna;
import it.arsinfo.smd.data.StatoStorico;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Campagna;
import it.arsinfo.smd.entity.Nota;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.entity.RivistaAbbonamento;
import it.arsinfo.smd.entity.Storico;
import it.arsinfo.smd.service.Smd;
import it.arsinfo.smd.service.SmdService;

@Service
public class StoricoServiceDaoImpl implements StoricoServiceDao {

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
    private AbbonamentoDao abbonamentoDao;

    @Autowired
    private RivistaAbbonamentoDao rivistaAbbonamentoDao;

    
    @Autowired
    private AnagraficaDao anagraficaDao;

    private static final Logger log = LoggerFactory.getLogger(StoricoServiceDao.class);

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
        if (entity.getNumero() <= 0) {
        	entity.setNumero(0);
        	entity.setStatoStorico(StatoStorico.Sospeso);
        }
        repository.save(entity);
        for (Nota nota:entity.getItems()) {
                itemRepository.save(nota);
        }
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
        if (storico == null) {
            throw new UnsupportedOperationException("Lo Storico è nullo, riportare errore all'amministratore");                 
        }
        if (campagna == null) {
            throw new UnsupportedOperationException("La Campagna da aggiornare deve essere valorizzato");                 
        }
        if (campagna.getStatoCampagna() == StatoCampagna.Chiusa) {
        	log.warn("aggiornaCampagna: Non è possibile aggiornare la campagna {}, {}",campagna,storico);
            throw new UnsupportedOperationException(campagna + " - La Campagna è chiusa non può essere cambiata.");                 
        }
        if (storico.getId() == null && storico.getNumero() <= 0) {
            storico.addItem(getNotaOnUpdate(storico, campagna, "salva",username));
    		save(storico);
        	return;
        }
        if (storico.getId() == null ) {
            storico.addItem(getNotaOnUpdate(storico, campagna, "genera",username));
    		save(storico);
    		genera(campagna, storico);
        	return;
        }
        RivistaAbbonamento ec = getByStorico(campagna, storico);
        if (storico.getNumero() <= 0 && ec  == null) {
        	itemRepository.findByStorico(storico).forEach(nota->itemRepository.deleteById(nota.getId()));
        	repository.delete(storico);
        	return;
        }
        if (storico.getNumero() <= 0) {
            storico.addItem(getNotaOnUpdate(storico, campagna, "rimuovi",username));
    		save(storico);
            Abbonamento abbonamento = abbonamentoDao.findById(ec.getAbbonamento().getId()).get();
            smdService.rimuovi(abbonamento,ec);
            return;
        } 
        if (ec == null) {
            storico.addItem(getNotaOnUpdate(storico, campagna, "genera",username));
    		save(storico);
    		genera(campagna, storico);
    		return;
        	
        }
        
        storico.addItem(getNotaOnUpdate(storico, campagna, "aggiorna",username));
        save(storico);
        ec.setNumero(storico.getNumero());
        ec.setTipoAbbonamentoRivista(storico.getTipoAbbonamentoRivista());
        smdService.aggiorna(ec);
    }

	private void genera(Campagna campagna, Storico storico) throws Exception {
		Anagrafica a = anagraficaDao.findById(storico.getIntestatario().getId()).get();
		Abbonamento abbonamento = abbonamentoDao.findByIntestatarioAndCampagnaAndCassa(a, campagna, storico.getCassa());
		if (abbonamento == null) {
			abbonamento = Smd.genera(campagna, a, storico);
		}
		abbonamento.addItem(Smd.genera(abbonamento, storico));
    	smdService.genera(abbonamento);

	}
	
    private RivistaAbbonamento getByStorico(Campagna campagna,Storico storico) throws Exception{
        List<RivistaAbbonamento> ecs = 
                rivistaAbbonamentoDao
                .findByStorico(storico)
                .stream()
                .filter(ec -> {
                    Abbonamento abb = abbonamentoDao.findById(ec.getAbbonamento().getId()).get();
                    return abb.getAnno() == campagna.getAnno();
                })
                .collect(Collectors.toList());

        if (ecs.size() > 1 ) {
            throw new Exception("Un solo Rivista Abbonamento per storico ogni anno");
        }        
        if (ecs.size()  == 0) {
        	return null;
        }        
        return ecs.iterator().next();
        
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
