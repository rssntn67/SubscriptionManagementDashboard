package it.arsinfo.smd.service.api;

import java.util.List;

import it.arsinfo.smd.entity.AreaSpedizione;
import it.arsinfo.smd.entity.RangeSpeseSpedizione;
import it.arsinfo.smd.entity.SpesaSpedizione;

public interface SpesaSpedizioneService extends SmdServiceBase<SpesaSpedizione> {
	
	List<SpesaSpedizione> searchBy(AreaSpedizione area, RangeSpeseSpedizione range);
}
