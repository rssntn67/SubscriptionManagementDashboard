package it.arsinfo.smd.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.InvioSpedizione;
import it.arsinfo.smd.data.Mese;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Spedizione;

public interface SpedizioneDao extends JpaRepository<Spedizione, Long> {
    List<Spedizione> findByAbbonamento(Abbonamento abbonamento);
    
    List<Spedizione> findByDestinatario(Anagrafica destinatario);
    List<Spedizione> findByMeseSpedizione(Mese mese);
    List<Spedizione> findByAnnoSpedizione(Anno anno);
    List<Spedizione> findByInvioSpedizione(InvioSpedizione invioSpedizione);
    
    List<Spedizione> findByDestinatarioAndMeseSpedizione(Anagrafica destinatario, Mese mese);
    List<Spedizione> findByDestinatarioAndAnnoSpedizione(Anagrafica destinatario, Anno anno);
    List<Spedizione> findByDestinatarioAndInvioSpedizione(Anagrafica destinatario, InvioSpedizione invio);
    List<Spedizione> findByMeseSpedizioneAndAnnoSpedizione(Mese mese,Anno anno);
    List<Spedizione> findByMeseSpedizioneAndInvioSpedizione(Mese mese,InvioSpedizione invio);
    List<Spedizione> findByAnnoSpedizioneAndInvioSpedizione(Anno mese,InvioSpedizione invio);
    
    List<Spedizione> findByDestinatarioAndMeseSpedizioneAndAnnoSpedizione(Anagrafica destinatario, Mese mese, Anno anno);
    List<Spedizione> findByDestinatarioAndMeseSpedizioneAndInvioSpedizione(Anagrafica destinatario, Mese mese, InvioSpedizione invio);
    List<Spedizione> findByDestinatarioAndAnnoSpedizioneAndInvioSpedizione(Anagrafica destinatario,Anno anno, InvioSpedizione invio);
    List<Spedizione> findByMeseSpedizioneAndAnnoSpedizioneAndInvioSpedizione(Mese mese,Anno anno, InvioSpedizione invio);
    
    List<Spedizione> findByDestinatarioAndMeseSpedizioneAndAnnoSpedizioneAndInvioSpedizione(Anagrafica destinatario,
			Mese mese, Anno anno, InvioSpedizione invio);
    List<Spedizione> findByMeseSpedizioneAndAnnoSpedizioneAndInvioSpedizioneNot	(Mese mese,Anno anno, InvioSpedizione invio);
    void deleteByAbbonamento(Abbonamento abbonamento);
	
}
