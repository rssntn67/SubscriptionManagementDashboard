package it.arsinfo.smd.dao;

import java.util.List;

import it.arsinfo.smd.data.AreaSpedizione;
import it.arsinfo.smd.data.RangeSpeseSpedizione;
import it.arsinfo.smd.entity.SpesaSpedizione;

public interface SpesaSpedizioneServiceDao extends SmdServiceDao<SpesaSpedizione> {
	
	List<SpesaSpedizione> searchBy(AreaSpedizione area, RangeSpeseSpedizione range);
}
