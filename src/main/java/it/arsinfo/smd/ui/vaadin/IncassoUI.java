package it.arsinfo.smd.ui.vaadin;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Title;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Notification;

import it.arsinfo.smd.Smd;
import it.arsinfo.smd.SmdService;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Incasso;
import it.arsinfo.smd.entity.Versamento;
import it.arsinfo.smd.repository.IncassoDao;
import it.arsinfo.smd.repository.VersamentoDao;

@SpringUI(path = SmdUI.URL_INCASSI)
@Title("Incassi ADP")
public class IncassoUI extends IncassoAbstractUI {
    /**
     * 
     */
    private static final long serialVersionUID = 7884064928998716106L;

    @Autowired
    private IncassoDao incassoDao;
    @Autowired    
    private VersamentoDao versamentoDao;

    @Autowired SmdService smdService;
    @Override
    protected void init(VaadinRequest request) {
        super.init(request,"Incassi");
        IncassoAdd add = new IncassoAdd("Aggiungi Incasso");
        IncassoUpload upload = new IncassoUpload("Incasso da Poste");
        IncassoSearch search = new IncassoSearch(incassoDao);
        SmdButton incassa = new SmdButton("Incassa con Code Line",VaadinIcons.AUTOMATION);
        SmdButton incassaSingolo = new SmdButton("Incassa con Code Line",VaadinIcons.AUTOMATION);
        IncassoGrid grid = new IncassoGrid("Incassi");

        IncassoEditor editor = new IncassoEditor(incassoDao) {
            @Override
            public void save() {
                if (get().getId() == null && get().getVersamenti().isEmpty()) {
                    Notification.show("Aggiungere Versamenti Prima di Salvare", Notification.Type.WARNING_MESSAGE);
                    return;
                }
                if (get().getId() == null && get().getDataContabile().after(Smd.getStandardDate(new Date()))) {
                    Notification.show("Non si può selezionare una data contabile futuro", Notification.Type.WARNING_MESSAGE);
                    return;
                }
                try {
                    smdService.save(get());
                    onChange();
                } catch (Exception e) {
                    Notification.show("Non è possibile salvare questo record: ",
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
                editor.get().addVersamento(get());
                Smd.calcoloImportoIncasso(editor.get());
                onChange();
            }
            
            @Override
            public void delete() {
                editor.get().deleteVersamento(get());
                Smd.calcoloImportoIncasso(editor.get());
                onChange();
            }
            
            @Override
            public void focus(boolean persisted, Versamento versamento) {
                super.focus(persisted, versamento);
                getDelete().setVisible(!persisted);
                getSave().setVisible(!persisted);
                getCancel().setVisible(!persisted);
                getImporto().setReadOnly(persisted);
            }
        };

        addSmdComponents(
                         upload,
                         add,                          
                         search,
                         incassa,
                         editor,
                         versAdd,
                         versEditor,
                         incassaSingolo,
                         versGrid,
                         grid
                         );

        editor.setVisible(false);
        versEditor.setVisible(false);
        incassaSingolo.setVisible(false);
        versGrid.setVisible(false);
        versAdd.setVisible(false);
        
        add.setChangeHandler(() -> {
            setHeader("Incasso:Nuovo");
            hideMenu();
            add.setVisible(false);
            upload.setVisible(false);
            search.setVisible(false);
            incassa.setVisible(false);
            grid.setVisible(false);
            editor.edit(add.generate());     
            versAdd.setIncasso(editor.get());
            versAdd.setVisible(true);
        });
        
        upload.setChangeHandler(() -> {
            upload.getIncassi().stream().forEach(incasso -> {
                try {
                    smdService.save(incasso);
                } catch (Exception e) {
                    Notification.show("Non è possibile salvare questo record: ",
                                      Notification.Type.ERROR_MESSAGE);
                }
            });
            grid.populate(upload.getIncassi());
        });
        
        search.setChangeHandler(() ->grid.populate(search.find()));

        incassa.setChangeHandler(() -> {
            for (Incasso iiii : search.find()) {
                versamentoDao.findByIncasso(iiii)
                    .stream()
                    .filter(v -> v.getResiduo().doubleValue() > 0 && v.getCodeLine() != null)
                    .forEach(v-> {
                    getAssociabili(v)
                    .stream()
                    .filter(abb -> abb.getCodeLine() != null && abb.getCodeLine().equals(v.getCodeLine()))
                    .forEach(abb -> incassa(abb, v));                        
                    });
            }
            grid.populate(search.find());                
        });

        versAdd.setChangeHandler(() -> {
            setHeader(String.format("%s:Spedizione:Nuova",editor.get().getHeader()));
            hideMenu();
            versEditor.edit(versAdd.generate());
            editor.setVisible(false);
            versAdd.setVisible(false);

        });
        editor.setChangeHandler(() -> {
            showMenu();
            setHeader("Incassi");
            grid.populate(search.find());
            add.setVisible(true);
            upload.setVisible(true);
            search.setVisible(true);
            incassa.setVisible(true);
            editor.setVisible(false);
            versGrid.setVisible(false);
            versAdd.setVisible(false);
            
        });

        grid.setChangeHandler(() -> {
            if (grid.getSelected() == null) {
                add.setVisible(true);
                upload.setVisible(true);
                search.setVisible(true);
                incassa.setVisible(true);
                incassaSingolo.setVisible(false);
                editor.setVisible(false);
                versGrid.setVisible(false);
            } else {
                editor.edit(grid.getSelected());
                versGrid.populate(versamentoDao.findByIncasso(grid.getSelected()));
                incassaSingolo.setVisible(true);
                add.setVisible(false);
                grid.setVisible(false);
                upload.setVisible(false);
                search.setVisible(false);
                incassa.setVisible(false);
            }
        });
        
        incassaSingolo.setChangeHandler(() -> {
            if (grid.getSelected() == null) {
                return;
            }
            versamentoDao.findByIncasso(grid.getSelected())
                .stream()
                .filter(v -> v.getResiduo().doubleValue() > 0 && v.getCodeLine() != null)
                .forEach(v-> {
                    List<Abbonamento> associabili = getAssociabili(v);
                    
                    if (associabili.size() == 1 ) {
                        Abbonamento associabile = associabili.iterator().next();
                        if (v.getImporto().subtract(associabile.getTotale()).signum() == 0) {
                            incassa(associabile, v);
                        }
                    }
                });
            grid.populate(search.find());
            grid.getGrid().select(editor.get());
        });
 
        versEditor.setChangeHandler(() -> {
            setHeader("Incasso:Nuovo");
            versAdd.setVisible(true);
            if (editor.get().getId() != null) {
               versGrid.populate(versamentoDao.findByIncasso(editor.get()));
            } else {
                versGrid.populate(editor.get().getVersamenti());
            }
            versEditor.setVisible(false);
            editor.edit(versEditor.get().getIncasso());
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
                versAdd.setVisible(false);
                versEditor.edit(versGrid.getSelected());
            }
        });
        
        grid.populate(search.findAll());
    }    
}
