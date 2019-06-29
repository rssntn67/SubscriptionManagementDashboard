package it.arsinfo.smd.ui.vaadin;

import java.util.EnumSet;
import java.util.List;

import com.vaadin.data.Binder;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;

import it.arsinfo.smd.Smd;
import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.Mese;
import it.arsinfo.smd.entity.Operazione;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.repository.OperazioneDao;

public class OperazioneEditor
        extends SmdEditor<Operazione> {

    private final ComboBox<Pubblicazione> pubblicazione = new ComboBox<Pubblicazione>("Pubblicazioni");
    private final ComboBox<Anno> annoPubblicazione = new ComboBox<Anno>("Anno Pubbl.",
            EnumSet.allOf(Anno.class));
    private final ComboBox<Mese> mesePubblicazione = new ComboBox<Mese>("Mese Pubbl.",
            EnumSet.allOf(Mese.class));
    private final TextField stimatoSped = new TextField("Stimato Spedizioniere");
    private final TextField stimatoSede = new TextField("Stimato Adp Sede");
    private final TextField definitivoSped = new TextField("Definitivo Spedizioniere");
    private final TextField definitivoSede = new TextField("Definitivo Adp Sede");
    private final ComboBox<Anno> anno = new ComboBox<Anno>("Anno",
            EnumSet.allOf(Anno.class));
    private final ComboBox<Mese> mese = new ComboBox<Mese>("Mese",
            EnumSet.allOf(Mese.class));
    public OperazioneEditor(
            OperazioneDao operazioneDao,
            List<Pubblicazione> pubblicazioni) {

        super(operazioneDao, new Binder<>(Operazione.class) );
        
        setComponents(getActions(), 
              new HorizontalLayout(mese, anno),
              new HorizontalLayout(pubblicazione, mesePubblicazione, annoPubblicazione),
              new HorizontalLayout(stimatoSped, stimatoSede,definitivoSped,definitivoSede));

        pubblicazione.setEmptySelectionAllowed(false);
        pubblicazione.setPlaceholder("Pubblicazione");
        pubblicazione.setItems(pubblicazioni);
        pubblicazione.setItemCaptionGenerator(Pubblicazione::getCaption);
        pubblicazione.setReadOnly(true);

        annoPubblicazione.setItemCaptionGenerator(Anno::getAnnoAsString);
        annoPubblicazione.setEmptySelectionAllowed(false);
        annoPubblicazione.setReadOnly(true);
        
        mesePubblicazione.setItemCaptionGenerator(Mese::getNomeBreve);
        mesePubblicazione.setEmptySelectionAllowed(false);
        mesePubblicazione.setReadOnly(true);

        anno.setItemCaptionGenerator(Anno::getAnnoAsString);
        anno.setEmptySelectionAllowed(false);
        anno.setReadOnly(true);
        
        mese.setItemCaptionGenerator(Mese::getNomeBreve);
        mese.setEmptySelectionAllowed(false);
        mese.setReadOnly(true);
        
        stimatoSped.setReadOnly(true);
        stimatoSede.setReadOnly(true);
        
        getBinder()
            .forField(definitivoSped)
            .withValidator(str -> str != null, "Inserire un numero")
            .withConverter(new StringToIntegerConverter(""))
            .withValidator(num -> num > 0,"deve essere maggiore di 0")
            .bind(Operazione::getDefinitivoSped, Operazione::setDefinitivoSped);
        
        getBinder()
        .forField(stimatoSped)
        .withConverter(new StringToIntegerConverter(""))
        .bind(Operazione::getStimatoSped, Operazione::setStimatoSped);

        getBinder()
        .forField(definitivoSede)
        .withValidator(str -> str != null, "Inserire un numero")
        .withConverter(new StringToIntegerConverter(""))
        .withValidator(num -> num > 0,"deve essere maggiore di 0")
        .bind(Operazione::getDefinitivoSede, Operazione::setDefinitivoSede);
    
        getBinder()
    .forField(stimatoSede)
    .withConverter(new StringToIntegerConverter(""))
    .bind(Operazione::getStimatoSede, Operazione::setStimatoSede);

    getBinder().bindInstanceFields(this);

    }

    @Override
    public void focus(boolean persisted, Operazione op) {
        boolean chiuso = (op.getAnno().getAnno() < Smd.getAnnoCorrente().getAnno()
                || (op.getAnno() == Smd.getAnnoCorrente() && op.getMese().getPosizione() < Smd.getMeseCorrente().getPosizione()));
        definitivoSped.setReadOnly(chiuso);
        definitivoSede.setReadOnly(chiuso);
        getSave().setEnabled(!chiuso);
        getCancel().setEnabled(!chiuso);
        definitivoSped.focus();        
    }

}
