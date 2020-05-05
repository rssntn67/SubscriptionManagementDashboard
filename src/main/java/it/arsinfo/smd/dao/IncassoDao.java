package it.arsinfo.smd.dao;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import it.arsinfo.smd.data.Cassa;
import it.arsinfo.smd.data.Ccp;
import it.arsinfo.smd.data.Cuas;
import it.arsinfo.smd.entity.Incasso;

public interface IncassoDao extends JpaRepository<Incasso, Long> {

        Incasso findByDataContabileAndCassaAndCcpAndCuas(Date data,Cassa cassa, Ccp ccp, Cuas cuas);
	List<Incasso> findByCuas(Cuas cuas);
	List<Incasso> findByCcp(Ccp ccp);
        List<Incasso> findByDataContabile(Date data);
        List<Incasso> findByCassa(Cassa cassa);

	
}
