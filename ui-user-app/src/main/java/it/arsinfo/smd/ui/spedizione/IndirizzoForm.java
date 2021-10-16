package it.arsinfo.smd.ui.spedizione;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;
import it.arsinfo.smd.entity.Paese;
import it.arsinfo.smd.entity.Provincia;
import it.arsinfo.smd.dto.Indirizzo;

import java.util.EnumSet;


public class IndirizzoForm extends FormLayout {

    private final Binder<Indirizzo> binder;
    private Button close = new Button("Cancel");


    public IndirizzoForm(Binder<Indirizzo> binder) {
        this.binder = binder;
        close.addClickListener(event -> fireEvent(new IndirizzoForm.CloseEvent(this)));
        TextField intestazione = new TextField("Intestazione");
        intestazione.setReadOnly(true);
        TextField sottoIntestazione = new TextField("Sotto Intestazione");
        sottoIntestazione.setReadOnly(true);
        TextField indirizzo = new TextField("indirizzo");
        indirizzo.setReadOnly(true);
        TextField cap = new TextField("CAP");
        cap.setReadOnly(true);
        TextField citta = new TextField("Città");
        citta.setReadOnly(true);
        ComboBox<Provincia> provincia = new ComboBox<>("Provincia", EnumSet.allOf(Provincia.class));
        provincia.setReadOnly(true);
        ComboBox<Paese> paese = new ComboBox<>("Paese",
                EnumSet.allOf(Paese.class));
        paese.setReadOnly(true);

        binder.forField(intestazione).bind(Indirizzo::getIntestazione,null);
        binder.forField(sottoIntestazione).bind(Indirizzo::getSottoIntestazione,null);
        binder.forField(indirizzo).bind(Indirizzo::getIndirizzo,null);
        binder.forField(cap).bind(Indirizzo::getCap,null);
        binder.forField(citta).bind(Indirizzo::getCitta,null);
        binder.forField(provincia).bind(Indirizzo::getProvincia,null);
        binder.forField(paese).bind(Indirizzo::getPaese,null);

        add(createButtonsLayout());
        add(intestazione);
        add(sottoIntestazione);
        add(indirizzo);
        add(cap);
        add(citta);
        add(provincia);
        add(paese);
    }

    public HorizontalLayout createButtonsLayout() {
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        close.addClickShortcut(Key.ESCAPE);
        return new HorizontalLayout(close);
    }

    public void setEntity(Indirizzo indirizzo) {
        binder.readBean(indirizzo);
    }

    public static class CloseEvent extends ComponentEvent<IndirizzoForm> {
        CloseEvent(IndirizzoForm source) {
            super(source,true);
        }
    }

    public <K extends ComponentEvent<?>> Registration addListener(Class<K> eventType,
                                                                  ComponentEventListener<K> listener) {
        return getEventBus().addListener(eventType, listener);
    }


}
