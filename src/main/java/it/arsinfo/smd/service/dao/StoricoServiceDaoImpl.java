package it.arsinfo.smd.service.dao;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.arsinfo.smd.dao.SmdService;
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
        }
    	log.info("save: {} {}", entity);
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
		throw new UnsupportedOperationException("Delete Nota non supportato");
	}

	@Override
	public Storico saveItem(Storico t, Nota item) throws Exception {
		t.addItem(item);
		t.addItem(getNotaOnSave(t,item.getOperatore()));
		return save(t);
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

	@Transactional
	public void aggiornaCampagna(Campagna campagna, Storico storico, String username) throws Exception {
        if (storico == null) {
        	log.warn("aggiornaCampagna: Lo Storico è nullo");
            throw new UnsupportedOperationException("Lo Storico è nullo, riportare errore all'amministratore");                 
        }
        if (campagna == null) {
        	log.warn("aggiornaCampagna: Lo Campagna è nulla");
            throw new UnsupportedOperationException("La Campagna da aggiornare deve essere valorizzato");                 
        }
        if (campagna.getStatoCampagna() == StatoCampagna.Chiusa) {
        	log.warn("aggiornaCampagna: {} {} Campagna Chiusa, non aggiornabile",campagna,storico);
            throw new UnsupportedOperationException(campagna + " - La Campagna è chiusa non può essere cambiata.");                 
        }
        if (storico.getId() == null && storico.getNumero() <= 0) {
        	log.warn("aggiornaCampagna: {} {} Storico nuovo con numero <= , non aggiornabile",campagna,storico);
            throw new UnsupportedOperationException(storico + "Errore impossibile aggiungere con Numero < 0.");                 
        }
        RivistaAbbonamento ec = getByStorico(campagna, storico);
        if (storico.getId() == null || (ec == null && storico.getNumero() > 0) ) {
        	log.info("aggiornaCampagna: genera {} {}",storico,campagna);
    		Anagrafica a = anagraficaDao.findById(storico.getIntestatario().getId()).get();
    		Abbonamento abbonamento = abbonamentoDao.findByIntestatarioAndCampagnaAndContrassegno(a, campagna, storico.isContrassegno());
    		if (abbonamento == null) {
    			abbonamento = Smd.genera(campagna, a, storico.isContrassegno());
    		}
    		Smd.genera(abbonamento, storico);
            storico.setStatoStorico(StatoStorico.Valido);
            storico.addItem(getNotaOnUpdate(storico, campagna, "genera",username));
            save(storico);
            if (abbonamento.getItems().size() >= 1) {
                smdService.genera(abbonamento);
            }
        	return;
        }
        if (storico.getNumero() <= 0 && ec  == null) {
        	log.info("aggiornaCampagna: not updating {} {}",storico,campagna);
        	return;
        }
        if (storico.getNumero() <= 0) {
        	log.info("aggiornaCampagna: rimuovi {} {}",storico,campagna);
            storico.addItem(getNotaOnUpdate(storico, campagna, "rimuovi",username));
    		save(storico);
            Abbonamento abbonamento = abbonamentoDao.findById(ec.getAbbonamento().getId()).get();
            smdService.rimuovi(abbonamento,ec);
            return;
        } 
        
    	log.info("aggiornaCampagna: aggiorna {} {}",storico,campagna);
        storico.addItem(getNotaOnUpdate(storico, campagna, "aggiorna",username));
        storico.setStatoStorico(StatoStorico.Valido);
        save(storico);
        smdService.aggiorna(ec,storico.getNumero(),storico.getTipoAbbonamentoRivista());
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

    public  Nota getNotaOnSave(Storico storico, String username) {
    	log.info("getNotaOnSave: {} {}", storico, username);
        Nota nota = new Nota(storico);
        nota.setOperatore("admin->"+username);
        if (storico.getId() == null) {
            nota.setDescription("Nuovo: " + storico.getHeader());
        } else {
            nota.setDescription("Aggiornato: " + storico.getHeader());                    
        }        
        return nota;
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

	@Override
	public List<Storico> searchBy(Anagrafica a) throws Exception {
    	if (a == null) {
    		throw new UnsupportedOperationException("Anagrafica deve essere valorizzata");
    	}
		return repository.findByDestinatarioOrIntestatario(a,a);
	}

}
