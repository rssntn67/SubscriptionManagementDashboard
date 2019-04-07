package it.arsinfo.smd.vaadin.ui;

import java.util.ArrayList;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Title;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;

import it.arsinfo.smd.Smd;
import it.arsinfo.smd.data.TipoPubblicazione;
import it.arsinfo.smd.entity.CampagnaItem;
import it.arsinfo.smd.repository.AbbonamentoDao;
import it.arsinfo.smd.repository.CampagnaDao;
import it.arsinfo.smd.repository.CampagnaItemDao;
import it.arsinfo.smd.repository.PubblicazioneDao;
import it.arsinfo.smd.repository.StoricoDao;
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
    StoricoDao storicoDao;

    @Autowired
    PubblicazioneDao pubblicazioneDao;
    
    @Autowired
    AbbonamentoDao abbonamentoDao;

    @Override
    protected void init(VaadinRequest request) {
        super.init(request, "Campagna");
        CampagnaItemEditor campagnaItemEditor = 
                new CampagnaItemEditor(
                   pubblicazioneDao.findAll()
                   .stream()
                   .filter(p -> p.isActive() && p.getTipo() != TipoPubblicazione.UNICO)
                   .collect(Collectors.toList())
               );
        CampagnaAdd add = new CampagnaAdd("Genera una nuova Campagna");
        CampagnaSearch search = new CampagnaSearch(campagnaDao);
        CampagnaGrid grid = new CampagnaGrid("");
        CampagnaEditor editor = new CampagnaEditor(campagnaDao) {
            @Override
            public void delete() {
                if (get().getAnno() == Smd.getAnnoCorrente()) {
                    Notification.show("Non è possibile cancellare campagna dell'anno corrente", Notification.Type.ERROR_MESSAGE);
                    return;
                    
                }
                if (get().getAnno() == Smd.getAnnoPassato()) {
                    Notification.show("Non è possibile cancellare campagna dell'anno passato", Notification.Type.ERROR_MESSAGE);
                    return;
                }
                super.delete();
            }
            
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
                if (get().getId() == null && !campagnaDao.findByAnno(get().getAnno()).isEmpty()) {
                    Notification.show("E' stata già generata la Campagna per Anno "+ get().getAnno() + ". Solo una Campagna per Anno", Notification.Type.ERROR_MESSAGE);
                    return;
                }
                if (get().getId() == null && get().getAnno().getAnno() < Smd.getAnnoCorrente().getAnno()) {
                    Notification.show("Anno deve essere anno corrente o successivi", Notification.Type.ERROR_MESSAGE);
                    return;
                }
                if (get().getId() == null  && get().getInizio().getPosizione() > get().getFine().getPosizione()) {
                    Notification.show("Anno corrente: il Mese Inizio deve essere successivo al Mese Fine", Notification.Type.ERROR_MESSAGE);
                    return;
                }
                if (get().getId() == null && get().getAnno().getAnno() == Smd.getAnnoCorrente().getAnno() && get().getInizio().getPosizione() < Smd.getMeseCorrente().getPosizione()) {
                    Notification.show("Anno corrente: il Mese Inizio deve essere il Mese corrente o successivo", Notification.Type.ERROR_MESSAGE);
                    return;
                }
                if (get().getId() == null &&  campagnaItemEditor.getSelected().isEmpty() ) {
                    Notification.show("Selezionare almeno una Pubblicazione Per Generare la Campagna Abbonamenti", Notification.Type.WARNING_MESSAGE);
                    return;
                }
                campagnaItemEditor.getSelected().forEach(p -> get().addCampagnaItem(new CampagnaItem(get(), p)));
                Smd.generaCampagna(get(), storicoDao.findAll(),abbonamentoDao.findAll());
                super.save();
            }
            
        };
        
        addSmdComponents(campagnaItemEditor,editor, add,search, grid);
        editor.setVisible(false);

        add.setChangeHandler(() -> {
            setHeader(String.format("Campagna:Add"));
            hideMenu();
            add.setVisible(false);
            search.setVisible(false);
            grid.setVisible(false);
            editor.edit(add.generate());
            campagnaItemEditor.edit(new ArrayList<>(), false);
        });

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
            campagnaItemEditor.edit(campagnaItemDao.findByCampagna(grid.getSelected()), true);
        });

        editor.setChangeHandler(() -> {
            editor.setVisible(false);
            campagnaItemEditor.setVisible(false);
            setHeader("Campagna");
            showMenu();
            add.setVisible(true);
            search.setVisible(true);
            grid.populate(search.find());
        });
        
        grid.addComponentColumn(campagna -> {
            Button button = new Button("Genera Anagrafica Ccp", VaadinIcons.ENVELOPES);
            button.addClickListener(click -> {
                Notification.show("Da realizzare", Notification.Type.WARNING_MESSAGE);
                });
            return button;
        });

        grid.populate(search.findAll());

    }

}
