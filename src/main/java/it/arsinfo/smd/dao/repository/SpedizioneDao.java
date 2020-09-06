package it.arsinfo.smd.dao.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.InvioSpedizione;
import it.arsinfo.smd.data.Mese;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Spedizione;

public interface SpedizioneDao extends JpaRepository<Spedizione, Long> {
    List<Spedizione> findByAbbonamento(Abbonamento abbonamento);
    List<Spedizione> findByMeseSpedizione(Mese mese);
    List<Spedizione> findByAnnoSpedizione(Anno anno);
    List<Spedizione> findByMeseSpedizioneAndAnnoSpedizione(Mese mese,Anno anno);
    List<Spedizione> findByMeseSpedizioneAndAnnoSpedizioneAndInvioSpedizione(Mese mese,Anno anno, InvioSpedizione invioSpedizione);
    List<Spedizione> findByMeseSpedizioneAndAnnoSpedizioneAndInvioSpedizioneNot	(Mese mese,Anno anno, InvioSpedizione invioSpedizione);
    List<Spedizione> findByInvioSpedizione(InvioSpedizione invioSpedizione);
    List<Spedizione> findByDestinatario(Anagrafica destinatario);
    void deleteByAbbonamento(Abbonamento abbonamento);

}
