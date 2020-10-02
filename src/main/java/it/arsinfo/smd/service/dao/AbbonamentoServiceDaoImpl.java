package it.arsinfo.smd.service.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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
import it.arsinfo.smd.data.Incassato;
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

    private List<Abbonamento> filterBy(Anagrafica dst, TipoAbbonamentoRivista tipo, Pubblicazione p,StatoAbbonamento stato, List<Abbonamento> abbonamenti) {
    	if (abbonamenti.size() == 0)
    		return abbonamenti;
    	if (dst == null && tipo == null && p == null && stato == null)
    		return abbonamenti;
    	List<Long> approved;
    	if (dst == null && tipo == null && stato == null)
            approved = itemRepository
            .findByPubblicazione(p)
            .stream().map( ec -> ec.getAbbonamento().getId()).collect(Collectors.toList());
    	else if (dst == null && p == null && stato == null)
    		approved = itemRepository
            .findByTipoAbbonamentoRivista(tipo)
            .stream().map( ec -> ec.getAbbonamento().getId()).collect(Collectors.toList());
    	else if (dst == null && p == null && tipo == null)
    		approved = itemRepository
            .findByStatoAbbonamento(stato)
            .stream().map( ec -> ec.getAbbonamento().getId()).collect(Collectors.toList());
    	else if (stato == null && p == null && tipo == null)
    		approved = itemRepository
            .findByDestinatario(dst)
            .stream().map( ec -> ec.getAbbonamento().getId()).collect(Collectors.toList());
    	else if (dst == null && stato == null)
    		approved = itemRepository
                .findByPubblicazioneAndTipoAbbonamentoRivista(p, tipo)
                .stream().map( ec -> ec.getAbbonamento().getId()).collect(Collectors.toList());
    	else if (p == null && stato == null)
    		approved = itemRepository
                .findByDestinatarioAndTipoAbbonamentoRivista(dst, tipo)
                .stream().map( ec -> ec.getAbbonamento().getId()).collect(Collectors.toList());
    	else if (tipo == null && stato == null)
    		approved = itemRepository
                .findByDestinatarioAndPubblicazione(dst, p)
                .stream().map( ec -> ec.getAbbonamento().getId()).collect(Collectors.toList());
    	else if (p == null && tipo == null)
    		approved = itemRepository
                .findByDestinatarioAndStatoAbbonamento(dst, stato)
                .stream().map( ec -> ec.getAbbonamento().getId()).collect(Collectors.toList());
    	else if (dst == null && tipo == null)
    		approved = itemRepository
                .findByPubblicazioneAndStatoAbbonamento(p, stato)
                .stream().map( ec -> ec.getAbbonamento().getId()).collect(Collectors.toList());
    	else if (dst == null && p == null)
    		approved = itemRepository
                .findByTipoAbbonamentoRivistaAndStatoAbbonamento(tipo, stato)
                .stream().map( ec -> ec.getAbbonamento().getId()).collect(Collectors.toList());
    	else if (dst == null )
    		approved = itemRepository
            .findByPubblicazioneAndTipoAbbonamentoRivistaAndStatoAbbonamento(p,tipo, stato)
            .stream().map( ec -> ec.getAbbonamento().getId()).collect(Collectors.toList());
    	else if (p == null )
    		approved = itemRepository
            .findByDestinatarioAndTipoAbbonamentoRivistaAndStatoAbbonamento(dst,tipo, stato)
            .stream().map( ec -> ec.getAbbonamento().getId()).collect(Collectors.toList());
    	else if (tipo == null )
    		approved = itemRepository
            .findByDestinatarioAndPubblicazioneAndStatoAbbonamento(dst,p, stato)
            .stream().map( ec -> ec.getAbbonamento().getId()).collect(Collectors.toList());
    	else if (stato == null )
    		approved = itemRepository
            .findByDestinatarioAndPubblicazioneAndTipoAbbonamentoRivista(dst,p, tipo)
            .stream().map( ec -> ec.getAbbonamento().getId()).collect(Collectors.toList());
    	else 
    		approved = itemRepository
            .findByDestinatarioAndPubblicazioneAndTipoAbbonamentoRivistaAndStatoAbbonamento(dst,p,tipo, stato)
            .stream().map( ec -> ec.getAbbonamento().getId()).collect(Collectors.toList());
        return abbonamenti.stream().filter(abb -> approved.contains(abb.getId())).collect(Collectors.toList());
    }

	@Override
	public List<Abbonamento> searchBy(
			Campagna campagna, 
			Anagrafica intestatario, 
			Anagrafica beneficiario, 
			Anno anno, 
			Pubblicazione p, 
			TipoAbbonamentoRivista t, 
			StatoAbbonamento s, 
			Incassato inc,
			String searchCodeLine,
			boolean checkContrassegno,
			boolean checkResiduo,
			boolean checkNotResiduo,
			boolean checkResiduoZero
			) 
	{
		List<Abbonamento> abbonamenti;
        if (campagna == null && intestatario == null && anno == null) {
            abbonamenti = findAll();            
        } else if (campagna == null && anno == null) {
        	abbonamenti = repository.findByIntestatario(intestatario);
        } else if (intestatario == null && anno == null) {
        	abbonamenti = repository.findByCampagna(campagna);
        } else if (intestatario == null && campagna == null) {
        	abbonamenti = repository.findByAnno(anno);
        } else if (anno == null) {
        	abbonamenti = repository.findByIntestatarioAndCampagna(intestatario, campagna);
        } else  if (campagna == null) {
           abbonamenti = repository.findByIntestatarioAndAnno(intestatario, anno);
        } else if (intestatario == null) {
        	abbonamenti = repository.findByCampagnaAndAnno(campagna,anno);
        } else {
        	abbonamenti = repository.findByIntestatarioAndCampagnaAndAnno(intestatario, campagna, anno);
        }
        if (inc != null) {
        	abbonamenti = abbonamenti.stream().filter(a -> inc == Smd.getStatoIncasso(a)).collect(Collectors.toList());
        }
        if (!StringUtils.isEmpty(searchCodeLine)) {
            abbonamenti=abbonamenti.stream().filter(a -> a.getCodeLine().toLowerCase().contains(searchCodeLine.toLowerCase())).collect(Collectors.toList());                  
        }
        if (checkContrassegno) {
            abbonamenti=abbonamenti.stream().filter(a -> a.isContrassegno() == checkContrassegno).collect(Collectors.toList());      
        }
        if (checkResiduo) {
            abbonamenti=abbonamenti.stream().filter(a -> a.getResiduo().signum() >0).collect(Collectors.toList());      
        }        
        if (checkNotResiduo) {
            abbonamenti=abbonamenti.stream().filter(a -> a.getResiduo().signum() <0).collect(Collectors.toList());      
        }        
        if (checkResiduoZero) {
            abbonamenti=abbonamenti.stream().filter(a -> a.getResiduo().signum() == 0).collect(Collectors.toList());      
        }        

        return filterBy(beneficiario,t, p, s, abbonamenti);
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
