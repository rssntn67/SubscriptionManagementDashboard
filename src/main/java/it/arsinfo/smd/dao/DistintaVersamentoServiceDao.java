package it.arsinfo.smd.dao;

import java.time.LocalDate;
import java.util.List;

import it.arsinfo.smd.dao.repository.DistintaVersamentoDao;
import it.arsinfo.smd.data.Cassa;
import it.arsinfo.smd.data.Ccp;
import it.arsinfo.smd.data.Cuas;
import it.arsinfo.smd.entity.DistintaVersamento;
import it.arsinfo.smd.entity.UserInfo;
import it.arsinfo.smd.entity.Versamento;

public interface DistintaVersamentoServiceDao extends SmdServiceDao<DistintaVersamento> {

	List<DistintaVersamento> searchBy(Cuas cuas, LocalDate dataContabile, Cassa cassa, Ccp ccp);
	DistintaVersamentoDao getRepository();
	List<Versamento> incassaCodeLine(List<DistintaVersamento> find, UserInfo loggedInUser) throws Exception;
}
