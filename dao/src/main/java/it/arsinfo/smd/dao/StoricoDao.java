package it.arsinfo.smd.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import it.arsinfo.smd.entity.InvioSpedizione;
import it.arsinfo.smd.entity.TipoAbbonamentoRivista;
import it.arsinfo.smd.entity.StatoStorico;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Storico;
import it.arsinfo.smd.entity.Pubblicazione;

public interface StoricoDao extends JpaRepository<Storico, Long> {

	List<Storico> findByIntestatario(Anagrafica intestatario);

	List<Storico> findByDestinatario(Anagrafica destinatario);

	List<Storico> findByPubblicazione(Pubblicazione pubblicazione);

	List<Storico> findByIntestatarioAndContrassegno(Anagrafica intestatario, boolean contrassegno);
	List<Storico> findByIntestatarioAndContrassegnoAndStatoStorico(Anagrafica intestatario, boolean contrassegno, StatoStorico stato);

	List<Storico> findByIntestatarioAndDestinatario(Anagrafica intestatario, Anagrafica destinatario);

	List<Storico> findByIntestatarioAndPubblicazione(Anagrafica intestatario, Pubblicazione pubblicazione);

	List<Storico> findByDestinatarioAndPubblicazione(Anagrafica destinatario, Pubblicazione pubblicazione);

	List<Storico> findByIntestatarioAndDestinatarioAndPubblicazione(Anagrafica intestatario, Anagrafica destinatario,
			Pubblicazione pubblicazione);

	List<Storico> findByTipoAbbonamentoRivista(TipoAbbonamentoRivista tipo);

	List<Storico> findByContrassegno(boolean contrassegno);
	List<Storico> findByInvioSpedizione(InvioSpedizione invioSpedizione);

	List<Storico> findByStatoStorico(StatoStorico statoStorico);
	List<Storico> findByNumero(Integer numero);
	List<Storico> findByStatoStoricoNotAndNumeroGreaterThan(StatoStorico statoStorico, Integer numero);

	List<Storico> findByDestinatarioOrIntestatario(Anagrafica a, Anagrafica a2);

}
