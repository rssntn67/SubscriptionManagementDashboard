package it.arsinfo.smd.service.api;

import it.arsinfo.smd.entity.*;

import java.util.List;

public interface AbbonamentoService extends SmdServiceItem<Abbonamento, Rivista> {
	
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

}
