package it.arsinfo.smd.vaadin;

import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.data.Binder;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

import it.arsinfo.smd.data.Cassa;
import it.arsinfo.smd.data.Invio;
import it.arsinfo.smd.data.Omaggio;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.AnagraficaPubblicazione;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.repository.AnagraficaDao;
import it.arsinfo.smd.repository.AnagraficaPubblicazioneDao;
import it.arsinfo.smd.repository.PubblicazioneDao;


public class AnagraficaPubblicazioneEditor extends SmdEditor {

    /**
     * 
     */
    private static final long serialVersionUID = 4673834235533544936L;

    private final AnagraficaPubblicazioneDao anagraficaPubblicazioneDao;

    /**
     * The currently edited customer
     */
    private AnagraficaPubblicazione anagraficaPubblicazione;
    
    private final ComboBox<Anagrafica> intestatario = new ComboBox<Anagrafica>("Intestatario");
    private final ComboBox<Anagrafica> destinatario = new ComboBox<Anagrafica>("Destinatario");
    private final ComboBox<Pubblicazione> pubblicazione = new ComboBox<Pubblicazione>("Pubblicazioni");
    private final ComboBox<Omaggio> omaggio = new ComboBox<Omaggio>("Omaggio",EnumSet.allOf(Omaggio.class));
    private final ComboBox<Cassa> cassa = new ComboBox<Cassa>("Cassa",EnumSet.allOf(Cassa.class)); 
    private final ComboBox<Invio> invio = new ComboBox<Invio>("Invio",EnumSet.allOf(Invio.class));
    private final TextField numero = new TextField("Numero");


    private Button save = new Button("Save", VaadinIcons.CHECK);
    private Button cancel = new Button("Cancel");
    private Button delete = new Button("Delete", VaadinIcons.TRASH);
    private Button back = new Button("Anagrafica");

    Binder<AnagraficaPubblicazione> binder = new Binder<>(AnagraficaPubblicazione.class);

    public AnagraficaPubblicazioneEditor(AnagraficaPubblicazioneDao anagraficaPubblicazioneDao, PubblicazioneDao pubblicazioneDao, AnagraficaDao anagraficaDao) {

        this.anagraficaPubblicazioneDao=anagraficaPubblicazioneDao;

        pubblicazione.setEmptySelectionAllowed(false);
        pubblicazione.setPlaceholder("pubblicazione");
        pubblicazione.setItems(pubblicazioneDao.findAll());
        pubblicazione.setItemCaptionGenerator(Pubblicazione::getCaption);

        intestatario.setEmptySelectionAllowed(false);
        intestatario.setPlaceholder("Intestatario");
        intestatario.setItems(anagraficaDao.findAll());
        intestatario.setItemCaptionGenerator(Anagrafica::getCaption);

        destinatario.setEmptySelectionAllowed(false);
        destinatario.setPlaceholder("Intestatario");
        destinatario.setItems(anagraficaDao.findAll());
        destinatario.setItemCaptionGenerator(Anagrafica::getCaption);

        addComponents(new HorizontalLayout(
                           save, cancel, delete,back
                          ),
                      new HorizontalLayout(
                           numero,intestatario,destinatario,pubblicazione
                           ),
                      new HorizontalLayout(
                           omaggio,cassa,invio
                       )
                  );
        setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);

        binder.bindInstanceFields(this);

        // Configure and style components
        setSpacing(true);

        save.addStyleName(ValoTheme.BUTTON_PRIMARY);
        delete.addStyleName(ValoTheme.BUTTON_DANGER);

        save.addClickListener(e -> save());
        delete.addClickListener(e -> delete());
        cancel.addClickListener(e -> edit(anagraficaPubblicazione));
        back.addClickListener(e->back());
        setVisible(false);

    }

    void back() {
        changeHandler.onChange();
    }
    
    void delete() {
        anagraficaPubblicazioneDao.delete(anagraficaPubblicazione);
        changeHandler.onChange();
    }

    void save() {
        anagraficaPubblicazioneDao.save(anagraficaPubblicazione);
        changeHandler.onChange();
    }

    public final void edit(AnagraficaPubblicazione c) {
        if (c == null) {
            setVisible(false);
            return;
        }
        final boolean persisted = c.getId() != null;
        if (persisted) {
            // Find fresh entity for editing
            anagraficaPubblicazione = anagraficaPubblicazioneDao.findById(c.getId()).get();
        } else {
            anagraficaPubblicazione = c;
        }
        cancel.setVisible(persisted);

        // Bind customer properties to similarly named fields
        // Could also use annotation or "manual binding" or programmatically
        // moving values from fields to entities before saving
        binder.setBean(anagraficaPubblicazione);

        setVisible(true);

        // Focus first name initially
        numero.focus();
    }
    
}
