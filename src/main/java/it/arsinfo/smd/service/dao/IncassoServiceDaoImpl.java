package it.arsinfo.smd.service.dao;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.arsinfo.smd.dao.IncassoServiceDao;
import it.arsinfo.smd.dao.repository.IncassoDao;
import it.arsinfo.smd.data.Cassa;
import it.arsinfo.smd.data.Ccp;
import it.arsinfo.smd.data.Cuas;
import it.arsinfo.smd.entity.Incasso;
import it.arsinfo.smd.service.Smd;

@Service
public class IncassoServiceDaoImpl implements IncassoServiceDao {

    @Autowired
    private IncassoDao repository;

	@Override
	public Incasso save(Incasso entity) throws Exception {
		return repository.save(entity);
	}

	@Override
	public void delete(Incasso entity) throws Exception {
		repository.delete(entity);
	}

	@Override
	public Incasso findById(Long id) {
		return repository.findById(id).get();
	}

	@Override
	public List<Incasso> findAll() {
		return repository.findAll();
	}

	public IncassoDao getRepository() {
		return repository;
	}

	@Override
	public List<Incasso> searchBy(Cuas cuas, LocalDate dataContabile, Cassa cassa, Ccp ccp) {
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
                    .findByDataContabile(Smd.getStandardDate(dataContabile));
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
                    .findByDataContabile(Smd.getStandardDate(dataContabile))
                    .stream()
                    .filter(inc -> inc.getCuas() == cuas)
                    .collect(Collectors.toList());
        }
        if (cuas == null && ccp == null) {
            return repository
                    .findByDataContabile(Smd.getStandardDate(dataContabile))
                    .stream()
                    .filter(inc -> inc.getCassa() == cassa)
                    .collect(Collectors.toList());
        }
        if (cassa == null && cuas == null) {
            return repository
                    .findByDataContabile(Smd.getStandardDate(dataContabile))
                    .stream()
                    .filter(inc -> inc.getCcp() == ccp)
                    .collect(Collectors.toList());
        }
        if (cassa == null) {
            return repository
                    .findByDataContabile(Smd.getStandardDate(dataContabile))
                    .stream()
                    .filter(inc -> inc.getCuas() == cuas && inc.getCcp() == ccp)
                    .collect(Collectors.toList());
        }
        if (cuas == null) {
            return repository
                    .findByDataContabile(Smd.getStandardDate(dataContabile))
                    .stream()
                    .filter(inc -> inc.getCassa() == cassa && inc.getCcp() == ccp)
                    .collect(Collectors.toList());
        }
        if (ccp == null) {
            return repository
                    .findByDataContabile(Smd.getStandardDate(dataContabile))
                    .stream()
                    .filter(inc -> inc.getCuas() == cuas && inc.getCassa() == cassa)
                    .collect(Collectors.toList());
        }

        return repository
                .findByDataContabile(Smd.getStandardDate(dataContabile))
                .stream()
                .filter(inc -> inc.getCassa() == cassa && inc.getCuas() == cuas && inc.getCcp() == ccp)            
                .collect(Collectors.toList());
	}
	
}
