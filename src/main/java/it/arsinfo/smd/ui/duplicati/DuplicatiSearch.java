package it.arsinfo.smd.ui.duplicati;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import it.arsinfo.smd.dao.VersamentoServiceDao;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Versamento;
import it.arsinfo.smd.ui.versamento.VersamentoSearch;

public class DuplicatiSearch extends VersamentoSearch {
  
	private Map<Long,Anagrafica> anagraficaMap=new HashMap<Long, Anagrafica>();
    public DuplicatiSearch(VersamentoServiceDao dao,List<Anagrafica> anagrafica) {
        super(dao);
        anagraficaMap=anagrafica.stream().collect(Collectors.toMap(Anagrafica::getId, Function.identity()));
    }
     
    @Override
    public List<Versamento> filterAll(List<Versamento> versamenti) {
    	List<Versamento> veri = super.filterAll(versamenti).stream()
                .filter(v -> v.getCommittente() != null)
                .collect(Collectors.toList());
    	for (Versamento versamento: veri) {
    		Anagrafica committente = anagraficaMap.get(versamento.getCommittente().getId());
    		versamento.setCommittente(committente);
    	}
        return veri;
    }
}
