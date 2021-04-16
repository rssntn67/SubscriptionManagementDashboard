package it.arsinfo.smd.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.StatoCampagna;
import it.arsinfo.smd.entity.Campagna;

public interface CampagnaDao extends JpaRepository<Campagna, Long> {

	Campagna findByAnno(Anno anno);
	List<Campagna> findByStatoCampagna(StatoCampagna stato);
	List<Campagna> findByStatoCampagnaNot(StatoCampagna stato);

}
