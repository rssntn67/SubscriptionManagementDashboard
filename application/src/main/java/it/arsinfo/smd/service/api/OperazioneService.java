package it.arsinfo.smd.service.api;

import java.util.List;

import it.arsinfo.smd.dao.OperazioneDao;
import it.arsinfo.smd.entity.Operazione;
import it.arsinfo.smd.entity.Pubblicazione;

public interface OperazioneService extends SmdServiceBase<Operazione> {
	List<Operazione> searchBy(Pubblicazione p);
	OperazioneDao getRepository();
	
}
