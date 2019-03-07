package it.arsinfo.smd.repository;

import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Campagna;
import it.arsinfo.smd.entity.Versamento;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AbbonamentoDao extends JpaRepository<Abbonamento, Long> {

	List<Abbonamento> findByIntestatario(Anagrafica intestatario);
	List<Abbonamento> findByCampagna(Campagna campagna);
	List<Abbonamento> findByVersamento(Versamento versamento);
	List<Abbonamento> findByCampo(String campo);
	List<Abbonamento> findByPagato(boolean pagato);

}
