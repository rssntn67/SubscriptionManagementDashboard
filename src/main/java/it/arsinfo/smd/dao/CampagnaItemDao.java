package it.arsinfo.smd.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import it.arsinfo.smd.entity.Campagna;
import it.arsinfo.smd.entity.CampagnaItem;
import it.arsinfo.smd.entity.Pubblicazione;

public interface CampagnaItemDao extends JpaRepository<CampagnaItem, Long> {

	List<CampagnaItem> findByCampagna(Campagna campagna);
        List<CampagnaItem> findByPubblicazione(Pubblicazione pubblicazione);
        Long deleteByCampagna(Campagna campagna);

}
