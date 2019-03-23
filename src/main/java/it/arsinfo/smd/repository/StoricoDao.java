package it.arsinfo.smd.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import it.arsinfo.smd.data.Omaggio;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Storico;
import it.arsinfo.smd.entity.Pubblicazione;

public interface StoricoDao extends JpaRepository<Storico, Long> {

	List<Storico> findByIntestatario(Anagrafica intestatario);
        List<Storico> findByDestinatario(Anagrafica destinatario);
	List<Storico> findByPubblicazione(Pubblicazione diocesi);
        List<Storico> findByOmaggio(Omaggio omaggio);

}
