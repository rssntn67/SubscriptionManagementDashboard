package it.arsinfo.smd.vaadin.ui;

import java.util.EnumSet;

import com.vaadin.data.Binder;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.data.validator.BeanValidator;
import com.vaadin.data.validator.IntegerRangeValidator;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;

import it.arsinfo.smd.data.Invio;
import it.arsinfo.smd.data.Omaggio;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.entity.Spedizione;
import it.arsinfo.smd.repository.AnagraficaDao;
import it.arsinfo.smd.repository.PubblicazioneDao;
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
            PubblicazioneDao pubblicazioneDao, AnagraficaDao anagraficaDao) {

        super(anagraficaPubblicazioneDao, new Binder<>(Spedizione.class) );
        pubblicazione.setEmptySelectionAllowed(false);
        pubblicazione.setPlaceholder("Pubblicazione");
        pubblicazione.setItems(pubblicazioneDao.findAll());
        pubblicazione.setItemCaptionGenerator(Pubblicazione::getCaption);

        destinatario.setEmptySelectionAllowed(false);
        destinatario.setPlaceholder("Destinatario");
        destinatario.setItems(anagraficaDao.findAll());
        destinatario.setItemCaptionGenerator(Anagrafica::getCaption);

        setComponents(new HorizontalLayout(numero, destinatario,
                                           pubblicazione),
                      new HorizontalLayout(omaggio, invio));
 
        getBinder()
            .forField(numero)
            .withConverter(new StringToIntegerConverter(""))
            .withValidator(new IntegerRangeValidator("il numero deve essere compreso fra 1 e 5000", 1, 5000))
            .bind(Spedizione::getNumero, Spedizione::setNumero);
        getBinder()
            .forField(pubblicazione)
            .asRequired()
            .withValidator(new BeanValidator(Pubblicazione.class, "nome"));
        getBinder().bindInstanceFields(this);

    }

    @Override
    public void focus(boolean persisted, Spedizione obj) {
        if (persisted && obj.getPubblicazione() != null && !obj.getPubblicazione().isActive()) {
            getSave().setEnabled(false);
        } else {
            getSave().setEnabled(true);
            destinatario.focus();
        }
        
    }

}
