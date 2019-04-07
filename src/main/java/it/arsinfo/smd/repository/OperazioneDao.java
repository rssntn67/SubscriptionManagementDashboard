package it.arsinfo.smd.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.Mese;
import it.arsinfo.smd.entity.Operazione;
import it.arsinfo.smd.entity.Pubblicazione;

public interface OperazioneDao extends JpaRepository<Operazione, Long> {

	List<Operazione> findByAnno(Anno anno);
        List<Operazione> findByAnnoAndMese(Anno anno,Mese mese);
        List<Operazione> findByAnnoAndPubblicazione(Anno anno,Pubblicazione pubblicazione);
        List<Operazione> findByAnnoAndMeseAndPubblicazione(Anno anno,Mese mese,Pubblicazione pubblicazione);
        List<Operazione> findByMeseAndPubblicazione(Mese mese,Pubblicazione pubblicazione);
}
