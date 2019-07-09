package it.arsinfo.smd.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.InvioSpedizione;
import it.arsinfo.smd.data.Mese;
import it.arsinfo.smd.data.StatoSpedizione;
import it.arsinfo.smd.entity.EstrattoConto;
import it.arsinfo.smd.entity.Spedizione;

public interface SpedizioneDao extends JpaRepository<Spedizione, Long> {
    List<Spedizione> findByEstrattoConto(EstrattoConto ec);
    List<Spedizione> findByMeseSpedizione(Mese mese);
    List<Spedizione> findByMesePubblicazione(Mese mese);
    List<Spedizione> findByAnnoSpedizione(Anno anno);
    List<Spedizione> findByAnnoPubblicazione(Anno anno);
    List<Spedizione> findByNumero(Integer numero);
    List<Spedizione> findByMeseSpedizioneAndAnnoSpedizione(Mese mese,Anno anno);
    List<Spedizione> findByMesePubblicazioneAndAnnoPubblicazione(Mese mese, Anno anno);
    List<Spedizione> findByStatoSpedizione(StatoSpedizione statoSpedizione);
    List<Spedizione> findByInvioSpedizione(InvioSpedizione invioSpedizione);
       
}
