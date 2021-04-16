package it.arsinfo.smd.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import it.arsinfo.smd.data.AreaSpedizione;
import it.arsinfo.smd.data.RangeSpeseSpedizione;
import it.arsinfo.smd.entity.SpesaSpedizione;

public interface SpesaSpedizioneDao extends JpaRepository<SpesaSpedizione, Long> {
    SpesaSpedizione findByAreaSpedizioneAndRangeSpeseSpedizione(AreaSpedizione area,RangeSpeseSpedizione range);
    List<SpesaSpedizione> findByAreaSpedizione(AreaSpedizione areaSpedizione);    
    List<SpesaSpedizione> findByRangeSpeseSpedizione(RangeSpeseSpedizione range);    
}
