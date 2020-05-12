package it.arsinfo.smd.dao;

import java.util.List;

import it.arsinfo.smd.entity.Nota;
import it.arsinfo.smd.entity.Storico;

public interface NotaServiceDao extends SmdServiceDao<Nota> {
	public List<Storico> findStoricoAll();
	public List<Nota> searchBy(String searchText, Storico storico);
}
