package it.arsinfo.smd.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import it.arsinfo.smd.entity.StatoCampagna;
import it.arsinfo.smd.entity.Campagna;
import it.arsinfo.smd.entity.OperazioneCampagna;

public interface OperazioneCampagnaDao extends JpaRepository<OperazioneCampagna, Long> {

	OperazioneCampagna findUniqueByCampagnaAndStato(Campagna c, StatoCampagna s);
	List<OperazioneCampagna> findByCampagna(Campagna c);
    List<OperazioneCampagna> findByStato(StatoCampagna s);
}
