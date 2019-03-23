package it.arsinfo.smd.vaadin.ui;

import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;

import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Campagna;
import it.arsinfo.smd.repository.AbbonamentoDao;
import it.arsinfo.smd.vaadin.model.SmdSearch;

public class AbbonamentoSearch extends SmdSearch<Abbonamento> {

    private Anagrafica customer;
    
    private Campagna campagna;

    public AbbonamentoSearch(AbbonamentoDao abbonamentoDao,
            List<Anagrafica> anagrafica, List<Campagna> campagne) {
        super(abbonamentoDao);

        ComboBox<Anagrafica> filterAnagrafica = new ComboBox<Anagrafica>();
        ComboBox<Campagna> filterCampagna = new ComboBox<Campagna>();

        setComponents(new HorizontalLayout(filterAnagrafica,filterCampagna));

        filterCampagna.setEmptySelectionAllowed(true);
        filterCampagna.setPlaceholder("Cerca per Campagna");
        filterCampagna.setItems(campagne);
        filterCampagna.setItemCaptionGenerator(Campagna::getCaption);
        filterCampagna.addSelectionListener(e -> {
            if (e.getValue() == null) {
                campagna = null;
            } else {
                campagna=e.getSelectedItem().get();
            }
            onChange();
        });
        
        filterAnagrafica.setEmptySelectionAllowed(true);
        filterAnagrafica.setPlaceholder("Cerca per Cliente");
        filterAnagrafica.setItems(anagrafica);
        filterAnagrafica.setItemCaptionGenerator(Anagrafica::getCaption);
        filterAnagrafica.addSelectionListener(e -> {
            if (e.getValue() == null) {
                customer = null;
            } else {
                customer = e.getSelectedItem().get();
            }
            onChange();
        });

    }

    @Override
    public List<Abbonamento> find() {
        if (campagna == null && customer == null) {
            return findAll();            
        }
        if (campagna == null) {
            return ((AbbonamentoDao) getRepo()).findByIntestatario(customer);
        }
        if (customer == null) {
            return ((AbbonamentoDao) getRepo()).findByCampagna(campagna);
        }
        return ((AbbonamentoDao) getRepo()).findByIntestatario(customer)
                .stream()
                .filter(a -> a.getCampagna().getId() == campagna.getId())
                .collect(Collectors.toList());
    }

}
