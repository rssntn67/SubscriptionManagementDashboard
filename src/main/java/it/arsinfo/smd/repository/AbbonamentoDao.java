package it.arsinfo.smd.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.Cassa;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Campagna;
import it.arsinfo.smd.entity.Versamento;

public interface AbbonamentoDao extends JpaRepository<Abbonamento, Long> {

        Abbonamento findByIntestatarioAndCampagnaAndCassa(Anagrafica intestatario, Campagna campagna, Cassa cassa);
	List<Abbonamento> findByIntestatario(Anagrafica intestatario);
	List<Abbonamento> findByCampagna(Campagna campagna);
        List<Abbonamento> findByIntestatarioAndCampagna(Anagrafica intestatario, Campagna campagna);
	List<Abbonamento> findByVersamento(Versamento versamento);
	List<Abbonamento> findByCampo(String campo);
        List<Abbonamento> findByCassa(Cassa cassa);
        List<Abbonamento> findByAnno(Anno anno);
	Long deleteByCampagna(Campagna campagna);

}
