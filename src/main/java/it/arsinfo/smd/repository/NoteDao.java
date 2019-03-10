package it.arsinfo.smd.repository;

import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Nota;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface NoteDao extends JpaRepository<Nota, Long> {

	List<Nota> findByDescriptionStartsWithIgnoreCase(String descr);
	List<Nota> findByAnagrafica(Anagrafica anagrafica);

}
