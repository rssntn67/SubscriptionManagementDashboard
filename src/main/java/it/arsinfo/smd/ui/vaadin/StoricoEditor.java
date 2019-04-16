package it.arsinfo.smd.ui.vaadin;

import java.util.EnumSet;
import java.util.List;

import com.vaadin.data.Binder;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;

import it.arsinfo.smd.data.Cassa;
import it.arsinfo.smd.data.Invio;
import it.arsinfo.smd.data.Omaggio;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.entity.Storico;
import it.arsinfo.smd.repository.StoricoDao;

public class StoricoEditor
        extends SmdEditor<Storico> {

    private final ComboBox<Anagrafica> destinatario = new ComboBox<Anagrafica>("Destinatario");
    private final ComboBox<Pubblicazione> pubblicazione = new ComboBox<Pubblicazione>("Pubblicazioni");
    private final ComboBox<Omaggio> omaggio = new ComboBox<Omaggio>("Omaggio",
                                                                    EnumSet.allOf(Omaggio.class));
    private final ComboBox<Invio> invio = new ComboBox<Invio>("Invio",
                                                              EnumSet.allOf(Invio.class));
    private final TextField numero = new TextField("Numero");
    
    private final CheckBox sospeso = new CheckBox("Sospeso");

    private final ComboBox<Cassa> cassa = new ComboBox<Cassa>("Cassa",EnumSet.allOf(Cassa.class));

    private final Label pagamentoRegolare = new Label();
    
    private final TextField nota = new TextField("Aggiungi Nota");

    public StoricoEditor(
            StoricoDao storicoDao,
            List<Pubblicazione> pubblicazioni, List<Anagrafica> anagrafiche) {

        super(storicoDao, new Binder<>(Storico.class) );
        pubblicazione.setEmptySelectionAllowed(false);
        pubblicazione.setPlaceholder("Pubblicazione");
        pubblicazione.setItems(pubblicazioni);
        pubblicazione.setItemCaptionGenerator(Pubblicazione::getNome);

        destinatario.setEmptySelectionAllowed(false);
        destinatario.setPlaceholder("Destinatario");
        destinatario.setItems(anagrafiche);
        destinatario.setItemCaptionGenerator(Anagrafica::getCaption);

        HorizontalLayout pri = new HorizontalLayout();
        pri.addComponentsAndExpand(destinatario);
        pri.addComponent(pubblicazione);
        pri.addComponent(numero);
        pri.addComponents(cassa,omaggio,invio);

        HorizontalLayout sec = new HorizontalLayout();
        sec.addComponents(sospeso);
        sec.addComponentsAndExpand(nota);
        setComponents(getActions(),
                      pri,
                      sec,
                      pagamentoRegolare
                      );
 
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
        pubblicazione.setReadOnly(persisted);
        if (persisted && obj.getPubblicazione() != null && !obj.getPubblicazione().isActive()) {
            getSave().setEnabled(false);
        } else {
            getSave().setEnabled(true);
        }
        
        numero.focus();
    }
    public TextField getNota() {
        return nota;
    }
    
    public ComboBox<Pubblicazione> getPubblicazione() {
        return pubblicazione;
    }
    public void setPagamentoRegolare(boolean isRegolare) {
        pagamentoRegolare.setContentMode(ContentMode.HTML);
        if (isRegolare)
            pagamentoRegolare.setValue("<b><font color=\"green\">In regola col pagamento</font></b>");
        else
            pagamentoRegolare.setValue("<b><font color=\"red\">Non in regola col pagamento</font></b>");
    }

}
