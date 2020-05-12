package it.arsinfo.smd.dao;

import java.util.List;

import it.arsinfo.smd.entity.Nota;
import it.arsinfo.smd.entity.Storico;

public interface NotaServiceDao extends SmdServiceDao<Nota> {
	List<Storico> findStoricoAll();
	List<Nota> searchBy(String searchText, Storico storico);
}
