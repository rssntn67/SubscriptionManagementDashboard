package it.arsinfo.smd.repository;

import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Anagrafica;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AbbonamentoDao extends JpaRepository<Abbonamento, Long> {

	List<Abbonamento> findByAnagrafica(Anagrafica anagrafica);

}
