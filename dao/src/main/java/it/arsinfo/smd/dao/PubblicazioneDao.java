package it.arsinfo.smd.dao;

import it.arsinfo.smd.entity.TipoPubblicazione;
import it.arsinfo.smd.entity.Pubblicazione;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PubblicazioneDao extends JpaRepository<Pubblicazione, Long> {

	List<Pubblicazione> findByNomeStartsWithIgnoreCase(String nome);
	List<Pubblicazione> findByTipo(TipoPubblicazione tipo);
	List<Pubblicazione> findByTipoNot(TipoPubblicazione tipo);
	List<Pubblicazione> findByTipoAndActive(TipoPubblicazione tipo,boolean isActive);
	List<Pubblicazione> findByTipoNotAndActive(TipoPubblicazione tipo,boolean isActive);
}
