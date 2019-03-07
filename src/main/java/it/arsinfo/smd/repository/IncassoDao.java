package it.arsinfo.smd.repository;

import it.arsinfo.smd.data.ContoCorrentePostale;
import it.arsinfo.smd.data.Cuas;
import it.arsinfo.smd.entity.Incasso;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface IncassoDao extends JpaRepository<Incasso, Long> {

	List<Incasso> findByCuas(Cuas cuas);
	List<Incasso> findByCcp(ContoCorrentePostale ccp);
        List<Incasso> findByDataContabile(Date date);
	
}
