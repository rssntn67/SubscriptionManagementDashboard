package it.arsinfo.smd.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.Mese;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.entity.SpedizioneItem;

public interface SpedizioneItemDao extends JpaRepository<SpedizioneItem, Long> {

    List<SpedizioneItem> findByAbbonamento(Abbonamento abbonamento);
    List<SpedizioneItem> findByAnnoPubblicazione(Anno anno);
    List<SpedizioneItem> findByMesePubblicazione(Mese mese);
    List<SpedizioneItem> findByMesePubblicazioneAndAnnoPubblicazione(Mese mese,
            Anno anno);
    List<SpedizioneItem> findByNumero(Integer numero);
    List<SpedizioneItem> findByPubblicazione(Pubblicazione pubblicazione);
    List<SpedizioneItem> findByPubblicazioneAndNumero(Pubblicazione pubblicazione, Integer numero);

}
