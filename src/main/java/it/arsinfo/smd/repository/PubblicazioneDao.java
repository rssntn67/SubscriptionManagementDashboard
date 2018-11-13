package it.arsinfo.smd.repository;

import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.entity.TipoPubblicazione;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PubblicazioneDao extends JpaRepository<Pubblicazione, Long> {

	List<Pubblicazione> findByNomeStartsWithIgnoreCase(String nome);
	List<Pubblicazione> findByTipo(TipoPubblicazione tipo);

}
