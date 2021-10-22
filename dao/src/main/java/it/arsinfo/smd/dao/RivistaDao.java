package it.arsinfo.smd.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import it.arsinfo.smd.entity.Anno;
import it.arsinfo.smd.entity.StatoRivista;
import it.arsinfo.smd.entity.TipoAbbonamentoRivista;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.entity.Rivista;
import it.arsinfo.smd.entity.Storico;

public interface RivistaDao extends JpaRepository<Rivista, Long> {

	List<Rivista> findByAbbonamento(Abbonamento abbonamento);
	List<Rivista> findByAbbonamentoAndPubblicazione(Abbonamento abbonamento, Pubblicazione p);
	List<Rivista> findByAbbonamentoAndTipoAbbonamentoRivista(Abbonamento abbonamento, TipoAbbonamentoRivista tipo);
    List<Rivista> findByDestinatario(Anagrafica destinatario);
    List<Rivista> findByStorico(Storico storico);
    List<Rivista> findByStoricoAndAnnoInizioAndAnnoFine(Storico storico, Anno inizio, Anno fine);
    List<Rivista> findByPubblicazione(Pubblicazione pubblicazione);
    List<Rivista> findByTipoAbbonamentoRivista(TipoAbbonamentoRivista tipo);
	List<Rivista> findByAbbonamentoAndStatoRivista(Abbonamento abbonamento, StatoRivista stato);
    List<Rivista> findByStatoRivista(StatoRivista stato);
    List<Rivista> findByStatoRivistaOrStatoRivista(StatoRivista statoA, StatoRivista statoB);
    List<Rivista> findByPubblicazioneAndTipoAbbonamentoRivista(Pubblicazione pubblicazione, TipoAbbonamentoRivista t);
    List<Rivista> findByPubblicazioneAndStatoRivista(Pubblicazione pubblicazione, StatoRivista s);
    List<Rivista> findByTipoAbbonamentoRivistaAndStatoRivista(TipoAbbonamentoRivista t, StatoRivista s);
    List<Rivista> findByPubblicazioneAndTipoAbbonamentoRivistaAndStatoRivista(Pubblicazione pubblicazione, TipoAbbonamentoRivista t, StatoRivista s);
    void deleteByAbbonamento(Abbonamento abbonamento);
	List<Rivista> findByDestinatarioAndPubblicazioneAndTipoAbbonamentoRivistaAndStatoRivista(
			Anagrafica dst, Pubblicazione p, TipoAbbonamentoRivista tipo, StatoRivista stato);
	List<Rivista> findByDestinatarioAndTipoAbbonamentoRivista(Anagrafica dst,
                                                              TipoAbbonamentoRivista tipo);
	List<Rivista> findByDestinatarioAndPubblicazione(Anagrafica dst, Pubblicazione p);
	List<Rivista> findByDestinatarioAndStatoRivista(Anagrafica dst, StatoRivista stato);
	List<Rivista> findByDestinatarioAndTipoAbbonamentoRivistaAndStatoRivista(Anagrafica dst,
                                                                             TipoAbbonamentoRivista tipo, StatoRivista stato);
	List<Rivista> findByDestinatarioAndPubblicazioneAndStatoRivista(Anagrafica dst,
                                                                    Pubblicazione p, StatoRivista stato);
	List<Rivista> findByDestinatarioAndPubblicazioneAndTipoAbbonamentoRivista(Anagrafica dst,
                                                                              Pubblicazione p, TipoAbbonamentoRivista tipo);
}
