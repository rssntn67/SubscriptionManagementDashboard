package it.arsinfo.smd.ui.vaadin;

import java.util.EnumSet;
import java.util.List;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;

import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.Mese;
import it.arsinfo.smd.entity.Operazione;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.repository.OperazioneDao;

public class OperazioneSearch extends SmdSearch<Operazione> {

    private Pubblicazione p;
    private Anno anno;
    private Mese mese;

    public OperazioneSearch(OperazioneDao operazioneDao, List<Pubblicazione> pubblicazioni) {
        super(operazioneDao);
        ComboBox<Pubblicazione> filterP = new ComboBox<Pubblicazione>();
        ComboBox<Anno> filterAnno = new ComboBox<Anno>();
        ComboBox<Mese> filterMese = new ComboBox<Mese>();

        setComponents(new HorizontalLayout(filterAnno,filterMese,filterP));

        filterP.setEmptySelectionAllowed(true);
        filterP.setPlaceholder("Cerca per Pubblicazione");
        filterP.setItems(pubblicazioni);
        filterP.setItemCaptionGenerator(Pubblicazione::getNome);
        filterP.addSelectionListener(e -> {
            if (e.getValue() == null) {
                p = null;
            } else {
                p=e.getSelectedItem().get();
            }
            onChange();
        });

        filterAnno.setEmptySelectionAllowed(true);
        filterAnno.setPlaceholder("Cerca per Anno");
        filterAnno.setItems(EnumSet.allOf(Anno.class));
        filterAnno.setItemCaptionGenerator(Anno::getAnnoAsString);
        filterAnno.addSelectionListener(e -> {
            if (e.getValue() == null) {
                anno = null;
            } else {
                anno=e.getSelectedItem().get();
            }
            onChange();
        });

        filterMese.setEmptySelectionAllowed(true);
        filterMese.setPlaceholder("Cerca per Mese");
        filterMese.setItems(EnumSet.allOf(Mese.class));
        filterMese.setItemCaptionGenerator(Mese::getNomeBreve);
        filterMese.addSelectionListener(e -> {
            if (e.getValue() == null) {
                mese = null;
            } else {
                mese=e.getSelectedItem().get();
            }
            onChange();
        });

        
    }
    
    @Override
    public List<Operazione> find() {
        if (p == null && anno == null && mese == null) {
            return findAll();
        }
        if (anno == null && mese == null) {
            return ((OperazioneDao)getRepo()).findByPubblicazione(p);
        }
        if (p == null && mese == null) {
            return ((OperazioneDao)getRepo()).findByAnno(anno);
        }
        if (p == null && anno == null) {
            return ((OperazioneDao)getRepo()).findByMese(mese);
        }
        if (p == null ) {
            return ((OperazioneDao)getRepo()).findByAnnoAndMese(anno, mese);
        }
        if (mese == null ) {
            return ((OperazioneDao)getRepo()).findByAnnoAndPubblicazione(anno, p);
        }
        if (anno == null ) {
            return ((OperazioneDao)getRepo()).findByMeseAndPubblicazione(mese, p);
        }
        
        return ((OperazioneDao)getRepo()).findByAnnoAndMeseAndPubblicazione(anno, mese, p);
    
    }

}
