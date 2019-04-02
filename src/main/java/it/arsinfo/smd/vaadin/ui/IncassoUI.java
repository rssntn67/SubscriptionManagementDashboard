package it.arsinfo.smd.vaadin.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Button;

import it.arsinfo.smd.data.Bollettino;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Versamento;
import it.arsinfo.smd.repository.AbbonamentoDao;
import it.arsinfo.smd.repository.IncassoDao;
import it.arsinfo.smd.repository.VersamentoDao;
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
        IncassoGrid grid = new IncassoGrid("");
        IncassoEditor editor = new IncassoEditor(incassoDao);
        VersamentoGrid versGrid = new VersamentoGrid("Versamenti");
        VersamentoEditor versEditor = new VersamentoEditor(versamentoDao);
        AbbonamentoGrid abbonamentiAssociatiGrid = new AbbonamentoGrid("Abbonamenti Associati");
        AbbonamentoGrid abbonamentiAssociabiliGrid = new AbbonamentoGrid("Abbonamenti Associabili");

        abbonamentiAssociatiGrid.addComponentColumn(abbonamento -> {
            Button button = new Button("Dissocia");
            button.addClickListener(click -> dissocia(abbonamento));
            abbonamentiAssociatiGrid.onChange();
            return button;
        });
        
        abbonamentiAssociabiliGrid.addComponentColumn(abbonamento -> {
            Button button = new Button("Associa");
            button.addClickListener(click -> incassa(abbonamento, versEditor.get()));
            abbonamentiAssociabiliGrid.onChange();
            return button;
        });

        addSmdComponents(add,upload, editor,abbonamentiAssociatiGrid,abbonamentiAssociabiliGrid,versEditor,versGrid,search, grid);
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
            }
            editor.edit(grid.getSelected());
            versGrid.populate(versamentoDao.findByIncasso(grid.getSelected()));
            add.setVisible(false);
            upload.setVisible(false);
            search.setVisible(false);
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
            abbonamentiAssociabiliGrid.populate(getAssociabili(versGrid.getSelected()));
            abbonamentiAssociatiGrid.populate(getAssociati(versGrid.getSelected())); 
        });
        
        abbonamentiAssociatiGrid.setChangeHandler(() -> {
            versEditor.edit(versGrid.getSelected());
            abbonamentiAssociabiliGrid.populate(getAssociabili(versGrid.getSelected()));
            abbonamentiAssociatiGrid.populate(getAssociati(versGrid.getSelected()));
        });
        
        grid.populate(search.findAll());

    }

    
    private void dissocia(Abbonamento abbonamento) {
        abbonamento.setVersamento(null);
        abbonamentoDao.save(abbonamento);
    }

    private void incassa(Abbonamento abbonamento, Versamento versamento) {
        abbonamento.setVersamento(versamento);
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
