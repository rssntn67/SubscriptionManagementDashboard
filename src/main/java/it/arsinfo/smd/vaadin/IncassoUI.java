package it.arsinfo.smd.vaadin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.vaadin.annotations.Title;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.themes.ValoTheme;

import it.arsinfo.smd.SmdApplication;
import it.arsinfo.smd.data.Cuas;
import it.arsinfo.smd.entity.Incasso;
import it.arsinfo.smd.entity.Versamento;
import it.arsinfo.smd.repository.IncassoDao;
import it.arsinfo.smd.repository.VersamentoDao;

@SpringUI(path = SmdUI.URL_INCASSI)
@Title("Incassi ADP")
public class IncassoUI extends SmdHeader
        implements Receiver, SucceededListener {

    private static final Logger log = LoggerFactory.getLogger(VersamentoUI.class);

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

    File file;
    
    Label avviso;
    @Override
    protected void init(VaadinRequest request) {
        super.init(request);

        Assert.notNull(repo, "repo must be not null");
        Label header = new Label("Incassi");
        avviso = new Label();
        ComboBox<Cuas> filterCuas = new ComboBox<Cuas>("Selezionare C.U.A.S.",
                                                       EnumSet.allOf(Cuas.class));
        DateField filterDataContabile = new DateField("Selezionare la data Contabile");
        filterDataContabile.setDateFormat("yyyy-MM-dd");

        Upload upload = new Upload("Aggiungi Incasso", this);
        upload.setImmediateMode(false);
        upload.setButtonCaption("Avvia Download");
        upload.addSucceededListener(this);


        gridIncasso = new Grid<>(Incasso.class);
        gridVersamento = new Grid<Versamento>(Versamento.class);
        HorizontalLayout actions = new HorizontalLayout(filterCuas,filterDataContabile);
        addComponents(header, upload, avviso,actions, gridIncasso,gridVersamento);

        header.addStyleName(ValoTheme.LABEL_H2);
        avviso.addStyleName(ValoTheme.LABEL_H3);
        avviso.setVisible(false);
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
        avviso.setVisible(false);
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
        FileInputStream fstream;
        try {
            fstream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            avviso.setValue("Incasso Cancellato: errore ->"+e.getMessage());
            log.error("Incasso Cancellato: " + e.getMessage());
            return;
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

        String strLine;

        List<Incasso> incassi = new ArrayList<>();
        boolean errorFound=false;
        //Read File Line By Line
        try {
            Set<String> versamenti = new HashSet<>();
            while ((strLine = br.readLine()) != null)   {
                if (SmdApplication.isVersamento(strLine)) {
                    versamenti.add(strLine);
                } else if (SmdApplication.isRiepilogo(strLine)) {
                    incassi.add(SmdApplication.generateIncasso(versamenti, strLine));
                    versamenti.clear();
                } else {
                    avviso.setValue("Incasso Cancellato: Valore non riconosciuto->" + strLine);
                    log.error("Incasso Cancellato: Valore non riconosciuto->" +strLine);
                    errorFound=true;
                    break;
                }
            }
        } catch (Exception e) {
            avviso.setValue("Incasso Cancellato: errore ->"+e.getMessage());
            log.error("Incasso Cancellato: " + e.getMessage());
            errorFound = true;
        }

        //Close the input stream
        try {
            br.close();
        } catch (IOException e) {
            avviso.setValue("Incasso Cancellato: errore ->"+e.getMessage());
            log.error("Incasso Cancellato: " + e.getMessage());
            return;
        }
        
        if (errorFound) {
            return;
        }
        incassi.stream().forEach(incasso -> repo.save(incasso));
        avviso.setValue("Incasso Eseguito");
        list();

    }

    @Override
    public OutputStream receiveUpload(String filename, String mimeType) {
        gridVersamento.setVisible(false);
        avviso.setValue("Uploading");
        avviso.setVisible(true);

        FileOutputStream fos = null; // Stream to write to
        try {
            // Open the file for writing.
            file = new File("/tmp/" + filename);
            fos = new FileOutputStream(file);
        } catch (final java.io.FileNotFoundException e) {
            new Notification("Could not open file",
                             e.getMessage(),
                             Notification.Type.ERROR_MESSAGE)
                .show(Page.getCurrent());
            return null;
        }
        return fos; // Return the output stream to write to    
    }
}
