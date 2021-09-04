package it.arsinfo.smd.ui.upload;

import com.vaadin.server.Page;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import it.arsinfo.smd.bancoposta.api.BancoPostaService;
import it.arsinfo.smd.bollettino.impl.BollettinoServiceConfigImpl;
import it.arsinfo.smd.entity.DistintaVersamento;
import it.arsinfo.smd.ui.vaadin.SmdChangeHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class IncassoUpload extends SmdChangeHandler implements Receiver, SucceededListener {

    /**
     * 
     */
    private static final long serialVersionUID = 9190730544104714862L;

    private static final Logger log = LoggerFactory.getLogger(IncassoUpload.class);

    private File file;

    private final BancoPostaService bancoPostaService;
    private final BollettinoServiceConfigImpl ccpConfig;
    private final List<DistintaVersamento> incassi = new ArrayList<>();

    public IncassoUpload(String caption, BancoPostaService bancoPostaService, BollettinoServiceConfigImpl ccpConfig) {
        super();
        this.bancoPostaService=bancoPostaService;
        this.ccpConfig=ccpConfig;
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
            incassi.addAll(bancoPostaService.uploadIncasso(file));
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
        
        file = bancoPostaService.getFile(ccpConfig,filename);
        try {
            log.info("Loading file: {}" , file.getName());
            return new FileOutputStream(file);
        } catch (final Exception e) {
            log.error("Cannot open file {}", e.getMessage());
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
