package it.arsinfo.smd.ui.vaadin;

import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.vaadin.server.Page;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;

import it.arsinfo.smd.entity.Incasso;
import it.arsinfo.smd.service.Smd;

public class IncassoUpload extends SmdChangeHandler implements Receiver, SucceededListener {

    /**
     * 
     */
    private static final long serialVersionUID = 9190730544104714862L;

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
        try {
        	Smd.uploadIncasso(file).forEach(incasso -> incassi.add(incasso));
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
        
        file = Smd.getIncassoFile(filename);
        try {
        	return Smd.getFileOutputStream(file);
        } catch (final Exception e) {
            new Notification("Could not open file",
                             e.getMessage(),
                             Notification.Type.ERROR_MESSAGE)
                .show(Page.getCurrent());
            return null;
        }
    }

    public List<Incasso> getIncassi() {
        return incassi;
    }

}
