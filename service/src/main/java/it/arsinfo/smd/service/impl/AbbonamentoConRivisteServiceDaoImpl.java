package it.arsinfo.smd.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import it.arsinfo.smd.entity.Anagrafica;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.arsinfo.smd.service.api.AbbonamentoConRivisteService;
import it.arsinfo.smd.service.api.AnagraficaService;
import it.arsinfo.smd.service.api.SmdService;
import it.arsinfo.smd.dao.AbbonamentoDao;
import it.arsinfo.smd.entity.Anno;
import it.arsinfo.smd.entity.AreaSpedizione;
import it.arsinfo.smd.entity.CentroDiocesano;
import it.arsinfo.smd.entity.Diocesi;
import it.arsinfo.smd.entity.Paese;
import it.arsinfo.smd.entity.Provincia;
import it.arsinfo.smd.entity.Regione;
import it.arsinfo.smd.entity.TitoloAnagrafica;
import it.arsinfo.smd.service.dto.AbbonamentoConRiviste;
import it.arsinfo.smd.entity.Abbonamento;

@Service
public class AbbonamentoConRivisteServiceDaoImpl implements AbbonamentoConRivisteService {

    @Autowired
    private AnagraficaService dao;

    @Autowired
    private AbbonamentoDao abbonamento;

    @Autowired
    SmdService service;

    @Override
	public List<AbbonamentoConRiviste> searchBy(
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
    		boolean filterPromotoreRegionale)

    {
		List<Long> intestatarioId =
				dao.searchBy(searchDiocesi, searchNome, searchDenominazione, searchCitta, searchCap,
						paese, areaSped, provincia,
						titolo, regioneVescovi, centroDiocesano,
						regioneDirettoreDiocesano, filterDirettoreDiocesano,
						filterPresidenteDiocesano, regionePresidenteDiocesano,
						filterDirettoreZonaMilano, filterConsiglioNazionaleADP,
						filterPresidenzaADP, filterDirezioneADP, filterCaricheSocialiADP,
						filterDelegatiRegionaliADP, filterElencoMarisaBisi,
						filterPromotoreRegionale)
				.stream().map(Anagrafica::getId).collect(Collectors.toList());
		List<Abbonamento> abbonamenti = abbonamento.findByAnno(anno).stream().filter(abb -> intestatarioId.contains(abb.getIntestatario().getId())).collect(Collectors.toList()); 
		return service.get(abbonamenti);
	}
	
}
