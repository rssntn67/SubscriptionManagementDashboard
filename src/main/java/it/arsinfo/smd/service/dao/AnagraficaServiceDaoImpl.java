package it.arsinfo.smd.service.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import it.arsinfo.smd.dao.AnagraficaServiceDao;
import it.arsinfo.smd.dao.repository.AnagraficaDao;
import it.arsinfo.smd.data.Diocesi;
import it.arsinfo.smd.entity.Anagrafica;

@Service
public class AnagraficaServiceDaoImpl implements AnagraficaServiceDao {

    @Autowired
    private AnagraficaDao repository;

	@Override
	public Anagrafica save(Anagrafica entity) throws Exception {
		return repository.save(entity);
	}

	@Override
	public void delete(Anagrafica entity) throws Exception {
		repository.delete(entity);
	}

	@Override
	public Anagrafica findById(Long id) {
		return repository.findById(id).get();
	}

	@Override
	public List<Anagrafica> findAll() {
		return repository.findAll();
	}

	public AnagraficaDao getRepository() {
		return repository;
	}

	@Override
	public List<Anagrafica> searchBy(Diocesi diocesi, String nome, String denominazione,
			String citta, String cap) {
        if (       diocesi == null 
        		&& StringUtils.isEmpty(nome) 
        		&& StringUtils.isEmpty(denominazione) 
        		&& StringUtils.isEmpty(citta)
        		&& StringUtils.isEmpty(cap)
        		) {
            return repository.findAll();
        }
        
        if (       StringUtils.isEmpty(nome) 
        		&& StringUtils.isEmpty(denominazione) 
        		&& StringUtils.isEmpty(citta)
        		&& StringUtils.isEmpty(cap)) {
            return repository.findByDiocesi(diocesi);
        }
        
        if (       diocesi == null 
        		&& StringUtils.isEmpty(denominazione) 
        		&& StringUtils.isEmpty(citta)
        		&& StringUtils.isEmpty(cap)) {
            return repository.findByNomeContainingIgnoreCase(nome);
        }
        
        if (       diocesi == null 
        		&& StringUtils.isEmpty(nome) 
        		&& StringUtils.isEmpty(citta)
        		&& StringUtils.isEmpty(cap)) {
            return repository.findByDenominazioneContainingIgnoreCase(denominazione);
        }
        
        if (       diocesi == null 
        		&& StringUtils.isEmpty(nome) 
        		&& StringUtils.isEmpty(denominazione) 
        		&& StringUtils.isEmpty(cap)) {
            return repository.findByCittaContainingIgnoreCase(citta);
        }
        
        if (       diocesi == null 
        		&& StringUtils.isEmpty(nome) 
        		&& StringUtils.isEmpty(denominazione) 
        		&& StringUtils.isEmpty(citta)) {
            return repository.findByCapContainingIgnoreCase(cap);
        }
        
        if (       StringUtils.isEmpty(denominazione) 
        		&& StringUtils.isEmpty(citta)
        		&& StringUtils.isEmpty(cap)) {
            return repository.findByDiocesiAndNomeContainingIgnoreCase(diocesi,nome);
        }
        if (    StringUtils.isEmpty(nome) 
        		&& StringUtils.isEmpty(citta) 
        		&& StringUtils.isEmpty(cap)
        		) {
            return repository.findByDiocesiAndDenominazioneContainingIgnoreCase(diocesi,denominazione);
        }
        if (    StringUtils.isEmpty(nome) 
        		&& StringUtils.isEmpty(denominazione) 
        		&& StringUtils.isEmpty(cap)
        		) {
            return repository.findByDiocesiAndCittaContainingIgnoreCase(diocesi,citta);
        }
        if (    StringUtils.isEmpty(nome) 
        		&& StringUtils.isEmpty(denominazione) 
        		&& StringUtils.isEmpty(citta)
        		) {
            return repository.findByDiocesiAndCapContainingIgnoreCase(diocesi,cap);
        }        
        if (       diocesi == null 
        		&& StringUtils.isEmpty(citta)
        		&& StringUtils.isEmpty(cap)) {
            return repository.findByNomeContainingIgnoreCaseAndDenominazioneContainingIgnoreCase(nome,denominazione);
        }
        if (       diocesi == null 
        		&& StringUtils.isEmpty(denominazione)
        		&& StringUtils.isEmpty(cap)) {
            return repository.findByNomeContainingIgnoreCaseAndCittaContainingIgnoreCase(nome,citta);
        }
        if (       diocesi == null 
        		&& StringUtils.isEmpty(denominazione)
        		&& StringUtils.isEmpty(citta)) {
            return repository.findByNomeContainingIgnoreCaseAndCapContainingIgnoreCase(nome,cap);
        }
        
        if (       diocesi == null 
        		&& StringUtils.isEmpty(nome) 
        		&& StringUtils.isEmpty(cap)) {
            return repository.findByDenominazioneContainingIgnoreCaseAndCittaContainingIgnoreCase(denominazione,citta);
        }
        if (       diocesi == null 
        		&& StringUtils.isEmpty(nome) 
        		&& StringUtils.isEmpty(citta)) {
            return repository.findByDenominazioneContainingIgnoreCaseAndCapContainingIgnoreCase(denominazione,cap);
        }
        
        if (       diocesi == null 
        		&& StringUtils.isEmpty(nome) 
        		&& StringUtils.isEmpty(denominazione) 
        ) {
            return repository.findByCittaContainingIgnoreCaseAndCapContainingIgnoreCase(citta,cap);
        }
        
        if (       StringUtils.isEmpty(citta)
        		&& StringUtils.isEmpty(cap)
    		) {
            return repository.findByDiocesiAndNomeContainingIgnoreCaseAndDenominazioneIgnoreCase(diocesi,nome,denominazione);
        }
        if (       StringUtils.isEmpty(denominazione)
        		&& StringUtils.isEmpty(cap)
    		) {
            return repository.findByDiocesiAndNomeContainingIgnoreCaseAndCittaIgnoreCase(diocesi,nome,citta);
        }
        if (       StringUtils.isEmpty(denominazione)
        		&& StringUtils.isEmpty(citta)
    		) {
            return repository.findByDiocesiAndNomeContainingIgnoreCaseAndCapIgnoreCase(diocesi,nome,cap);
        }
        if (       StringUtils.isEmpty(nome)
        		&& StringUtils.isEmpty(cap)
    		) {
            return repository.findByDiocesiAndDenominazioneContainingIgnoreCaseAndCittaIgnoreCase(diocesi,denominazione,citta);
        }
        if (       StringUtils.isEmpty(nome)
        		&& StringUtils.isEmpty(citta)
    		) {
            return repository.findByDiocesiAndDenominazioneContainingIgnoreCaseAndCapIgnoreCase(diocesi,denominazione,cap);
        }
        if (       StringUtils.isEmpty(nome)
        		&& StringUtils.isEmpty(denominazione)
    		) {
            return repository.findByDiocesiAndCittaContainingIgnoreCaseAndCapIgnoreCase(diocesi,citta,cap);
        }
        
        if (       diocesi == null 
        		&& StringUtils.isEmpty(cap)) {
            return repository.findByNomeContainingIgnoreCaseAndDenominazioneContainingIgnoreCaseAndCittaContainingIgnoreCase(nome,denominazione,citta);
        }
        if (       diocesi == null 
        		&& StringUtils.isEmpty(citta)) {
            return repository.findByNomeContainingIgnoreCaseAndDenominazioneContainingIgnoreCaseAndCapContainingIgnoreCase(nome,denominazione,cap);
        }
        if (       diocesi == null 
        		&& StringUtils.isEmpty(denominazione)) {
            return repository.findByNomeContainingIgnoreCaseAndCittaContainingIgnoreCaseAndCapContainingIgnoreCase(nome,citta,cap);
        }
        if (       diocesi == null 
        		&& StringUtils.isEmpty(nome)) {
            return repository.findByDenominazioneContainingIgnoreCaseAndCittaContainingIgnoreCaseAndCapContainingIgnoreCase(denominazione,citta,cap);
        }


        if ( diocesi == null ) {
            return repository.findByNomeContainingIgnoreCaseAndDenominazioneContainingIgnoreCaseAndCittaContainingIgnoreCaseAndCapContainingIgnoreCase(nome,denominazione,citta,cap);
        }
        if (       StringUtils.isEmpty(nome)) {
            return repository.findByDiocesiAndDenominazioneContainingIgnoreCaseAndCittaContainingIgnoreCaseAndCapIgnoreCase(diocesi,denominazione,citta,cap);
        }
        if (       StringUtils.isEmpty(denominazione)) {
            return repository.findByDiocesiAndNomeContainingIgnoreCaseAndCittaContainingIgnoreCaseAndCapIgnoreCase(diocesi,nome,citta,cap);
        }
        if (       StringUtils.isEmpty(citta)) {
            return repository.findByDiocesiAndNomeContainingIgnoreCaseAndDenominazioneContainingIgnoreCaseAndCapIgnoreCase(diocesi,nome,denominazione,cap);
        }

        return repository.findByDiocesiAndNomeContainingIgnoreCaseAndDenominazioneContainingIgnoreCaseAndCittaContainingIgnoreCaseAndCapIgnoreCase(diocesi,nome,denominazione,citta,cap);
        
	}
	
}
