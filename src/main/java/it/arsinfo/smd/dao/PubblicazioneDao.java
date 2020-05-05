package it.arsinfo.smd.dao;

import it.arsinfo.smd.data.TipoPubblicazione;
import it.arsinfo.smd.entity.Pubblicazione;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PubblicazioneDao extends JpaRepository<Pubblicazione, Long> {

	List<Pubblicazione> findByNomeStartsWithIgnoreCase(String nome);
	List<Pubblicazione> findByTipo(TipoPubblicazione tipo);
}
