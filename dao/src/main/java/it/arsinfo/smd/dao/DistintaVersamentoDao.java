package it.arsinfo.smd.dao;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import it.arsinfo.smd.entity.Cassa;
import it.arsinfo.smd.entity.Ccp;
import it.arsinfo.smd.entity.Cuas;
import it.arsinfo.smd.entity.DistintaVersamento;

public interface DistintaVersamentoDao extends JpaRepository<DistintaVersamento, Long> {

        DistintaVersamento findByDataContabileAndCassaAndCcpAndCuas(Date data,Cassa cassa, Ccp ccp, Cuas cuas);
	    List<DistintaVersamento> findByCuas(Cuas cuas);
	    List<DistintaVersamento> findByCcp(Ccp ccp);
        List<DistintaVersamento> findByDataContabile(Date data);
        List<DistintaVersamento> findByDataContabileBetween(Date startdate, Date enddate);
        List<DistintaVersamento> findByCassa(Cassa cassa);

	
}
