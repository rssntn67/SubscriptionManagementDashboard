package it.arsinfo.smd.dao;

import java.util.List;

import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.dto.AbbonamentoConRiviste;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Campagna;
import it.arsinfo.smd.entity.Pubblicazione;

public interface CampagnaServiceDao extends SmdServiceDao<Campagna> {

	List<Pubblicazione> findPubblicazioni();
	List<Pubblicazione> findPubblicazioniValide();
	List<AbbonamentoConRiviste> findAbbonamentoConRivisteGenerati(Campagna entity);
	List<AbbonamentoConRiviste> findAbbonamentoConRivisteInviati(Campagna entity);
	List<AbbonamentoConRiviste> findAbbonamentoConRivisteEstrattoConto(Campagna entity);
	List<AbbonamentoConRiviste> findAbbonamentoConRivisteAnnullati(Campagna entity);
	
	List<Abbonamento> findInviatiByCampagna(Campagna entity);
	List<Abbonamento> findEstrattoContoByCampagna(Campagna entity);
	List<Abbonamento> findAnnullatiByCampagna(Campagna entity);

	
	void invia(Campagna campagna) throws Exception;
	void estratto(Campagna campagna) throws Exception;	
	void chiudi(Campagna campagna) throws Exception;
	List<Campagna> searchBy(Anno anno);
}
