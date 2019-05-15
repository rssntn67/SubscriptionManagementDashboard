package it.arsinfo.smd.ui.vaadin;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;

import it.arsinfo.smd.Smd;
import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.Mese;
import it.arsinfo.smd.data.Omaggio;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.repository.AbbonamentoDao;
import it.arsinfo.smd.repository.ProspettoDao;

public class ProspettoGenera extends SmdChangeHandler {

    private final HorizontalLayout buttons = new HorizontalLayout();
    private final Button genera;
    private final Button indietro;
    private final PubblicazioneBox pBox;
    private final SmdBox<Anno> aBox;
    private final SmdBox<Mese> mBox;
    private final SmdBox<Omaggio> oBox;
    public ProspettoGenera(String caption, VaadinIcons icon,final ProspettoDao prospettoDao, AbbonamentoDao abbonamentoDao, List<Pubblicazione> pubblicazioni) {
        pBox = new PubblicazioneBox(pubblicazioni);
        aBox = new SmdBox<Anno>(EnumSet.allOf(Anno.class)) {
            
            @Override
            public boolean getReadOnly(Anno t, boolean persisted) {
                return false;
            }
            
            @Override
            public String getBoxCaption(Anno t) {
                return t.getAnnoAsString();
            }
        };
        mBox = new SmdBox<Mese>(EnumSet.allOf(Mese.class)) {
            
            @Override
            public boolean getReadOnly(Mese t, boolean persisted) {
                return false;
            }
            
            @Override
            public String getBoxCaption(Mese t) {
                return t.getNomeBreve();
            }
        };

        oBox = new SmdBox<Omaggio>(EnumSet.allOf(Omaggio.class)) {
            
            @Override
            public boolean getReadOnly(Omaggio t, boolean persisted) {
                return false;
            }
            
            @Override
            public String getBoxCaption(Omaggio t) {
                return t.name();
            }
        };
        indietro = new Button("indietro");
        indietro.addClickListener(click -> onChange());
        genera = new Button(caption,icon);
        genera.addClickListener(click -> {
            if (pBox.getSelected().isEmpty()) {
                Notification.show("Selezionare almeno una Pubblicazione", Notification.Type.WARNING_MESSAGE);
                return;
            }
            if (aBox.getSelected().isEmpty()) {
                Notification.show("Selezionare almeno un Anno", Notification.Type.WARNING_MESSAGE);
                return;
            }
            if (mBox.getSelected().isEmpty()) {
                Notification.show("Selezionare almeno un Mese", Notification.Type.WARNING_MESSAGE);
                return;
            }
            if (oBox.getSelected().isEmpty()) {
                Notification.show("Selezionare almeno un Omaggio", Notification.Type.WARNING_MESSAGE);
                return;
            }
            Set<Mese> mesi = mBox.getSelected().stream()
                    .collect(Collectors.toSet());
            Set<Omaggio> omaggi = oBox.getSelected().stream()
                    .collect(Collectors.toSet());
            
            aBox.getSelected().stream().forEach(a -> {
                omaggi.stream().forEach(o -> {
                    mesi.stream().forEach(m -> {
                        pubblicazioni.stream().forEach(p -> {
                            prospettoDao.findByAnnoAndMeseAndPubblicazioneAndOmaggio(a, m, p,o).forEach(prosp -> prospettoDao.delete(prosp));
                        });
                    });
                });
                Smd.generaProspetti(pBox.getSelected(), abbonamentoDao.findByAnno(a), a, mesi,omaggi).forEach(prosp -> prospettoDao.save(prosp));
                onChange();
            });
        });
        buttons.addComponent(genera);
        buttons.addComponent(indietro);
        setComponents(buttons,pBox.getLayout(),aBox.getLayout(),mBox.getLayout(),oBox.getLayout());
    }

    public void edit() {
        buttons.setVisible(true);
        genera.setVisible(true);
        indietro.setVisible(true);
        pBox.edit(false);
        aBox.edit(false);
        mBox.edit(false);
        oBox.edit(false);
    }
    
}
