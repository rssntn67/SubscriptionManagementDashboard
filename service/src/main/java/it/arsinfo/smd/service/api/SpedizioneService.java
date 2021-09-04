package it.arsinfo.smd.service.api;

import it.arsinfo.smd.dao.SpedizioneDao;
import it.arsinfo.smd.entity.Anno;
import it.arsinfo.smd.entity.InvioSpedizione;
import it.arsinfo.smd.entity.Mese;
import it.arsinfo.smd.entity.StatoSpedizione;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.entity.Spedizione;
import it.arsinfo.smd.entity.SpedizioneItem;
import it.arsinfo.smd.service.dto.Indirizzo;

import java.util.List;

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

	List<Spedizione> searchBy(
			Anagrafica a,
			Anno annoSped,
			Mese mesesped
	);

	void spedisci(Spedizione sped);
}
