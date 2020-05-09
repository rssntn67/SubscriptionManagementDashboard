package it.arsinfo.smd.dao;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.arsinfo.smd.dao.repository.AbbonamentoDao;
import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.StatoAbbonamento;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Campagna;
import it.arsinfo.smd.service.SmdService;

@Service
public class AbbonamentoServiceDao implements SmdServiceDao<Abbonamento> {

    @Autowired
    AbbonamentoDao repository;

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
        if (entity.getId() == null && entity.getEstrattiConto().size() == 0) {
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


}
