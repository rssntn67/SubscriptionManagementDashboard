package it.arsinfo.smd.ui.incasso;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import it.arsinfo.smd.service.api.DistintaVersamentoService;
import it.arsinfo.smd.service.dto.IncassoGiornaliero;
import it.arsinfo.smd.ui.vaadin.SmdChangeHandler;

import java.time.LocalDate;
import java.util.List;

public class IncassoGiornalieroSearch extends SmdChangeHandler {

    private final DistintaVersamentoService dao;
    private final DateField filterDataContabileFrom = new DateField("Selezionare la data Contabile iniziale");
    private final DateField filterDataContabileTo = new DateField("Selezionare la data Contabile finale");

    public IncassoGiornalieroSearch(DistintaVersamentoService dao) {
        this.dao=dao;

        filterDataContabileFrom.setDateFormat("dd/MM/yyyy");
        filterDataContabileFrom.setValue(LocalDate.now().minusDays(7));

        filterDataContabileTo.setDateFormat("dd/MM/yyyy");
        filterDataContabileTo.setValue(LocalDate.now());

        Button searchButton = new Button("Cerca", VaadinIcons.SEARCH);
        setComponents(searchButton,
                      new HorizontalLayout(
                              filterDataContabileFrom,
                              filterDataContabileTo
                                           ));

        searchButton.addClickListener(e -> onChange());


    }

    public List<IncassoGiornaliero> find() {
        return dao.getIncassiGiornaliero(filterDataContabileFrom.getValue(),filterDataContabileTo.getValue());
    }
}
