package it.arsinfo.smd.ui.vaadin;

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
import com.vaadin.ui.Notification;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;

import it.arsinfo.smd.Smd;
import it.arsinfo.smd.entity.Incasso;

public class IncassoUpload extends SmdChangeHandler implements Receiver, SucceededListener {

    /**
     * 
     */
    private static final long serialVersionUID = 9190730544104714862L;

    private static final Logger log = LoggerFactory.getLogger(IncassoUpload.class);

    private File file;
        
    private final List<Incasso> incassi = new ArrayList<>();

    public IncassoUpload(String caption) {
        super();

        Upload upload = new Upload("",this);
        upload.setImmediateMode(true);
        upload.setButtonCaption("Upload File Poste");
        upload.addSucceededListener(this);
        
        setComponents(new HorizontalLayout(upload));

    }

    @Override
    public void uploadSucceeded(SucceededEvent event) {
        incassi.clear();
        FileInputStream fstream;
        try {
            fstream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            Notification.show("Incasso Cancellato: "+e.getMessage(),Notification.Type.ERROR_MESSAGE);
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
                if (strLine.trim().equals("")) {
                    log.debug("Riga vuota!");
                } else if (Smd.isVersamento(strLine)) {
                    versamenti.add(strLine);
                } else if (Smd.isRiepilogo(strLine)) {
                    incassi.add(Smd.generaIncasso(versamenti, strLine));
                    versamenti.clear();                    
                } else {
                    Notification.show("Incasso Cancellato: Valore non riconosciuto: " + strLine,Notification.Type.ERROR_MESSAGE);
                    log.error("Incasso Cancellato: Valore non riconosciuto->" +strLine);
                    errorFound=true;
                    break;
                }
            }
        } catch (Exception e) {
            Notification.show("Incasso Cancellato: "+e.getMessage(),Notification.Type.ERROR_MESSAGE);
            log.error("Incasso Cancellato: " + e.getMessage());
            errorFound = true;
        }

        //Close the input stream
        try {
            br.close();
        } catch (IOException e) {
            Notification.show("Incasso Non completato: "+e.getMessage(),Notification.Type.ERROR_MESSAGE);
            log.error("Incasso Non completato: " + e.getMessage());
            return;
        }
        
        if (errorFound) {
            return;
        }
        Notification.show("Incasso Eseguito!",Notification.Type.HUMANIZED_MESSAGE);
        onChange();

    }

    @Override
    public OutputStream receiveUpload(String filename, String mimeType) {
        Notification.show("Uploading......",Notification.Type.HUMANIZED_MESSAGE);
        
        FileOutputStream fos = null; // Stream to write to
        try {
            // Open the file for writing.
            file = new File("/tmp/" + filename);
            fos = new FileOutputStream(file);
            log.info("Loading file: {}" , filename);
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

}
