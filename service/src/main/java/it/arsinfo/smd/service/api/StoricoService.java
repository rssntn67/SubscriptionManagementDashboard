package it.arsinfo.smd.service.api;

import java.util.List;

import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.entity.*;

public interface StoricoService extends SmdServiceItem<Storico, Nota> {
	
	List<Storico> searchBy(Anagrafica intestatario, Anagrafica destinatario, Pubblicazione pubblicazione); 
	
	List<Pubblicazione> findPubblicazioni();
    List<Anagrafica> findAnagrafica();
	List<Campagna> findCampagne();
	Campagna getByAnno(Anno anno);

	void aggiornaCampagna(Campagna campagna, Storico storico, String username) throws Exception;
	Nota getNotaOnSave(Storico storico,String username);

	List<Storico> searchBy(Anagrafica tValue) throws Exception;
	List<Abbonamento> findAbbonamento(Campagna campagna, Anagrafica intestatario, Anno anno);
}
