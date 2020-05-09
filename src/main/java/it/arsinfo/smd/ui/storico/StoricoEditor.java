package it.arsinfo.smd.ui.storico;

import java.util.EnumSet;
import java.util.List;

import com.vaadin.data.Binder;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;

import it.arsinfo.smd.dao.repository.StoricoDao;
import it.arsinfo.smd.data.Cassa;
import it.arsinfo.smd.data.Invio;
import it.arsinfo.smd.data.InvioSpedizione;
import it.arsinfo.smd.data.StatoStorico;
import it.arsinfo.smd.data.TipoEstrattoConto;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.entity.Storico;
import it.arsinfo.smd.ui.vaadin.SmdRepositoryDaoEditor;

public class StoricoEditor
        extends SmdRepositoryDaoEditor<Storico> {

    private final ComboBox<Anagrafica> intestatario = new ComboBox<Anagrafica>("Intestatario");
    private final ComboBox<Anagrafica> destinatario = new ComboBox<Anagrafica>("Destinatario");
    private final ComboBox<Pubblicazione> pubblicazione = new ComboBox<Pubblicazione>("Pubblicazioni");
    private final ComboBox<TipoEstrattoConto> tipoEstrattoConto = new ComboBox<TipoEstrattoConto>("Tipo",
                                                                    EnumSet.allOf(TipoEstrattoConto.class));
    private final ComboBox<Invio> invio = new ComboBox<Invio>("Invio",
                                                              EnumSet.allOf(Invio.class));
    private final ComboBox<InvioSpedizione> invioSpedizione = new ComboBox<InvioSpedizione>("Sped.",
            EnumSet.allOf(InvioSpedizione.class));
    private final TextField numero = new TextField("Numero");
    
    private final ComboBox<Cassa> cassa = new ComboBox<Cassa>("Cassa",EnumSet.allOf(Cassa.class));

    private final ComboBox<StatoStorico> statoStorico = new ComboBox<StatoStorico>("Stato", EnumSet.allOf(StatoStorico.class));
    
    private final TextField nota = new TextField("Aggiungi Nota");

    public StoricoEditor(
            StoricoDao storicoDao,
            List<Pubblicazione> pubblicazioni, 
            List<Anagrafica> anagrafiche) {

        super(storicoDao, new Binder<>(Storico.class) );
        
        intestatario.setEmptySelectionAllowed(false);
        intestatario.setPlaceholder("Intestatario");
        intestatario.setItems(anagrafiche);
        intestatario.setItemCaptionGenerator(Anagrafica::getIntestazione);


        destinatario.setEmptySelectionAllowed(false);
        destinatario.setPlaceholder("Destinatario");
        destinatario.setItems(anagrafiche);
        destinatario.setItemCaptionGenerator(Anagrafica::getIntestazione);

        pubblicazione.setEmptySelectionAllowed(false);
        pubblicazione.setPlaceholder("Pubblicazione");
        pubblicazione.setItems(pubblicazioni);
        pubblicazione.setItemCaptionGenerator(Pubblicazione::getNome);

        cassa.setEmptySelectionAllowed(false);
        tipoEstrattoConto.setEmptySelectionAllowed(false);
        invio.setEmptySelectionAllowed(false);
        invioSpedizione.setEmptySelectionAllowed(false);

        statoStorico.setReadOnly(true);
        
        HorizontalLayout intestatarioHL = new HorizontalLayout();
        intestatarioHL.addComponentsAndExpand(intestatario);
        
        HorizontalLayout destinatarioHL = new HorizontalLayout();
        destinatarioHL.addComponentsAndExpand(destinatario);

        HorizontalLayout tipoECHL = new HorizontalLayout();
        tipoECHL.addComponentsAndExpand(tipoEstrattoConto);

        HorizontalLayout dati1HL = new HorizontalLayout();
        dati1HL.addComponent(pubblicazione);
        dati1HL.addComponent(numero);
        dati1HL.addComponents(statoStorico);
        
        HorizontalLayout dati2HL = new HorizontalLayout();
        dati2HL.addComponents(cassa,invio,invioSpedizione);

        HorizontalLayout noteHL = new HorizontalLayout();
        noteHL.addComponentsAndExpand(nota);

        setComponents(getActions(),intestatarioHL,destinatarioHL,tipoECHL,dati1HL,dati2HL,noteHL);
         
        getBinder()
            .forField(numero)
            .withConverter(new StringToIntegerConverter("Inserire un numero"))
            .withValidator(num -> num >= 0,"deve essere maggiore o uguale a 0")
            .bind(Storico::getNumero, Storico::setNumero);
        getBinder().bindInstanceFields(this);

    }

    @Override
    public void focus(boolean persisted, Storico obj) {
        intestatario.setReadOnly(persisted);
        cassa.setReadOnly(persisted);
        pubblicazione.setReadOnly(persisted);
        destinatario.setReadOnly(persisted);
        invio.setReadOnly(persisted);
        invioSpedizione.setReadOnly(persisted);
        
        if (persisted && obj.getPubblicazione() != null && !obj.getPubblicazione().isActive()) {
            getSave().setEnabled(false);
        } else {
            getSave().setEnabled(true);
        }
        getDelete().setEnabled(persisted && obj.getStatoStorico() == StatoStorico.Nuovo);
        
        numero.focus();
    }
    public TextField getNota() {
        return nota;
    }
    
    public ComboBox<Pubblicazione> getPubblicazione() {
        return pubblicazione;
    }

    public ComboBox<Anagrafica> getDestinatario() {
        return destinatario;
    }

    public ComboBox<Anagrafica> getIntestatario() {
        return intestatario;
    }
}
