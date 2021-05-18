package it.arsinfo.smd.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import it.arsinfo.smd.data.Diocesi;
import it.arsinfo.smd.entity.Anagrafica;

public interface AnagraficaDao extends JpaRepository<Anagrafica, Long> {

	Anagrafica findByCodeLineBase(String code);

	List<Anagrafica> findByDenominazioneContainingIgnoreCase(String denominazione);

	List<Anagrafica> findByNomeContainingIgnoreCase(String nome);

	List<Anagrafica> findByCapContainingIgnoreCase(String cap);

	List<Anagrafica> findByDiocesi(Diocesi diocesi);

	List<Anagrafica> findByCo(Anagrafica co);

	List<Anagrafica> findByCittaContainingIgnoreCase(String citta);

	List<Anagrafica> findByDiocesiAndNomeContainingIgnoreCase(Diocesi diocesi, String nome);

	List<Anagrafica> findByDiocesiAndDenominazioneContainingIgnoreCase(Diocesi diocesi, String denominazione);

	List<Anagrafica> findByDiocesiAndCittaContainingIgnoreCase(Diocesi diocesi, String citta);

	List<Anagrafica> findByDiocesiAndCapContainingIgnoreCase(Diocesi diocesi, String cap);

	List<Anagrafica> findByNomeContainingIgnoreCaseAndCittaContainingIgnoreCase(String nome, String citta);

	List<Anagrafica> findByNomeContainingIgnoreCaseAndDenominazioneContainingIgnoreCase(String nome,
			String denominazione);

	List<Anagrafica> findByNomeContainingIgnoreCaseAndCapContainingIgnoreCase(String nome, String cap);

	List<Anagrafica> findByDenominazioneContainingIgnoreCaseAndCittaContainingIgnoreCase(String denominazione,
			String citta);

	List<Anagrafica> findByDenominazioneContainingIgnoreCaseAndCapContainingIgnoreCase(String denominazione,
			String cap);

	List<Anagrafica> findByCittaContainingIgnoreCaseAndCapContainingIgnoreCase(String citta, String cap);

	List<Anagrafica> findByDiocesiAndNomeContainingIgnoreCaseAndDenominazioneIgnoreCase(Diocesi diocesi, String nome,
			String denominazione);

	List<Anagrafica> findByDiocesiAndNomeContainingIgnoreCaseAndCittaIgnoreCase(Diocesi diocesi, String nome,
			String citta);

	List<Anagrafica> findByDiocesiAndNomeContainingIgnoreCaseAndCapIgnoreCase(Diocesi diocesi, String nome,
			String cap);

	List<Anagrafica> findByDiocesiAndDenominazioneContainingIgnoreCaseAndCittaIgnoreCase(Diocesi diocesi,
			String denominazione, String citta);

	List<Anagrafica> findByDiocesiAndDenominazioneContainingIgnoreCaseAndCapIgnoreCase(Diocesi diocesi,
			String denominazione, String cap);

	List<Anagrafica> findByDiocesiAndCittaContainingIgnoreCaseAndCapIgnoreCase(Diocesi diocesi, String citta,
			String cap);

	List<Anagrafica> findByNomeContainingIgnoreCaseAndDenominazioneContainingIgnoreCaseAndCittaContainingIgnoreCase(
			String nome, String denominazione, String citta);

	List<Anagrafica> findByNomeContainingIgnoreCaseAndDenominazioneContainingIgnoreCaseAndCapContainingIgnoreCase(
			String nome, String denominazione, String cap);

	List<Anagrafica> findByNomeContainingIgnoreCaseAndCittaContainingIgnoreCaseAndCapContainingIgnoreCase(String nome,
			String citta, String cap);

	List<Anagrafica> findByDenominazioneContainingIgnoreCaseAndCittaContainingIgnoreCaseAndCapContainingIgnoreCase(
			String denominazione, String citta, String cap);

	List<Anagrafica> findByDiocesiAndNomeContainingIgnoreCaseAndDenominazioneContainingIgnoreCaseAndCapIgnoreCase(
			Diocesi diocesi, String nome, String denominazione, String cap);

	List<Anagrafica> findByDiocesiAndNomeContainingIgnoreCaseAndCittaContainingIgnoreCaseAndCapIgnoreCase(
			Diocesi diocesi, String nome, String citta, String cap);

	List<Anagrafica> findByDiocesiAndDenominazioneContainingIgnoreCaseAndCittaContainingIgnoreCaseAndCapIgnoreCase(
			Diocesi diocesi, String denominazione, String citta, String cap);

	List<Anagrafica> findByNomeContainingIgnoreCaseAndDenominazioneContainingIgnoreCaseAndCittaContainingIgnoreCaseAndCapContainingIgnoreCase(
			String nome, String denominazione, String citta, String cap);

	List<Anagrafica> findByDiocesiAndNomeContainingIgnoreCaseAndDenominazioneContainingIgnoreCaseAndCittaContainingIgnoreCaseAndCapIgnoreCase(
			Diocesi diocesi, String nome, String denominazione, String citta, String cap);

}
