package it.arsinfo.smd.ui.vaadin;

import java.util.EnumSet;
import java.util.List;

import com.vaadin.data.Binder;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

import it.arsinfo.smd.Smd;
import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.Cassa;
import it.arsinfo.smd.data.Invio;
import it.arsinfo.smd.data.InvioSpedizione;
import it.arsinfo.smd.data.StatoStorico;
import it.arsinfo.smd.data.TipoEstrattoConto;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.EstrattoConto;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.entity.Spedizione;
import it.arsinfo.smd.entity.SpesaSpedizione;
import it.arsinfo.smd.entity.Storico;
import it.arsinfo.smd.repository.AbbonamentoDao;
import it.arsinfo.smd.repository.EstrattoContoDao;
import it.arsinfo.smd.repository.SpedizioneDao;
import it.arsinfo.smd.repository.StoricoDao;

public class StoricoEditor
        extends SmdEditor<Storico> {

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
            AbbonamentoDao abbonamentoDao,
            EstrattoContoDao estrattoContoDao,
            SpedizioneDao spedizioneDao,
            List<Pubblicazione> pubblicazioni, 
            List<Anagrafica> anagrafiche,
            List<SpesaSpedizione> spese) {

        super(storicoDao, new Binder<>(Storico.class) );
        SmdButton update = new SmdButton("Salva ed Aggiorna Campagna", VaadinIcons.ARCHIVES);
        update.getButton().addStyleName(ValoTheme.BUTTON_PRIMARY);
        
        intestatario.setEmptySelectionAllowed(false);
        intestatario.setPlaceholder("Intestatario");
        intestatario.setItems(anagrafiche);
        intestatario.setItemCaptionGenerator(Anagrafica::getCaption);


        destinatario.setEmptySelectionAllowed(false);
        destinatario.setPlaceholder("Destinatario");
        destinatario.setItems(anagrafiche);
        destinatario.setItemCaptionGenerator(Anagrafica::getCaption);

        pubblicazione.setEmptySelectionAllowed(false);
        pubblicazione.setPlaceholder("Pubblicazione");
        pubblicazione.setItems(pubblicazioni);
        pubblicazione.setItemCaptionGenerator(Pubblicazione::getNome);

        cassa.setEmptySelectionAllowed(false);
        tipoEstrattoConto.setEmptySelectionAllowed(false);
        invio.setEmptySelectionAllowed(false);
        invioSpedizione.setEmptySelectionAllowed(false);
        statoStorico.setItemCaptionGenerator(StatoStorico::getDescr);

        HorizontalLayout pri = new HorizontalLayout();
        pri.addComponentsAndExpand(intestatario);
        pri.addComponentsAndExpand(destinatario);
        pri.addComponent(pubblicazione);
        pri.addComponent(numero);
        pri.addComponents(statoStorico);
        
        HorizontalLayout hhh = new HorizontalLayout();
        hhh.addComponentsAndExpand(tipoEstrattoConto);
        hhh.addComponents(cassa,invio,invioSpedizione);

        HorizontalLayout sec = new HorizontalLayout();
        sec.addComponentsAndExpand(nota);
        setComponents(getActions(),update.getButton(),pri,hhh,sec);
        
        //FIXME a lot of use cases
        update.setChangeHandler(() -> {
            List<EstrattoConto> ecs = estrattoContoDao.findByStorico(get());
            ecs.stream().filter(ec -> ec.getAbbonamento() != null && ec.getAbbonamento().getAnno() == Anno.getAnnoProssimo()).forEach( ec ->{
                ec.setNumero(get().getNumero());
                ec.setTipoEstrattoConto(get().getTipoEstrattoConto());
                ec.setPubblicazione(get().getPubblicazione());
                Abbonamento abb = ec.getAbbonamento();
                List<Spedizione> spedizioni = spedizioneDao.findByAbbonamento(abb);
                Smd.aggiornaEC(abb, ec,spedizioni,spese);
                abbonamentoDao.save(abb);
                estrattoContoDao.save(ec);
            });
            save();
 
        });
 
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
        statoStorico.setReadOnly(!persisted);
        pubblicazione.setReadOnly(persisted);
        if (persisted && obj.getPubblicazione() != null && !obj.getPubblicazione().isActive()) {
            getSave().setEnabled(false);
        } else {
            getSave().setEnabled(true);
        }
        getDelete().setEnabled(false);
        
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
