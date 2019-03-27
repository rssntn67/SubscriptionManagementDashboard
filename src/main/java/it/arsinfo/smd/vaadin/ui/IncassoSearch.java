package it.arsinfo.smd.vaadin.ui;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;

import it.arsinfo.smd.data.Cassa;
import it.arsinfo.smd.data.Cuas;
import it.arsinfo.smd.entity.Incasso;
import it.arsinfo.smd.repository.IncassoDao;
import it.arsinfo.smd.vaadin.model.SmdSearch;

public class IncassoSearch extends SmdSearch<Incasso> {

    private Cassa cassa;
    private Cuas cuas;
    private LocalDate dataContabile;

    public IncassoSearch(IncassoDao repo) {
        super(repo);

        ComboBox<Cassa> filterCassa = new ComboBox<Cassa>("Selezionare Cassa",
                EnumSet.allOf(Cassa.class));
        ComboBox<Cuas> filterCuas = new ComboBox<Cuas>("Selezionare C.U.A.S.",
                                                       EnumSet.allOf(Cuas.class));
        DateField filterDataContabile = new DateField("Selezionare la data Contabile");
        filterDataContabile.setDateFormat("yyyy-MM-dd");

        setComponents(new HorizontalLayout(filterCassa,filterCuas, filterDataContabile));

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
            dataContabile = e.getValue();
            onChange();
        });

    }

    @Override
    public List<Incasso> find() {
        if (cuas == null && dataContabile == null && cassa == null) {
            return findAll();
        }
        if (dataContabile == null && cassa == null ) {
            return ((IncassoDao) getRepo()).findByCuas(cuas);
        }
        if (dataContabile == null && cuas == null) {
            return ((IncassoDao) getRepo()).findByCassa(cassa);
        }
        if (dataContabile == null) {
            return ((IncassoDao) getRepo()).findByCassa(cassa)
                    .stream()
                    .filter(inc -> inc.getCuas() == cuas)
                    .collect(Collectors.toList());
        }
        Stream<Incasso> incassi = ((IncassoDao) getRepo())
                    .findByDataContabile(
                         java.util.Date.from(
                             dataContabile.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()
                         )
                    )
                .stream();
        if (cuas == null && cassa == null) {
            return incassi
                .collect(Collectors.toList());
        }
            
        if (cassa == null) {
            return incassi
                .filter(inc -> inc.getCuas() == cuas)
                .collect(Collectors.toList());
        }
        if (cuas == null) {
            return incassi
                .filter(inc -> inc.getCassa() == cassa)
                .collect(Collectors.toList());
        }
        return incassi
                .filter(inc -> inc.getCassa() == cassa && inc.getCuas() == cuas)            
                .collect(Collectors.toList());
    }

}
