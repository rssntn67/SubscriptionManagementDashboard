package it.arsinfo.smd.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import it.arsinfo.smd.entity.Prospetto;
import it.arsinfo.smd.entity.ProspettoItem;

public interface ProspettoItemDao extends JpaRepository<ProspettoItem, Long> {

	List<ProspettoItem> findByProspetto(Prospetto prospetto);
	Long deleteByProspetto(Prospetto prospetto);
}
