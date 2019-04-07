package it.arsinfo.smd.vaadin.ui;

import java.time.LocalDate;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;

import it.arsinfo.smd.SmdApplication;
import it.arsinfo.smd.data.Cassa;
import it.arsinfo.smd.data.Ccp;
import it.arsinfo.smd.data.Cuas;
import it.arsinfo.smd.entity.Incasso;
import it.arsinfo.smd.repository.IncassoDao;
import it.arsinfo.smd.vaadin.model.SmdSearch;

public class IncassoSearch extends SmdSearch<Incasso> {

    private Ccp ccp;
    private Cassa cassa;
    private Cuas cuas;
    private LocalDate dataContabile;

    public IncassoSearch(IncassoDao repo) {
        super(repo);

        DateField filterDataContabile = new DateField("Selezionare la data Contabile");
        filterDataContabile.setDateFormat("yyyy-MM-dd");
        ComboBox<Ccp> filterCcp = new ComboBox<Ccp>("Selezionare Conto Corrente",EnumSet.allOf(Ccp.class));
        ComboBox<Cassa> filterCassa = new ComboBox<Cassa>("Selezionare Cassa",EnumSet.allOf(Cassa.class));
        ComboBox<Cuas> filterCuas = new ComboBox<Cuas>("Selezionare C.U.A.S.",EnumSet.allOf(Cuas.class));

        setComponents(new HorizontalLayout(filterDataContabile,filterCcp,filterCassa,filterCuas));

        filterCcp.setEmptySelectionAllowed(true);
        filterCcp.setItemCaptionGenerator(Ccp::getCcp);
        filterCcp.setPlaceholder("Cerca per Conto Corrente");
        filterCcp.addSelectionListener(e -> {
            ccp = e.getValue();
            onChange();
        });
        
        filterCassa.setEmptySelectionAllowed(true);
        filterCassa.setPlaceholder("Cerca per Cassa");
        filterCassa.addSelectionListener(e -> {
            cassa = e.getValue();
            onChange();
        });

        filterCuas.setEmptySelectionAllowed(true);
        filterCuas.setPlaceholder("Cerca per CUAS");
        filterCuas.setItemCaptionGenerator(Cuas::getDenominazione);
        filterCuas.addSelectionListener(e -> {
            cuas = e.getValue();
            onChange();
        });

        filterDataContabile.addValueChangeListener(e -> {
            if (e.getValue() == null) {
                dataContabile = null;
            } else {
                dataContabile = e.getValue();
            }
            onChange();
        });

    }

    @Override
    public List<Incasso> find() {
        if (cuas == null && dataContabile == null && cassa == null && ccp == null) {
            return findAll();
        }
        
        if (dataContabile == null && cassa == null && ccp == null) {
            return ((IncassoDao) getRepo()).findByCuas(cuas);
        }
        if (dataContabile == null && cuas == null && ccp == null) {
            return ((IncassoDao) getRepo()).findByCassa(cassa);
        }
        if (dataContabile == null && cuas == null && cassa == null) {
            return ((IncassoDao) getRepo()).findByCcp(ccp);
        }
        if (cuas == null && cassa == null && ccp == null) {
            return ((IncassoDao) getRepo())
                    .findByDataContabile(SmdApplication.getStandardDate(dataContabile));
        }
        
        if (dataContabile == null && ccp == null) {
            return ((IncassoDao) getRepo()).findByCassa(cassa)
                    .stream()
                    .filter(inc -> inc.getCuas() == cuas)
                    .collect(Collectors.toList());
        }
        if (dataContabile == null && cuas == null) {
            return ((IncassoDao) getRepo()).findByCassa(cassa)
                    .stream()
                    .filter(inc -> inc.getCcp() == ccp)
                    .collect(Collectors.toList());
        }
        if (dataContabile == null && cassa == null) {
            return ((IncassoDao) getRepo()).findByCuas(cuas)
                    .stream()
                    .filter(inc -> inc.getCcp() == ccp)
                    .collect(Collectors.toList());
        }
            
        if (cassa == null && ccp == null) {
            return ((IncassoDao) getRepo())
                    .findByDataContabile(SmdApplication.getStandardDate(dataContabile))
                    .stream()
                    .filter(inc -> inc.getCuas() == cuas)
                    .collect(Collectors.toList());
        }
        if (cuas == null && ccp == null) {
            return ((IncassoDao) getRepo())
                    .findByDataContabile(SmdApplication.getStandardDate(dataContabile))
                    .stream()
                    .filter(inc -> inc.getCassa() == cassa)
                    .collect(Collectors.toList());
        }
        if (cassa == null && cuas == null) {
            return ((IncassoDao) getRepo())
                    .findByDataContabile(SmdApplication.getStandardDate(dataContabile))
                    .stream()
                    .filter(inc -> inc.getCcp() == ccp)
                    .collect(Collectors.toList());
        }
        if (cassa == null) {
            return ((IncassoDao) getRepo())
                    .findByDataContabile(SmdApplication.getStandardDate(dataContabile))
                    .stream()
                    .filter(inc -> inc.getCuas() == cuas && inc.getCcp() == ccp)
                    .collect(Collectors.toList());
        }
        if (cuas == null) {
            return ((IncassoDao) getRepo())
                    .findByDataContabile(SmdApplication.getStandardDate(dataContabile))
                    .stream()
                    .filter(inc -> inc.getCassa() == cassa && inc.getCcp() == ccp)
                    .collect(Collectors.toList());
        }
        if (ccp == null) {
            return ((IncassoDao) getRepo())
                    .findByDataContabile(SmdApplication.getStandardDate(dataContabile))
                    .stream()
                    .filter(inc -> inc.getCuas() == cuas && inc.getCassa() == cassa)
                    .collect(Collectors.toList());
        }

        return ((IncassoDao) getRepo())
                .findByDataContabile(SmdApplication.getStandardDate(dataContabile))
                .stream()
                .filter(inc -> inc.getCassa() == cassa && inc.getCuas() == cuas && inc.getCcp() == ccp)            
                .collect(Collectors.toList());
    }

}
