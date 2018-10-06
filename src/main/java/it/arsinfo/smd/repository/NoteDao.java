package it.arsinfo.smd.repository;

import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Note;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface NoteDao extends JpaRepository<Note, Long> {

	List<Note> findByDescriptionStartsWithIgnoreCase(String descr);
	List<Note> findByAnagrafica(Anagrafica anagrafica);

}
