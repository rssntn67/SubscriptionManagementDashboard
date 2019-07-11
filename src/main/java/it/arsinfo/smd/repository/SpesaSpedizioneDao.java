package it.arsinfo.smd.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import it.arsinfo.smd.data.AreaSpedizione;
import it.arsinfo.smd.data.RangeSpeseSpedizione;
import it.arsinfo.smd.entity.SpesaSpedizione;

public interface SpesaSpedizioneDao extends JpaRepository<SpesaSpedizione, Long> {
    List<SpesaSpedizione> findByAreaSpedizioneAndRange(AreaSpedizione area,RangeSpeseSpedizione range);    
}
