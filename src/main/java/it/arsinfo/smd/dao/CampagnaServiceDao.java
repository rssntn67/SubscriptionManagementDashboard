package it.arsinfo.smd.dao;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.arsinfo.smd.dao.repository.CampagnaDao;
import it.arsinfo.smd.dao.repository.PubblicazioneDao;
import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.TipoPubblicazione;
import it.arsinfo.smd.entity.Campagna;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.service.SmdService;

@Service
public class CampagnaServiceDao implements SmdServiceDao<Campagna> {
	
    @Autowired
    private CampagnaDao repository;

    @Autowired
    private PubblicazioneDao pubblicazioneDao;

    @Autowired
    private SmdService smdService;

	@Override
	public Campagna save(Campagna entity) throws Exception {
        if (entity.getId() != null) {
            throw new UnsupportedOperationException("Impossibile Rigenerare Campagna");
        }
        if (entity.getAnno() == null) {
            throw new UnsupportedOperationException("Anno Campagna non definito");
        }
        if (entity.getAnno().getAnno() <= Anno.getAnnoCorrente().getAnno()) {
            throw new UnsupportedOperationException("Anno deve essere almeno anno successivo");
        }
        List<Pubblicazione> attivi = pubblicazioneDao.findAll().stream().filter(p -> p.isActive()
                && p.getTipo() != TipoPubblicazione.UNICO).collect(Collectors.toList());

        return smdService.genera(entity, attivi);
	}

	@Override
	public void delete(Campagna entity) throws Exception {
		smdService.delete(entity);
	}

	@Override
	public Campagna findById(Long id) {
		return repository.findById(id).get();
	}

	@Override
	public List<Campagna> findAll() {
		return repository.findAll();
	}

	public CampagnaDao getRepository() {
		return repository;
	}
	
}
