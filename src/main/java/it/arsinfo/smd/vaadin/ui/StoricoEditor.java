package it.arsinfo.smd.vaadin.ui;

import java.util.EnumSet;
import java.util.List;

import com.vaadin.data.Binder;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;

import it.arsinfo.smd.data.Cassa;
import it.arsinfo.smd.data.Invio;
import it.arsinfo.smd.data.Omaggio;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.entity.Storico;
import it.arsinfo.smd.repository.StoricoDao;
import it.arsinfo.smd.vaadin.model.SmdEditor;

public class StoricoEditor
        extends SmdEditor<Storico> {

    private final ComboBox<Anagrafica> intestatario = new ComboBox<Anagrafica>("Intestatario");
    private final ComboBox<Anagrafica> destinatario = new ComboBox<Anagrafica>("Destinatario");
    private final ComboBox<Pubblicazione> pubblicazione = new ComboBox<Pubblicazione>("Pubblicazioni");
    private final ComboBox<Omaggio> omaggio = new ComboBox<Omaggio>("Omaggio",
                                                                    EnumSet.allOf(Omaggio.class));
    private final ComboBox<Invio> invio = new ComboBox<Invio>("Invio",
                                                              EnumSet.allOf(Invio.class));
    private final TextField numero = new TextField("Numero");
    
    private final CheckBox sospeso = new CheckBox("Sospeso");

    private final ComboBox<Cassa> cassa = new ComboBox<Cassa>("Cassa",EnumSet.allOf(Cassa.class));

    private final Panel pagamentoRegolare = new Panel();
    
    public StoricoEditor(
            StoricoDao storicoDao,
            List<Pubblicazione> pubblicazioni, List<Anagrafica> anagrafiche) {

        super(storicoDao, new Binder<>(Storico.class) );
        pubblicazione.setEmptySelectionAllowed(false);
        pubblicazione.setPlaceholder("Pubblicazione");
        pubblicazione.setItems(pubblicazioni);
        pubblicazione.setItemCaptionGenerator(Pubblicazione::getCaption);

        intestatario.setEmptySelectionAllowed(false);
        intestatario.setPlaceholder("Intestatario");
        intestatario.setItems(anagrafiche);
        intestatario.setItemCaptionGenerator(Anagrafica::getCaption);

        destinatario.setEmptySelectionAllowed(false);
        destinatario.setPlaceholder("Destinatario");
        destinatario.setItems(anagrafiche);
        destinatario.setItemCaptionGenerator(Anagrafica::getCaption);

        setComponents(getActions(),
                      new HorizontalLayout(numero, intestatario, destinatario,
                                           pubblicazione),
                      new HorizontalLayout(cassa,omaggio, invio),
                      new HorizontalLayout(pagamentoRegolare,sospeso));
 
        getBinder()
            .forField(numero)
            .withValidator(str -> str != null, "Inserire un numero")
            .withConverter(new StringToIntegerConverter(""))
            .withValidator(num -> num > 0,"deve essere maggiore di 0")
            .bind(Storico::getNumero, Storico::setNumero);
        getBinder().bindInstanceFields(this);

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

    public ComboBox<Pubblicazione> getPubblicazione() {
        return pubblicazione;
    }
    
    public void setPagamentoRegolare(boolean isRegolare) {
        if (isRegolare) {
            pagamentoRegolare.setContent(new Label("<b>Pagamenti Regolari</b>",ContentMode.HTML));
        } else {
            pagamentoRegolare.setContent(new Label("<b>Pagamenti Non Regolari</b>",ContentMode.HTML));            
        }
    }

}
