package it.arsinfo.smd.ui.versamento;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;

import it.arsinfo.smd.service.api.AbbonamentoService;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Campagna;
import it.arsinfo.smd.ui.vaadin.SmdSearch;

public class VersamentoAbbonamentoSearch extends SmdSearch<Abbonamento> {

    private Anagrafica customer;
    private Campagna campagna;
    private ComboBox<Anagrafica> filterAnagrafica = new ComboBox<Anagrafica>("Cerca Abbonamento per Intestatario");
    private ComboBox<Campagna> filterCampagna = new ComboBox<Campagna>("Cerca Abbonamento per Campagna");

    private ArrayList<Abbonamento> abbonamenti = new ArrayList<>();
    
    public VersamentoAbbonamentoSearch(AbbonamentoService dao,
                                       List<Anagrafica> anagrafica,
                                       List<Campagna> campagne) {
    	
        super(dao);
        HorizontalLayout anag = new HorizontalLayout(filterCampagna);
        anag.addComponentsAndExpand(filterAnagrafica);
        
        setComponents(anag);

        filterCampagna.setEmptySelectionAllowed(true);
        filterCampagna.setPlaceholder("Seleziona Campagna");
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
        filterAnagrafica.setPlaceholder("Cerca per Anagrafica");
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
    
    private List<Abbonamento> findByCustomer(List<Abbonamento> abbonamenti) {
           return    abbonamenti
                .stream()
                .filter(abb -> abb.getIntestatario().getId().longValue() == customer.getId().longValue()).collect(Collectors.toList());
    }
    
    private List<Abbonamento> findByCampagna(List<Abbonamento> abbonamenti) {
        return abbonamenti
                .stream()
                .filter(
                        abb -> 
                    abb.getCampagna() != null &&    
                    abb.getCampagna().getId().longValue() ==
                     campagna.getId().longValue())
                .collect(Collectors.toList());         
        
    }
    @Override
    public List<Abbonamento> find() {
        @SuppressWarnings("unchecked")
        List<Abbonamento> abbs = (List<Abbonamento>) abbonamenti.clone();
        if (customer != null) {
            abbs = findByCustomer(abbs);
        }
        if (campagna!= null) {
            abbs = findByCampagna(abbs);
        }
        return abbs;
    }
    
    public void setItems(List<Abbonamento> abbonamenti) {
        this.abbonamenti = new ArrayList<>(abbonamenti);        
    }
    
    public void reset() {
    	customer = null;
    	campagna = null;
    	filterAnagrafica.setSelectedItem(null);
    	filterCampagna.setSelectedItem(null);
    }
}
