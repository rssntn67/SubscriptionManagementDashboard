package it.arsinfo.smd.vaadin;

import java.io.OutputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.EnumSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.themes.ValoTheme;

import it.arsinfo.smd.entity.Cuas;
import it.arsinfo.smd.entity.Incasso;
import it.arsinfo.smd.entity.Versamento;
import it.arsinfo.smd.repository.IncassoDao;
import it.arsinfo.smd.repository.VersamentoDao;

@SpringUI(path = SmdUI.URL_INCASSI)
@Title("Incassi ADP")
public class IncassoUI extends SmdHeader
        implements Receiver, SucceededListener {

    /**
     * 
     */
    private static final long serialVersionUID = 7884064928998716106L;

    Grid<Incasso> gridIncasso;

    Grid<Versamento> gridVersamento;

    @Autowired
    IncassoDao repo;
    
    @Autowired
    VersamentoDao versRepo;

    @Override
    protected void init(VaadinRequest request) {
        super.init(request);

        Assert.notNull(repo, "repo must be not null");
        Label header = new Label("Incassi");
        ComboBox<Cuas> filterCuas = new ComboBox<Cuas>("Selezionare C.U.A.S.",
                                                       EnumSet.allOf(Cuas.class));
        DateField filterDataContabile = new DateField("Selezionare la data Contabile");
        filterDataContabile.setDateFormat("yyMMdd");

        Upload upload = new Upload("Aggiungi Incasso", this);
        upload.setImmediateMode(false);
        upload.setButtonCaption("Avvia Download");

        gridIncasso = new Grid<>(Incasso.class);
        gridVersamento = new Grid<Versamento>(Versamento.class);
        HorizontalLayout actions = new HorizontalLayout(filterCuas,filterDataContabile);
        addComponents(header, upload, actions, gridIncasso,gridVersamento);

        header.addStyleName(ValoTheme.LABEL_H2);

        filterCuas.setEmptySelectionAllowed(false);
        filterCuas.setPlaceholder("Cerca per CUAS");
        filterCuas.setItemCaptionGenerator(Cuas::getDenominazione);
        gridIncasso.setColumns("id", "cuas.denominazione", "ccp.ccp",
                        "dataContabile", "totaleDocumenti", "totaleImporto",
                        "documentiEsatti", "importoDocumentiEsatti",
                        "documentiErrati", "importoDocumentiErrati");
        gridIncasso.setWidth("80%");

        gridVersamento.setColumns("id", "bobina", "progressivoBobina",
                                  "progressivo","errore",
                                  "dataPagamento","dataContabile","importo",
                                  "tipoDocumento.bollettino","provincia","ufficio","sportello",
                                  "tipoAccettazione.tipo","tipoSostitutivo.descr"
                                  );
        gridVersamento.setVisible(false);
        gridVersamento.setWidth("80%");

        filterCuas.addSelectionListener(e -> listType(e.getSelectedItem().get()));
        filterDataContabile.addValueChangeListener(e -> listByContabile(e.getValue()));

        gridIncasso.asSingleSelect().addValueChangeListener(e -> {
            edit(e.getValue());
        });
        list();

    }

    private void edit(Incasso incasso) {
        if (incasso == null) {
           gridVersamento.setVisible(false); 
           return;
        }
        gridVersamento.setItems(versRepo.findByIncasso(incasso));
        gridVersamento.setVisible(true); 
        
    }

    private void listByContabile(LocalDate value) {
        if (value == null) {
            gridIncasso.setItems(repo.findAll());
        } else {
            gridIncasso.setItems(repo.findByDataContabile(java.util.Date.from(value.atStartOfDay()
      .atZone(ZoneId.systemDefault())
      .toInstant())));
        }
    }

    void list() {
        gridIncasso.setItems(repo.findAll());
    }

    void listType(Cuas cuas) {
        if (cuas != null) {
            gridIncasso.setItems(repo.findByCuas(cuas));
        } else {
            gridIncasso.setItems(repo.findAll());
        }
    }

    @Override
    public void uploadSucceeded(SucceededEvent event) {
        // TODO Auto-generated method stub

    }

    @Override
    public OutputStream receiveUpload(String filename, String mimeType) {
        // TODO Auto-generated method stub
        return null;
    }

}
