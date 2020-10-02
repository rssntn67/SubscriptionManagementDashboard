package it.arsinfo.smd.dao.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.StatoAbbonamento;
import it.arsinfo.smd.data.TipoAbbonamentoRivista;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.entity.RivistaAbbonamento;
import it.arsinfo.smd.entity.Storico;

public interface RivistaAbbonamentoDao extends JpaRepository<RivistaAbbonamento, Long> {

	List<RivistaAbbonamento> findByAbbonamento(Abbonamento abbonamento);
	List<RivistaAbbonamento> findByAbbonamentoAndPubblicazione(Abbonamento abbonamento, Pubblicazione p);
	List<RivistaAbbonamento> findByAbbonamentoAndTipoAbbonamentoRivista(Abbonamento abbonamento, TipoAbbonamentoRivista tipo);
    List<RivistaAbbonamento> findByDestinatario(Anagrafica destinatario);
    List<RivistaAbbonamento> findByStorico(Storico storico);
    List<RivistaAbbonamento> findByStoricoAndAnnoInizioAndAnnoFine(Storico storico, Anno inizio, Anno fine);
    List<RivistaAbbonamento> findByPubblicazione(Pubblicazione pubblicazione);
    List<RivistaAbbonamento> findByTipoAbbonamentoRivista(TipoAbbonamentoRivista tipo);
	List<RivistaAbbonamento> findByAbbonamentoAndStatoAbbonamento(Abbonamento abbonamento, StatoAbbonamento stato);
    List<RivistaAbbonamento> findByStatoAbbonamento(StatoAbbonamento stato);
    List<RivistaAbbonamento> findByStatoAbbonamentoOrStatoAbbonamento(StatoAbbonamento statoA, StatoAbbonamento statoB);
    List<RivistaAbbonamento> findByPubblicazioneAndTipoAbbonamentoRivista(Pubblicazione pubblicazione, TipoAbbonamentoRivista t);
    List<RivistaAbbonamento> findByPubblicazioneAndStatoAbbonamento(Pubblicazione pubblicazione, StatoAbbonamento s);
    List<RivistaAbbonamento> findByTipoAbbonamentoRivistaAndStatoAbbonamento(TipoAbbonamentoRivista t, StatoAbbonamento s);
    List<RivistaAbbonamento> findByPubblicazioneAndTipoAbbonamentoRivistaAndStatoAbbonamento(Pubblicazione pubblicazione, TipoAbbonamentoRivista t, StatoAbbonamento s);
    void deleteByAbbonamento(Abbonamento abbonamento);
	List<RivistaAbbonamento> findByDestinatarioAndPubblicazioneAndTipoAbbonamentoRivistaAndStatoAbbonamento(
			Anagrafica dst, Pubblicazione p, TipoAbbonamentoRivista tipo, StatoAbbonamento stato);
	List<RivistaAbbonamento> findByDestinatarioAndTipoAbbonamentoRivista(Anagrafica dst,
			TipoAbbonamentoRivista tipo);
	List<RivistaAbbonamento> findByDestinatarioAndPubblicazione(Anagrafica dst, Pubblicazione p);
	List<RivistaAbbonamento> findByDestinatarioAndStatoAbbonamento(Anagrafica dst, StatoAbbonamento stato);
	List<RivistaAbbonamento> findByDestinatarioAndTipoAbbonamentoRivistaAndStatoAbbonamento(Anagrafica dst,
			TipoAbbonamentoRivista tipo, StatoAbbonamento stato);
	List<RivistaAbbonamento> findByDestinatarioAndPubblicazioneAndStatoAbbonamento(Anagrafica dst,
			Pubblicazione p, StatoAbbonamento stato);
	List<RivistaAbbonamento> findByDestinatarioAndPubblicazioneAndTipoAbbonamentoRivista(Anagrafica dst,
			Pubblicazione p, TipoAbbonamentoRivista tipo);
}
