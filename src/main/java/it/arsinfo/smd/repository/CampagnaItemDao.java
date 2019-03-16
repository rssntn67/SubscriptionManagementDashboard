package it.arsinfo.smd.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import it.arsinfo.smd.entity.Campagna;
import it.arsinfo.smd.entity.CampagnaItem;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.entity.Spedizione;

public interface CampagnaItemDao extends JpaRepository<CampagnaItem, Long> {

	List<Spedizione> findByCampagna(Campagna campagna);
        List<Spedizione> findByPubblicazione(Pubblicazione pubblicazione);

}
