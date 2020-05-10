package it.arsinfo.smd.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.arsinfo.smd.dao.repository.AbbonamentoDao;
import it.arsinfo.smd.dao.repository.AnagraficaDao;
import it.arsinfo.smd.dao.repository.CampagnaDao;
import it.arsinfo.smd.dao.repository.EstrattoContoDao;
import it.arsinfo.smd.dao.repository.PubblicazioneDao;
import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.StatoAbbonamento;
import it.arsinfo.smd.data.TipoEstrattoConto;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Campagna;
import it.arsinfo.smd.entity.EstrattoConto;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.service.SmdService;

@Service
public class AbbonamentoServiceDao implements SmdServiceItemDao<Abbonamento,EstrattoConto> {

    @Autowired
    private AbbonamentoDao repository;

    @Autowired
    private EstrattoContoDao itemRepository;

    @Autowired
    private PubblicazioneDao pubblicazioneDao;

    @Autowired
    private AnagraficaDao anagraficaDao;

    @Autowired
    private CampagnaDao campagnaDao;

	@Autowired
	private SmdService smdService;

	@Override
	@Transactional
	public Abbonamento save(Abbonamento entity) throws Exception {
        if (entity.getId() == null && entity.getAnno() == null) {
        	throw new UnsupportedOperationException("Selezionare Anno Prima di Salvare");
        }
        if (entity.getId() == null && entity.getAnno().getAnno() < Anno.getAnnoCorrente().getAnno()) {
        	throw new UnsupportedOperationException("Anno deve essere anno corrente o successivi");
        }
        if (entity.getId() == null && entity.getItems().size() == 0) {
        	throw new UnsupportedOperationException("Aggiungere Estratto Conto Prima di Salvare");
        }
        if (entity.getId() == null) {
            entity.setCodeLine(Abbonamento.generaCodeLine(entity.getAnno()));
        }
    	repository.save(entity);
        if (entity.getId() == null ) {
            smdService.genera(entity);
        } 
		return entity;
	}

	@Override
	@Transactional
	public void delete(Abbonamento entity) throws Exception {
        if (entity.getId() == null) {
			throw new UnsupportedOperationException("Abbonamento non Salvato");
        }
        if (entity.getCampagna() != null) {
			throw new UnsupportedOperationException("Abbonamento associato a Campagna va gestito da Storico");
        }
        if (entity.getStatoAbbonamento() != StatoAbbonamento.Nuovo) {
			throw new UnsupportedOperationException("Stato Abbonamento diverso da Nuovo va gestito da Campagna");
        }
        smdService.cancella(entity);
        repository.delete(entity);
	}

	@Override
	public Abbonamento findById(Long id) {
		return repository.findById(id).get();
	}

	@Override
	public List<Abbonamento> findAll() {
		return repository.findAll();
	}

	public AbbonamentoDao getRepository() {
		return repository;
	}

	public List<Abbonamento> findByCampagna(Campagna entity) {
		return repository.findByCampagna(entity);
	}
	
	public List<Abbonamento> findInviatiByCampagna(Campagna entity) {
		return repository.findByCampagna(entity).stream().filter(a -> a.getTotale().signum() > 0)
				.collect(Collectors.toList());
	}
	
	public List<Abbonamento> findEstrattoContoByCampagna(Campagna entity) {
		return Stream
		.of(repository.findByCampagnaAndStatoAbbonamento(entity, StatoAbbonamento.ValidoInviatoEC),
				repository.findByCampagnaAndStatoAbbonamento(entity,
						StatoAbbonamento.SospesoInviatoEC))
		.flatMap(Collection::stream).collect(Collectors.toList());
	}
	
	public List<Abbonamento> findAnnullatiByCampagna(Campagna entity) {
		return 
                repository.findByCampagna(entity)
                .stream()
                .filter(a -> a.getStatoAbbonamento() == StatoAbbonamento.Annullato)
                .collect(Collectors.toList());
	}

	public List<Anagrafica> getAnagrafica() {
		return anagraficaDao.findAll();
	}

	public List<Pubblicazione> getPubblicazioni() {
		return pubblicazioneDao.findAll();
	}

	public List<Campagna> getCampagne() {
		return campagnaDao.findAll();
	}

	@Override
	public List<EstrattoConto> getItems(Abbonamento t) {
		if (t.getId() == null) {
			return new ArrayList<>();
		}
		return itemRepository.findByAbbonamento(t);
	}

	@Override
	public Abbonamento deleteItem(Abbonamento t, EstrattoConto item) throws Exception{
		System.err.println("deleteItem: " + item);
		if (item.getId() == null ) {
            if (!t.removeItem(item)) {
            	throw new UnsupportedOperationException("Non posso rimuovere EC");
            }
        } else {
            smdService.rimuovi(t,item);        	
        }
        Abbonamento abbonamento = findById(t.getId());
        return abbonamento;
	}

	@Override
	public Abbonamento saveItem(Abbonamento t, EstrattoConto item) throws Exception {
        if (item.getDestinatario() == null) {
        	throw new UnsupportedOperationException("Selezionare il Destinatario");
        }
        if (item.getPubblicazione() == null) {
        	throw new UnsupportedOperationException("Selezionare la Pubblicazione");
        }
        if (item.getId() == null && item.getAbbonamento().getId() == null) {
            t.addItem(item);
            return t;
        }
        if (item.getId() == null ) {
            t.getItems().clear();
            t.addItem(item);
            smdService.genera(t);
        } else {
        	smdService.aggiorna(item);
        }
        return findById(t.getId());
	}

	public List<EstrattoConto> findByTipoEstrattoConto(TipoEstrattoConto tec) {
		return itemRepository.findByTipoEstrattoConto(tec);
	}

	public List<EstrattoConto> findByDestinatario(Anagrafica customer) {
		return itemRepository.findByDestinatario(customer);
	}

	public List<Abbonamento> findByIntestatario(Anagrafica customer) {
		return repository.findByIntestatario(customer);
	}

	public List<EstrattoConto> findByPubblicazione(Pubblicazione pubblicazione) {
		return itemRepository.findByPubblicazione(pubblicazione);
	}

	public List<EstrattoConto> findAllItems() {
		return itemRepository.findAll();
	}

}
