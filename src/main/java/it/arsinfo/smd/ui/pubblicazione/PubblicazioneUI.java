package it.arsinfo.smd.ui.pubblicazione;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;

import it.arsinfo.smd.dao.PubblicazioneServiceDao;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.ui.SmdAbstractUI;
import it.arsinfo.smd.ui.SmdUI;

@SpringUI(path = SmdUI.URL_PUBBLICAZIONI)
@Title("Pubblicazioni ADP")
public class PubblicazioneUI extends SmdAbstractUI<Pubblicazione> {

    /**
     * 
     */
    private static final long serialVersionUID = 7884064928998716106L;

    @Autowired
    PubblicazioneServiceDao pubblicazionedao;

    @Override
    protected void init(VaadinRequest request) {
        PubblicazioneAdd add = new PubblicazioneAdd("Aggiungi Pubblicazione");
        PubblicazioneSearch search = new PubblicazioneSearch(pubblicazionedao.getRepository());
        PubblicazioneGrid grid = new PubblicazioneGrid("Pubblicazioni");
        PubblicazioneEditor editor = new PubblicazioneEditor(pubblicazionedao);
                        
        init(request,add, search,editor, grid,"Pubblicazioni");
    }

}
