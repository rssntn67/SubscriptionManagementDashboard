package it.arsinfo.smd.vaadin.ui.anagrafica;

import java.util.EnumSet;

import com.vaadin.data.Binder;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;

import it.arsinfo.smd.data.Cassa;
import it.arsinfo.smd.data.Invio;
import it.arsinfo.smd.data.Omaggio;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.AnagraficaPubblicazione;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.repository.AnagraficaDao;
import it.arsinfo.smd.repository.AnagraficaPubblicazioneDao;
import it.arsinfo.smd.repository.PubblicazioneDao;
import it.arsinfo.smd.vaadin.ui.editor.SmdEditor;

public class AnagraficaPubblicazioneEditor
        extends SmdEditor<AnagraficaPubblicazione> {

    /**
     * 
     */
    private static final long serialVersionUID = 4673834235533544936L;

    private final ComboBox<Anagrafica> intestatario = new ComboBox<Anagrafica>("Intestatario");
    private final ComboBox<Anagrafica> destinatario = new ComboBox<Anagrafica>("Destinatario");
    private final ComboBox<Pubblicazione> pubblicazione = new ComboBox<Pubblicazione>("Pubblicazioni");
    private final ComboBox<Omaggio> omaggio = new ComboBox<Omaggio>("Omaggio",
                                                                    EnumSet.allOf(Omaggio.class));
    private final ComboBox<Cassa> cassa = new ComboBox<Cassa>("Cassa",
                                                              EnumSet.allOf(Cassa.class));
    private final ComboBox<Invio> invio = new ComboBox<Invio>("Invio",
                                                              EnumSet.allOf(Invio.class));
    private final TextField numero = new TextField("Numero");

    public AnagraficaPubblicazioneEditor(
            AnagraficaPubblicazioneDao anagraficaPubblicazioneDao,
            PubblicazioneDao pubblicazioneDao, AnagraficaDao anagraficaDao) {

        super(anagraficaPubblicazioneDao, new Binder<>(AnagraficaPubblicazione.class) );
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
                      new HorizontalLayout(omaggio, cassa, invio));
        setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);

        getBinder().forField(numero).withConverter(new StringToIntegerConverter("")).bind(AnagraficaPubblicazione::getNumero, AnagraficaPubblicazione::setNumero);
        getBinder().bindInstanceFields(this);

        // Configure and style components
        setSpacing(true);
        setVisible(false);

    }

    @Override
    public void focus(boolean persisted, AnagraficaPubblicazione obj) {
        numero.focus();
    }

}
