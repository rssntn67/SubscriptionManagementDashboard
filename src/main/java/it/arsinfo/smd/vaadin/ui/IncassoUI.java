package it.arsinfo.smd.vaadin.ui;

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
            button.setEnabled(versEditor.get().getBollettino() != Bollettino.TIPO674);
            return button;
        });
        
        abbonamentiAssociabiliGrid.addComponentColumn(abbonamento -> {
            Button button = new Button("Associa");
            button.addClickListener(click -> incassa(abbonamento, versEditor.get()));
            button.setEnabled(versEditor.get().getBollettino() != Bollettino.TIPO674);
            return button;
        });

        addSmdComponents(add,upload, editor,abbonamentiAssociatiGrid,abbonamentiAssociabiliGrid,versEditor,versGrid,versGrid,search, grid);
        editor.setVisible(false);
        versGrid.setVisible(false);

        add.setChangeHandler(() -> {
            editor.edit(add.generate());
            add.setVisible(false);
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
            editor.setVisible(false);
        });

        grid.setChangeHandler(() -> {
            editor.edit(grid.getSelected());
            versGrid.populate(versamentoDao.findByIncasso(grid.getSelected()));
            add.setVisible(false);
            upload.setVisible(false);
            search.setVisible(false);
        });
        
        versGrid.setChangeHandler(() -> {
            versEditor.edit(versGrid.getSelected());
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

}
