package it.arsinfo.smd.dao;

import java.util.List;

import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.TipoAbbonamentoRivista;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Campagna;
import it.arsinfo.smd.entity.OperazioneIncasso;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.entity.RivistaAbbonamento;
import it.arsinfo.smd.entity.UserInfo;

public interface AbbonamentoServiceDao extends SmdServiceItemDao<Abbonamento,RivistaAbbonamento> {
	
	List<Anagrafica> getAnagrafica();
	List<Pubblicazione> getPubblicazioni();
	List<Campagna> getCampagne();
	void incassa(Abbonamento entity, String incassato, UserInfo user ) throws Exception;
	List<OperazioneIncasso> getOperazioneIncassoAssociate(Abbonamento abbonamento);
	List<Abbonamento> searchBy(Campagna campagna, Anagrafica customer, Anno anno, Pubblicazione p, TipoAbbonamentoRivista t);
	List<Abbonamento> searchBy(Anagrafica tValue, Anno sValue) throws Exception;
}
