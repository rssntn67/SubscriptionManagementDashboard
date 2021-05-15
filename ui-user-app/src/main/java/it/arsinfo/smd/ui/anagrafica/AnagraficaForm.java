package it.arsinfo.smd.ui.anagrafica;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.EmailValidator;
import it.arsinfo.smd.data.*;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.ui.entity.EntityForm;

import java.util.EnumSet;

public class AnagraficaForm extends EntityForm<Anagrafica> {

    public AnagraficaForm(Binder<Anagrafica> binder) {
        super (binder);

        ComboBox<Diocesi> diocesi = new ComboBox<>("Diocesi",
                EnumSet.allOf(Diocesi.class));
        ComboBox<TitoloAnagrafica> titolo = new ComboBox<>("Titolo",
                EnumSet.allOf(TitoloAnagrafica.class));
        TextField nome = new TextField("Nome");
        TextField denominazione = new TextField("Denominazione");

        TextField citta = new TextField("citta");
        TextField cap = new TextField("cap");
        ComboBox<Provincia> provincia = new ComboBox<>("Provincia",
                EnumSet.allOf(Provincia.class));
        TextField indirizzo = new TextField("indirizzo");

        ComboBox<Paese> paese = new ComboBox<>("Paese",
                EnumSet.allOf(Paese.class));
        ComboBox<AreaSpedizione> areaSpedizione = new ComboBox<>("Area spedizione",
                EnumSet.allOf(AreaSpedizione.class));
        TextField indirizzoSecondaRiga = new TextField("Indirizzo+");

        TextField email = new TextField("Email");
        TextField telefono = new TextField("Telefono");
        TextField cellulare = new TextField("Cellulare");
        TextField codfis = new TextField("Cod. Fis.");
        TextField piva = new TextField("P.Iva");
        TextField codeLineBase = new TextField("Code Line Base");
        TextField descr = new TextField("Descr");
        HorizontalLayout poste = new HorizontalLayout(codeLineBase, descr);

        // Configure and style components
        diocesi.setItemLabelGenerator(Diocesi::getDetails);
        provincia.setItemLabelGenerator(Provincia::getNome);
        paese.setItemLabelGenerator(Paese::getNome);
        codeLineBase.setReadOnly(true);

        binder.forField(titolo).asRequired().bind(Anagrafica::getTitolo,Anagrafica::setTitolo);
        binder.forField(diocesi).asRequired().bind(Anagrafica::getDiocesi,Anagrafica::setDiocesi);
        binder.forField(denominazione).asRequired().bind(Anagrafica::getDenominazione,Anagrafica::setDenominazione);
        binder.forField(paese).asRequired().bind(Anagrafica::getPaese,Anagrafica::setPaese);
        binder.forField(email).asRequired()
                .withValidator(new EmailValidator("Immettere un indizzo di mail valido"))
                .bind(Anagrafica::getEmail,Anagrafica::setEmail);
        binder.bindInstanceFields(this);

        HorizontalLayout intestazioni = new HorizontalLayout(diocesi, titolo, nome);
        intestazioni.addAndExpand(denominazione);
        HorizontalLayout residenza = new HorizontalLayout(citta, provincia, cap);
        residenza.addAndExpand(indirizzo);
        HorizontalLayout residenza2 = new HorizontalLayout(paese, areaSpedizione);
        residenza2.addAndExpand(indirizzoSecondaRiga);
        HorizontalLayout dati = new HorizontalLayout(email,
                telefono,
                cellulare,
                codfis,
                piva
        );
        add(intestazioni,residenza,residenza2,dati,poste);

        getSave().addClickListener(event -> {
            if (validate()) {
                fireEvent(new AnagraficaForm.SaveEvent(this,getEntity()));
            }

        });
        getDelete().addClickListener(event -> fireEvent(new AnagraficaForm.DeleteEvent(this, getEntity())));
        getClose().addClickListener(event -> fireEvent(new AnagraficaForm.CloseEvent(this)));
    }

    public static abstract class FormEvent extends ComponentEvent<AnagraficaForm> {
        private final Anagrafica t;

        protected FormEvent(AnagraficaForm source, Anagrafica t) {
            super(source, false);
            this.t = t;
        }

        public Anagrafica getEntity() {
            return t;
        }
    }

    public static class SaveEvent extends FormEvent {
        SaveEvent(AnagraficaForm source, Anagrafica t) {
            super(source, t);
        }
    }

    public static class DeleteEvent extends FormEvent {
        DeleteEvent(AnagraficaForm source, Anagrafica t) {
            super(source, t);
        }

    }

    public static class CloseEvent extends FormEvent {
        CloseEvent(AnagraficaForm source) {
            super(source,null);
        }
    }

}
