package it.arsinfo.smd.ui.campagna;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import it.arsinfo.smd.data.InvioSpedizione;
import it.arsinfo.smd.data.StatoStorico;
import it.arsinfo.smd.data.TipoAbbonamentoRivista;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.entity.Storico;
import it.arsinfo.smd.ui.entity.EntityForm;

import java.util.EnumSet;
import java.util.List;

public class CampagnaForm extends EntityForm<Storico> {

    private final ComboBox<Anagrafica> intestatario = new ComboBox<Anagrafica>("Intestatario");
    private final ComboBox<Anagrafica> destinatario = new ComboBox<Anagrafica>("Destinatario");
    private final ComboBox<Pubblicazione> pubblicazione = new ComboBox<Pubblicazione>("Pubblicazioni");
    private final ComboBox<TipoAbbonamentoRivista> tipoAbbonamentoRivista =
            new ComboBox<TipoAbbonamentoRivista>("Tipo",EnumSet.allOf(TipoAbbonamentoRivista.class));
    private final ComboBox<InvioSpedizione> invioSpedizione = new ComboBox<InvioSpedizione>("Sped.",
            EnumSet.allOf(InvioSpedizione.class));
    private final TextField numero = new TextField("Numero");

    private final Checkbox contrassegno = new Checkbox("Contrassegno");

    private final ComboBox<StatoStorico> statoStorico = new ComboBox<StatoStorico>("Stato", EnumSet.allOf(StatoStorico.class));

    public CampagnaForm(Binder<Storico> binder, List<Anagrafica> anagrafiche,List<Pubblicazione> pubblicazioni) {
        super (binder);

        intestatario.setReadOnly(true);
        intestatario.setPlaceholder("Intestatario");
        intestatario.setItems(anagrafiche);
        intestatario.setItemLabelGenerator(Anagrafica::getCaption);


        destinatario.setReadOnly(true);
        destinatario.setPlaceholder("Destinatario");
        destinatario.setItems(anagrafiche);
        destinatario.setItemLabelGenerator(Anagrafica::getCaption);

        pubblicazione.setReadOnly(true);
        pubblicazione.setPlaceholder("Pubblicazione");
        pubblicazione.setItems(pubblicazioni);
        pubblicazione.setItemLabelGenerator(Pubblicazione::getNome);

        tipoAbbonamentoRivista.setReadOnly(true);

        statoStorico.setReadOnly(true);

        // Configure and style components

        add(createButtonsLayout());
        add(intestatario,destinatario,pubblicazione,tipoAbbonamentoRivista,invioSpedizione,numero,contrassegno,statoStorico);
        getSave().addClickListener(event -> {
            if (validate()) {
                fireEvent(new SaveEvent(this,getEntity()));
            }

        });
        getDelete().addClickListener(event -> {
            if (validate()) {
                fireEvent(new DeleteEvent(this, getEntity()));
            }
        });
        getClose().addClickListener(event -> fireEvent(new CloseEvent(this)));
    }

    public static abstract class FormEvent extends ComponentEvent<CampagnaForm> {
        private final Storico t;

        protected FormEvent(CampagnaForm source, Storico t) {
            super(source, false);
            this.t = t;
        }

        public Storico getEntity() {
            return t;
        }
    }

    public static class SaveEvent extends FormEvent {
        SaveEvent(CampagnaForm source, Storico t) {
            super(source, t);
        }
    }

    public static class DeleteEvent extends FormEvent {
        DeleteEvent(CampagnaForm source, Storico t) {
            super(source, t);
        }

    }

    public static class CloseEvent extends FormEvent {
        CloseEvent(CampagnaForm source) {
            super(source,null);
        }
    }

}
