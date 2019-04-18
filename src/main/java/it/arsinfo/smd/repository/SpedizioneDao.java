package it.arsinfo.smd.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import it.arsinfo.smd.data.Invio;
import it.arsinfo.smd.data.Omaggio;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.entity.Spedizione;

public interface SpedizioneDao extends JpaRepository<Spedizione, Long> {

	List<Spedizione> findByAbbonamento(Abbonamento abbonamento);
        List<Spedizione> findByPubblicazione(Pubblicazione pubblicazione);
        List<Spedizione> findByDestinatario(Anagrafica destinatario);
        List<Spedizione> findByIntestatario(Anagrafica intestatario);
        List<Spedizione> findByIntestatarioAndDestinatario(Anagrafica intestatario, Anagrafica destinatario);
        List<Spedizione> findByIntestatarioAndPubblicazione(Anagrafica intestatario, Pubblicazione pubblicazione);
        List<Spedizione> findByDestinatarioAndPubblicazione(Anagrafica destinatario, Pubblicazione pubblicazione);
        List<Spedizione> findByIntestatarioAndDestinatarioAndPubblicazione(Anagrafica intestatario, Anagrafica destinatario, Pubblicazione pubblicazione);
        List<Spedizione> findByOmaggio(Omaggio omaggio);
        List<Spedizione> findByInvio(Invio invio);
}
