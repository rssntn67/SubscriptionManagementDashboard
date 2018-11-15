package it.arsinfo.smd.vaadin;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

import it.arsinfo.smd.entity.Versamento;
import it.arsinfo.smd.repository.AbbonamentoDao;
import it.arsinfo.smd.repository.VersamentoDao;

@SpringUI(path = SmdUI.URL_VERSAMENTI)
@Title("Abbonamenti ADP")
public class VersamentoUI extends SmdHeader {

    /**
     * 
     */
    private static final long serialVersionUID = 7884064928998716106L;

    Grid<Versamento> grid;
    
    @Autowired
    AbbonamentoDao abbRepo;

    @Autowired
    VersamentoDao verRepo;

    @Override
    protected void init(VaadinRequest request) {
        super.init(request);

        Assert.notNull(abbRepo, "repo must be not null");
        Label header = new Label("Versamenti Clienti");
        
        DateField filterDataContabile = new DateField("Selezionare la data Contabile");
        filterDataContabile.setDateFormat("yyyy-MM-dd");

        DateField filterDataPagamento = new DateField("Selezionare la data Pagamento");
        filterDataPagamento.setDateFormat("yyyy-MM-dd");

        TextField filterImporto = new TextField("Inserire Importo");
        grid = new Grid<>(Versamento.class);
        HorizontalLayout actions = new HorizontalLayout(filterImporto,filterDataPagamento,filterDataContabile);
        addComponents(header, actions, grid);

        header.addStyleName(ValoTheme.LABEL_H2);
        grid.setColumns("id", "ccp.ccp","campo","campovalido",              
                        "dataPagamento","dataContabile","importo",
                        "errore",
                        "tipoDocumento.bollettino",
                        "tipoAccettazione.tipo","tipoSostitutivo.descr",
                        "provincia","ufficio","sportello","bobina", "progressivoBobina"
                                  );

        grid.setWidth("80%");

        filterDataContabile.addValueChangeListener(e -> listByContabile(e.getValue()));
        filterDataPagamento.addValueChangeListener(e -> listByPagamento(e.getValue()));
        filterImporto.setValueChangeMode(ValueChangeMode.LAZY);
        filterImporto.addValueChangeListener(e -> listByImporto(e.getValue()));
        
        list();

    }

    private void listByImporto(String value) {
        if (value == null || value.equals("")) {
            grid.setItems(verRepo.findAll());
        } else {
            try {
                grid.setItems(verRepo.findByImporto(new BigDecimal(value)));
            } catch (NumberFormatException e) {
                grid.setItems(new ArrayList<>());
            }
        }
    }

    private void listByPagamento(LocalDate value) {
        if (value == null) {
            grid.setItems(verRepo.findAll());
        } else {
            grid.setItems(verRepo.findByDataPagamento(java.util.Date.from(value.atStartOfDay()
      .atZone(ZoneId.systemDefault())
      .toInstant())));
        }
    }

    private void listByContabile(LocalDate value) {
        if (value == null) {
            grid.setItems(verRepo.findAll());
        } else {
            grid.setItems(verRepo.findByDataContabile(java.util.Date.from(value.atStartOfDay()
      .atZone(ZoneId.systemDefault())
      .toInstant())));
        }
    }

    void list() {
        grid.setItems(verRepo.findAll());
    }

}
