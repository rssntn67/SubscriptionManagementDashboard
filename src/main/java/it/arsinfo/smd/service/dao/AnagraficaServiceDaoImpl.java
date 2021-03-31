package it.arsinfo.smd.service.dao;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import it.arsinfo.smd.dao.AnagraficaServiceDao;
import it.arsinfo.smd.dao.repository.AnagraficaDao;
import it.arsinfo.smd.data.AreaSpedizione;
import it.arsinfo.smd.data.CentroDiocesano;
import it.arsinfo.smd.data.Diocesi;
import it.arsinfo.smd.data.Paese;
import it.arsinfo.smd.data.Provincia;
import it.arsinfo.smd.data.Regione;
import it.arsinfo.smd.data.TitoloAnagrafica;
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

	private List<Anagrafica> search(Diocesi diocesi, 
			String nome, 
			String denominazione,
			String citta, 
			String cap) {
        if (       diocesi == null 
        		&& !StringUtils.hasLength(nome) 
        		&& !StringUtils.hasLength(denominazione) 
        		&& !StringUtils.hasLength(citta)
        		&& !StringUtils.hasLength(cap)
        		) {
            return repository.findAll();
        }
        
        if (       !StringUtils.hasLength(nome) 
        		&& !StringUtils.hasLength(denominazione) 
        		&& !StringUtils.hasLength(citta)
        		&& !StringUtils.hasLength(cap)) {
            return repository.findByDiocesi(diocesi);
        }
        
        if (       diocesi == null 
        		&& !StringUtils.hasLength(denominazione) 
        		&& !StringUtils.hasLength(citta)
        		&& !StringUtils.hasLength(cap)) {
            return repository.findByNomeContainingIgnoreCase(nome);
        }
        
        if (       diocesi == null 
        		&& !StringUtils.hasLength(nome) 
        		&& !StringUtils.hasLength(citta)
        		&& !StringUtils.hasLength(cap)) {
            return repository.findByDenominazioneContainingIgnoreCase(denominazione);
        }
        
        if (       diocesi == null 
        		&& !StringUtils.hasLength(nome) 
        		&& !StringUtils.hasLength(denominazione) 
        		&& !StringUtils.hasLength(cap)) {
            return repository.findByCittaContainingIgnoreCase(citta);
        }
        
        if (       diocesi == null 
        		&& !StringUtils.hasLength(nome) 
        		&& !StringUtils.hasLength(denominazione) 
        		&& !StringUtils.hasLength(citta)) {
            return repository.findByCapContainingIgnoreCase(cap);
        }
        
        if (       !StringUtils.hasLength(denominazione) 
        		&& !StringUtils.hasLength(citta)
        		&& !StringUtils.hasLength(cap)) {
            return repository.findByDiocesiAndNomeContainingIgnoreCase(diocesi,nome);
        }
        if (    !StringUtils.hasLength(nome) 
        		&& !StringUtils.hasLength(citta) 
        		&& !StringUtils.hasLength(cap)
        		) {
            return repository.findByDiocesiAndDenominazioneContainingIgnoreCase(diocesi,denominazione);
        }
        if (    !StringUtils.hasLength(nome) 
        		&& !StringUtils.hasLength(denominazione) 
        		&& !StringUtils.hasLength(cap)
        		) {
            return repository.findByDiocesiAndCittaContainingIgnoreCase(diocesi,citta);
        }
        if (    !StringUtils.hasLength(nome) 
        		&& !StringUtils.hasLength(denominazione) 
        		&& !StringUtils.hasLength(citta)
        		) {
            return repository.findByDiocesiAndCapContainingIgnoreCase(diocesi,cap);
        }        
        if (       diocesi == null 
        		&& !StringUtils.hasLength(citta)
        		&& !StringUtils.hasLength(cap)) {
            return repository.findByNomeContainingIgnoreCaseAndDenominazioneContainingIgnoreCase(nome,denominazione);
        }
        if (       diocesi == null 
        		&& !StringUtils.hasLength(denominazione)
        		&& !StringUtils.hasLength(cap)) {
            return repository.findByNomeContainingIgnoreCaseAndCittaContainingIgnoreCase(nome,citta);
        }
        if (       diocesi == null 
        		&& !StringUtils.hasLength(denominazione)
        		&& !StringUtils.hasLength(citta)) {
            return repository.findByNomeContainingIgnoreCaseAndCapContainingIgnoreCase(nome,cap);
        }
        
        if (       diocesi == null 
        		&& !StringUtils.hasLength(nome) 
        		&& !StringUtils.hasLength(cap)) {
            return repository.findByDenominazioneContainingIgnoreCaseAndCittaContainingIgnoreCase(denominazione,citta);
        }
        if (       diocesi == null 
        		&& !StringUtils.hasLength(nome) 
        		&& !StringUtils.hasLength(citta)) {
            return repository.findByDenominazioneContainingIgnoreCaseAndCapContainingIgnoreCase(denominazione,cap);
        }
        
        if (       diocesi == null 
        		&& !StringUtils.hasLength(nome) 
        		&& !StringUtils.hasLength(denominazione) 
        ) {
            return repository.findByCittaContainingIgnoreCaseAndCapContainingIgnoreCase(citta,cap);
        }
        
        if (       !StringUtils.hasLength(citta)
        		&& !StringUtils.hasLength(cap)
    		) {
            return repository.findByDiocesiAndNomeContainingIgnoreCaseAndDenominazioneIgnoreCase(diocesi,nome,denominazione);
        }
        if (       !StringUtils.hasLength(denominazione)
        		&& !StringUtils.hasLength(cap)
    		) {
            return repository.findByDiocesiAndNomeContainingIgnoreCaseAndCittaIgnoreCase(diocesi,nome,citta);
        }
        if (       !StringUtils.hasLength(denominazione)
        		&& !StringUtils.hasLength(citta)
    		) {
            return repository.findByDiocesiAndNomeContainingIgnoreCaseAndCapIgnoreCase(diocesi,nome,cap);
        }
        if (       !StringUtils.hasLength(nome)
        		&& !StringUtils.hasLength(cap)
    		) {
            return repository.findByDiocesiAndDenominazioneContainingIgnoreCaseAndCittaIgnoreCase(diocesi,denominazione,citta);
        }
        if (       !StringUtils.hasLength(nome)
        		&& !StringUtils.hasLength(citta)
    		) {
            return repository.findByDiocesiAndDenominazioneContainingIgnoreCaseAndCapIgnoreCase(diocesi,denominazione,cap);
        }
        if (       !StringUtils.hasLength(nome)
        		&& !StringUtils.hasLength(denominazione)
    		) {
            return repository.findByDiocesiAndCittaContainingIgnoreCaseAndCapIgnoreCase(diocesi,citta,cap);
        }
        
        if (       diocesi == null 
        		&& !StringUtils.hasLength(cap)) {
            return repository.findByNomeContainingIgnoreCaseAndDenominazioneContainingIgnoreCaseAndCittaContainingIgnoreCase(nome,denominazione,citta);
        }
        if (       diocesi == null 
        		&& !StringUtils.hasLength(citta)) {
            return repository.findByNomeContainingIgnoreCaseAndDenominazioneContainingIgnoreCaseAndCapContainingIgnoreCase(nome,denominazione,cap);
        }
        if (       diocesi == null 
        		&& !StringUtils.hasLength(denominazione)) {
            return repository.findByNomeContainingIgnoreCaseAndCittaContainingIgnoreCaseAndCapContainingIgnoreCase(nome,citta,cap);
        }
        if (       diocesi == null 
        		&& !StringUtils.hasLength(nome)) {
            return repository.findByDenominazioneContainingIgnoreCaseAndCittaContainingIgnoreCaseAndCapContainingIgnoreCase(denominazione,citta,cap);
        }


        if ( diocesi == null ) {
            return repository.findByNomeContainingIgnoreCaseAndDenominazioneContainingIgnoreCaseAndCittaContainingIgnoreCaseAndCapContainingIgnoreCase(nome,denominazione,citta,cap);
        }
        if (       !StringUtils.hasLength(nome)) {
            return repository.findByDiocesiAndDenominazioneContainingIgnoreCaseAndCittaContainingIgnoreCaseAndCapIgnoreCase(diocesi,denominazione,citta,cap);
        }
        if (       !StringUtils.hasLength(denominazione)) {
            return repository.findByDiocesiAndNomeContainingIgnoreCaseAndCittaContainingIgnoreCaseAndCapIgnoreCase(diocesi,nome,citta,cap);
        }
        if (       !StringUtils.hasLength(citta)) {
            return repository.findByDiocesiAndNomeContainingIgnoreCaseAndDenominazioneContainingIgnoreCaseAndCapIgnoreCase(diocesi,nome,denominazione,cap);
        }

        return repository.findByDiocesiAndNomeContainingIgnoreCaseAndDenominazioneContainingIgnoreCaseAndCittaContainingIgnoreCaseAndCapIgnoreCase(diocesi,nome,denominazione,citta,cap);
		
	}

	@Override
	public List<Anagrafica> searchBy(
			Diocesi diocesi, 
			String nome, 
			String denominazione,
			String citta, 
			String cap, 
			Paese paese,
	   		AreaSpedizione areaSped, 
    		Provincia provincia, 
    		TitoloAnagrafica titolo,
    		Regione regioneVescovi,
    		CentroDiocesano centroDiocesano,
    		Regione regioneDirettoreDiocesano,
    		boolean filterDirettoreDiocesano,
    		boolean filterPresidenteDiocesano,
    		Regione regionePresidenteDiocesano,
    		boolean filterDirettoreZonaMilano,
    		boolean filterConsiglioNazionaleADP,
    		boolean filterPresidenzaADP,
    		boolean filterDirezioneADP,
    		boolean filterCaricheSocialiADP,
    		boolean filterDelegatiRegionaliADP,
    		boolean filterElencoMarisaBisi,
    		boolean filterPromotoreRegionale
    		) {
		List<Anagrafica> anagrafiche = search(diocesi, nome, denominazione, citta, cap);

		if (paese != null) {
            anagrafiche = anagrafiche.stream().filter(a -> paese == a.getPaese()).collect(Collectors.toList());
        }
        if (areaSped != null) {
            anagrafiche = anagrafiche.stream().filter(a -> areaSped == a.getAreaSpedizione()).collect(Collectors.toList());
        }
        if (provincia != null) {
            anagrafiche = anagrafiche.stream().filter(a -> provincia == a.getProvincia()).collect(Collectors.toList());
        }
        if (titolo != null) {
            anagrafiche = anagrafiche.stream().filter(a -> titolo == a.getTitolo()).collect(Collectors.toList());
        }
        if (regioneVescovi != null) {
            anagrafiche = anagrafiche.stream().filter(a -> regioneVescovi == a.getRegioneVescovi()).collect(Collectors.toList());
        }
        if (centroDiocesano != null) {
            anagrafiche = anagrafiche.stream().filter(a -> centroDiocesano == a.getCentroDiocesano()).collect(Collectors.toList());
        }
        if (regioneDirettoreDiocesano != null) {
            anagrafiche = anagrafiche.stream().filter(a -> regioneDirettoreDiocesano == a.getRegioneDirettoreDiocesano()).collect(Collectors.toList());
        }
        if (regionePresidenteDiocesano != null) {
            anagrafiche = anagrafiche.stream().filter(a -> regionePresidenteDiocesano == a.getRegionePresidenteDiocesano()).collect(Collectors.toList());
        }
        if (filterDirettoreDiocesano) {
            anagrafiche = anagrafiche.stream().filter(a -> a.isDirettoreDiocesiano()).collect(Collectors.toList());
        }
        if (filterPresidenteDiocesano) {
            anagrafiche = anagrafiche.stream().filter(a -> a.isPresidenteDiocesano()).collect(Collectors.toList());
        }
        if (filterDirettoreZonaMilano) {
            anagrafiche = anagrafiche.stream().filter(a -> a.isDirettoreZonaMilano()).collect(Collectors.toList());
        }
        if (filterConsiglioNazionaleADP) {
            anagrafiche = anagrafiche.stream().filter(a -> a.isConsiglioNazionaleADP()).collect(Collectors.toList());
        }
        if (filterPresidenzaADP) {
            anagrafiche = anagrafiche.stream().filter(a -> a.isPresidenzaADP()).collect(Collectors.toList());
        }
        if (filterDirezioneADP) {
            anagrafiche = anagrafiche.stream().filter(a -> a.isDirezioneADP()).collect(Collectors.toList());
        }
        if (filterCaricheSocialiADP) {
            anagrafiche = anagrafiche.stream().filter(a -> a.isCaricheSocialiADP()).collect(Collectors.toList());
        }
        if (filterDelegatiRegionaliADP) {
            anagrafiche = anagrafiche.stream().filter(a -> a.isDelegatiRegionaliADP()).collect(Collectors.toList());
        }
        if (filterElencoMarisaBisi) {
            anagrafiche = anagrafiche.stream().filter(a -> a.isElencoMarisaBisi()).collect(Collectors.toList());
        }
        if (filterPromotoreRegionale) {
            anagrafiche = anagrafiche.stream().filter(a -> a.isPromotoreRegionale()).collect(Collectors.toList());            
        }
       
        return anagrafiche;
    }

	
}
