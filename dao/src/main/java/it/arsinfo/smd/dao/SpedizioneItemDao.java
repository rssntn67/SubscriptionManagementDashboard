package it.arsinfo.smd.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import it.arsinfo.smd.entity.Anno;
import it.arsinfo.smd.entity.Mese;
import it.arsinfo.smd.entity.StatoSpedizione;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.entity.Rivista;
import it.arsinfo.smd.entity.Spedizione;
import it.arsinfo.smd.entity.SpedizioneItem;

public interface SpedizioneItemDao extends JpaRepository<SpedizioneItem, Long> {

    List<SpedizioneItem> findBySpedizione(Spedizione spedizione);
    List<SpedizioneItem> findBySpedizioneAndStatoSpedizione(Spedizione spedizione, StatoSpedizione statoSpedizione);
    List<SpedizioneItem> findBySpedizioneAndStatoSpedizioneAndRivista(Spedizione spedizione, StatoSpedizione statoSpedizione, Rivista ec);
    List<SpedizioneItem> findBySpedizioneAndStatoSpedizioneAndPubblicazione(Spedizione spedizione, StatoSpedizione statoSpedizione, Pubblicazione p);
    List<SpedizioneItem> findByAnnoPubblicazione(Anno anno);
    List<SpedizioneItem> findByPubblicazione(Pubblicazione p);
    List<SpedizioneItem> findByPubblicazioneAndStatoSpedizione(Pubblicazione p, StatoSpedizione stato);
    List<SpedizioneItem> findByMesePubblicazione(Mese mese);
    List<SpedizioneItem> findByNumero(Integer numero);
    List<SpedizioneItem> findByRivista(Rivista ec);
    List<SpedizioneItem> findByStatoSpedizione(StatoSpedizione statoSpedizione);
    List<SpedizioneItem> findByRivistaAndStatoSpedizione(Rivista ec, StatoSpedizione statoSpedizione);

}
