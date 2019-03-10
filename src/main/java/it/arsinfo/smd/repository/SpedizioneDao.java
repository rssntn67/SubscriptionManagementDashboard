package it.arsinfo.smd.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.entity.Spedizione;

public interface SpedizioneDao extends JpaRepository<Spedizione, Long> {

	List<Spedizione> findByAbbonamento(Abbonamento abbonamento);
        List<Spedizione> findByPubblicazione(Pubblicazione pubblicazione);
        List<Spedizione> findByDestinatario(Anagrafica destinatario);

}
