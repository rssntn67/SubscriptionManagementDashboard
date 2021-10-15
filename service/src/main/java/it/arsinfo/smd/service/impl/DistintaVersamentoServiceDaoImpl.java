package it.arsinfo.smd.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import it.arsinfo.smd.dao.*;
import it.arsinfo.smd.entity.*;
import it.arsinfo.smd.service.Smd;
import it.arsinfo.smd.service.dto.IncassoGiornaliero;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.arsinfo.smd.service.api.DistintaVersamentoService;
import it.arsinfo.smd.service.api.SmdService;

@Service
public class DistintaVersamentoServiceDaoImpl implements DistintaVersamentoService {

    private static final Logger log = LoggerFactory.getLogger(DistintaVersamentoService.class);

    @Autowired
    private DistintaVersamentoDao repository;

    @Autowired
    private VersamentoDao versamentoDao;

    @Autowired
    private AbbonamentoDao abbonamentoDao;

    @Autowired
    private RivistaAbbonamentoDao rivistaAbbonamentoDao;

    @Autowired
    private AnagraficaDao anagraficaDao;

    @Autowired
    private OperazioneIncassoDao operazioneIncassoDao;

    @Autowired
    private SmdService smdService;

	@Override
	public DistintaVersamento save(DistintaVersamento entity) throws Exception {
        if (entity.getId() == null && entity.getItems().isEmpty()) {
            throw new UnsupportedOperationException("Aggiungere Versamenti Prima di Salvare");
        }
        if (entity.getDataContabile().after(SmdEntity.getStandardDate(new Date()))) {
        	throw new UnsupportedOperationException("Non si può selezionare una data contabile futuro");
        }       
        repository.save(entity);
        entity.getItems().stream().filter(v -> v.getId() == null).forEach(vers -> {
        	versamentoDao.save(vers);
            log.info("save: {}", vers);        	
        });

		return entity;
	}

	@Override
	public void delete(DistintaVersamento entity) throws Exception {
		repository.delete(entity);
	}

	@Override
	public DistintaVersamento findById(Long id) {
		return repository.findById(id).orElse(null);
	}

	@Override
	public List<DistintaVersamento> findAll() {
		return repository.findAll();
	}

    @Override
    public List<DistintaVersamento> searchByDefault() {
        return new ArrayList<>();
    }

    @Override
    public DistintaVersamento add() {
        return new DistintaVersamento();
	}

    public DistintaVersamentoDao getRepository() {
		return repository;
	}

	@Override
	public List<DistintaVersamento> searchBy(Cuas cuas, LocalDate dataContabile, Cassa cassa, Ccp ccp) {
        if (cuas == null && dataContabile == null && cassa == null && ccp == null) {
            return findAll();
        }
        
        if (dataContabile == null && cassa == null && ccp == null) {
            return repository.findByCuas(cuas);
        }
        if (dataContabile == null && cuas == null && ccp == null) {
            return repository.findByCassa(cassa);
        }
        if (dataContabile == null && cuas == null && cassa == null) {
            return repository.findByCcp(ccp);
        }
        if (cuas == null && cassa == null && ccp == null) {
            return repository
                    .findByDataContabile(SmdEntity.getStandardDate(dataContabile));
        }
        
        if (dataContabile == null && ccp == null) {
            return repository.findByCassa(cassa)
                    .stream()
                    .filter(inc -> inc.getCuas() == cuas)
                    .collect(Collectors.toList());
        }
        if (dataContabile == null && cuas == null) {
            return repository.findByCassa(cassa)
                    .stream()
                    .filter(inc -> inc.getCcp() == ccp)
                    .collect(Collectors.toList());
        }
        if (dataContabile == null && cassa == null) {
            return repository.findByCuas(cuas)
                    .stream()
                    .filter(inc -> inc.getCcp() == ccp)
                    .collect(Collectors.toList());
        }
            
        if (cassa == null && ccp == null) {
            return repository
                    .findByDataContabile(SmdEntity.getStandardDate(dataContabile))
                    .stream()
                    .filter(inc -> inc.getCuas() == cuas)
                    .collect(Collectors.toList());
        }
        if (cuas == null && ccp == null) {
            return repository
                    .findByDataContabile(SmdEntity.getStandardDate(dataContabile))
                    .stream()
                    .filter(inc -> inc.getCassa() == cassa)
                    .collect(Collectors.toList());
        }
        if (cassa == null && cuas == null) {
            return repository
                    .findByDataContabile(SmdEntity.getStandardDate(dataContabile))
                    .stream()
                    .filter(inc -> inc.getCcp() == ccp)
                    .collect(Collectors.toList());
        }
        if (cassa == null) {
            return repository
                    .findByDataContabile(SmdEntity.getStandardDate(dataContabile))
                    .stream()
                    .filter(inc -> inc.getCuas() == cuas && inc.getCcp() == ccp)
                    .collect(Collectors.toList());
        }
        if (cuas == null) {
            return repository
                    .findByDataContabile(SmdEntity.getStandardDate(dataContabile))
                    .stream()
                    .filter(inc -> inc.getCassa() == cassa && inc.getCcp() == ccp)
                    .collect(Collectors.toList());
        }
        if (ccp == null) {
            return repository
                    .findByDataContabile(SmdEntity.getStandardDate(dataContabile))
                    .stream()
                    .filter(inc -> inc.getCuas() == cuas && inc.getCassa() == cassa)
                    .collect(Collectors.toList());
        }

        assert dataContabile != null;
        return repository
                .findByDataContabile(SmdEntity.getStandardDate(dataContabile))
                .stream()
                .filter(inc -> inc.getCassa() == cassa && inc.getCuas() == cuas && inc.getCcp() == ccp)            
                .collect(Collectors.toList());
	}

	@Override
	public List<Versamento> incassaCodeLine(List<DistintaVersamento> find, UserInfo loggedInUser) throws Exception {
    	Map<Long, Versamento> versamentoMap = new HashMap<>();
    	for (DistintaVersamento incasso:find) {
    		if (incasso.getResiduo().signum() == 0) {
    			continue;
    		}
    		for (Versamento v: versamentoDao.findByDistintaVersamento(incasso)) {
    			if (v.getResiduo().signum() == 0) {
    				continue;
    			}
    			final Abbonamento abbonamento = abbonamentoDao.findByCodeLine(v.getCodeLine());
    			if (abbonamento != null && abbonamento.getResiduo().signum() > 0 ) {
    				smdService.incassa(abbonamento,v,loggedInUser,"Incassato con CodeLine");
    				versamentoMap.put(v.getId(), v);
    			}
    		}
		}
    	return new ArrayList<>(versamentoMap.values());
	}

    @Override
    public List<IncassoGiornaliero> getIncassiGiornaliero(LocalDate from, LocalDate to) {
        log.info("getIncassiGiornaliero: start");
	    final Map<Integer,IncassoGiornaliero> incassimap = new HashMap<>();
        final Map<Long,Integer> incassoToHashMap = new HashMap<>();
	    final Set<Long> incassoids = new HashSet<>();
        log.info("getIncassiGiornaliero: start find distinta");
	    repository.findByDataContabileBetween(SmdEntity.getStandardDate(from),SmdEntity.getStandardDate(to)).forEach(distinta -> {
	        incassoids.add(distinta.getId());
	        IncassoGiornaliero ig = new IncassoGiornaliero(distinta.getDataContabile(),distinta.getCassa(),distinta.getCcp());
            incassoToHashMap.put(distinta.getId(),ig.hashCode());
	        if (incassimap.containsKey(ig.hashCode())) {
	            ig = incassimap.get(ig.hashCode());
            } else {
	            incassimap.put(ig.hashCode(),ig);
            }
	        ig.setImporto(ig.getImporto().add(distinta.getImporto()));
	        ig.setIncassato(ig.getIncassato().add(distinta.getIncassato()));
	        ig.setResiduo(ig.getResiduo().add(distinta.getResiduo()));
        });
        log.info("getIncassiGiornaliero: find {} distinta", incassoids.size());
	    if (incassoids.size() == 0) {
	        return new ArrayList<>();
        }
        log.info("getIncassiGiornaliero: end find distinta");
	    final Set<Long> abbonamentoids = new HashSet<>();
        log.info("getIncassiGiornaliero: start find abbonamento");
	    operazioneIncassoDao.
                findByDataAfter(SmdEntity.getStandardDate(from)).
                stream().
                filter(operazione -> incassoids.contains(operazione.getVersamento().getDistintaVersamento().getId())).
                forEach(op -> {
                    IncassoGiornaliero ig = incassimap.get(incassoToHashMap.get(op.getVersamento().getDistintaVersamento().getId()));
                    log.info("getIncassiGiornaliero: {}", ig);
                    Abbonamento abb = op.getAbbonamento();
                    if (!abbonamentoids.contains(abb.getId())) {
                        for (RivistaAbbonamento ec: rivistaAbbonamentoDao.findByAbbonamento(abb)) {
                            log.info("getIncassiGiornaliero: {}", ec);
                            switch (ec.getPubblicazione().getNome()) {
                                case "Messaggio":
                                    ig.setMessaggio(ig.getMessaggio().add(ec.getImporto()));
                                    break;
                                case "Lodare":
                                    ig.setLodare(ig.getLodare().add(ec.getImporto()));
                                    break;
                                case "Blocchetti":
                                    ig.setBlocchetti(ig.getBlocchetti().add(ec.getImporto()));
                                    break;
                                case "Estratti":
                                    ig.setManifesti(ig.getManifesti().add(ec.getImporto()));
                                    break;
                            }

                        }
                        abbonamentoids.add(abb.getId());
                    }
                });
        log.info("getIncassiGiornaliero: end find abbonamento");

        incassimap.values()
                .forEach(incasso -> incasso.setNonAssociati(incasso.getIncassato().subtract(incasso.getManifesti()).subtract(incasso.getMessaggio()).subtract(incasso.getBlocchetti()).subtract(incasso.getLodare())));
        log.info("getIncassiGiornaliero: end");
        return new ArrayList<>(incassimap.values());
    }

    @Override
	public List<Versamento> getItems(DistintaVersamento t) {
        List<Versamento> versamenti = versamentoDao.findByDistintaVersamento(t);
      	Set<Long> ids = new HashSet<>();
        for (Versamento v: versamenti) {
    		if (v.getCommittente() != null) {
    			ids.add(v.getCommittente().getId());
    		}
    	}
      	return VersamentoServiceDaoImpl.getWithAnagrafiche(versamentoDao.findByDistintaVersamento(t), anagraficaDao.findAllById(ids));
	}

	@Override
	public DistintaVersamento deleteItem(DistintaVersamento t, Versamento item) throws UnsupportedOperationException {
        if (t.getId() == null) {
            t.removeItem(item);
            Smd.calcoloImportoIncasso(t);
            return t;
        } 
        log.info("deleteItem: {}", item);
        if (item.getDistintaVersamento() == null || item.getDistintaVersamento().getId() == null ) {
        	log.warn("deleteItem: Versamento: incasso non esistente, {} ", item);
            throw new UnsupportedOperationException("delete: Versamento: incasso non esistente" );
        	
    	}
    	if (item.getIncassato().signum() != 0) {
        	log.warn("deleteItem: Versamento: non posso cancellare un versamento incassato, {} ", item);
            throw new UnsupportedOperationException("delete: Versamento: non posso calcellare un versamento incassato");
        }
        versamentoDao.delete(item);
    	DistintaVersamento incasso = findById(item.getDistintaVersamento().getId());
        List<Versamento> versamenti = versamentoDao.findByDistintaVersamento(incasso);
        if (versamenti.size() == 0) {
            repository.delete(incasso);
        } else {
            Smd.calcoloImportoIncasso(incasso,versamenti);
            repository.save(incasso);
        }                
		return t;
	}

	@Override
	public DistintaVersamento saveItem(DistintaVersamento t, Versamento item) throws UnsupportedOperationException {
        if (item.getImporto().compareTo(BigDecimal.ZERO) <= 0) {
            throw new UnsupportedOperationException("L'Importo del Versamento non deve essere ZERO");
        }
        if (item.getDataPagamento().after(item.getDataContabile())) {
        	throw new UnsupportedOperationException("La data di pagamento deve  essere anteriore alla data contabile");
        }
        if (t.getId() == null) {
            t.addItem(item);
            Smd.calcoloImportoIncasso(t);
            return t;
        } 
        log.info("saveItem: {}", item);
        if (item.getDistintaVersamento() == null || item.getDistintaVersamento().getId() == null ) {
        	log.warn("saveItem: Versamento: incasso non esistente, {} ", item);
            throw new UnsupportedOperationException("save: Versamento: incasso non esistente" );
        	
    	}
    	if (item.getIncassato().signum() != 0) {
        	log.warn("saveItem: Versamento: non posso aggiornare un versamento incassato, {} ", item);
            throw new UnsupportedOperationException("save: Versamento: non posso aggiornare un versamento incassato");
        }
        versamentoDao.save(item);
        DistintaVersamento incasso = findById(item.getDistintaVersamento().getId());
        Smd.calcoloImportoIncasso(incasso, versamentoDao.findByDistintaVersamento(incasso));
        repository.save(incasso);        
        return t;
    }
        
	@Override
	public List<Versamento> findAllItems() {
		return versamentoDao.findAll();
	}

    @Override
    public Versamento addItem(DistintaVersamento distintaVersamento) {
        return new Versamento(distintaVersamento);
	}

}
