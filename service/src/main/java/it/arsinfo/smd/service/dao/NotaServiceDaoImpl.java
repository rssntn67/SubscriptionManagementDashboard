package it.arsinfo.smd.service.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import it.arsinfo.smd.service.api.NotaService;
import it.arsinfo.smd.dao.NotaDao;
import it.arsinfo.smd.dao.StoricoDao;
import it.arsinfo.smd.entity.Nota;
import it.arsinfo.smd.entity.Storico;

@Service
public class NotaServiceDaoImpl implements NotaService {

	@Autowired
	private NotaDao repository;

	@Autowired
	private StoricoDao storicoDao;

	@Override
	public Nota save(Nota entity) throws Exception {
		return repository.save(entity);
	}

	@Override
	public void delete(Nota entity) throws Exception {
		repository.delete(entity);
	}

	@Override
	public Nota findById(Long id) {
		return repository.findById(id).get();
	}

	@Override
	public List<Nota> findAll() {
		return repository.findAll();
	}

	@Override
	public List<Nota> searchByDefault() {
		return new ArrayList<>();
	}

	public NotaDao getRepository() {
		return repository;
	}

	public List<Storico> findStoricoAll() {
		return storicoDao.findAll();
	}
		
	public List<Nota> searchBy(String searchText, Storico storico) {
        if (!StringUtils.hasLength(searchText) && storico == null) {
            return findAll();
        }
        if (!StringUtils.hasLength(searchText)) {
            return repository.findByStorico(storico);
        }
        if (storico == null) {
            return repository.findByDescriptionContainingIgnoreCase(searchText);
        }
        return repository.findByDescriptionContainingIgnoreCase(searchText)
                .stream()
                .filter(n -> 
                n.getStorico().getId() 
                        == storico.getId())
                .collect(Collectors.toList());
	}
}
