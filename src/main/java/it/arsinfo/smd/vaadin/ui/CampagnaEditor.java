package it.arsinfo.smd.vaadin.ui;

import java.util.EnumSet;

import com.vaadin.data.Binder;
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

        setComponents(pri, pag, getActions());

        anno.setItemCaptionGenerator(Anno::getAnnoAsString);

        inizio.setItemCaptionGenerator(Mese::getNomeBreve);
        fine.setItemCaptionGenerator(Mese::getNomeBreve);

        getBinder().bindInstanceFields(this);

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
