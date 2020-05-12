package it.arsinfo.smd.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.arsinfo.smd.dao.SmdServiceItemDao;
import it.arsinfo.smd.dao.repository.AnagraficaDao;
import it.arsinfo.smd.dao.repository.CampagnaDao;
import it.arsinfo.smd.dao.repository.NotaDao;
import it.arsinfo.smd.dao.repository.PubblicazioneDao;
import it.arsinfo.smd.dao.repository.StoricoDao;
import it.arsinfo.smd.data.StatoCampagna;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Campagna;
import it.arsinfo.smd.entity.Nota;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.entity.Storico;
import it.arsinfo.smd.service.SmdService;

public interface StoricoServiceDao extends SmdServiceItemDao<Storico, Nota> {
	
	List<Storico> searchBy(Anagrafica intestatario, Anagrafica destinatario, Pubblicazione pubblicazione); 
	
	List<Pubblicazione> findPubblicazioni();
    List<Anagrafica> findAnagrafica();
	List<Campagna> findCampagne();

	void aggiornaCampagna(Campagna campagna, Storico storico, String username) throws Exception;
}
