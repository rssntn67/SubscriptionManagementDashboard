package it.arsinfo.smd.ui.vaadin;

import java.util.EnumSet;
import java.util.List;

import com.vaadin.data.Binder;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;

import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.Mese;
import it.arsinfo.smd.entity.Operazione;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.repository.OperazioneDao;

public class OperazioneEditor
        extends SmdEditor<Operazione> {

    private final ComboBox<Pubblicazione> pubblicazione = new ComboBox<Pubblicazione>("Pubblicazioni");
    private final TextField stimato = new TextField("Stimato");
    private final TextField definitivo = new TextField("Definitivo");
    private final ComboBox<Anno> anno = new ComboBox<Anno>("Anno",
            EnumSet.allOf(Anno.class));
    private final ComboBox<Mese> mese = new ComboBox<Mese>("Mese",
            EnumSet.allOf(Mese.class));
    public OperazioneEditor(
            OperazioneDao operazioneDao,
            List<Pubblicazione> pubblicazioni) {

        super(operazioneDao, new Binder<>(Operazione.class) );
        
        setComponents(getActions(), 
                      new HorizontalLayout(pubblicazione, anno, mese,
                                                         stimato),
                                    definitivo);

        pubblicazione.setEmptySelectionAllowed(false);
        pubblicazione.setPlaceholder("Pubblicazione");
        pubblicazione.setItems(pubblicazioni);
        pubblicazione.setItemCaptionGenerator(Pubblicazione::getCaption);
        pubblicazione.setReadOnly(true);
        
        anno.setItemCaptionGenerator(Anno::getAnnoAsString);
        anno.setEmptySelectionAllowed(false);
        anno.setReadOnly(true);
        
        mese.setItemCaptionGenerator(Mese::getNomeBreve);
        mese.setEmptySelectionAllowed(false);
        mese.setReadOnly(true);
        
        stimato.setReadOnly(true);
        
        getBinder()
            .forField(definitivo)
            .withValidator(str -> str != null, "Inserire un numero")
            .withConverter(new StringToIntegerConverter(""))
            .withValidator(num -> num > 0,"deve essere maggiore di 0")
            .bind(Operazione::getDefinitivo, Operazione::setDefinitivo);
        getBinder()
        .forField(stimato)
        .withConverter(new StringToIntegerConverter(""))
        .bind(Operazione::getStimato, Operazione::setStimato);

        getBinder().bindInstanceFields(this);

    }

    @Override
    public void focus(boolean persisted, Operazione op) {
        getSave().setEnabled(!op.chiuso());
        getCancel().setEnabled(!op.chiuso());
        definitivo.focus();        
    }

}
