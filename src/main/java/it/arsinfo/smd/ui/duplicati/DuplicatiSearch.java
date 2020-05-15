package it.arsinfo.smd.ui.duplicati;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;

import it.arsinfo.smd.dao.VersamentoServiceDao;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Versamento;
import it.arsinfo.smd.ui.versamento.VersamentoSearch;

public class DuplicatiSearch extends VersamentoSearch {
  
    private Anagrafica committente;

	private Map<Long,Anagrafica> anagraficaMap=new HashMap<Long, Anagrafica>();
    public DuplicatiSearch(VersamentoServiceDao dao,List<Anagrafica> anagrafica) {
        super(dao);
        anagraficaMap=anagrafica.stream().collect(Collectors.toMap(Anagrafica::getId, Function.identity()));
        ComboBox<Anagrafica> filterAnagrafica = new ComboBox<Anagrafica>();
        HorizontalLayout ana = new HorizontalLayout();
        ana.addComponentsAndExpand(filterAnagrafica);
        addComponents(ana);
        
        filterAnagrafica.setEmptySelectionAllowed(true);
        filterAnagrafica.setPlaceholder("Cerca per Anagrafica");
        filterAnagrafica.setItems(anagrafica);
        filterAnagrafica.setItemCaptionGenerator(Anagrafica::getCaption);
        filterAnagrafica.addSelectionListener(e -> {
            if (e.getValue() == null) {
            	committente = null;
            } else {
            	committente = e.getSelectedItem().get();
            }
            onChange();
        });

    }
     
    @Override
    public List<Versamento> find() {
    	List<Versamento> veri = super.find().stream()
                .filter(v -> v.getCommittente() != null)
                .collect(Collectors.toList());
    	for (Versamento versamento: veri) {
    		Anagrafica committente = anagraficaMap.get(versamento.getCommittente().getId());
    		versamento.setCommittente(committente);
    	}
    	if (committente == null) {
    		return veri;
    	}
        return veri.stream().filter(v -> v.getCommittente().equals(committente)).collect(Collectors.toList());
    }
}
