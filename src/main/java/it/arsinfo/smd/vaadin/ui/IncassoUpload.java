package it.arsinfo.smd.vaadin.ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.server.Page;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.themes.ValoTheme;

import it.arsinfo.smd.SmdApplication;
import it.arsinfo.smd.entity.Incasso;
import it.arsinfo.smd.vaadin.model.SmdChangeHandler;

public class IncassoUpload extends SmdChangeHandler implements Receiver, SucceededListener {

    /**
     * 
     */
    private static final long serialVersionUID = 9190730544104714862L;

    private static final Logger log = LoggerFactory.getLogger(IncassoUpload.class);

    private File file;
        
    private Label avviso = new Label();

    private List<Incasso> incassi = new ArrayList<>();

    public IncassoUpload() {
        super();

        Upload upload = new Upload("Aggiungi Incasso",this);
        upload.setImmediateMode(false);
        upload.setButtonCaption("Avvia Download");
        upload.addSucceededListener(this);
        avviso.addStyleName(ValoTheme.LABEL_H3);
        avviso.setVisible(false);
        
        setComponents(new HorizontalLayout(upload,avviso));

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
        avviso.setValue("Incasso Eseguito");
        onChange();

    }

    @Override
    public OutputStream receiveUpload(String filename, String mimeType) {
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

    public List<Incasso> getIncassi() {
        return incassi;
    }

    public void setIncassi(List<Incasso> incassi) {
        this.incassi = incassi;
    }
}
