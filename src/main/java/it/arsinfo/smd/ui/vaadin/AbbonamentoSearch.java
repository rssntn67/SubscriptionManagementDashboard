package it.arsinfo.smd.ui.vaadin;

import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;

import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.Cassa;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Campagna;
import it.arsinfo.smd.repository.AbbonamentoDao;

public class AbbonamentoSearch extends SmdSearch<Abbonamento> {

    private Anagrafica customer;
    private Anno anno;
    private Campagna campagna;
    private final ComboBox<Cassa> filterCassa = new ComboBox<Cassa>();

    public AbbonamentoSearch(AbbonamentoDao abbonamentoDao,
            List<Anagrafica> anagrafica, List<Campagna> campagne) {
        super(abbonamentoDao);

        ComboBox<Anagrafica> filterAnagrafica = new ComboBox<Anagrafica>();
        ComboBox<Anno> filterAnno = new ComboBox<Anno>();
        ComboBox<Campagna> filterCampagna = new ComboBox<Campagna>();

        setComponents(new HorizontalLayout(filterAnagrafica,filterCampagna,filterAnno,filterCassa));

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

        filterAnno.setEmptySelectionAllowed(true);
        filterAnno.setPlaceholder("Cerca per Anno");
        filterAnno.setItems(EnumSet.allOf(Anno.class));
        filterAnno.setItemCaptionGenerator(Anno::getAnnoAsString);
        filterAnno.addSelectionListener(e -> {
            if (e.getValue() == null) {
                anno = null;
            } else {
                anno=e.getSelectedItem().get();
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
        
        filterCassa.setPlaceholder("Seleziona Cassa");
        filterCassa.setItems(EnumSet.allOf(Cassa.class));
        filterCassa.addSelectionListener(e ->onChange());


    }

    @Override
    public List<Abbonamento> find() {
        if (campagna == null && customer == null && anno == null) {
            return filterAll(findAll());            
        }
        if (campagna == null && anno == null) {
            return filterAll(((AbbonamentoDao) getRepo()).findByIntestatario(customer));
        }
        if (customer == null && anno == null) {
            return filterAll(((AbbonamentoDao) getRepo()).findByCampagna(campagna));
        }
        if (customer == null && campagna == null) {
            return filterAll(((AbbonamentoDao) getRepo()).findByAnno(anno));
        }
        
        if (anno == null) {
           return filterAll(((AbbonamentoDao) getRepo()).findByIntestatario(customer)
            .stream()
            .filter(a -> 
                a.getCampagna() != null
                && a.getCampagna().getId() == campagna.getId()
            )
            .collect(Collectors.toList()));
        }
        if (campagna == null) {
           return filterAll(((AbbonamentoDao) getRepo()).findByIntestatario(customer)
            .stream()
            .filter(a -> 
                a.getAnno() == anno
                )
            .collect(Collectors.toList()));
        }
        if (customer == null) {
            return filterAll(((AbbonamentoDao) getRepo()).findByCampagna(campagna)
                .stream()
                    .filter(a -> 
                        a.getAnno() == anno
                        )
                    .collect(Collectors.toList()));
        }
        return filterAll(((AbbonamentoDao) getRepo()).findByIntestatario(customer)
                .stream()
                .filter(a -> 
                    a.getCampagna() != null
                    && a.getCampagna().getId() == campagna.getId()
                    && a.getAnno() == anno
                    )
                .collect(Collectors.toList()));
    }

    private List<Abbonamento> filterAll(List<Abbonamento> abbonamenti) {
        if (filterCassa.getValue() != null) {
            abbonamenti=abbonamenti.stream().filter(a -> a.getCassa() == filterCassa.getValue()).collect(Collectors.toList());      
        }
        
        return abbonamenti;
    }

}
