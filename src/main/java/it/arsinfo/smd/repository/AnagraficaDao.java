package it.arsinfo.smd.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import it.arsinfo.smd.data.Diocesi;
import it.arsinfo.smd.entity.Anagrafica;

public interface AnagraficaDao extends JpaRepository<Anagrafica, Long> {

	List<Anagrafica> findByCognomeStartsWithIgnoreCase(String lastName);
	List<Anagrafica> findByDiocesi(Diocesi diocesi);

}
