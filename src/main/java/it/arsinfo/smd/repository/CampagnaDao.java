package it.arsinfo.smd.repository;

import it.arsinfo.smd.entity.Anno;
import it.arsinfo.smd.entity.Campagna;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CampagnaDao extends JpaRepository<Campagna, Long> {

	List<Campagna> findByAnno(Anno anno);

}
