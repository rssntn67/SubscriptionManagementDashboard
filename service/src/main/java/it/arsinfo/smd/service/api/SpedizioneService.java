package it.arsinfo.smd.service.api;

import java.util.List;

import it.arsinfo.smd.dao.SpedizioneDao;
import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.InvioSpedizione;
import it.arsinfo.smd.data.Mese;
import it.arsinfo.smd.data.StatoSpedizione;
import it.arsinfo.smd.service.dto.Indirizzo;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.entity.Spedizione;
import it.arsinfo.smd.entity.SpedizioneItem;

public interface SpedizioneService extends SmdServiceItem<Spedizione,SpedizioneItem> {
	
	List<Spedizione> searchBy(
			Anagrafica a, 
			Anno annoSped, 
			Mese mesesped,
			InvioSpedizione invio,			
			Pubblicazione p, 
			StatoSpedizione stato 
			);	
    SpedizioneDao getRepository();
	List<Pubblicazione> findPubblicazioni();
	List<Anagrafica> findAnagrafica();
	void inviaDuplicato(Spedizione sped, InvioSpedizione invio) throws Exception;
	Indirizzo getIndirizzo(Spedizione sped);

	void spedisci(Spedizione sped);
}