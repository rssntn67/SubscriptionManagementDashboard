package it.arsinfo.smd.ui.upload;

import com.vaadin.annotations.Title;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Notification;
import it.arsinfo.smd.bancoposta.api.BancoPostaApiService;
import it.arsinfo.smd.service.api.DistintaVersamentoService;
import it.arsinfo.smd.ui.SmdUI;
import it.arsinfo.smd.ui.distinta.DistintaVersamentoGrid;
import it.arsinfo.smd.ui.vaadin.SmdButton;
import it.arsinfo.smd.ui.versamento.VersamentoGrid;
import org.springframework.beans.factory.annotation.Autowired;

@SpringUI(path = SmdUI.URL_UPLOAD_POSTE)
@Title(SmdUI.TITLE_UPLOAD_POSTE)
public class UploadUI extends SmdUI {
    /**
     * 
     */
    private static final long serialVersionUID = 7884064928998716106L;

    @Autowired
    private DistintaVersamentoService dao;

    @Autowired
    private BancoPostaApiService bancoPostaService;

    @Override
    protected void init(VaadinRequest request) {
        super.init(request,"Incassi");
        IncassoUpload upload = new IncassoUpload("Importa Incassi da File Poste",bancoPostaService);
        DistintaVersamentoGrid grid = new DistintaVersamentoGrid("Distinte Versamenti Importate");

        VersamentoGrid versGrid = new VersamentoGrid("Versamenti Importati");
        SmdButton indietro = new SmdButton("Indietro",VaadinIcons.BACKSPACE);

        addSmdComponents(upload,
        				indietro,
                         versGrid,
                         grid
                         );

        indietro.setVisible(false);
        grid.setVisible(false);
        versGrid.setVisible(false);

        upload.setChangeHandler(() -> {
            upload.getIncassi().forEach(incasso -> {
                try {
                    dao.save(incasso);
                } catch (Exception e) {
                    Notification.show(e.getMessage(),
                                      Notification.Type.ERROR_MESSAGE);
                }
            });
            grid.populate(upload.getIncassi());
        });

        indietro.setChangeHandler(() -> {
        	showMenu();
        	upload.setVisible(false);
            grid.setVisible(false);
            versGrid.setVisible(false);
        	indietro.setVisible(false);
        });

        grid.setChangeHandler(() -> {
            if (grid.getSelected() == null) {
                showMenu();
                versGrid.setVisible(false);
            } else {
                hideMenu();
                versGrid.populate(dao.getItems(grid.getSelected()));
                grid.setVisible(true);
            }
        });
        
                
    }  
    
}
