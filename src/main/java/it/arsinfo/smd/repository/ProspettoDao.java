package it.arsinfo.smd.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.Mese;
import it.arsinfo.smd.entity.Prospetto;
import it.arsinfo.smd.entity.Pubblicazione;

public interface ProspettoDao extends JpaRepository<Prospetto, Long> {

	List<Prospetto> findByAnno(Anno anno);
        List<Prospetto> findByAnnoAndMese(Anno anno,Mese mese);
        List<Prospetto> findByAnnoAndPubblicazione(Anno anno,Pubblicazione pubblicazione);
        List<Prospetto> findByAnnoAndMeseAndPubblicazione(Anno anno,Mese mese,Pubblicazione pubblicazione);
        List<Prospetto> findByMeseAndPubblicazione(Mese mese,Pubblicazione pubblicazione);
}
