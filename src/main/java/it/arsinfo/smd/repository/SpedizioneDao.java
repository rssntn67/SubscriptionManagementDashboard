package it.arsinfo.smd.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.Invio;
import it.arsinfo.smd.data.InvioSpedizione;
import it.arsinfo.smd.data.Mese;
import it.arsinfo.smd.data.StatoSpedizione;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Spedizione;

public interface SpedizioneDao extends JpaRepository<Spedizione, Long> {
    List<Spedizione> findByAbbonamento(Abbonamento abbonamento);
    List<Spedizione> findByMeseSpedizione(Mese mese);
    List<Spedizione> findByAnnoSpedizione(Anno anno);
    List<Spedizione> findByMeseSpedizioneAndAnnoSpedizione(Mese mese,Anno anno);
    List<Spedizione> findByStatoSpedizione(StatoSpedizione statoSpedizione);
    List<Spedizione> findByInvioSpedizione(InvioSpedizione invioSpedizione);
    List<Spedizione> findByDestinatario(Anagrafica destinatario);
    List<Spedizione> findByInvio(Invio invio);
    void deleteByAbbonamento(Abbonamento abbonamento);

}
