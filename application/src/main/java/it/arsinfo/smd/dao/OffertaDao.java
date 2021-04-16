package it.arsinfo.smd.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import it.arsinfo.smd.entity.OfferteCumulate;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Offerta;
import it.arsinfo.smd.entity.Versamento;

public interface OffertaDao extends JpaRepository<Offerta, Long> {

	List<Offerta> findByOfferteCumulate(OfferteCumulate abbonamento);
	List<Offerta> findByVersamento(Versamento versamento);
	List<Offerta> findByCommittente(Anagrafica committente);
	List<Offerta> findByOfferteCumulateAndVersamento(OfferteCumulate abbonamento, Versamento versamento);

}
