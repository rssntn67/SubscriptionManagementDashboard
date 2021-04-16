package it.arsinfo.smd.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.entity.OfferteCumulate;

public interface OfferteCumulateDao extends JpaRepository<OfferteCumulate, Long> {

	OfferteCumulate findByAnno(Anno anno);


}
