package it.arsinfo.smd.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.TipoEstrattoConto;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.EstrattoConto;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.entity.Storico;

public interface EstrattoContoDao extends JpaRepository<EstrattoConto, Long> {

	List<EstrattoConto> findByAbbonamento(Abbonamento abbonamento);
        List<EstrattoConto> findByDestinatario(Anagrafica destinatario);
        List<EstrattoConto> findByStorico(Storico storico);
        List<EstrattoConto> findByStoricoAndAnnoInizioAndAnnoFine(Storico storico, Anno inizio, Anno fine);
        List<EstrattoConto> findByPubblicazione(Pubblicazione pubblicazione);
        List<EstrattoConto> findByTipoEstrattoConto(TipoEstrattoConto tipo);
        void deleteByAbbonamento(Abbonamento abbonamento);
}
