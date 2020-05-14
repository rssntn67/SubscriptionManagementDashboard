package it.arsinfo.smd.dao;

import java.time.LocalDate;
import java.util.List;

import it.arsinfo.smd.entity.Versamento;

public interface VersamentoServiceDao extends SmdServiceDao<Versamento> {

	List<Versamento> searchBy(String importo, LocalDate dataContabile, LocalDate dataPagamento,
			String codeLine);	
}
