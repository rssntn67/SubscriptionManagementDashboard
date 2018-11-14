package it.arsinfo.smd.repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import it.arsinfo.smd.entity.ContoCorrentePostale;
import it.arsinfo.smd.entity.Incasso;
import it.arsinfo.smd.entity.Versamento;

public interface VersamentoDao extends JpaRepository<Versamento, Long> {

	List<Versamento> findByIncasso(Incasso incasso);
	List<Versamento> findByCcp(ContoCorrentePostale ccp);
        List<Versamento> findByDataContabile(Date date);
        List<Versamento> findByDataPagamento(Date date);
        List<Versamento> findByImporto(BigDecimal importo);
	
}
