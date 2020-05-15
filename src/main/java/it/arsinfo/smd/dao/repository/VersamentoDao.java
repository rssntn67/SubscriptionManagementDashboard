package it.arsinfo.smd.dao.repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import it.arsinfo.smd.entity.DistintaVersamento;
import it.arsinfo.smd.entity.Versamento;

public interface VersamentoDao extends JpaRepository<Versamento, Long> {

	List<Versamento> findByDistintaVersamento(DistintaVersamento incasso);
    List<Versamento> findByDataContabile(Date data);
    List<Versamento> findByDataPagamento(Date data);
    List<Versamento> findByImporto(BigDecimal importo);
    List<Versamento> findByCodeLineContainingIgnoreCase(String codeLine);
	
}
