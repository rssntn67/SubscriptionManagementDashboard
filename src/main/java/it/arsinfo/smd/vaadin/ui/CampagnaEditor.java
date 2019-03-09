package it.arsinfo.smd.vaadin.ui;

import java.util.EnumSet;

import com.vaadin.data.Binder;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;

import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.Mese;
import it.arsinfo.smd.entity.Campagna;
import it.arsinfo.smd.repository.AnagraficaDao;
import it.arsinfo.smd.repository.CampagnaDao;
import it.arsinfo.smd.repository.PubblicazioneDao;
import it.arsinfo.smd.vaadin.model.SmdEditor;

public class CampagnaEditor extends SmdEditor<Campagna> {

    /**
     * 
     */
    private static final long serialVersionUID = 4673834235533544936L;

    private final CheckBox rinnovaSoloAbbonatiInRegola = new CheckBox("Selezionare per rinnovo Abbonati in Regola");

    private final ComboBox<Anno> anno = new ComboBox<Anno>("Selezionare Anno",
                                                           EnumSet.allOf(Anno.class));
    private final ComboBox<Mese> inizio = new ComboBox<Mese>("Selezionare Inizio",
                                                             EnumSet.allOf(Mese.class));
    private final ComboBox<Mese> fine = new ComboBox<Mese>("Selezionare Fine",
                                                           EnumSet.allOf(Mese.class));

    HorizontalLayout pri = new HorizontalLayout(anno, inizio, fine);
    HorizontalLayout pag = new HorizontalLayout(rinnovaSoloAbbonatiInRegola);

    public CampagnaEditor(CampagnaDao repo, AnagraficaDao anadao,
            PubblicazioneDao pubdao) {

        super(repo, new Binder<>(Campagna.class));

        addComponents(pri, pag, getActions());
        setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);

        anno.setItemCaptionGenerator(Anno::getAnnoAsString);

        inizio.setItemCaptionGenerator(Mese::getNomeBreve);
        fine.setItemCaptionGenerator(Mese::getNomeBreve);

        getBinder().bindInstanceFields(this);
        // Configure and style components
        setSpacing(true);

        setVisible(false);

    }

    @Override
    public void focus(boolean read, Campagna obj) {

        anno.setReadOnly(read);
        inizio.setReadOnly(read);
        fine.setReadOnly(read);

        getSave().setEnabled(!read);
        getCancel().setEnabled(!read);
        anno.focus();

    }

}
