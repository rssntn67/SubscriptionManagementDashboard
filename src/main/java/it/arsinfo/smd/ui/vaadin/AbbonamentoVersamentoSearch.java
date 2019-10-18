package it.arsinfo.smd.ui.vaadin;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.util.StringUtils;

import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;

import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Campagna;
import it.arsinfo.smd.repository.AbbonamentoDao;

public class AbbonamentoVersamentoSearch extends SmdSearch<Abbonamento> {

    private String searchCodeLine;
    private String searchCap;
    private Anagrafica customer;
    private Campagna campagna;
    
    private ArrayList<Abbonamento> abbonamenti = new ArrayList<>();
    
    public AbbonamentoVersamentoSearch(AbbonamentoDao abbonamentoDao, 
            List<Anagrafica> anagrafica, 
            List<Campagna> campagne) {
        super(abbonamentoDao);

        ComboBox<Anagrafica> filterAnagrafica = new ComboBox<Anagrafica>("Cerca Abbonamento per Intestatario");
        ComboBox<Campagna> filterCampagna = new ComboBox<Campagna>("Cerca Abbonamento per Campagna");
        
        TextField filterCodeLine = new TextField("Cerca abbonamento per Codeline");
        TextField filterCap = new TextField("Cerca Abbonamento per Cap");

        HorizontalLayout anag = new HorizontalLayout(filterCap,filterCodeLine,filterCampagna);
        anag.addComponentsAndExpand(filterAnagrafica);
        
        setComponents(anag);
        filterCodeLine.setPlaceholder("Inserisci Code Line");
        filterCodeLine.setValueChangeMode(ValueChangeMode.LAZY);
        filterCodeLine.addValueChangeListener(e -> {
            searchCodeLine = e.getValue();
            onChange();
        });

        filterCap.setPlaceholder("Inserisci CAP");
        filterCap.setValueChangeMode(ValueChangeMode.LAZY);
        filterCap.addValueChangeListener(e -> {
            searchCap = e.getValue();
            onChange();
        });

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
    
    private List<Abbonamento> findByCap(List<Abbonamento> abbonamenti) {
        return abbonamenti
                 .stream()
                 .filter(
                         abb -> 
                     abb.getIntestatario().getCap() != null &&    
                     abb.getIntestatario().getCap().toLowerCase()
                     .contains(searchCap.toLowerCase()))
                 .collect(Collectors.toList());         
    }

    private List<Abbonamento> findByCodeline(List<Abbonamento> abbonamenti) {
        return abbonamenti
                 .stream()
                 .filter(
                         abb -> 
                     abb.getCodeLine() != null &&    
                     abb.getCodeLine().toLowerCase()
                     .contains(searchCodeLine.toLowerCase()))
                 .collect(Collectors.toList());         
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
        if (!StringUtils.isEmpty(searchCap)) {
            abbs = findByCap(abbs);
        }
        if (!StringUtils.isEmpty(searchCodeLine)) {
            abbs=findByCodeline(abbs);                  
        }
        return abbs;
    }
    
    public void setItems(List<Abbonamento> abbonamenti) {
        this.abbonamenti = new ArrayList<>(abbonamenti);        
    }
}
