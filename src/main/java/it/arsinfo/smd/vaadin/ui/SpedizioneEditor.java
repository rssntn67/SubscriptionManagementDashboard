package it.arsinfo.smd.vaadin.ui;

import java.util.EnumSet;
import java.util.List;

import com.vaadin.data.Binder;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;

import it.arsinfo.smd.data.Invio;
import it.arsinfo.smd.data.Omaggio;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.entity.Spedizione;
import it.arsinfo.smd.repository.SpedizioneDao;
import it.arsinfo.smd.vaadin.model.SmdEditor;

public class SpedizioneEditor
        extends SmdEditor<Spedizione> {

    private final ComboBox<Anagrafica> destinatario = new ComboBox<Anagrafica>("Destinatario");
    private final ComboBox<Pubblicazione> pubblicazione = new ComboBox<Pubblicazione>("Pubblicazioni");
    private final ComboBox<Omaggio> omaggio = new ComboBox<Omaggio>("Omaggio",
                                                                    EnumSet.allOf(Omaggio.class));
    private final ComboBox<Invio> invio = new ComboBox<Invio>("Invio",
                                                              EnumSet.allOf(Invio.class));
    private final TextField numero = new TextField("Numero");

    public SpedizioneEditor(
            SpedizioneDao anagraficaPubblicazioneDao,
            List<Pubblicazione> pubblicazioni, List<Anagrafica> anagrafica) {

        super(anagraficaPubblicazioneDao, new Binder<>(Spedizione.class) );
        pubblicazione.setEmptySelectionAllowed(false);
        pubblicazione.setPlaceholder("Pubblicazione");
        pubblicazione.setItems(pubblicazioni);
        pubblicazione.setItemCaptionGenerator(Pubblicazione::getCaption);

        destinatario.setEmptySelectionAllowed(false);
        destinatario.setPlaceholder("Destinatario");
        destinatario.setItems(anagrafica);
        destinatario.setItemCaptionGenerator(Anagrafica::getCaption);

        setComponents(getActions(), new HorizontalLayout(numero, destinatario,
                                           pubblicazione),
                      new HorizontalLayout(omaggio, invio));
 
        getBinder()
            .forField(numero)
            .withValidator(str -> str != null, "Inserire un numero")
            .withConverter(new StringToIntegerConverter(""))
            .withValidator(num -> num > 0,"deve essere maggiore di 0")
            .bind(Spedizione::getNumero, Spedizione::setNumero);
        getBinder()
            .forField(pubblicazione)
            .asRequired()
            .withValidator(p -> p != null, "Pubblicazione deve essere selezionata")
            .bind(Spedizione::getPubblicazione,Spedizione::setPubblicazione);
        getBinder().bindInstanceFields(this);

    }

    @Override
    public void focus(boolean persisted, Spedizione obj) {
        getSave().setEnabled(!persisted);
        getCancel().setEnabled(!persisted);
        getDelete().setEnabled(!persisted);
        destinatario.focus();        
    }

}
