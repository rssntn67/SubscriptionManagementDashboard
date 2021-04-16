package it.arsinfo.smd.dao;

import java.util.List;

import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.dto.AbbonamentoConRiviste;
import it.arsinfo.smd.entity.Campagna;
import it.arsinfo.smd.entity.OperazioneCampagna;
import it.arsinfo.smd.entity.OperazioneSospendi;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.entity.UserInfo;

public interface CampagnaServiceDao extends SmdServiceDao<Campagna> {

	List<Pubblicazione> findPubblicazioni();
	List<Pubblicazione> findPubblicazioniValide();
	
	List<AbbonamentoConRiviste> findAbbonamentoConRivisteGenerati(Campagna entity);
	List<AbbonamentoConRiviste> findAbbonamentoConRivisteInviati(Campagna entity);
	List<AbbonamentoConRiviste> findAbbonamentoConRivisteSollecito(Campagna campagna);
	List<AbbonamentoConRiviste> findAbbonamentoConRivisteEstrattoConto(Campagna entity);
	List<AbbonamentoConRiviste> findAbbonamentoConDebito(Campagna entity);
	
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
