package it.arsinfo.smd.dao.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.StatoRivista;
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
	List<RivistaAbbonamento> findByAbbonamentoAndStatoRivista(Abbonamento abbonamento, StatoRivista stato);
    List<RivistaAbbonamento> findByStatoRivista(StatoRivista stato);
    List<RivistaAbbonamento> findByStatoRivistaOrStatoRivista(StatoRivista statoA, StatoRivista statoB);
    List<RivistaAbbonamento> findByPubblicazioneAndTipoAbbonamentoRivista(Pubblicazione pubblicazione, TipoAbbonamentoRivista t);
    List<RivistaAbbonamento> findByPubblicazioneAndStatoRivista(Pubblicazione pubblicazione, StatoRivista s);
    List<RivistaAbbonamento> findByTipoAbbonamentoRivistaAndStatoRivista(TipoAbbonamentoRivista t, StatoRivista s);
    List<RivistaAbbonamento> findByPubblicazioneAndTipoAbbonamentoRivistaAndStatoRivista(Pubblicazione pubblicazione, TipoAbbonamentoRivista t, StatoRivista s);
    void deleteByAbbonamento(Abbonamento abbonamento);
	List<RivistaAbbonamento> findByDestinatarioAndPubblicazioneAndTipoAbbonamentoRivistaAndStatoRivista(
			Anagrafica dst, Pubblicazione p, TipoAbbonamentoRivista tipo, StatoRivista stato);
	List<RivistaAbbonamento> findByDestinatarioAndTipoAbbonamentoRivista(Anagrafica dst,
			TipoAbbonamentoRivista tipo);
	List<RivistaAbbonamento> findByDestinatarioAndPubblicazione(Anagrafica dst, Pubblicazione p);
	List<RivistaAbbonamento> findByDestinatarioAndStatoRivista(Anagrafica dst, StatoRivista stato);
	List<RivistaAbbonamento> findByDestinatarioAndTipoAbbonamentoRivistaAndStatoRivista(Anagrafica dst,
			TipoAbbonamentoRivista tipo, StatoRivista stato);
	List<RivistaAbbonamento> findByDestinatarioAndPubblicazioneAndStatoRivista(Anagrafica dst,
			Pubblicazione p, StatoRivista stato);
	List<RivistaAbbonamento> findByDestinatarioAndPubblicazioneAndTipoAbbonamentoRivista(Anagrafica dst,
			Pubblicazione p, TipoAbbonamentoRivista tipo);
}
