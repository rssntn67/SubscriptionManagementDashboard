package it.arsinfo.smd.ui.upload;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Notification;

import it.arsinfo.smd.dao.SmdService;
import it.arsinfo.smd.dao.repository.VersamentoDao;
import it.arsinfo.smd.ui.SmdUI;
import it.arsinfo.smd.ui.distinta.DistintaVersamentoGrid;
import it.arsinfo.smd.ui.distinta.VersamentoGrid;

@SpringUI(path = SmdUI.URL_UPLOAD_POSTE)
@Title(SmdUI.TITLE_UPLOAD_POSTE)
public class UploadUI extends SmdUI {
    /**
     * 
     */
    private static final long serialVersionUID = 7884064928998716106L;

    @Autowired    
    private VersamentoDao versamentoDao;

    @Autowired 
    private SmdService smdService;
    
    @Override
    protected void init(VaadinRequest request) {
        super.init(request,"Incassi");
        IncassoUpload upload = new IncassoUpload("Importa Incassi da File Poste");
        DistintaVersamentoGrid grid = new DistintaVersamentoGrid("Distinte Versamenti Importate");

        VersamentoGrid versGrid = new VersamentoGrid("Versamenti Importati");

        addSmdComponents(upload,
                         versGrid,
                         grid
                         );

        grid.setVisible(false);
        versGrid.setVisible(false);

        grid.setChangeHandler(() -> {
            if (grid.getSelected() == null) {
                showMenu();
                versGrid.setVisible(false);
            } else {
                hideMenu();
                versGrid.populate(versamentoDao.findByDistintaVersamento(grid.getSelected()));
                grid.setVisible(true);
            }
        });
        
        upload.setChangeHandler(() -> {
            upload.getIncassi().stream().forEach(incasso -> {
                try {
                    smdService.save(incasso);
                } catch (Exception e) {
                    Notification.show(e.getMessage(),
                                      Notification.Type.ERROR_MESSAGE);
                }
            });
            grid.populate(upload.getIncassi());
        });
                
    }  
    
}
