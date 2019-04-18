package it.arsinfo.smd.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import it.arsinfo.smd.data.Cassa;
import it.arsinfo.smd.data.Invio;
import it.arsinfo.smd.data.Omaggio;
import it.arsinfo.smd.data.StatoStorico;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Storico;
import it.arsinfo.smd.entity.Pubblicazione;

public interface StoricoDao extends JpaRepository<Storico, Long> {

	List<Storico> findByIntestatario(Anagrafica intestatario);
        List<Storico> findByDestinatario(Anagrafica destinatario);
	List<Storico> findByPubblicazione(Pubblicazione pubblicazione);
	List<Storico> findByIntestatarioAndDestinatario(Anagrafica intestatario, Anagrafica destinatario);
        List<Storico> findByIntestatarioAndPubblicazione(Anagrafica intestatario, Pubblicazione pubblicazione);
        List<Storico> findByDestinatarioAndPubblicazione(Anagrafica destinatario, Pubblicazione pubblicazione);
        List<Storico> findByIntestatarioAndDestinatarioAndPubblicazione(Anagrafica intestatario, Anagrafica destinatario, Pubblicazione pubblicazione);
        List<Storico> findByOmaggio(Omaggio omaggio);
        List<Storico> findByCassa(Cassa cassa);
        List<Storico> findByInvio(Invio invio);
        List<Storico> findByStatoStorico(StatoStorico statoStorico);

}
