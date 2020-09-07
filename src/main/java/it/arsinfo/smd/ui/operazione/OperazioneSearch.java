package it.arsinfo.smd.ui.operazione;

import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;

import it.arsinfo.smd.dao.OperazioneServiceDao;
import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.Mese;
import it.arsinfo.smd.entity.Operazione;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.ui.vaadin.SmdSearch;

public class OperazioneSearch extends SmdSearch<Operazione> {

    private Pubblicazione p;
    ComboBox<Anno> filterAnno = new ComboBox<Anno>();
    ComboBox<Mese> filterMese = new ComboBox<Mese>();

    private final OperazioneServiceDao dao;
    public OperazioneSearch(OperazioneServiceDao dao, List<Pubblicazione> pubblicazioni) {
        super(dao);
        this.dao = dao;
        ComboBox<Pubblicazione> filterP = new ComboBox<Pubblicazione>();

        setComponents(new HorizontalLayout(filterAnno,filterMese,filterP));

        filterP.setEmptySelectionAllowed(true);
        filterP.setPlaceholder("Cerca per Pubblicazione");
        filterP.setItems(pubblicazioni);
        filterP.setItemCaptionGenerator(Pubblicazione::getNome);
        filterP.addSelectionListener(e -> {
            if (e.getValue() == null) {
                p = null;
            } else {
                p=e.getSelectedItem().get();
            }
            onChange();
        });

        filterAnno.setEmptySelectionAllowed(true);
        filterAnno.setPlaceholder("Cerca per Anno");
        filterAnno.setItems(EnumSet.allOf(Anno.class));
        filterAnno.setItemCaptionGenerator(Anno::getAnnoAsString);
        filterAnno.addSelectionListener(e -> {
            onChange();
        });

        filterMese.setEmptySelectionAllowed(true);
        filterMese.setPlaceholder("Cerca per Mese");
        filterMese.setItems(EnumSet.allOf(Mese.class));
        filterMese.setItemCaptionGenerator(Mese::getNomeBreve);
        filterMese.addSelectionListener(e -> {
            onChange();
        });
        
    }
    
    @Override
    public List<Operazione> find() {
    	return filterAll(dao.searchBy(p));
    }
    
    private List<Operazione> filterAll(List<Operazione> operazioni) {
        if (filterAnno.getValue() != null) {
            operazioni=operazioni
                    .stream()
                    .filter(o -> o.getAnno() == filterAnno.getValue())
                    .collect(Collectors.toList());
        }
        if (filterMese.getValue() != null) {
            operazioni=operazioni
                    .stream()
                    .filter(o -> o.getMese() == filterMese.getValue())
                    .collect(Collectors.toList());
        }
        return operazioni;
    }
    

}
