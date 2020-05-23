package it.arsinfo.smd.dao.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import it.arsinfo.smd.data.Cassa;
import it.arsinfo.smd.data.InvioSpedizione;
import it.arsinfo.smd.data.TipoAbbonamentoRivista;
import it.arsinfo.smd.data.StatoStorico;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Storico;
import it.arsinfo.smd.entity.Pubblicazione;

public interface StoricoDao extends JpaRepository<Storico, Long> {

	List<Storico> findByIntestatario(Anagrafica intestatario);

	List<Storico> findByDestinatario(Anagrafica destinatario);

	List<Storico> findByPubblicazione(Pubblicazione pubblicazione);

	List<Storico> findByIntestatarioAndCassa(Anagrafica intestatario, Cassa cassa);

	List<Storico> findByIntestatarioAndDestinatario(Anagrafica intestatario, Anagrafica destinatario);

	List<Storico> findByIntestatarioAndPubblicazione(Anagrafica intestatario, Pubblicazione pubblicazione);

	List<Storico> findByDestinatarioAndPubblicazione(Anagrafica destinatario, Pubblicazione pubblicazione);

	List<Storico> findByIntestatarioAndDestinatarioAndPubblicazione(Anagrafica intestatario, Anagrafica destinatario,
			Pubblicazione pubblicazione);

	List<Storico> findByTipoAbbonamentoRivista(TipoAbbonamentoRivista tipo);

	List<Storico> findByCassa(Cassa cassa);
	List<Storico> findByInvioSpedizione(InvioSpedizione invioSpedizione);

	List<Storico> findByStatoStorico(StatoStorico statoStorico);
	List<Storico> findByStatoStoricoNotAndNumeroGreaterThan(StatoStorico statoStorico, Integer numero);

	List<Storico> findByDestinatarioOrIntestatario(Anagrafica a, Anagrafica a2);

}
