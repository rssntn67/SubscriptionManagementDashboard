package it.arsinfo.smd.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Campagna;
import it.arsinfo.smd.entity.Versamento;

public interface AbbonamentoDao extends JpaRepository<Abbonamento, Long> {

	List<Abbonamento> findByIntestatario(Anagrafica intestatario);
	List<Abbonamento> findByCampagna(Campagna campagna);
	List<Abbonamento> findByVersamento(Versamento versamento);
	List<Abbonamento> findByCampo(String campo);
	List<Abbonamento> findByCostoGreaterThanAndVersamentoNotNull(BigDecimal costo);
	Long deleteByCampagna(Campagna campagna);

}
