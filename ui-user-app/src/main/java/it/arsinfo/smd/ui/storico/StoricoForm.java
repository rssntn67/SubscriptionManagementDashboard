package it.arsinfo.smd.ui.storico;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import it.arsinfo.smd.entity.AreaSpedizione;
import it.arsinfo.smd.entity.InvioSpedizione;
import it.arsinfo.smd.entity.StatoStorico;
import it.arsinfo.smd.entity.TipoAbbonamentoRivista;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.entity.Storico;
import it.arsinfo.smd.ui.entity.EntityForm;

import java.util.EnumSet;
import java.util.List;

public class StoricoForm extends EntityForm<Storico> {

    private final ComboBox<Anagrafica> destinatario = new ComboBox<>("Destinatario");
    private final ComboBox<InvioSpedizione> invioSpedizione = new ComboBox<>("Sped.",
            EnumSet.allOf(InvioSpedizione.class));
    private final ComboBox<Pubblicazione> pubblicazione = new ComboBox<>("Pubblicazione");

    private final Checkbox contrassegno = new Checkbox("Contrassegno");
    private final TextField numero = new TextField("Numero");


    public StoricoForm(Binder<Storico> binder, List<Anagrafica> anagrafiche, List<Pubblicazione> pubblicazioni) {
        super(binder);
        ComboBox<Anagrafica> intestatario = new ComboBox<>("Intestatario");
        intestatario.isRequired();
        intestatario.setReadOnly(true);
        intestatario.setPlaceholder("Intestatario");
        intestatario.setItems(anagrafiche);
        intestatario.setItemLabelGenerator(Anagrafica::getCaption);

        destinatario.isRequired();
        destinatario.setPlaceholder("Destinatario");
        destinatario.setItems(anagrafiche);
        destinatario.setItemLabelGenerator(Anagrafica::getCaption);

        pubblicazione.isRequired();
        pubblicazione.setPlaceholder("Pubblicazione");
        pubblicazione.setItems(pubblicazioni);
        pubblicazione.setItemLabelGenerator(Pubblicazione::getNome);
        pubblicazione.setReadOnly(true);
        ComboBox<TipoAbbonamentoRivista> tipoAbbonamentoRivista =
                new ComboBox<>("Tipo",EnumSet.allOf(TipoAbbonamentoRivista.class));
        tipoAbbonamentoRivista.isRequired();
        tipoAbbonamentoRivista.setReadOnly(true);

        invioSpedizione.setReadOnly(true);

        ComboBox<StatoStorico> statoStorico = new ComboBox<>("Stato", EnumSet.allOf(StatoStorico.class));
        statoStorico.setReadOnly(true);


        binder.forField(intestatario).asRequired().bind(Storico::getIntestatario,Storico::setIntestatario);
        binder.forField(destinatario).asRequired().bind(Storico::getDestinatario,Storico::setDestinatario);
        binder.forField(pubblicazione).asRequired().bind(Storico::getPubblicazione,Storico::setPubblicazione);
        binder.forField(tipoAbbonamentoRivista).asRequired().bind(Storico::getTipoAbbonamentoRivista,Storico::setTipoAbbonamentoRivista);
        binder.forField(numero)
                .withConverter(new StringToIntegerConverter("Inserire un numero"))
                .withValidator(num -> num >= 0,"deve essere maggiore o uguale a 0")
                .bind(Storico::getNumero, Storico::setNumero);
        binder.forField(invioSpedizione).asRequired().bind(Storico::getInvioSpedizione,Storico::setInvioSpedizione);
        binder.forField(contrassegno).bind(Storico::isContrassegno,Storico::setContrassegno);
        binder.forField(statoStorico).bind(Storico::getStatoStorico,Storico::setStatoStorico);

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

    @Override
    public void isNew() {
        pubblicazione.setReadOnly(false);
    }

    @Override
    public void setReadOnly(boolean readonly) {
        switch (getEntity().getTipoAbbonamentoRivista()) {
            case OmaggioCuriaDiocesiana:
            case OmaggioCuriaGeneralizia:
            case OmaggioDirettoreAdp:
            case OmaggioEditore:
            case OmaggioGesuiti:
            case Scontato:
                destinatario.setReadOnly(true);
                contrassegno.setReadOnly(true);
                invioSpedizione.setReadOnly(true);
                pubblicazione.setReadOnly(true);
                numero.setReadOnly(true);
                super.setReadOnly(true);
                break;
            default:
                destinatario.setReadOnly(readonly);
                contrassegno.setReadOnly(readonly);
                invioSpedizione.setReadOnly(readonly);
                numero.setReadOnly(readonly);
                super.setReadOnly(readonly);
                break;
        }

        if (getEntity().getDestinatario().getAreaSpedizione() != AreaSpedizione.Italia) {
            invioSpedizione.setReadOnly(true);
        }

    }

    public static abstract class FormEvent extends ComponentEvent<StoricoForm> {
        private final Storico t;

        protected FormEvent(StoricoForm source, Storico t) {
            super(source, false);
            this.t = t;
        }

        public Storico getEntity() {
            return t;
        }
    }

    public static class SaveEvent extends FormEvent {
        SaveEvent(StoricoForm source, Storico t) {
            super(source, t);
        }
    }

    public static class DeleteEvent extends FormEvent {
        DeleteEvent(StoricoForm source, Storico t) {
            super(source, t);
        }

    }

    public static class CloseEvent extends FormEvent {
        CloseEvent(StoricoForm source) {
            super(source,null);
        }
    }

}
