package it.arsinfo.smd.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import it.arsinfo.smd.data.Diocesi;
import it.arsinfo.smd.entity.Anagrafica;

public interface AnagraficaDao extends JpaRepository<Anagrafica, Long> {

	List<Anagrafica> findByDenominazioneContainingIgnoreCase(String denominazione);
    List<Anagrafica> findByNomeContainingIgnoreCase(String nome);
    List<Anagrafica> findByCapContainingIgnoreCase(String cap);
	List<Anagrafica> findByDiocesi(Diocesi diocesi);
    List<Anagrafica> findByCo(Anagrafica co);
    List<Anagrafica> findByCittaContainingIgnoreCase(
                String citta);

}
