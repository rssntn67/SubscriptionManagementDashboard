package it.arsinfo.smd.dao.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.StatoAbbonamento;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Campagna;

public interface AbbonamentoDao extends JpaRepository<Abbonamento, Long> {

    Abbonamento findByIntestatarioAndCampagnaAndContrassegno(Anagrafica intestatario, Campagna campagna, boolean contrassegno);
	List<Abbonamento> findByIntestatario(Anagrafica intestatario);
    List<Abbonamento> findByIntestatarioAndAnnoAndContrassegno(Anagrafica intestatario, Anno anno, boolean contrassegno);
    List<Abbonamento> findByIntestatarioAndAnno(Anagrafica intestatario, Anno anno);
	List<Abbonamento> findByCampagna(Campagna campagna);
	List<Abbonamento> findByCampagnaAndAnno(Campagna campagna, Anno anno);
	List<Abbonamento> findByCampagnaAndStatoAbbonamento(Campagna campagna,StatoAbbonamento statoAbbonamento);
    List<Abbonamento> findByIntestatarioAndCampagna(Anagrafica intestatario, Campagna campagna);
    List<Abbonamento> findByIntestatarioAndCampagnaAndAnno(Anagrafica intestatario, Campagna campagna,Anno anno);
	Abbonamento findByCodeLine(String codeLine);
    List<Abbonamento> findByContrassegno(boolean contrassegno);
    List<Abbonamento> findByAnno(Anno anno);
	Long deleteByCampagna(Campagna campagna);
	
	@Query("SELECT a FROM Abbonamento a WHERE a.importo+a.spese+a.pregresso+a.speseEstero+a.speseEstrattoConto-a.incassato > 0 AND a.anno = ?1 ")
	List<Abbonamento> findWithResiduoAndAnno(Anno anno);

}
