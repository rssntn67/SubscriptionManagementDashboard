package it.arsinfo.smd.vaadin.ui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;

import it.arsinfo.smd.repository.AnagraficaDao;
import it.arsinfo.smd.repository.StoricoDao;
import it.arsinfo.smd.repository.PubblicazioneDao;
import it.arsinfo.smd.vaadin.model.SmdUI;
import it.arsinfo.smd.vaadin.model.SmdUIHelper;

@SpringUI(path = SmdUIHelper.URL_ANAGRAFICA)
@Title("Anagrafica Clienti ADP")
public class AnagraficaUI extends SmdUI {

    /**
     * 
     */
    private static final long serialVersionUID = 7884064928998716106L;

    @Autowired
    AnagraficaDao anagraficaDao;
    @Autowired
    PubblicazioneDao pubblicazioneDao;
    @Autowired
    StoricoDao anagraficaPubblicazioneDao;

    @Override
    protected void init(VaadinRequest request) {
        super.init(request, "Anagrafica");
        Assert.notNull(anagraficaDao, "anagraficaDao must be not null");
        Assert.notNull(anagraficaPubblicazioneDao,
                       "anagraficaPubblicazioneDao must be not null");
        AnagraficaSearch search = new AnagraficaSearch(anagraficaDao);
        AnagraficaEditor editor = new AnagraficaEditor(anagraficaDao);
        StoricoSubSearch storicoSubSearch = new StoricoSubSearch(anagraficaPubblicazioneDao);
        StoricoEditor storicoEditor = new StoricoEditor(anagraficaPubblicazioneDao,
                                                                                    pubblicazioneDao,
                                                                                    anagraficaDao);
        addComponents(storicoEditor, editor, storicoSubSearch, search);
        
        editor.setWidth("100%");
        search.setWidth("120%");
        storicoEditor.setWidth("100%");
        storicoSubSearch.setWidth("120%");

        editor.setVisible(false);
        storicoSubSearch.setVisible(false);
        storicoEditor.setVisible(false);

        search.setChangeHandler(() -> {
            if (search.getSelected() == null) {
                return;
            }
            setHeader(String.format("Anagrafica:Edit:%s", search.getSelected().getCaption()));
            hideMenu();
            editor.edit(search.getSelected());
            storicoSubSearch.setKey(search.getSelected());
            storicoSubSearch.onSearch();
        });


        editor.setChangeHandler(() -> {
            search.onSearch();
            editor.setVisible(false);
            storicoSubSearch.setVisible(false);
            showMenu();
            setHeader("Anagrafica");
        });

        storicoSubSearch.setChangeHandler(() -> {
            if (storicoSubSearch.getSelected() == null) {
                return;
            }
            storicoEditor.edit(storicoSubSearch.getSelected());
            editor.setVisible(false);
            setHeader(String.format("Anagrafica:Pubblicazione:Edit:%s-%s",
                                    storicoSubSearch.getSelected().getCaptionIntestatario(),
                                    storicoSubSearch.getSelected().getCaptionPubblicazione()));
        });

        storicoEditor.setChangeHandler(() -> {
            storicoSubSearch.onSearch();
            setHeader(String.format("Anagrafica:Edit:%s", search.getSelected().getCaption()));
            editor.setVisible(true);
            storicoEditor.setVisible(false);
        });


    }

}
