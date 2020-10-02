package it.arsinfo.smd.dao;

import java.util.List;

import it.arsinfo.smd.dao.repository.SpedizioneDao;
import it.arsinfo.smd.data.StatoSpedizione;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.entity.Spedizione;
import it.arsinfo.smd.entity.SpedizioneItem;

public interface SpedizioneServiceDao extends SmdServiceItemDao<Spedizione,SpedizioneItem> {
	
	List<Spedizione> searchBy(Pubblicazione p, Anagrafica a, StatoSpedizione stato);	
    SpedizioneDao getRepository();
}
