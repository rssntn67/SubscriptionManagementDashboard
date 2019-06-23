package it.arsinfo.smd.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import it.arsinfo.smd.data.TipoEstrattoConto;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.EstrattoConto;
import it.arsinfo.smd.entity.Pubblicazione;

public interface EstrattoContoDao extends JpaRepository<EstrattoConto, Long> {

	List<EstrattoConto> findByAbbonamento(Abbonamento abbonamento);
        List<EstrattoConto> findByPubblicazione(Pubblicazione pubblicazione);
        List<EstrattoConto> findByTipoEstrattoConto(TipoEstrattoConto tipo);
}
