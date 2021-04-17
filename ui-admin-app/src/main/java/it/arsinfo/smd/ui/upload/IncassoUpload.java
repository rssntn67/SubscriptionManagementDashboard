package it.arsinfo.smd.ui.upload;

import com.vaadin.server.Page;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import it.arsinfo.smd.entity.DistintaVersamento;
import it.arsinfo.smd.service.Smd;
import it.arsinfo.smd.ui.vaadin.SmdChangeHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class IncassoUpload extends SmdChangeHandler implements Receiver, SucceededListener {

    /**
     * 
     */
    private static final long serialVersionUID = 9190730544104714862L;

    private static final Logger log = LoggerFactory.getLogger(IncassoUpload.class);

    private File file;
        
    private final List<DistintaVersamento> incassi = new ArrayList<>();

    public static OutputStream getFileOutputStream(File file) throws Exception {
        try {
            log.info("Loading file: {}" , file.getName());
            return new FileOutputStream(file);
        } catch (final java.io.FileNotFoundException e) {
            log.error("Cannot open file {}", e.getMessage());
            throw e;
        }
    }

    public static File getIncassoFile(String filename) {
        return new File("/tmp/" + filename);
    }

    public static List<DistintaVersamento> uploadIncasso(File file) throws Exception {
        List<DistintaVersamento> incassi = new ArrayList<>();
        FileInputStream fstream;
        try {
            fstream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            log.error("Incasso Cancellato: " + e.getMessage());
            throw e;
        }

        //Read File Line By Line
        try (BufferedReader br = new BufferedReader(new InputStreamReader(fstream))) {
            String strLine;
            Set<String> versamentiLine = new HashSet<>();
            while ((strLine = br.readLine()) != null) {
                if (strLine.trim().equals("")) {
                    log.warn("uploadIncasso: Riga vuota!");
                } else if (isVersamento(strLine)) {
                    versamentiLine.add(strLine);
                } else if (isRiepilogo(strLine)) {
                    incassi.add(Smd.generaIncasso(versamentiLine, strLine));
                    versamentiLine.clear();
                } else {
                    throw new UnsupportedOperationException("Valore non riconosciuto->" + strLine);
                }
            }
        } catch (Exception e) {
            log.error("uploadIncasso:: Incasso da File Cancellato: " + e.getMessage());
            throw e;
        }
        return incassi;
    }

    public static boolean isVersamento(String versamento) {
        return (
                versamento != null && versamento.length() == 200
                        && (versamento.trim().length() == 82 || versamento.trim().length() == 89));
    }

    public static boolean isRiepilogo(String riepilogo) {
        return ( riepilogo != null &&
                riepilogo.length() == 200 &&
                riepilogo.trim().length() == 96 &&
                riepilogo.substring(19,33).trim().length() == 0 &&
                riepilogo.startsWith("999", 33)
        );
    }

    public IncassoUpload(String caption) {
        super();

        Upload upload = new Upload(caption,this);
        upload.setImmediateMode(true);
        upload.setButtonCaption("Upload File Poste");
        upload.addSucceededListener(this);
        
        setComponents(new HorizontalLayout(upload));

    }

    @Override
    public void uploadSucceeded(SucceededEvent event) {
        incassi.clear();
        try {
            incassi.addAll(uploadIncasso(file));
        } catch (Exception e) {
            Notification.show("Incasso Cancellato: "+e.getMessage(),Notification.Type.ERROR_MESSAGE);
            return;
        }
        Notification.show("Upload Eseguito!",Notification.Type.HUMANIZED_MESSAGE);
        onChange();
    }

    @Override
    public OutputStream receiveUpload(String filename, String mimeType) {
        Notification.show("Uploading......",Notification.Type.HUMANIZED_MESSAGE);
        
        file = getIncassoFile(filename);
        try {
        	return getFileOutputStream(file);
        } catch (final Exception e) {
            new Notification("Could not open file",
                             e.getMessage(),
                             Notification.Type.ERROR_MESSAGE)
                .show(Page.getCurrent());
            return null;
        }
    }

    public List<DistintaVersamento> getIncassi() {
        return incassi;
    }

}
