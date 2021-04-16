package it.arsinfo.smd.ui.service.api;

import java.util.List;

import it.arsinfo.smd.data.AreaSpedizione;
import it.arsinfo.smd.data.RangeSpeseSpedizione;
import it.arsinfo.smd.entity.SpesaSpedizione;

public interface SpesaSpedizioneService extends SmdServiceBase<SpesaSpedizione> {
	
	List<SpesaSpedizione> searchBy(AreaSpedizione area, RangeSpeseSpedizione range);
}
