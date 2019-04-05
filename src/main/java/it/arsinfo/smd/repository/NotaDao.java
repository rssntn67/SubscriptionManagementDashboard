package it.arsinfo.smd.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import it.arsinfo.smd.entity.Nota;
import it.arsinfo.smd.entity.Storico;

public interface NotaDao extends JpaRepository<Nota, Long> {

	List<Nota> findByDescriptionContainingIgnoreCase(String descr);
	List<Nota> findByStorico(Storico storico);

}
