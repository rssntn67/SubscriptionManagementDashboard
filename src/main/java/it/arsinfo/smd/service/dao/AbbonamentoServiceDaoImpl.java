package it.arsinfo.smd.service.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.arsinfo.smd.dao.AbbonamentoServiceDao;
import it.arsinfo.smd.dao.SmdService;
import it.arsinfo.smd.dao.repository.AbbonamentoDao;
import it.arsinfo.smd.dao.repository.AnagraficaDao;
import it.arsinfo.smd.dao.repository.CampagnaDao;
import it.arsinfo.smd.dao.repository.DistintaVersamentoDao;
import it.arsinfo.smd.dao.repository.OperazioneIncassoDao;
import it.arsinfo.smd.dao.repository.PubblicazioneDao;
import it.arsinfo.smd.dao.repository.RivistaAbbonamentoDao;
import it.arsinfo.smd.dao.repository.VersamentoDao;
import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.StatoAbbonamento;
import it.arsinfo.smd.data.TipoAbbonamentoRivista;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Campagna;
import it.arsinfo.smd.entity.DistintaVersamento;
import it.arsinfo.smd.entity.OperazioneIncasso;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.entity.RivistaAbbonamento;
import it.arsinfo.smd.entity.UserInfo;
import it.arsinfo.smd.entity.Versamento;
import it.arsinfo.smd.service.Smd;

@Service
public class AbbonamentoServiceDaoImpl implements AbbonamentoServiceDao {

    @Autowired
    private AbbonamentoDao repository;

    @Autowired
    private RivistaAbbonamentoDao itemRepository;

    @Autowired
    private PubblicazioneDao pubblicazioneDao;

    @Autowired
    private AnagraficaDao anagraficaDao;

    @Autowired
    private CampagnaDao campagnaDao;

    @Autowired
    private DistintaVersamentoDao incassoDao;

    @Autowired
    private VersamentoDao versamentoDao;

    @Autowired
    private OperazioneIncassoDao operazioneIncassoDao;

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
        	throw new UnsupportedOperationException("Aggiungere Rivista Prima di Salvare");
        }
        if (entity.getIntestatario() == null) {
        	throw new UnsupportedOperationException("Aggiungere Intestatario Prima di Salvare");
        }
        if (entity.getId() == null) {
            entity.setCodeLine(Abbonamento.generaCodeLine(entity.getAnno()));
        }
        if (entity.getId() == null ) {
            smdService.genera(entity);
        } else {
        	repository.save(entity);
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
        smdService.rimuovi(entity);
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
	public List<RivistaAbbonamento> getItems(Abbonamento t) {
		if (t.getId() == null) {
			return new ArrayList<>();
		}
		return itemRepository.findByAbbonamento(t);
	}

	@Override
	@Transactional
	public Abbonamento deleteItem(Abbonamento t, RivistaAbbonamento item) throws Exception{
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
	@Transactional
	public Abbonamento saveItem(Abbonamento t, RivistaAbbonamento item) throws Exception {
        if (item.getDestinatario() == null) {
        	throw new UnsupportedOperationException("Selezionare il Destinatario");
        }
        if (item.getPubblicazione() == null) {
        	throw new UnsupportedOperationException("Selezionare la Pubblicazione");
        }
        if (item.getId() == null && item.getAbbonamento().getId() == null) {
            t.addItem(item);
            return save(t);
        }
        if (item.getId() == null ) {
            t.getItems().clear();
            t.addItem(item);
            smdService.genera(t);
        } else {
        	RivistaAbbonamento persisted = itemRepository.findById(item.getId()).get();
        	smdService.aggiorna(persisted,item.getNumero(),item.getTipoAbbonamentoRivista());
        }
        return findById(t.getId());
	}

	public void incassa(Abbonamento entity, String incassato, UserInfo user ) throws Exception { 
    	if (incassato == null) {
    		throw new UnsupportedOperationException("Devi inserire l'importo da incassare");
    	}
    	if (entity.getDataContabile() == null) {
    		throw new UnsupportedOperationException("Devi inserire la data contabile");
    	}
    	if (entity.getDataPagamento() == null) {
    		throw new UnsupportedOperationException("Devi inserire la data pagamento");
    	}
    	if (entity.getProgressivo() == null) {
    		throw new UnsupportedOperationException("Aggiungere Riferimento nel Campo Progressivo");
    	}
    	
    	try {
    		new BigDecimal(incassato);
    	} catch (Exception e) {
    		throw new UnsupportedOperationException("Incassato non è un valore accettabile " + incassato);
		}

        DistintaVersamento incasso = 
                incassoDao
                    .findByDataContabileAndCassaAndCcpAndCuas(
                    		entity.getDataContabile(),
                    		entity.getCassa(),
                    		entity.getCcp(),
                    		entity.getCuas()
                            );
        if (incasso == null) {
            incasso = new DistintaVersamento();
            incasso.setDataContabile(entity.getDataContabile());
            incasso.setCassa(entity.getCassa());
            incasso.setCcp(entity.getCcp());
            incasso.setCuas(entity.getCuas());
            incassoDao.save(incasso);
        }
        Versamento versamento = new Versamento(incasso,new BigDecimal(incassato));
        versamento.setCodeLine(entity.getCodeLine());
        versamento.setProgressivo(entity.getProgressivo());
        versamento.setDataPagamento(entity.getDataPagamento());
        versamentoDao.save(versamento);
        Smd.calcoloImportoIncasso(incasso,
                                  versamentoDao.findByDistintaVersamento(incasso));
        incassoDao.save(incasso);
        smdService.incassa(entity, versamento,user,"Incassato da Abbonamento");
	}
	
	public List<OperazioneIncasso> getOperazioneIncassoAssociate(Abbonamento abbonamento) {
    	return operazioneIncassoDao.findByAbbonamento(abbonamento);
	}
	
	public List<RivistaAbbonamento> findByTipo(TipoAbbonamentoRivista tec) {
		return itemRepository.findByTipoAbbonamentoRivista(tec);
	}

	public List<RivistaAbbonamento> findByPubblicazione(Pubblicazione pubblicazione) {
		return itemRepository.findByPubblicazione(pubblicazione);
	}

	public List<RivistaAbbonamento> findAllItems() {
		return itemRepository.findAll();
	}

    private List<Abbonamento> addByDestinatario(Anagrafica destinatario, List<Abbonamento> found) {
        final Map<Long,Abbonamento> abbMap = 
        		found.stream().collect(Collectors.toMap(Abbonamento::getId, Function.identity()));
         
         itemRepository.findByDestinatario(destinatario)
             .stream()
             .filter(ec -> !abbMap.containsKey(ec.getAbbonamento().getId()))
             .forEach( ec -> {
                 Abbonamento  abb = findById(ec.getAbbonamento().getId());
                 abbMap.put(abb.getId(), abb);
             });        
         return abbMap.values().stream().collect(Collectors.toList());
     }

    private List<Abbonamento> filterBy(TipoAbbonamentoRivista t, Pubblicazione p,StatoAbbonamento s, List<Abbonamento> abbonamenti) {
    	if (abbonamenti.size() == 0)
    		return abbonamenti;
    	if (t == null && p == null && s == null)
    		return abbonamenti;
    	List<Long> approved;
    	if (t == null && s == null)
            approved = itemRepository
            .findByPubblicazione(p)
            .stream().map( ec -> ec.getAbbonamento().getId()).collect(Collectors.toList());
    	else if (p == null && s == null)
    		approved = itemRepository
            .findByTipoAbbonamentoRivista(t)
            .stream().map( ec -> ec.getAbbonamento().getId()).collect(Collectors.toList());
    	else if (p == null && t == null)
    		approved = itemRepository
            .findByStatoAbbonamento(s)
            .stream().map( ec -> ec.getAbbonamento().getId()).collect(Collectors.toList());
    	else if (s == null)
    		approved = itemRepository
                .findByPubblicazioneAndTipoAbbonamentoRivista(p, t)
                .stream().map( ec -> ec.getAbbonamento().getId()).collect(Collectors.toList());
    	else if (t == null)
    		approved = itemRepository
                .findByPubblicazioneAndStatoAbbonamento(p, s)
                .stream().map( ec -> ec.getAbbonamento().getId()).collect(Collectors.toList());
    	else if (p == null)
    		approved = itemRepository
                .findByTipoAbbonamentoRivistaAndStatoAbbonamento(t, s)
                .stream().map( ec -> ec.getAbbonamento().getId()).collect(Collectors.toList());
    	else 
    		approved = itemRepository
            .findByPubblicazioneAndTipoAbbonamentoRivistaAndStatoAbbonamento(p,t, s)
            .stream().map( ec -> ec.getAbbonamento().getId()).collect(Collectors.toList());

        return abbonamenti.stream().filter(abb -> approved.contains(abb.getId())).collect(Collectors.toList());
    }

	@Override
	public List<Abbonamento> searchBy(Campagna campagna, Anagrafica customer, Anno anno, Pubblicazione p, TipoAbbonamentoRivista t, StatoAbbonamento s) {
		List<Abbonamento> abbonamenti;
        if (campagna == null && customer == null && anno == null) {
            abbonamenti = findAll();            
        } else if (campagna == null && anno == null) {
        	abbonamenti = addByDestinatario(customer, repository.findByIntestatario(customer));
        } else if (customer == null && anno == null) {
        	abbonamenti = repository.findByCampagna(campagna);
        } else if (customer == null && campagna == null) {
        	abbonamenti = repository.findByAnno(anno);
        } else if (anno == null) {
        	abbonamenti = addByDestinatario(customer,repository.findByIntestatarioAndCampagna(customer, campagna));
        } else  if (campagna == null) {
           abbonamenti = addByDestinatario(customer,repository.findByIntestatarioAndAnno(customer, anno));
        } else if (customer == null) {
        	abbonamenti = repository.findByCampagnaAndAnno(campagna,anno);
        } else {
        	abbonamenti =  addByDestinatario(customer,repository.findByIntestatarioAndCampagnaAndAnno(customer, campagna, anno));
        }
        
        return filterBy(t, p, s, abbonamenti);
	}

	@Override
	public List<Abbonamento> searchBy(Anagrafica tValue, Anno sValue) throws Exception {
    	if (tValue == null) {
    		throw new UnsupportedOperationException("Anagrafica deve essere valorizzata");
    	}
    	if (sValue == null) {
    		throw new UnsupportedOperationException("Anno deve essere valorizzato");
    	}

		return repository.findByIntestatarioAndAnno(tValue, sValue);
	}

}
