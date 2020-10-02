package it.arsinfo.smd.dao;

import java.util.List;

import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.Incassato;
import it.arsinfo.smd.data.StatoAbbonamento;
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
	List<Abbonamento> searchBy(
			Campagna campagna, 
			Anagrafica intestatario, 
			Anagrafica beneficiario, 
			Anno anno, 
			Pubblicazione p, 
			TipoAbbonamentoRivista t, 
			StatoAbbonamento s, 
			Incassato inc,
			String codeLine,
			boolean checkContrassegno,
			boolean checkResiduo,
			boolean checkNotResiduo,
			boolean checkResiduoZero
			);
	List<Abbonamento> searchBy(Anagrafica tValue, Anno sValue) throws Exception;
}
