package it.arsinfo.smd.ui.anagrafica;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import it.arsinfo.smd.data.*;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.ui.MainLayout;
import it.arsinfo.smd.ui.entity.EntityForm;

import java.util.EnumSet;

@Route(value="adp/anagrafica", layout = MainLayout.class)
@PageTitle("Anagrafica | ADP")
public class AnagraficaForm extends EntityForm<Anagrafica> {

    public AnagraficaForm() {
        super (new BeanValidationBinder<>(Anagrafica.class));

        ComboBox<Diocesi> diocesi = new ComboBox<>("Diocesi",
                EnumSet.allOf(Diocesi.class));
        ComboBox<TitoloAnagrafica> titolo = new ComboBox<>("Titolo",
                EnumSet.allOf(TitoloAnagrafica.class));
        TextField nome = new TextField("Nome");
        HorizontalLayout intestazioni = new HorizontalLayout(diocesi, titolo, nome);
        TextField denominazione = new TextField("Denominazione");
        intestazioni.addAndExpand(denominazione);

        TextField citta = new TextField("citta");
        TextField cap = new TextField("cap");
        ComboBox<Provincia> provincia = new ComboBox<>("Provincia",
                EnumSet.allOf(Provincia.class));
        HorizontalLayout residenza = new HorizontalLayout(citta, provincia, cap);
        TextField indirizzo = new TextField("indirizzo");
        residenza.addAndExpand(indirizzo);

        ComboBox<Paese> paese = new ComboBox<>("Paese",
                EnumSet.allOf(Paese.class));
        ComboBox<AreaSpedizione> areaSpedizione = new ComboBox<>("Area spedizione",
                EnumSet.allOf(AreaSpedizione.class));
        HorizontalLayout residenza2 = new HorizontalLayout(paese, areaSpedizione);
        TextField indirizzoSecondaRiga = new TextField("Indirizzo+");
        residenza2.addAndExpand(indirizzoSecondaRiga);

        TextField email = new TextField("Email");
        TextField telefono = new TextField("Telefono");
        TextField cellulare = new TextField("Cellulare");
        TextField codfis = new TextField("Cod. Fis.");
        TextField piva = new TextField("P.Iva");
        HorizontalLayout dati = new HorizontalLayout(email,
                telefono,
                cellulare,
                codfis,
                piva
        );
        TextField codeLineBase = new TextField("Code Line Base");
        TextField descr = new TextField("Descr");
        HorizontalLayout poste = new HorizontalLayout(codeLineBase, descr);

        add(intestazioni,residenza,residenza2,dati,poste);

        getBinder().forField(titolo).asRequired();
        getBinder().forField(diocesi).asRequired();
        getBinder().forField(denominazione).asRequired();
        getBinder().forField(paese).asRequired();
        getBinder().forField(email).withValidator(new EmailValidator("Immettere un indizzo di mail valido"));
        getBinder().bindInstanceFields(this);

        // Configure and style components
        diocesi.setItemLabelGenerator(Diocesi::getDetails);
        provincia.setItemLabelGenerator(Provincia::getNome);
        paese.setItemLabelGenerator(Paese::getNome);
        codeLineBase.setReadOnly(true);
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
