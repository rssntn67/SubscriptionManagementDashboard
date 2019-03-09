package it.arsinfo.smd.vaadin.ui;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

import it.arsinfo.smd.data.TipoDocumentoBollettino;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Versamento;
import it.arsinfo.smd.repository.AbbonamentoDao;
import it.arsinfo.smd.repository.VersamentoDao;
import it.arsinfo.smd.vaadin.model.SmdUI;
import it.arsinfo.smd.vaadin.model.SmdUIHelper;

@SpringUI(path = SmdUIHelper.URL_VERSAMENTI)
@Title("Abbonamenti ADP")
public class VersamentoUI extends SmdUI {

    /**
     * 
     */
    private static final long serialVersionUID = 7884064928998716106L;

    private static final Logger log = LoggerFactory.getLogger(VersamentoUI.class);

    Grid<Versamento> grid;
    
    Grid<Abbonamento> abbonamentiAssociabili;
    Grid<Abbonamento> abbonamentiAssociati;
    
    @Autowired
    AbbonamentoDao abbRepo;

    @Autowired
    VersamentoDao verRepo;

    Versamento versamento;
    
    Label residuo; 
    
    Label avviso; 

    @Override
    protected void init(VaadinRequest request) {
        super.init(request, "Versamenti");

        Assert.notNull(abbRepo, "repo must be not null");
        residuo = new Label(); 
        avviso = new Label();
        
        HorizontalLayout info = new HorizontalLayout(avviso,residuo);
        DateField filterDataContabile = new DateField("Selezionare la data Contabile");
        filterDataContabile.setDateFormat("yyyy-MM-dd");

        DateField filterDataPagamento = new DateField("Selezionare la data Pagamento");
        filterDataPagamento.setDateFormat("yyyy-MM-dd");

        TextField filterImporto = new TextField("Inserire Importo");
        grid = new Grid<>(Versamento.class);
        abbonamentiAssociati = new Grid<>(Abbonamento.class);
        abbonamentiAssociabili = new Grid<>(Abbonamento.class);
        HorizontalLayout actions = new HorizontalLayout(filterImporto,filterDataPagamento,filterDataContabile);
        addComponents(info,actions, grid,abbonamentiAssociati,abbonamentiAssociabili);
        
        grid.setColumns("id", "ccp.ccp","campo","campovalido",              
                        "dataPagamento","dataContabile","importo",
                        "errore",
                        "tipoDocumento.bollettino",
                        "tipoAccettazione.tipo","tipoSostitutivo.descr",
                        "provincia","ufficio","sportello","bobina", "progressivoBobina"
                      );
        grid.setWidth("80%");
        grid.asSingleSelect().addValueChangeListener(e -> {
            edit(e.getValue());
        });
        
        filterDataContabile.addValueChangeListener(e -> listByContabile(e.getValue()));
        filterDataPagamento.addValueChangeListener(e -> listByPagamento(e.getValue()));
        filterImporto.setValueChangeMode(ValueChangeMode.LAZY);
        filterImporto.addValueChangeListener(e -> listByImporto(e.getValue()));

        abbonamentiAssociati.setColumns("anagrafica.cognome", "anagrafica.nome","contoCorrentePostale.ccp","cost","campo","incasso","pagato","omaggio");                
        abbonamentiAssociati.addComponentColumn(abbonamento -> {
           Button button = new Button("Dissocia");
           button.addClickListener(click -> dissocia(abbonamento));
           button.setEnabled(versamento.getTipoDocumento() != TipoDocumentoBollettino.TIPO674);
           return button;
        });
        abbonamentiAssociati.setWidth("100%");
        abbonamentiAssociati.setVisible(false);
        
        abbonamentiAssociabili.setColumns("anagrafica.cognome", "anagrafica.nome","contoCorrentePostale.ccp","cost","campo","incasso","pagato","omaggio");                
        abbonamentiAssociabili.addComponentColumn(abbonamento -> {
           Button button = new Button("Associa");
           button.addClickListener(click -> incassa(abbonamento));
           button.setEnabled(versamento.getTipoDocumento() != TipoDocumentoBollettino.TIPO674);
           return button;
        });
        abbonamentiAssociabili.setWidth("100%");  
        abbonamentiAssociabili.setVisible(false);
        residuo.addStyleName(ValoTheme.LABEL_H3);
        residuo.setVisible(false);
        avviso.addStyleName(ValoTheme.LABEL_H3);
        avviso.setVisible(false);
        list();

    }

    private void dissocia(Abbonamento abbonamento) {
        abbonamento.setVersamento(null);
        abbonamento.setPagato(false);
        abbonamento.setIncasso(null);
        abbRepo.save(abbonamento);
        edit(versamento);
    }

    private void incassa(Abbonamento abbonamento) {
        abbonamento.setVersamento(versamento);
        abbonamento.setPagato(true);
        abbonamento.setIncasso(versamento.getDataPagamento());
        abbRepo.save(abbonamento);
        edit(versamento);
    }

    private void edit(Versamento value) {
        if (value == null) {
            abbonamentiAssociabili.setVisible(false);
            abbonamentiAssociati.setVisible(false);
            residuo.setVisible(false);
            avviso.setVisible(false);
            return;
        }
        versamento=value;
        List<Abbonamento> matching;
        if (versamento.getTipoDocumento() == TipoDocumentoBollettino.TIPO674) {
            matching = abbRepo.findByCampo(versamento.getCampo());
            abbonamentiAssociabili.setVisible(false);
        } else {
            matching = abbRepo.findByVersamento(versamento);
            abbonamentiAssociabili.setItems(abbRepo.findByPagato(false));
            abbonamentiAssociabili.setVisible(true);
        }
        avviso.setVisible(true);
        residuo.setVisible(true);
        
        if (matching.size() == 0 ) {
            residuo.setValue("Residuo EUR: " + versamento.getImporto().toString());
            avviso.setValue("Nessun Abbonamento Trovato Per il Versamento Selezionato");
            abbonamentiAssociati.setVisible(false);
            return;
        } 
        
        abbonamentiAssociati.setItems(matching);
        abbonamentiAssociati.setVisible(true);
        matching.stream().filter(abbonamento -> !abbonamento.isPagato()).forEach(abbonamento -> {
                log.info("incasso");
                log.info(abbonamento.toString());
                incassa(abbonamento);
        });
        BigDecimal diff = versamento.getImporto();
        for (Abbonamento abbonamento: matching) {
            diff = diff.subtract(abbonamento.getCost());
        }
        residuo.setValue("Residuo EUR: " + diff.toString());
        
        if (matching.size() == 1) {
            avviso.setValue("Trovato " + matching.size() + " Abbonamento Per il Versamento Selezionato");
        } else {
            avviso.setValue("Trovati " + matching.size() + " Abbonamenti Per il Versamento Selezionato");
        }
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
