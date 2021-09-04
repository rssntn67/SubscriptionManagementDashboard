package it.arsinfo.smd.service.api;

import java.util.List;

import org.springframework.stereotype.Service;

import it.arsinfo.smd.entity.Anno;
import it.arsinfo.smd.entity.AreaSpedizione;
import it.arsinfo.smd.entity.CentroDiocesano;
import it.arsinfo.smd.entity.Diocesi;
import it.arsinfo.smd.entity.Paese;
import it.arsinfo.smd.entity.Provincia;
import it.arsinfo.smd.entity.Regione;
import it.arsinfo.smd.entity.TitoloAnagrafica;
import it.arsinfo.smd.service.dto.AbbonamentoConRiviste;

@Service
public interface AbbonamentoConRivisteService {

	 List<AbbonamentoConRiviste> searchBy(
			Anno anno,
			Diocesi searchDiocesi, 
			String searchNome,
			String searchDenominazione,
			String searchCitta,
			String searchCap, 
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
    		);
	
}
