package it.arsinfo.smd.ui.incasso;

import java.time.LocalDate;
import java.util.EnumSet;
import java.util.List;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;

import it.arsinfo.smd.dao.DistintaVersamentoServiceDao;
import it.arsinfo.smd.data.Cassa;
import it.arsinfo.smd.data.Ccp;
import it.arsinfo.smd.data.Cuas;
import it.arsinfo.smd.entity.DistintaVersamento;
import it.arsinfo.smd.ui.vaadin.SmdSearch;

public class IncassoSearch extends SmdSearch<DistintaVersamento> {

    private Ccp ccp;
    private Cassa cassa;
    private Cuas cuas;
    private LocalDate dataContabile;
    
    private final DistintaVersamentoServiceDao dao;

    public IncassoSearch(DistintaVersamentoServiceDao dao) {
        super(dao);
        this.dao = dao;

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
    public List<DistintaVersamento> find() {
    	return dao.searchBy(cuas,dataContabile,cassa,ccp);
    }

}
