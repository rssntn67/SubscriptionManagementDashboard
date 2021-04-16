package it.arsinfo.smd.service.dao;

import java.util.List;
import java.util.stream.Collectors;

import it.arsinfo.smd.entity.Anagrafica;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.arsinfo.smd.dao.AbbonamentoConRivisteServiceDao;
import it.arsinfo.smd.dao.AnagraficaServiceDao;
import it.arsinfo.smd.dao.SmdService;
import it.arsinfo.smd.dao.repository.AbbonamentoDao;
import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.AreaSpedizione;
import it.arsinfo.smd.data.CentroDiocesano;
import it.arsinfo.smd.data.Diocesi;
import it.arsinfo.smd.data.Paese;
import it.arsinfo.smd.data.Provincia;
import it.arsinfo.smd.data.Regione;
import it.arsinfo.smd.data.TitoloAnagrafica;
import it.arsinfo.smd.dto.AbbonamentoConRiviste;
import it.arsinfo.smd.entity.Abbonamento;

@Service
public class AbbonamentoConRivisteServiceDaoImpl implements AbbonamentoConRivisteServiceDao {

    @Autowired
    private AnagraficaServiceDao dao;

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
