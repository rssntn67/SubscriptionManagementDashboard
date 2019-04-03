package it.arsinfo.smd.vaadin.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Title;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Button;

import it.arsinfo.smd.SmdApplication;
import it.arsinfo.smd.data.Bollettino;
import it.arsinfo.smd.entity.Abbonamento;
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
    
    private static final Logger log = LoggerFactory.getLogger(SmdApplication.class);

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
        IncassoGrid grid = new IncassoGrid("");
        IncassoEditor editor = new IncassoEditor(incassoDao);
        VersamentoGrid versGrid = new VersamentoGrid("Versamenti");
        VersamentoEditor versEditor = new VersamentoEditor(versamentoDao);
        AbbonamentoGrid abbonamentiAssociatiGrid = new AbbonamentoGrid("Abbonamenti Associati");
        AbbonamentoGrid abbonamentiAssociabiliGrid = new AbbonamentoGrid("Abbonamenti Associabili");

        addSmdComponents(
                         incassa,
                         editor,
                         versEditor,
                         abbonamentiAssociatiGrid,
                         abbonamentiAssociabiliGrid,
                         versGrid,
                         add,
                         upload, 
                         search,
                         grid
                         );
        incassa.setVisible(false);
        editor.setVisible(false);
        abbonamentiAssociatiGrid.setVisible(false);
        abbonamentiAssociabiliGrid.setVisible(false);
        versEditor.setVisible(false);
        versGrid.setVisible(false);

        add.setChangeHandler(() -> {
            editor.edit(add.generate());
            upload.setVisible(false);
            search.setVisible(false);
        });
        
        upload.setChangeHandler(() -> {
            upload.getIncassi().stream().forEach(incasso -> incassoDao.save(incasso));
            grid.populate(upload.getIncassi());
        });
        
        search.setChangeHandler(() -> grid.populate(search.find()));
        
        editor.setChangeHandler(() -> {
            grid.populate(search.find());
            add.setVisible(true);
            upload.setVisible(true);
            search.setVisible(true);
        });

        grid.setChangeHandler(() -> {
            if (grid.getSelected() == null) {
                add.setVisible(true);
                upload.setVisible(true);
                search.setVisible(true);
                editor.setVisible(false);
                incassa.setVisible(false);
            }
            editor.edit(grid.getSelected());
            incassa.setVisible(true);
            versGrid.populate(versamentoDao.findByIncasso(grid.getSelected()));
            add.setVisible(false);
            upload.setVisible(false);
            search.setVisible(false);
        });
        
        incassa.setChangeHandler(() -> {
            if (grid.getSelected() == null) {
                return;
            }
            versamentoDao.findByIncasso(grid.getSelected())
                .stream()
                .filter(v -> v.getResiduo().doubleValue() > 0 && v.getCampo() != null)
                .forEach(v-> {
                    log.info(v.toString());
                    List<Abbonamento> associabili = getAssociabili(v);
                    log.info("associabili:"+associabili.size());
                    
                    if (associabili.size() == 1 ) {
                        Abbonamento associabile = associabili.iterator().next();
                        log.info(associabile.toString());
                        if (v.getImporto().subtract(associabile.getCosto().subtract(associabile.getSpese())).signum() == 0) {
                            incassa(associabile, v);
                            log.info("incassato:" + v.toString());
                        }
                    }
                });
            incassa.setVisible(false);
            versGrid.populate(versamentoDao.findByIncasso(editor.get()));
            
        });
 
        versEditor.setChangeHandler(() -> {
            versEditor.setVisible(false);
            abbonamentiAssociatiGrid.setVisible(false);
            abbonamentiAssociabiliGrid.setVisible(false);
            versGrid.populate(versamentoDao.findByIncasso(grid.getSelected()));
            editor.edit(grid.getSelected());
        });

        versGrid.setChangeHandler(() -> {
            versEditor.edit(versGrid.getSelected());
            abbonamentiAssociabiliGrid.populate(getAssociabili(versGrid.getSelected()));
            abbonamentiAssociatiGrid.populate(getAssociati(versGrid.getSelected()));
            editor.setVisible(false);
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
                dissocia(abbonamento, versEditor.get());
                abbonamentiAssociatiGrid.onChange();
                });
            return button;
        });
        
        abbonamentiAssociabiliGrid.addComponentColumn(abbonamento -> {
            Button button = new Button("Incassa");
            button.addClickListener(click -> {
                incassa(abbonamento, versEditor.get());   
                abbonamentiAssociabiliGrid.onChange();
            });
            return button;
        });

        grid.populate(search.findAll());

    }

    
    private void dissocia(Abbonamento abbonamento, Versamento versamento) {
        versamentoDao.save(SmdApplication.dissocia(versamento, abbonamento));
        abbonamentoDao.save(abbonamento);
    }

    private void incassa(Abbonamento abbonamento, Versamento versamento) {
        versamentoDao.save(SmdApplication.incassa(versamento, abbonamento));
        abbonamentoDao.save(abbonamento);
    }

    private List<Abbonamento> getAssociati(Versamento versamento) {
        if (versamento == null) {
            return new ArrayList<>();
        }
        return abbonamentoDao.findByVersamento(versamento);
    }
    
    private List<Abbonamento> getAssociabili(Versamento versamento) {
        if (versamento == null) {
            return new ArrayList<>();
        }
        return abbonamentoDao
                .findByVersamento(null)
                .stream()
                .filter(abb -> abb.getIncassato().equals("No") && 
                        (versamento.getBollettino() != Bollettino.TIPO674) || versamento.getCampo().equals(abb.getCampo()))
                .collect(Collectors.toList());
    }
}
