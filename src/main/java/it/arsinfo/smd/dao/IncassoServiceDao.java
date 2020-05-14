package it.arsinfo.smd.dao;

import java.time.LocalDate;
import java.util.List;

import it.arsinfo.smd.dao.repository.IncassoDao;
import it.arsinfo.smd.data.Cassa;
import it.arsinfo.smd.data.Ccp;
import it.arsinfo.smd.data.Cuas;
import it.arsinfo.smd.entity.Incasso;

public interface IncassoServiceDao extends SmdServiceDao<Incasso> {

	List<Incasso> searchBy(Cuas cuas, LocalDate dataContabile, Cassa cassa, Ccp ccp);
	IncassoDao getRepository();
}
