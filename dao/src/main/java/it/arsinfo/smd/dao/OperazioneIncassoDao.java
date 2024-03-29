package it.arsinfo.smd.dao;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import it.arsinfo.smd.entity.StatoOperazioneIncasso;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.OperazioneIncasso;
import it.arsinfo.smd.entity.Versamento;

public interface OperazioneIncassoDao extends JpaRepository<OperazioneIncasso, Long> {

	List<OperazioneIncasso> findByDescriptionContainingIgnoreCase(String descr);
	List<OperazioneIncasso> findByAbbonamento(Abbonamento abbonamento);
	List<OperazioneIncasso> findByVersamento(Versamento versamento);
	List<OperazioneIncasso> findByAbbonamentoAndVersamento(Abbonamento abbonamento, Versamento versamento);
	List<OperazioneIncasso> findByAbbonamentoAndVersamentoAndStatoOperazioneIncasso(Abbonamento abbonamento, Versamento versamento,StatoOperazioneIncasso statoOperazioneIncasso );
	List<OperazioneIncasso> findByDataAfter(Date date);
}
