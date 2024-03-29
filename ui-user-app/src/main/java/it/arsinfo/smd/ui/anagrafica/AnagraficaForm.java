package it.arsinfo.smd.ui.anagrafica;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.EmailValidator;
import it.arsinfo.smd.entity.*;
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
        TextField denominazione = new TextField("Denominazione/Cognome");

        TextField citta = new TextField("Citta");
        TextField cap = new TextField("Cap");
        ComboBox<Provincia> provincia = new ComboBox<>("Provincia",
                EnumSet.allOf(Provincia.class));
        TextField indirizzo = new TextField("Indirizzo");

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
        TextField codeLineBase = new TextField("Codice Identificativo");
        TextField descr = new TextField("Descr");

        // Configure and style components
        diocesi.setItemLabelGenerator(Diocesi::getDetails);
        provincia.setItemLabelGenerator(Provincia::getNome);
        paese.setItemLabelGenerator(Paese::getNome);
        codeLineBase.setReadOnly(true);

        binder.forField(titolo).asRequired().bind(Anagrafica::getTitolo,Anagrafica::setTitolo);
        binder.forField(diocesi).asRequired().bind(Anagrafica::getDiocesi,Anagrafica::setDiocesi);
        binder.forField(nome).bind(Anagrafica::getNome,Anagrafica::setNome);
        binder.forField(denominazione).asRequired().bind(Anagrafica::getDenominazione,Anagrafica::setDenominazione);
        binder.forField(citta).asRequired().bind(Anagrafica::getCitta,Anagrafica::setCitta);
        binder.forField(provincia).asRequired().bind(Anagrafica::getProvincia,Anagrafica::setProvincia);
        binder.forField(cap).asRequired().bind(Anagrafica::getCap,Anagrafica::setCap);
        binder.forField(indirizzo).asRequired().bind(Anagrafica::getIndirizzo,Anagrafica::setIndirizzo);
        binder.forField(indirizzoSecondaRiga).bind(Anagrafica::getIndirizzoSecondaRiga,Anagrafica::setIndirizzoSecondaRiga);
        binder.forField(paese).asRequired().bind(Anagrafica::getPaese,Anagrafica::setPaese);
        binder.forField(areaSpedizione).asRequired().bind(Anagrafica::getAreaSpedizione,Anagrafica::setAreaSpedizione);
        binder.forField(email).asRequired()
                .withValidator(new EmailValidator("Immettere un indizzo di mail valido"))
                .bind(Anagrafica::getEmail,Anagrafica::setEmail);
        binder.forField(telefono).bind(Anagrafica::getTelefono,Anagrafica::setTelefono);
        binder.forField(cellulare).bind(Anagrafica::getCellulare,Anagrafica::setCellulare);
        binder.forField(codfis).bind(Anagrafica::getCodfis,Anagrafica::setCodfis);
        binder.forField(piva).bind(Anagrafica::getPiva,Anagrafica::setPiva);
        binder.forField(codeLineBase).bind(Anagrafica::getCodeLineBase,Anagrafica::setCodeLineBase);
        binder.forField(descr).bind(Anagrafica::getDescr,Anagrafica::setDescr);

        add(createButtonsLayout());
        add(titolo, nome);
        add(denominazione);
        add(indirizzo);
        add(indirizzoSecondaRiga);
        add(citta, provincia, cap);
        add(diocesi);
        add(paese, areaSpedizione);
        add(email,telefono,cellulare,codfis,piva);
        add(codeLineBase, descr);

        getSave().addClickListener(event -> {
            if (validate()) {
                fireEvent(new AnagraficaForm.SaveEvent(this,getEntity()));
            }

        });
        getDelete().setEnabled(false);
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
