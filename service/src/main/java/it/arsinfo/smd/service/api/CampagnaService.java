package it.arsinfo.smd.service.api;

import java.util.List;

import it.arsinfo.smd.entity.*;
import it.arsinfo.smd.dto.AbbonamentoDto;

public interface CampagnaService extends SmdServiceBase<Campagna> {

	List<Pubblicazione> findPubblicazioni();
	List<Pubblicazione> findPubblicazioniValide();
	
	List<AbbonamentoDto> findAbbonamentoConRivisteGenerati(Campagna entity);
	List<AbbonamentoDto> findAbbonamentoConRivisteInviati(Campagna entity);
	List<AbbonamentoDto> findAbbonamentoConRivisteSollecito(Campagna campagna);
	List<AbbonamentoDto> findAbbonamentoConRivisteEstrattoConto(Campagna entity);
	List<AbbonamentoDto> findAbbonamentoConDebito(Campagna entity);
	List<AbbonamentoDto> searchBy(
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

	void genera(Campagna campagna, UserInfo user) throws Exception;
	void invia(Campagna campagna, UserInfo user) throws Exception;
	void sollecita(Campagna c, UserInfo operatore) throws Exception;
	void sospendi(Campagna campagna, Pubblicazione p, UserInfo user) throws Exception;
	void estratto(Campagna campagna, UserInfo user) throws Exception;	
	void chiudi(Campagna campagna, UserInfo user) throws Exception;

	List<Campagna> searchBy(Anno anno);
	List<OperazioneCampagna> getOperazioni(Campagna campagna);
	List<OperazioneSospendi> getSospensioni(Campagna campagna);
}
