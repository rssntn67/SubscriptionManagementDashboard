package it.arsinfo.smd.service.dao;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import it.arsinfo.smd.dao.SmdServiceDao;
import it.arsinfo.smd.dao.repository.NotaDao;
import it.arsinfo.smd.dao.repository.StoricoDao;
import it.arsinfo.smd.entity.Nota;
import it.arsinfo.smd.entity.Storico;

@Service
public class NotaServiceDao implements SmdServiceDao<Nota> {

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

	public NotaDao getRepository() {
		return repository;
	}

	public List<Storico> findStoricoAll() {
		return storicoDao.findAll();
	}
		
	public List<Nota> searchBy(String searchText, Storico storico) {
        if (StringUtils.isEmpty(searchText) && storico == null) {
            return findAll();
        }
        if (StringUtils.isEmpty(searchText)) {
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
