package it.arsinfo.smd.dao;

import java.util.List;

import it.arsinfo.smd.dao.repository.OperazioneDao;
import it.arsinfo.smd.entity.Operazione;
import it.arsinfo.smd.entity.Pubblicazione;

public interface OperazioneServiceDao extends SmdServiceDao<Operazione> {
	List<Operazione> searchBy(Pubblicazione p);
	OperazioneDao getRepository();
	
}
