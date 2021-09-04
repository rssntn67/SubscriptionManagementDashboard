package it.arsinfo.smd.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import it.arsinfo.smd.entity.Anno;
import it.arsinfo.smd.entity.OfferteCumulate;

public interface OfferteCumulateDao extends JpaRepository<OfferteCumulate, Long> {

	OfferteCumulate findByAnno(Anno anno);


}
