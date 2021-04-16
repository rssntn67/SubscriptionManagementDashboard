package it.arsinfo.smd.dao;

import java.util.List;

import org.springframework.stereotype.Service;

import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Offerta;

@Service
public interface OffertaServiceDao extends SmdServiceDao<Offerta> {

	List<Offerta> findByCommittente(Anagrafica tValue, Anno sValue) throws Exception;
	
}
