package it.arsinfo.smd.dao.repository;

import it.arsinfo.smd.data.TipoPubblicazione;
import it.arsinfo.smd.entity.Pubblicazione;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PubblicazioneDao extends JpaRepository<Pubblicazione, Long> {

	List<Pubblicazione> findByNomeStartsWithIgnoreCase(String nome);
	List<Pubblicazione> findByTipo(TipoPubblicazione tipo);
	List<Pubblicazione> findNotByTipo(TipoPubblicazione tipo);
	List<Pubblicazione> findByTipoAndActive(TipoPubblicazione tipo,boolean isActive);
	List<Pubblicazione> findNotByTipoAndActive(TipoPubblicazione tipo,boolean isActive);
}
