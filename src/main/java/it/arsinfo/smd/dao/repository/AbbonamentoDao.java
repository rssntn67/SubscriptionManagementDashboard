package it.arsinfo.smd.dao.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.Cassa;
import it.arsinfo.smd.data.StatoAbbonamento;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Campagna;

public interface AbbonamentoDao extends JpaRepository<Abbonamento, Long> {

    Abbonamento findByIntestatarioAndCampagnaAndCassa(Anagrafica intestatario, Campagna campagna, Cassa cassa);
	List<Abbonamento> findByIntestatario(Anagrafica intestatario);
    List<Abbonamento> findByIntestatarioAndAnnoAndCassa(Anagrafica intestatario, Anno anno, Cassa cassa);
	List<Abbonamento> findByCampagna(Campagna campagna);
	List<Abbonamento> findByCampagnaAndStatoAbbonamento(Campagna campagna,StatoAbbonamento statoAbbonamento);
    List<Abbonamento> findByIntestatarioAndCampagna(Anagrafica intestatario, Campagna campagna);
	Abbonamento findByCodeLine(String codeLine);
        List<Abbonamento> findByCassa(Cassa cassa);
        List<Abbonamento> findByAnno(Anno anno);
	Long deleteByCampagna(Campagna campagna);

}
