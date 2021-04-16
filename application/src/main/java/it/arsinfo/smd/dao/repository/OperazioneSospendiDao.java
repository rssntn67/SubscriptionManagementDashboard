package it.arsinfo.smd.dao.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import it.arsinfo.smd.entity.Campagna;
import it.arsinfo.smd.entity.OperazioneSospendi;
import it.arsinfo.smd.entity.Pubblicazione;

public interface OperazioneSospendiDao extends JpaRepository<OperazioneSospendi, Long> {

	OperazioneSospendi findUniqueByCampagnaAndPubblicazione(Campagna c, Pubblicazione p);
	List<OperazioneSospendi> findByCampagna(Campagna c);
    List<OperazioneSospendi> findByPubblicazione(Pubblicazione p);
}
