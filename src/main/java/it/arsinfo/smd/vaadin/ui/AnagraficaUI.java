package it.arsinfo.smd.vaadin.ui;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Notification;

import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Storico;
import it.arsinfo.smd.repository.AnagraficaDao;
import it.arsinfo.smd.repository.StoricoDao;
import it.arsinfo.smd.repository.PubblicazioneDao;
import it.arsinfo.smd.vaadin.model.SmdUI;
import it.arsinfo.smd.vaadin.model.SmdUIHelper;

@SpringUI(path = SmdUIHelper.URL_ANAGRAFICA)
@Title("Anagrafica Clienti ADP")
public class AnagraficaUI extends SmdUI {

    /**
     * 
     */
    private static final long serialVersionUID = 7884064928998716106L;

    @Autowired
    AnagraficaDao anagraficaDao;
    @Autowired
    PubblicazioneDao pubblicazioneDao;
    @Autowired
    StoricoDao storicoDao;

    @Override
    protected void init(VaadinRequest request) {
        super.init(request, "Anagrafica");
        Assert.notNull(anagraficaDao, "anagraficaDao must be not null");
        Assert.notNull(storicoDao,
                       "anagraficaPubblicazioneDao must be not null");
        AnagraficaAdd add = new AnagraficaAdd("Aggiungi ad Anagrafica");
        AnagraficaSearch search = new AnagraficaSearch(anagraficaDao);
        AnagraficaGrid grid = new AnagraficaGrid("");
        AnagraficaEditor editor = new AnagraficaEditor(anagraficaDao);
        
        StoricoAdd storicoAdd = new StoricoAdd("Aggiungi Storico");
        StoricoEditor storicoEditor = 
                new StoricoEditor(
                      storicoDao,
                      pubblicazioneDao.findAll(),
                      anagraficaDao.findAll()
        ) {
            @Override
            public void save() {
                if (getPubblicazione().isEmpty()) {
                    Notification.show("Pubblicazione deve essere valorizzata");
                    return;
                }
                super.save();
            }
        };
        StoricoGrid storicoGrid = new StoricoGrid("Storico");
        addSmdComponents(storicoAdd,storicoEditor, editor, storicoGrid, add,search, grid);
        
        editor.setVisible(false);
        storicoGrid.setVisible(false);
        storicoEditor.setVisible(false);
        storicoAdd.setVisible(false);

        add.setChangeHandler(() -> {
            setHeader("Anagrafica:Nuova");
            hideMenu();
            add.setVisible(false);
            search.setVisible(false);
            grid.setVisible(false);
            editor.edit(add.generate());
        });

        search.setChangeHandler(() -> {
            grid.populate(search.find());
        });
        
        grid.setChangeHandler(() -> {
            if (grid.getSelected() == null) {
                return;
            }
            setHeader(grid.getSelected().getHeader());
            hideMenu();
            add.setVisible(false);
            search.setVisible(false);
            editor.edit(grid.getSelected());
            storicoAdd.setIntestatario(grid.getSelected());
            storicoAdd.setVisible(true);
            storicoGrid.populate(findByCustomer(grid.getSelected()));
        });

        editor.setChangeHandler(() -> {
            grid.populate(search.find());
            editor.setVisible(false);
            setHeader("Anagrafica");
            showMenu();
            storicoGrid.setVisible(false);
            storicoAdd.setVisible(false);
            add.setVisible(true);
            search.setVisible(true);
        });

        storicoGrid.setChangeHandler(() -> {
            if (storicoGrid.getSelected() == null) {
                return;
            }
            setHeader(storicoGrid.getSelected().getHeader());
            storicoEditor.edit(storicoGrid.getSelected());
            add.setVisible(false);
            search.setVisible(false);
            editor.setVisible(false);
            storicoAdd.setVisible(false);
        });

        storicoEditor.setChangeHandler(() -> {
            storicoGrid.populate(findByCustomer(grid.getSelected()));
            setHeader(grid.getSelected().getHeader());
            editor.setVisible(true);
            storicoEditor.setVisible(false);
            storicoAdd.setVisible(true);
        });

        storicoAdd.setChangeHandler(() -> {
            storicoEditor.edit(storicoAdd.generate());
            setHeader(String.format("%s:Storico:Nuovo",editor.get().getHeader()));
            storicoAdd.setVisible(false);
            editor.setVisible(false);
        });

        grid.populate(search.findAll());

    }
    
    public List<Storico> findByCustomer(Anagrafica customer) {
        List<Storico> list = storicoDao.findByIntestatario(customer);
        list.addAll(storicoDao.findByDestinatario(customer)
                    .stream()
                    .filter(ap -> customer.getId() != ap.getIntestatario().getId())
                    .collect(Collectors.toList()));
        return list;
    }

}
