package it.arsinfo.smd.vaadin.ui;

import java.util.EnumSet;

import com.vaadin.data.Binder;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;

import it.arsinfo.smd.data.Invio;
import it.arsinfo.smd.data.Omaggio;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.entity.Storico;
import it.arsinfo.smd.repository.AnagraficaDao;
import it.arsinfo.smd.repository.PubblicazioneDao;
import it.arsinfo.smd.repository.StoricoDao;
import it.arsinfo.smd.vaadin.model.SmdEditor;

public class StoricoEditor
        extends SmdEditor<Storico> {

    /**
     * 
     */
    private static final long serialVersionUID = 4673834235533544936L;

    private final ComboBox<Anagrafica> intestatario = new ComboBox<Anagrafica>("Intestatario");
    private final ComboBox<Anagrafica> destinatario = new ComboBox<Anagrafica>("Destinatario");
    private final ComboBox<Pubblicazione> pubblicazione = new ComboBox<Pubblicazione>("Pubblicazioni");
    private final ComboBox<Omaggio> omaggio = new ComboBox<Omaggio>("Omaggio",
                                                                    EnumSet.allOf(Omaggio.class));
    private final ComboBox<Invio> invio = new ComboBox<Invio>("Invio",
                                                              EnumSet.allOf(Invio.class));
    private final TextField numero = new TextField("Numero");

    public StoricoEditor(
            StoricoDao anagraficaPubblicazioneDao,
            PubblicazioneDao pubblicazioneDao, AnagraficaDao anagraficaDao) {

        super(anagraficaPubblicazioneDao, new Binder<>(Storico.class) );
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

        addComponents(getActions(),
                      new HorizontalLayout(numero, intestatario, destinatario,
                                           pubblicazione),
                      new HorizontalLayout(omaggio, invio));
        setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);

        getBinder().forField(numero).withConverter(new StringToIntegerConverter("")).bind(Storico::getNumero, Storico::setNumero);
        getBinder().bindInstanceFields(this);

        // Configure and style components
        setSpacing(true);

    }

    @Override
    public void focus(boolean persisted, Storico obj) {
        if (persisted && obj.getPubblicazione() != null && !obj.getPubblicazione().isActive()) {
            getSave().setEnabled(false);
        } else {
            getSave().setEnabled(true);
        }
        
        numero.focus();
    }

}
