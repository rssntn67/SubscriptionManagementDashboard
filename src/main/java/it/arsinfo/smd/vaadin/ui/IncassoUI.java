package it.arsinfo.smd.vaadin.ui;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Title;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;

import it.arsinfo.smd.SmdApplication;
import it.arsinfo.smd.data.Bollettino;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Incasso;
import it.arsinfo.smd.entity.Versamento;
import it.arsinfo.smd.repository.AbbonamentoDao;
import it.arsinfo.smd.repository.IncassoDao;
import it.arsinfo.smd.repository.VersamentoDao;
import it.arsinfo.smd.vaadin.model.SmdButton;
import it.arsinfo.smd.vaadin.model.SmdUI;
import it.arsinfo.smd.vaadin.model.SmdUIHelper;

@SpringUI(path = SmdUIHelper.URL_INCASSI)
@Title("Incassi ADP")
public class IncassoUI extends SmdUI {
    /**
     * 
     */
    private static final long serialVersionUID = 7884064928998716106L;

    @Autowired
    private IncassoDao incassoDao;
    @Autowired    
    private VersamentoDao versamentoDao;
    @Autowired    
    private AbbonamentoDao abbonamentoDao;

    @Override
    protected void init(VaadinRequest request) {
        super.init(request,"Incassi");
        IncassoAdd add = new IncassoAdd("Aggiungi Incasso");
        IncassoUpload upload = new IncassoUpload("Incasso da Poste");
        IncassoSearch search = new IncassoSearch(incassoDao);
        SmdButton incassa = new SmdButton("Incassa con V campo",VaadinIcons.AUTOMATION);
        SmdButton incassaSingolo = new SmdButton("Incassa con V campo",VaadinIcons.AUTOMATION);
        IncassoGrid grid = new IncassoGrid("");

        IncassoEditor editor = new IncassoEditor(incassoDao) {
            @Override
            public void save() {
                if (get().getId() == null && get().getVersamenti().isEmpty()) {
                    Notification.show("Aggiungere Versamenti Prima di Salvare", Notification.Type.WARNING_MESSAGE);
                    return;
                }
                if (get().getId() == null && get().getDataContabile().after(SmdApplication.getStandardDate(new Date()))) {
                    Notification.show("Non si può selezionare una data contabile futuro", Notification.Type.WARNING_MESSAGE);
                    return;
                }

                super.save();
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
                SmdApplication.calcoloImportoIncasso(editor.get());
                onChange();
            }
            
            @Override
            public void delete() {
                editor.get().deleteVersamento(get());
                SmdApplication.calcoloImportoIncasso(editor.get());
                onChange();
            }
        };
        AbbonamentoGrid abbonamentiAssociatiGrid = new AbbonamentoGrid("Abbonamenti Associati");
        AbbonamentoGrid abbonamentiAssociabiliGrid = new AbbonamentoGrid("Abbonamenti Associabili");

        addSmdComponents(
                         add,
                         upload, 
                         search,
                         incassa,
                         editor,
                         versAdd,
                         versEditor,
                         abbonamentiAssociatiGrid,
                         abbonamentiAssociabiliGrid,
                         incassaSingolo,
                         versGrid,
                         grid
                         );

        editor.setVisible(false);
        abbonamentiAssociatiGrid.setVisible(false);
        abbonamentiAssociabiliGrid.setVisible(false);
        versEditor.setVisible(false);
        incassaSingolo.setVisible(false);
        versGrid.setVisible(false);
        versAdd.setVisible(false);
        
        abbonamentiAssociabiliGrid.getGrid().setHeight("150px");
        abbonamentiAssociatiGrid.getGrid().setHeight("150px");
        versGrid.getGrid().setHeight("300px");
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
            upload.getIncassi().stream().forEach(incasso -> incassoDao.save(incasso));
            grid.populate(upload.getIncassi());
        });
        
        search.setChangeHandler(() ->grid.populate(search.find()));

        incassa.setChangeHandler(() -> {
            for (Incasso iiii : search.find()) {
                versamentoDao.findByIncasso(iiii)
                    .stream()
                    .filter(v -> v.getResiduo().doubleValue() > 0 && v.getCampo() != null)
                    .forEach(v-> {
                        List<Abbonamento> associabili = getAssociabili(v);
                        
                        if (associabili.size() == 1 ) {
                            Abbonamento associabile = associabili.iterator().next();
                            if (v.getImporto().subtract(associabile.getCosto().subtract(associabile.getSpese())).signum() == 0) {
                                incassa(iiii,associabile, v);
                            }
                        }
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
                .filter(v -> v.getResiduo().doubleValue() > 0 && v.getCampo() != null)
                .forEach(v-> {
                    List<Abbonamento> associabili = getAssociabili(v);
                    
                    if (associabili.size() == 1 ) {
                        Abbonamento associabile = associabili.iterator().next();
                        if (v.getImporto().subtract(associabile.getCosto().subtract(associabile.getSpese())).signum() == 0) {
                            incassa(grid.getSelected(),associabile, v);
                        }
                    }
                });
            grid.populate(search.find());
            grid.getGrid().select(editor.get());
        });
 
        versEditor.setChangeHandler(() -> {
            setHeader("Incasso:Nuovo");
            versAdd.setVisible(true);
            versGrid.populate(editor.get().getVersamenti());
            versEditor.setVisible(false);
            abbonamentiAssociatiGrid.setVisible(false);
            abbonamentiAssociabiliGrid.setVisible(false);
            editor.edit(versEditor.get().getIncasso());
        });

        versGrid.setChangeHandler(() -> {
            if (versGrid.getSelected() == null) {
                editor.edit(grid.getSelected());
                abbonamentiAssociabiliGrid.setVisible(false);
                abbonamentiAssociatiGrid.setVisible(false);
                versEditor.setVisible(false);
            } else if (versGrid.getSelected().getId() != null ){
                versEditor.edit(versGrid.getSelected());
                abbonamentiAssociatiGrid.populate(getAssociati(versGrid.getSelected()));
                abbonamentiAssociabiliGrid.populate(getAssociabili(versGrid.getSelected()));
                editor.setVisible(false);
            } else {
                editor.setVisible(false);
                versAdd.setVisible(false);
                versEditor.edit(versGrid.getSelected());
            }
        });
        
        
        
        abbonamentiAssociabiliGrid.setChangeHandler(() -> {
            versEditor.edit(versGrid.getSelected());
            versGrid.populate(versamentoDao.findByIncasso(grid.getSelected()));
            abbonamentiAssociabiliGrid.setVisible(false);
            abbonamentiAssociatiGrid.setVisible(false); 
        });
        
        abbonamentiAssociatiGrid.setChangeHandler(() -> {
            versEditor.edit(versGrid.getSelected());
            versGrid.populate(versamentoDao.findByIncasso(grid.getSelected()));
            abbonamentiAssociabiliGrid.setVisible(false);
            abbonamentiAssociatiGrid.setVisible(false); 
        });
        
        abbonamentiAssociatiGrid.addComponentColumn(abbonamento -> {
            Button button = new Button("Dissocia");
            button.addClickListener(click -> {
                dissocia(incassoDao.findById(versEditor.get().getIncasso().getId()).get(),abbonamento, versEditor.get());
                abbonamentiAssociatiGrid.onChange();
                });
            return button;
        });
        
        abbonamentiAssociabiliGrid.addComponentColumn(abbonamento -> {
            Button button = new Button("Incassa");
            button.addClickListener(click -> {
                incassa(incassoDao.findById(versEditor.get().getIncasso().getId()).get(),abbonamento, versEditor.get());   
                abbonamentiAssociabiliGrid.onChange();
            });
            return button;
        });

        grid.populate(search.findAll());
    }

    
    private void dissocia(Incasso incasso,Abbonamento abbonamento, Versamento versamento) {
        try {
            versamentoDao.save(SmdApplication.dissocia(incasso, versamento, abbonamento));
            incassoDao.save(incasso);
            abbonamentoDao.save(abbonamento);
        } catch (UnsupportedOperationException e) {
            Notification.show(e.getMessage(), Notification.Type.ERROR_MESSAGE);
            
        }
    }

    private void incassa(Incasso incasso,Abbonamento abbonamento, Versamento versamento) {
        try {
            versamentoDao.save(SmdApplication.incassa(incasso,versamento, abbonamento));
            incassoDao.save(incasso);
            abbonamentoDao.save(abbonamento);
        } catch (UnsupportedOperationException e) {
            Notification.show(e.getMessage(), Notification.Type.ERROR_MESSAGE);
        } 
    }

    private List<Abbonamento> getAssociati(Versamento versamento) {
        if (versamento == null) {
            return new ArrayList<>();
        }
        return abbonamentoDao.findByVersamento(versamento);
    }
    
    private List<Abbonamento> getAssociabili(Versamento versamento) {
        if (versamento == null || versamento.getResiduo().compareTo(BigDecimal.ZERO) <= 0) {
            return new ArrayList<>();
        }
        return abbonamentoDao
                .findByVersamento(null)
                .stream()
                .filter(abb -> abb.getIncassato().equals("No") 
                        && versamento.getResiduo().subtract(abb.getTotale()).compareTo(BigDecimal.ZERO) >= 0
                        && (versamento.getBollettino() != Bollettino.TIPO674) 
                            || abb.getCampo().equals(versamento.getCampo())
                            )
                .collect(Collectors.toList());
    }
    
}
