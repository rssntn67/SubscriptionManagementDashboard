package it.arsinfo.smd.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.AbbonamentoPubblicazione;
import it.arsinfo.smd.entity.Pubblicazione;

public interface AbbonamentoPubblicazioneDao extends JpaRepository<AbbonamentoPubblicazione, Long> {

	List<AbbonamentoPubblicazione> findByAbbonamento(Abbonamento abbonamento);
        List<AbbonamentoPubblicazione> findByPubblicazione(Pubblicazione pubblicazione);

}
