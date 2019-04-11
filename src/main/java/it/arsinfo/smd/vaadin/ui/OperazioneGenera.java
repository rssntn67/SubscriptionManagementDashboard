package it.arsinfo.smd.vaadin.ui;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;

import it.arsinfo.smd.Smd;
import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.Mese;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.repository.AbbonamentoDao;
import it.arsinfo.smd.repository.OperazioneDao;
import it.arsinfo.smd.vaadin.model.SmdBox;
import it.arsinfo.smd.vaadin.model.SmdChangeHandler;

public class OperazioneGenera extends SmdChangeHandler {

    private final Button genera;
    private final PubblicazioneBox pBox;
    private final SmdBox<Anno> aBox;
    private final SmdBox<Mese> mBox;
    public OperazioneGenera(String caption, VaadinIcons icon,OperazioneDao operazioneDao, AbbonamentoDao abbonamentoDao, List<Pubblicazione> pubblicazioni) {
        pBox = new PubblicazioneBox(pubblicazioni);
        final Anno annoCorrente = Smd.getAnnoCorrente();
        List<Anno> anni = 
                EnumSet.allOf(Anno.class)
                .stream()
                .filter(anno -> anno.getAnno() >= annoCorrente.getAnno())
                .collect(Collectors.toList());
         aBox = new SmdBox<Anno>(anni) {
            
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
            aBox.getSelected().stream().forEach(anno -> {
                Set<Mese> mesi = mBox.getSelected().stream()
                        .collect(Collectors.toSet());
                if (anno.getAnno() == annoCorrente.getAnno()) {
                    Mese meseCorrente = Smd.getMeseCorrente();
                    mesi = mesi.stream()
                            .filter(m -> m.getPosizione() > meseCorrente.getPosizione())
                            .collect(Collectors.toSet());
                }
                mesi.stream().forEach(mese -> {
                    pubblicazioni.stream().forEach(p -> {
                        operazioneDao.deleteByAnnoAndMeseAndPubblicazione(anno, mese, p);
                    });
                });
                operazioneDao.saveAll(Smd.generaOperazioni(pBox.getSelected(), abbonamentoDao.findByAnno(anno), anno, mesi));
                onChange();
            });
        });
        
        setComponents(genera,pBox.getLayout(),aBox.getLayout(),mBox.getLayout());
    }

    public void edit() {
        genera.setVisible(true);
        pBox.edit(false);
        aBox.edit(false);
        mBox.edit(false);
    }
    
}
