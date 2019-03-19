package it.arsinfo.smd.vaadin.ui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Notification;

import it.arsinfo.smd.SmdApplication;
import it.arsinfo.smd.repository.AnagraficaDao;
import it.arsinfo.smd.repository.CampagnaDao;
import it.arsinfo.smd.repository.CampagnaItemDao;
import it.arsinfo.smd.repository.PubblicazioneDao;
import it.arsinfo.smd.vaadin.model.SmdUI;
import it.arsinfo.smd.vaadin.model.SmdUIHelper;

@SpringUI(path = SmdUIHelper.URL_CAMPAGNA)
@Title("Campagna Abbonamenti ADP")
public class CampagnaUI extends SmdUI {

    /**
     * 
     */
    private static final long serialVersionUID = 7884064928998716106L;

    @Autowired
    CampagnaDao campagnaDao;

    @Autowired
    CampagnaItemDao campagnaItemDao;

    @Autowired
    AnagraficaDao anagraficaDao;

    @Autowired
    PubblicazioneDao pubblicazioneDao;

    @Override
    protected void init(VaadinRequest request) {
        super.init(request, "Campagna");
        Assert.notNull(campagnaDao, "campagnaDao must be not null");
        Assert.notNull(campagnaItemDao, "campagnaItemDao must be not null");
        Assert.notNull(anagraficaDao, "anagraficaDao must be not null");
        Assert.notNull(pubblicazioneDao, "pubblicazioneDao must be not null");
        CampagnaAdd add = new CampagnaAdd("Genera una nuova Campagna");
        CampagnaSearch search = new CampagnaSearch(campagnaDao);
        CampagnaGrid grid = new CampagnaGrid("");
        CampagnaEditor editor = new CampagnaEditor(campagnaDao, 
                                                   anagraficaDao.findAll(),
                                                   pubblicazioneDao.findAll()) {
            @Override
            public void save() {
                if (get().getId() == null && get().getInizio() == null ) {
                    Notification.show("Selezionare Mese Inizio Prima di Salvare", Notification.Type.ERROR_MESSAGE);
                    return;
                }
                if (get().getId() == null && get().getFine() == null ) {
                    Notification.show("Selezionare Mese Fine Prima di Salvare", Notification.Type.ERROR_MESSAGE);
                    return;
                }
                if (get().getId() == null && get().getAnno() == null) {
                    Notification.show("Selezionare Anno Prima di Salvare", Notification.Type.ERROR_MESSAGE);
                    return;
                }
                if (get().getId() == null && get().getAnno().getAnno() < SmdApplication.getAnnoCorrente().getAnno()) {
                    Notification.show("Anno deve essere anno corrente o successivi", Notification.Type.ERROR_MESSAGE);
                    return;
                }
                if (get().getId() == null  && get().getInizio().getPosizione() > get().getFine().getPosizione()) {
                    Notification.show("Anno corrente: il Mese Inizio deve essere il corrente o successivo", Notification.Type.ERROR_MESSAGE);
                    return;
                }
                if (get().getId() == null && get().getAnno().getAnno() == SmdApplication.getAnnoCorrente().getAnno() && get().getInizio().getPosizione() < SmdApplication.getMeseCorrente().getPosizione()) {
                    Notification.show("Anno corrente: il Mese Inizio deve essere il corrente o successivo", Notification.Type.ERROR_MESSAGE);
                    return;
                }
                if (get().getId() == null && get().getCampagnaItems().isEmpty()) {
                    Notification.show("Aggiungere Pubblicazioni Prima di Generare", Notification.Type.WARNING_MESSAGE);
                    return;
                }
                SmdApplication.generaCampagna(get(), anagraficaDao.findAll());
                super.save();
            }
            
        };
        
        
        CampagnaItemAdd itemAdd = new CampagnaItemAdd("Aggiungi Pubblicazione");
        CampagnaItemGrid itemGrid = new CampagnaItemGrid("Pubblicazioni");
        CampagnaItemEditor itemEditor = new CampagnaItemEditor(campagnaItemDao, pubblicazioneDao.findByActiveAndAbbonamento(true, true)) {
            @Override
            public void save() {
                if (get().getPubblicazione() == null) {
                    Notification.show("Selezionare la Pubblicazione",Notification.Type.WARNING_MESSAGE);
                    return;
                }
                editor.get().addCampagnaItem(get());
                onChange();
            };
            
            @Override 
            public void delete() {
                editor.get().deleteCampagnaItem(get());
                onChange();
            };
            
        };
        
        addSmdComponents(itemEditor,editor,itemAdd,itemGrid, add,search, grid);

        editor.setVisible(false);
        itemEditor.setVisible(false);
        itemAdd.setVisible(false);
        itemGrid.setVisible(false);

        add.setChangeHandler(() -> {
            setHeader(String.format("Campagna:Add"));
            hideMenu();
            add.setVisible(false);
            search.setVisible(false);
            grid.setVisible(false);
            editor.edit(add.generate());
            itemAdd.setCampagna(editor.get());
            itemAdd.setVisible(true);        });

        search.setChangeHandler(() -> grid.populate(search.find()));
        grid.setChangeHandler(() -> {
            if (grid.getSelected() == null) {
                return;
            }
            setHeader("Campagna:Edit");
            hideMenu();
            add.setVisible(false);
            search.setVisible(false);
            editor.edit(grid.getSelected());
            itemAdd.setVisible(false);
            itemEditor.setVisible(false);
            itemGrid.populate(campagnaItemDao.findByCampagna(grid.getSelected()));;

        });

        editor.setChangeHandler(() -> {
            editor.setVisible(false);
            itemAdd.setVisible(false);
            itemGrid.setVisible(false);
            setHeader("Campagna");
            showMenu();
            add.setVisible(true);
            search.setVisible(true);
            grid.populate(search.find());
        });
        
        itemAdd.setChangeHandler(() -> {
            setHeader(String.format("Item:new:%s",editor.get().getAnno()));
            hideMenu();
            itemEditor.edit(itemAdd.generate());
            editor.setVisible(false);
            itemAdd.setVisible(false);
        });
        
        itemEditor.setChangeHandler(() -> {
            setHeader(String.format("Campagna:new"));
            itemAdd.setVisible(true);
            itemEditor.setVisible(false);
            editor.edit(editor.get());
            itemGrid.populate(editor.get().getCampagnaItems());
        });
        
        itemGrid.setChangeHandler(() -> {
            itemEditor.edit(itemGrid.getSelected());
        });

        grid.populate(search.findAll());

    }

}
