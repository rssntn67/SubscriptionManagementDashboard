package it.arsinfo.smd.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.AnagraficaPubblicazione;
import it.arsinfo.smd.entity.Pubblicazione;

public interface AnagraficaPubblicazioneDao extends JpaRepository<AnagraficaPubblicazione, Long> {

	List<AnagraficaPubblicazione> findByIntestatario(Anagrafica intestatario);
        List<AnagraficaPubblicazione> findByDestinatario(Anagrafica destinatario);
	List<AnagraficaPubblicazione> findByPubblicazione(Pubblicazione diocesi);

}
