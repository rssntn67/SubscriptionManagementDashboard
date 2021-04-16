package it.arsinfo.smd.service.api;

import java.util.List;

import it.arsinfo.smd.entity.Nota;
import it.arsinfo.smd.entity.Storico;

public interface NotaService extends SmdServiceBase<Nota> {
	List<Storico> findStoricoAll();
	List<Nota> searchBy(String searchText, Storico storico);
}
