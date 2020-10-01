package it.arsinfo.smd.service.dao;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import it.arsinfo.smd.dao.SmdService;
import it.arsinfo.smd.dao.VersamentoServiceDao;
import it.arsinfo.smd.dao.repository.AbbonamentoDao;
import it.arsinfo.smd.dao.repository.AnagraficaDao;
import it.arsinfo.smd.dao.repository.DocumentiTrasportoCumulatiDao;
import it.arsinfo.smd.dao.repository.DocumentoTrasportoDao;
import it.arsinfo.smd.dao.repository.OffertaDao;
import it.arsinfo.smd.dao.repository.OfferteCumulateDao;
import it.arsinfo.smd.dao.repository.OperazioneIncassoDao;
import it.arsinfo.smd.dao.repository.VersamentoDao;
import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.DocumentiTrasportoCumulati;
import it.arsinfo.smd.entity.DocumentoTrasporto;
import it.arsinfo.smd.entity.Offerta;
import it.arsinfo.smd.entity.OfferteCumulate;
import it.arsinfo.smd.entity.OperazioneIncasso;
import it.arsinfo.smd.entity.UserInfo;
import it.arsinfo.smd.entity.Versamento;
import it.arsinfo.smd.service.Smd;

@Service
public class VersamentoServiceDaoImpl implements VersamentoServiceDao {

    @Autowired
    private VersamentoDao repository;

    @Autowired
    private AnagraficaDao anagraficaDao;

    @Autowired
    private AbbonamentoDao abbonamentoDao;

    @Autowired
    private OperazioneIncassoDao operazioneIncassoDao;

    @Autowired
    private OffertaDao offertaDao;
    @Autowired
    private OfferteCumulateDao offerteCumulateDao;

    @Autowired
    private DocumentoTrasportoDao documentoTrasportoDao;
    @Autowired
    private DocumentiTrasportoCumulatiDao documentiTrasportoCumulatiDao;

    @Autowired
    private SmdService smdService;

	@Override
	public Versamento save(Versamento entity) throws Exception {
		return repository.save(entity);
	}

	@Override
	public void delete(Versamento entity) throws Exception {
		repository.delete(entity);
	}

	@Override
	public Versamento findById(Long id) {
		Versamento v = repository.findById(id).get();
		if (v.getCommittente() != null) {
			return Smd.getWithAnagrafica(v, anagraficaDao.findById(v.getCommittente().getId()).get());
		}

		return v;
	}

	@Override
	public List<Versamento> findAll() {
      	return Smd.getWithAnagrafiche(repository.findAll(), anagraficaDao.findAll());
	}

	public VersamentoDao getRepository() {
		return repository;
	}

	public List<Versamento> searchBy(String importo, LocalDate dataContabile, LocalDate dataPagamento,
			String codeLine) {
        if (!StringUtils.isEmpty(importo)) {
            try {
                new BigDecimal(importo);
            } catch (NumberFormatException e) {
                return new ArrayList<>();
            }
        }

        if (StringUtils.isEmpty(importo) && dataContabile == null
                && dataPagamento == null && StringUtils.isEmpty(codeLine)) {
            return findAll();
        }
        List<Versamento> vs;
        if (dataContabile == null && dataPagamento == null && StringUtils.isEmpty(codeLine)) {
                vs =  repository
                    .findByImporto(new BigDecimal(importo));
        } else if (dataContabile == null && dataPagamento == null && StringUtils.isEmpty(importo)) {
            vs =  repository
                    .findByCodeLineContainingIgnoreCase(codeLine);
        } else if (StringUtils.isEmpty(importo) && dataPagamento == null && StringUtils.isEmpty(codeLine)) {
            vs =  repository
                    .findByDataContabile(Smd.getStandardDate(dataContabile));
        } else  if (StringUtils.isEmpty(importo) && dataContabile == null && StringUtils.isEmpty(codeLine)) {
            vs =  repository
                    .findByDataPagamento(Smd.getStandardDate(dataPagamento));
        } else if (dataContabile == null && dataPagamento == null) {
            vs =  repository
                    .findByCodeLineContainingIgnoreCase(codeLine)
                    .stream()
                    .filter(v-> v.getImporto().compareTo(new BigDecimal(importo)) == 0)
                    .collect(Collectors.toList());
        } else if (dataContabile == null && StringUtils.isEmpty(codeLine)) {
            vs =  repository
                    .findByImporto(new BigDecimal(importo))
                    .stream()
                    .filter(v -> v.getDataPagamento().getTime() == Smd.getStandardDate(dataPagamento).getTime())
                    .collect(Collectors.toList());
        } else if (dataPagamento == null && StringUtils.isEmpty(codeLine)) {
            vs =  repository
                    .findByImporto(new BigDecimal(importo))
                    .stream()
                    .filter(v -> v.getDataContabile().getTime() == Smd.getStandardDate(dataContabile).getTime())
                    .collect(Collectors.toList());
        } else if (dataContabile == null && StringUtils.isEmpty(importo)) {
            vs =  repository
                    .findByCodeLineContainingIgnoreCase(codeLine)
                    .stream()
                    .filter(v -> v.getDataPagamento().getTime() == Smd.getStandardDate(dataPagamento).getTime())
                    .collect(Collectors.toList());
        } else if (dataPagamento == null && StringUtils.isEmpty(importo)) {
            vs =  repository
                    .findByCodeLineContainingIgnoreCase(codeLine)
                    .stream()
                    .filter(v -> v.getDataContabile().getTime() == Smd.getStandardDate(dataContabile).getTime())
                    .collect(Collectors.toList());
        } else if (StringUtils.isEmpty(codeLine) && StringUtils.isEmpty(importo)) {
            vs =  repository
                    .findByDataPagamento(Smd.getStandardDate(dataPagamento))
                    .stream()
                    .filter(v -> v.getDataContabile().getTime() == Smd.getStandardDate(dataContabile).getTime())
                    .collect(Collectors.toList());
        } else if (StringUtils.isEmpty(codeLine)) {
            vs =  repository
                    .findByImporto(new BigDecimal(importo))
                    .stream()
                    .filter(v -> 
                       v.getDataContabile().getTime() == Smd.getStandardDate(dataContabile).getTime()
                    && v.getDataPagamento().getTime() == Smd.getStandardDate(dataPagamento).getTime())
                    .collect(Collectors.toList());
        } else if (StringUtils.isEmpty(importo)) {
            vs =  repository
                    .findByCodeLineContainingIgnoreCase(codeLine)
                    .stream()
                    .filter(v -> 
                       v.getDataContabile().getTime() == Smd.getStandardDate(dataContabile).getTime()
                    && v.getDataPagamento().getTime() == Smd.getStandardDate(dataPagamento).getTime())
                    .collect(Collectors.toList());
        } else if (dataPagamento == null) {
            vs =  repository
                    .findByCodeLineContainingIgnoreCase(codeLine)
                    .stream()
                    .filter(v -> 
                       v.getDataContabile().getTime() == Smd.getStandardDate(dataContabile).getTime()
                    && v.getImporto().compareTo(new BigDecimal(importo)) == 0 )
                    .collect(Collectors.toList());
            
        } else if (dataContabile == null) {
            vs =  repository
                    .findByCodeLineContainingIgnoreCase(codeLine)
                    .stream()
                    .filter(v -> 
                       v.getDataPagamento().getTime() == Smd.getStandardDate(dataPagamento).getTime()
                    && v.getImporto().compareTo(new BigDecimal(importo)) == 0 )
                    .collect(Collectors.toList());
            
        } else {
        	vs =  repository
                .findByCodeLineContainingIgnoreCase(codeLine)
                .stream()
                .filter(v -> 
                   v.getDataContabile().getTime() == Smd.getStandardDate(dataContabile).getTime()
                && v.getDataPagamento().getTime() == Smd.getStandardDate(dataPagamento).getTime()
                && v.getImporto().compareTo(new BigDecimal(importo)) == 0 )
                .collect(Collectors.toList());
        }
        return Smd.getWithAnagrafiche(vs, anagraficaDao.findAll());
	}

	@Override
	public void associaCommittente(Anagrafica committente, Versamento versamento) {
		versamento.setCommittente(committente);
		repository.save(versamento);		
	}

	@Override
	public void rimuoviCommittente(Versamento versamento) {
		if (versamento.getCommittente() == null) {
			return;
		}
		versamento.setCommittente(null);
		repository.save(versamento);		
	}

	@Override
	public List<OperazioneIncasso> getAssociati(Versamento versamento) {
    	return operazioneIncassoDao.findByVersamento(versamento);
	}

	@Override
	public List<Abbonamento> getAssociabili(Versamento versamento) {
        if (versamento == null || versamento.getResiduo().signum() == 0) {
        	return new ArrayList<>();
        }

        return abbonamentoDao
        .findAll()
        .stream()
        .filter(abb -> 
            abb.getResiduo().signum() > 0 
            ).collect(Collectors.toList());       
	}

	@Override
	@Transactional
	public void storna(OperazioneIncasso operazioneIncasso, UserInfo loggedInUser, String string) throws Exception {
		smdService.storna(operazioneIncasso, loggedInUser, string);
	}

	@Override
	@Transactional
	public void incassa(Abbonamento abbonamento, Versamento selected, UserInfo loggedInUser, String string) throws Exception {
		smdService.incassa(abbonamento,selected,loggedInUser, string);		
	}

	@Override
	public Anagrafica findCommittente(Versamento selected) {
		return anagraficaDao.findById(selected.getCommittente().getId()).get();
	}

	@Override
	@Transactional
	public void storna(DocumentoTrasporto ddt, UserInfo loggedInUser) throws Exception {
		smdService.storna(ddt, loggedInUser);
	}

	@Override
	@Transactional
	public void storna(Offerta offerta, UserInfo loggedInUser) throws Exception {
		smdService.storna(offerta, loggedInUser);
	}

	@Override
	@Transactional
	public void incassaOfferta(String importo,Anno anno, Versamento selected, UserInfo loggedInUser, Anagrafica committente) throws Exception {
		if (importo == null) {
			throw new UnsupportedOperationException("Selezionare Importo");
		}
		try {
			new BigDecimal(importo);
		} catch (Exception e) {
			throw new UnsupportedOperationException("formato importo non corretto " + importo);
		}
		if (anno == null) {
			throw new UnsupportedOperationException("Selezionare Anno");
		}
		if (selected == null) {
			throw new UnsupportedOperationException("Selezionare Versamento");
		}
		if (loggedInUser == null) {
			throw new UnsupportedOperationException("loggedInUser null!");
		}
		if (committente == null) {
			throw new UnsupportedOperationException("Selezionare Committente");
		}
		
		OfferteCumulate offerteAnno = offerteCumulateDao.findByAnno(anno);
		if (offerteAnno == null) {
			offerteAnno = new OfferteCumulate();
			offerteAnno.setAnno(anno);
			offerteCumulateDao.save(offerteAnno);
		}
		smdService.incassa(new BigDecimal(importo),offerteAnno,selected,loggedInUser, committente);				
	}

	@Override
	@Transactional
	public void incassaDdt(String ddt,String importo,Anno anno, Versamento selected, UserInfo loggedInUser, Anagrafica committente) throws Exception {
		if (importo == null) {
			throw new UnsupportedOperationException("Selezionare Importo");
		}
		try {
			new BigDecimal(importo);
		} catch (Exception e) {
			throw new UnsupportedOperationException("formato importo non corretto " + importo);
		}
		if (anno == null) {
			throw new UnsupportedOperationException("Selezionare Anno");
		}
		if (selected == null) {
			throw new UnsupportedOperationException("Selezionare Versamento");
		}
		if (loggedInUser == null) {
			throw new UnsupportedOperationException("loggedInUser null!");
		}
		if (committente == null) {
			throw new UnsupportedOperationException("Selezionare Committente");
		}
		
		DocumentiTrasportoCumulati ddtAnno = documentiTrasportoCumulatiDao.findByAnno(anno);
		if (ddtAnno == null) {
			ddtAnno = new DocumentiTrasportoCumulati();
			ddtAnno.setAnno(anno);
			documentiTrasportoCumulatiDao.save(ddtAnno);
		}
		smdService.incassa(ddt,new BigDecimal(importo),ddtAnno,selected,loggedInUser, committente);				
	}

	@Override
	public List<Offerta> getOfferte(Versamento selected) {
		return offertaDao.findByVersamento(selected);
	}

	@Override
	public List<Versamento> searchBy(Anagrafica tValue, Anno anno) throws Exception {
    	if (tValue == null) {
    		throw new UnsupportedOperationException("Anagrafica deve essere valorizzata");
    	}
    	if (anno == null) {
    		throw new UnsupportedOperationException("Anno deve essere valorizzato");
    	}
    	SimpleDateFormat dateFor = new SimpleDateFormat("dd/MM/yyyy");
    	Date start = dateFor.parse("01/01/"+Anno.getAnnoPrecedente(anno).getAnnoAsString());
    	Date end = dateFor.parse("01/01/"+Anno.getAnnoSuccessivo(anno).getAnnoAsString());
		List<Versamento> versamenti = repository.findByCommittente(tValue)
				.stream().filter(v -> v.getDataContabile().after(start) && v.getDataContabile().before(end))
				.collect(Collectors.toList());
		return Smd.getWithAnagrafiche(versamenti, anagraficaDao.findAll());
	}

	@Override
	public List<DocumentoTrasporto> getDocumentiTrasporto(Versamento selected) {
		return documentoTrasportoDao.findByVersamento(selected);
	}	
}
