package it.arsinfo.smd.ui.vaadin;

import java.util.EnumSet;
import java.util.List;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;

import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.Mese;
import it.arsinfo.smd.data.Omaggio;
import it.arsinfo.smd.entity.Prospetto;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.repository.ProspettoDao;

public class ProspettoSearch extends SmdSearch<Prospetto> {

    private Pubblicazione p;
    private Omaggio omaggio;
    private Anno anno;
    private Mese mese;

    public ProspettoSearch(ProspettoDao prospettoDao,
            List<Pubblicazione> pubblicazioni) {
        super(prospettoDao);
        ComboBox<Pubblicazione> filterP = new ComboBox<Pubblicazione>();
        ComboBox<Anno> filterAnno = new ComboBox<Anno>();
        ComboBox<Mese> filterMese = new ComboBox<Mese>();
        ComboBox<Omaggio> filterOmaggio = new ComboBox<Omaggio>();

        setComponents(new HorizontalLayout(filterAnno, filterMese, filterP,
                                           filterOmaggio));

        filterP.setEmptySelectionAllowed(true);
        filterP.setPlaceholder("Cerca per Pubblicazione");
        filterP.setItems(pubblicazioni);
        filterP.setItemCaptionGenerator(Pubblicazione::getNome);
        filterP.addSelectionListener(e -> {
            if (e.getValue() == null) {
                p = null;
            } else {
                p = e.getSelectedItem().get();
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
                anno = e.getSelectedItem().get();
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
                mese = e.getSelectedItem().get();
            }
            onChange();
        });

        filterOmaggio.setEmptySelectionAllowed(true);
        filterOmaggio.setPlaceholder("Cerca per Omaggio");
        filterOmaggio.setItems(EnumSet.allOf(Omaggio.class));
        filterOmaggio.addSelectionListener(e -> {
            if (e.getValue() == null) {
                omaggio = null;
            } else {
                omaggio = e.getSelectedItem().get();
            }
            onChange();
        });

    }

    @Override
    public List<Prospetto> find() {
        if (p == null && anno == null && mese == null && omaggio == null) {
            return findAll();
        }

        if (anno == null && mese == null && omaggio == null) {
            return ((ProspettoDao) getRepo()).findByPubblicazione(p);
        }
        if (p == null && mese == null && omaggio == null) {
            return ((ProspettoDao) getRepo()).findByAnno(anno);
        }
        if (p == null && anno == null && omaggio == null) {
            return ((ProspettoDao) getRepo()).findByMese(mese);
        }
        if (anno == null && mese == null && p == null) {
            return ((ProspettoDao) getRepo()).findByOmaggio(omaggio);
        }

        if (p == null && omaggio == null) {
            return ((ProspettoDao) getRepo()).findByAnnoAndMese(anno, mese);
        }
        if (mese == null && omaggio == null) {
            return ((ProspettoDao) getRepo()).findByAnnoAndPubblicazione(anno,
                                                                         p);
        }
        if (anno == null && omaggio == null) {
            return ((ProspettoDao) getRepo()).findByMeseAndPubblicazione(mese,
                                                                         p);
        }
        if (p == null && mese == null) {
            return ((ProspettoDao) getRepo()).findByAnnoAndOmaggio(anno,
                                                                   omaggio);
        }
        if (anno == null && p == null) {
            return ((ProspettoDao) getRepo()).findByMeseAndOmaggio(mese,
                                                                   omaggio);
        }
        if (anno == null && mese == null) {
            return ((ProspettoDao) getRepo()).findByPubblicazioneAndOmaggio(p,
                                                                            omaggio);
        }
        
        if (anno == null) {
            return ((ProspettoDao) getRepo()).findByMeseAndPubblicazioneAndOmaggio(mese,p,
                                                                            omaggio);
        }
        if (mese == null) {
            return ((ProspettoDao) getRepo()).findByAnnoAndPubblicazioneAndOmaggio(anno,p,
                                                                            omaggio);
        }
        if (p == null) {
            return ((ProspettoDao) getRepo()).findByAnnoAndMeseAndOmaggio(anno,mese,
                                                                            omaggio);
        }
        if (omaggio == null) {
            return ((ProspettoDao) getRepo()).findByAnnoAndMeseAndPubblicazione(anno,mese,
                                                                            p);
        }
        return ((ProspettoDao) getRepo()).findByAnnoAndMeseAndPubblicazioneAndOmaggio(anno,
                                                                                      mese,
                                                                                      p,
                                                                                      omaggio);
    
    }

}
