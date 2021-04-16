package it.arsinfo.smd.dao.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import it.arsinfo.smd.data.Cassa;
import it.arsinfo.smd.data.Ccp;
import it.arsinfo.smd.data.Cuas;
import it.arsinfo.smd.entity.DistintaVersamento;

public interface DistintaVersamentoDao extends JpaRepository<DistintaVersamento, Long> {

        DistintaVersamento findByDataContabileAndCassaAndCcpAndCuas(Date data,Cassa cassa, Ccp ccp, Cuas cuas);
	    List<DistintaVersamento> findByCuas(Cuas cuas);
	    List<DistintaVersamento> findByCcp(Ccp ccp);
        List<DistintaVersamento> findByDataContabile(Date data);
        List<DistintaVersamento> findByCassa(Cassa cassa);

	
}
