package it.arsinfo.smd.ui.spesaspedizione;

import java.util.EnumSet;
import java.util.List;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;

import it.arsinfo.smd.ui.service.api.SpesaSpedizioneService;
import it.arsinfo.smd.data.AreaSpedizione;
import it.arsinfo.smd.data.RangeSpeseSpedizione;
import it.arsinfo.smd.entity.SpesaSpedizione;
import it.arsinfo.smd.ui.vaadin.SmdSearch;

public class SpesaSpedizioneSearch extends SmdSearch<SpesaSpedizione> {

    private AreaSpedizione area;
    private RangeSpeseSpedizione range;

    private final SpesaSpedizioneService dao;
    public SpesaSpedizioneSearch(SpesaSpedizioneService dao) {
        super(dao);
        this.dao = dao;
        ComboBox<AreaSpedizione> filterArea = new ComboBox<AreaSpedizione>(null,
                                                                                 EnumSet.allOf(AreaSpedizione.class));
        ComboBox<RangeSpeseSpedizione> filterRange = new ComboBox<RangeSpeseSpedizione>(null,
                EnumSet.allOf(RangeSpeseSpedizione.class));

        setComponents(new HorizontalLayout(filterArea, filterRange));

        filterArea.setEmptySelectionAllowed(true);
        filterArea.setPlaceholder("Cerca per Area");
        filterArea.setWidth("300px");

        filterRange.setEmptySelectionAllowed(true);
        filterRange.setPlaceholder("Cerca per Range");
        filterRange.setWidth("300px");

        filterArea.addSelectionListener(e -> {
            if (e.getValue() == null) {
                area = null;
            } else {
                area = e.getSelectedItem().get();
            }
            onChange();
        });
        
        filterRange.addSelectionListener(e -> {
            if (e.getValue() == null) {
                range = null;
            } else {
                range = e.getSelectedItem().get();
            }
            onChange();
        });


    }

    @Override
    public List<SpesaSpedizione> find() {
    	return dao.searchBy(area, range);
    }

}
