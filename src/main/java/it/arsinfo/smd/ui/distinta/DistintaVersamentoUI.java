package it.arsinfo.smd.ui.distinta;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Notification;

import it.arsinfo.smd.dao.DistintaVersamentoServiceDao;
import it.arsinfo.smd.dao.SmdService;
import it.arsinfo.smd.dao.repository.VersamentoDao;
import it.arsinfo.smd.service.Smd;
import it.arsinfo.smd.ui.SmdUI;

@SpringUI(path = SmdUI.URL_DISTINTA_VERSAMENTI)
@Title("Distinta versamenti")
public class DistintaVersamentoUI extends SmdUI {
    /**
     * 
     */
    private static final long serialVersionUID = 7884064928998716106L;

    @Autowired
    private DistintaVersamentoServiceDao dao;
    @Autowired    
    private VersamentoDao versamentoDao;

    @Autowired 
    private SmdService smdService;
    
    @Override
    protected void init(VaadinRequest request) {
        super.init(request,"Distinte");
        DistintaVersamentoAdd add = new DistintaVersamentoAdd("Aggiungi Distinta");
        DistintaVersamentoSearch search = new DistintaVersamentoSearch(dao);
        DistintaVersamentoGrid grid = new DistintaVersamentoGrid("Distinte Versamenti");

        DistintaVersamentoEditor editor = new DistintaVersamentoEditor(dao.getRepository()) {
            @Override
            public void save() {
                if (get().getId() == null && get().getVersamenti().isEmpty()) {
                    Notification.show("Aggiungere Versamenti Prima di Salvare", Notification.Type.WARNING_MESSAGE);
                    return;
                }
                if (get().getDataContabile().after(Smd.getStandardDate(new Date()))) {
                    Notification.show("Non si può selezionare una data contabile futuro", Notification.Type.WARNING_MESSAGE);
                    return;
                }
                try {
            		smdService.save(get());
                    onChange();
                } catch (Exception e) {
                    Notification.show("Non è possibile salvare questo record: "+ e.getMessage(),
                                      Notification.Type.ERROR_MESSAGE);
                }
            }
        };
        VersamentoAdd versAdd = new VersamentoAdd("Aggiungi Versamento");
        VersamentoGrid versGrid = new VersamentoGrid("Versamenti");
        VersamentoEditor versEditor = new VersamentoEditor(versamentoDao) {
            @Override
            public void save() {
                if (get().getImporto().compareTo(BigDecimal.ZERO) <= 0) {
                    Notification.show("Importo non deve essere ZERO",Notification.Type.WARNING_MESSAGE);
                    return;
                }
                if (get().getDataPagamento().after(get().getDataContabile())) {
                    Notification.show("La data di pagamento deve  essere anteriore alla data contabile",Notification.Type.WARNING_MESSAGE);
                    return;
                }
                if (editor.get().getId() == null) {
                    editor.get().addVersamento(get());
                    Smd.calcoloImportoIncasso(editor.get());
                    onChange();
                    return;
                } 
                try {
                    smdService.save(get());
                    onChange();
                } catch (Exception e) {
                    Notification.show(e.getMessage(),
                                      Notification.Type.ERROR_MESSAGE);
                }
            }
            
            @Override
            public void delete() {
                if (editor.get().getId() == null) {
                    editor.get().deleteVersamento(get());
                    Smd.calcoloImportoIncasso(editor.get());
                    onChange();
                    return;
                } 
                
                try {
                    smdService.delete(get());
                    onChange();
                } catch (Exception e) {
                    Notification.show(e.getMessage(),
                                      Notification.Type.ERROR_MESSAGE);
                }
            }              
        };

        addSmdComponents(add,search,
                         editor,
                         versAdd,
                         versEditor,
                         versGrid,
                         grid
                         );

        editor.setVisible(false);
        versAdd.setVisible(false);
        versGrid.setVisible(false);

        versEditor.setVisible(false);

        grid.setChangeHandler(() -> {
            if (grid.getSelected() == null) {
                showMenu();
                add.setVisible(true);
                search.setVisible(true);

                editor.setVisible(false);
                versAdd.setVisible(false);
                versGrid.setVisible(false);
            } else {
                hideMenu();
                
                editor.edit(grid.getSelected());
                versAdd.setIncasso(grid.getSelected());
                versAdd.setVisible(true);
                versGrid.populate(versamentoDao.findByDistintaVersamento(grid.getSelected()));
                add.setVisible(false);
                search.setVisible(false);
                grid.setVisible(false);
            }
        });

        editor.setChangeHandler(() -> {
            showMenu();
            setHeader("Incassi");
            add.setVisible(true);
            search.setVisible(true);
            grid.populate(search.find());

            editor.setVisible(false);
            versAdd.setVisible(false);
            versGrid.setVisible(false);
        });

        add.setChangeHandler(() -> {
            setHeader("Incasso:Aggiungi");
            hideMenu();
            add.setVisible(false);
            search.setVisible(false);
            grid.setVisible(false);
            
            editor.edit(add.generate());     
            versAdd.setIncasso(editor.get());
            versAdd.setVisible(true);           
        });
                
        search.setChangeHandler(() ->grid.populate(search.find()));

        versAdd.setChangeHandler(() -> {
            setHeader(String.format("%s:Versamento:Aggiungi",editor.get().getHeader()));
            hideMenu();
            versEditor.edit(versAdd.generate());
            editor.setVisible(false);
            versAdd.setVisible(false);
        });

        versEditor.setChangeHandler(() -> {
            setHeader("Incasso");
            if (editor.get().getId() != null) {
               versGrid.populate(versamentoDao.findByDistintaVersamento(editor.get()));
            } else {
                versGrid.populate(editor.get().getVersamenti());
            }
            versEditor.setVisible(false);
            editor.edit(versEditor.get().getDistintaVersamento());
            versAdd.setVisible(true);
        });

        versGrid.setChangeHandler(() -> {
            if (versGrid.getSelected() == null) {
                editor.edit(grid.getSelected());
                versEditor.setVisible(false);
            } else if (versGrid.getSelected().getId() != null ){
                versEditor.edit(versGrid.getSelected());
                editor.setVisible(false);
            } else {
                editor.setVisible(false);
                versEditor.edit(versGrid.getSelected());
            }
        });
        
        grid.populate(search.findAll());
    }  
    
}
