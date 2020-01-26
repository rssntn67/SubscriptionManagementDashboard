package it.arsinfo.smd.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.entity.OperazioneSospendi;
import it.arsinfo.smd.entity.Pubblicazione;

public interface OperazioneSospendiDao extends JpaRepository<OperazioneSospendi, Long> {

	OperazioneSospendi findUniqueByAnnoAndPubblicazione(Anno anno, Pubblicazione pubblicazione);
	List<OperazioneSospendi> findByAnno(Anno anno);
    List<OperazioneSospendi> findByPubblicazione(Pubblicazione anno);
}
