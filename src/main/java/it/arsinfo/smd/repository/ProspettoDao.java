package it.arsinfo.smd.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.Mese;
import it.arsinfo.smd.data.Omaggio;
import it.arsinfo.smd.entity.Prospetto;
import it.arsinfo.smd.entity.Pubblicazione;

public interface ProspettoDao extends JpaRepository<Prospetto, Long> {

    List<Prospetto> findByAnno(Anno anno);

    List<Prospetto> findByPubblicazione(Pubblicazione anno);

    List<Prospetto> findByMese(Mese anno);

    List<Prospetto> findByOmaggio(Omaggio anno);

    List<Prospetto> findByAnnoAndMese(Anno anno, Mese mese);

    List<Prospetto> findByAnnoAndPubblicazione(Anno anno,
            Pubblicazione pubblicazione);

    List<Prospetto> findByMeseAndPubblicazione(Mese mese,
            Pubblicazione pubblicazione);

    List<Prospetto> findByAnnoAndOmaggio(Anno anno, Omaggio omaggio);

    List<Prospetto> findByPubblicazioneAndOmaggio(Pubblicazione pubblicazione,
            Omaggio omaggio);

    List<Prospetto> findByMeseAndOmaggio(Mese anno, Omaggio omaggio);

    List<Prospetto> findByAnnoAndMeseAndPubblicazione(Anno anno, Mese mese,
            Pubblicazione pubblicazione);

    List<Prospetto> findByAnnoAndMeseAndOmaggio(Anno anno, Mese mese,
            Omaggio omaggio);

    List<Prospetto> findByAnnoAndPubblicazioneAndOmaggio(Anno anno,
            Pubblicazione pubblicazione, Omaggio omaggio);

    List<Prospetto> findByMeseAndPubblicazioneAndOmaggio(Mese mese,
            Pubblicazione pubblicazione, Omaggio omaggio);

    List<Prospetto> findByAnnoAndMeseAndPubblicazioneAndOmaggio(Anno anno,
            Mese mese, Pubblicazione pubblicazione, Omaggio omaggio);

    void deleteByAnnoAndMeseAndPubblicazioneAndOmaggio(Anno anno, Mese mese,
            Pubblicazione pubblicazione, Omaggio omaggio);
}
