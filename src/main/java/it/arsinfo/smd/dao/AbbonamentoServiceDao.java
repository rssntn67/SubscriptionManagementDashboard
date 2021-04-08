package it.arsinfo.smd.dao;

import it.arsinfo.smd.data.*;
import it.arsinfo.smd.entity.*;

import java.io.File;
import java.util.List;

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
			StatoRivista sr, 
			Incassato inc,
			String codeLine,
			boolean checkContrassegno,
			boolean checkResiduo,
			boolean checkNotResiduo,
			boolean checkResiduoZero,
			boolean checkSollecitato,
			boolean checkInviatoEC
			);
	List<Abbonamento> searchBy(Anagrafica tValue, Anno sValue) throws Exception;

	File getBollettino(Abbonamento abbonamento, boolean download);

}
