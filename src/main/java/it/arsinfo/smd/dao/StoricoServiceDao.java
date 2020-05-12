package it.arsinfo.smd.dao;

import java.util.List;

import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Campagna;
import it.arsinfo.smd.entity.Nota;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.entity.Storico;

public interface StoricoServiceDao extends SmdServiceItemDao<Storico, Nota> {
	
	List<Storico> searchBy(Anagrafica intestatario, Anagrafica destinatario, Pubblicazione pubblicazione); 
	
	List<Pubblicazione> findPubblicazioni();
    List<Anagrafica> findAnagrafica();
	List<Campagna> findCampagne();

	void aggiornaCampagna(Campagna campagna, Storico storico, String username) throws Exception;
}
