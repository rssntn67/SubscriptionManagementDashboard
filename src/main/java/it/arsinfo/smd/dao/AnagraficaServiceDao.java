package it.arsinfo.smd.dao;

import java.util.List;

import org.springframework.stereotype.Service;

import it.arsinfo.smd.data.Diocesi;
import it.arsinfo.smd.entity.Anagrafica;

@Service
public interface AnagraficaServiceDao extends SmdServiceDao<Anagrafica> {

	public List<Anagrafica> searchBy(Diocesi searchDiocesi, String searchNome, String searchDenominazione,
			String searchCitta, String searchCap);
	
}
