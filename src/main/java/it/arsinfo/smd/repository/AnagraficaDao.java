package it.arsinfo.smd.repository;

import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Anagrafica.BmCassa;
import it.arsinfo.smd.entity.Anagrafica.Diocesi;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AnagraficaDao extends JpaRepository<Anagrafica, Long> {

	List<Anagrafica> findByCognomeStartsWithIgnoreCase(String lastName);
	List<Anagrafica> findByDiocesi(Diocesi diocesi);
	List<Anagrafica> findByBmCassa(BmCassa bmcassa);

}
