package it.arsinfo.smd.vaadin.ui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;

import it.arsinfo.smd.repository.AnagraficaDao;
import it.arsinfo.smd.repository.AnagraficaPubblicazioneDao;
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
    AnagraficaPubblicazioneDao anagraficaPubblicazioneDao;

    @Override
    protected void init(VaadinRequest request) {
        super.init(request, "Anagrafica");
        Assert.notNull(anagraficaDao, "anagraficaDao must be not null");
        Assert.notNull(anagraficaPubblicazioneDao,
                       "anagraficaPubblicazioneDao must be not null");
        AnagraficaSearch search = new AnagraficaSearch(anagraficaDao);
        AnagraficaEditor editor = new AnagraficaEditor(anagraficaDao);
        AnagraficaPubblicazioneSubSearch apSubSearch = new AnagraficaPubblicazioneSubSearch(anagraficaPubblicazioneDao);
        AnagraficaPubblicazioneEditor apEditor = new AnagraficaPubblicazioneEditor(anagraficaPubblicazioneDao,
                                                                                    pubblicazioneDao,
                                                                                    anagraficaDao);
        addComponents(apEditor, editor, apSubSearch, search);

        editor.setWidth("100%");
        search.setWidth("120%");
        apEditor.setWidth("100%");
        apSubSearch.setWidth("120%");

        editor.setVisible(false);
        apSubSearch.setVisible(false);
        apEditor.setVisible(false);

        search.setChangeHandler(() -> {
            if (search.getSelected() == null) {
                return;
            }
            setHeader(String.format("Anagrafica:Edit:%s", search.getSelected().getCaption()));
            hideMenu();
            editor.edit(search.getSelected());
            apSubSearch.setKey(search.getSelected());
            apSubSearch.onSearch();
        });


        editor.setChangeHandler(() -> {
            search.onSearch();
            editor.setVisible(false);
            apSubSearch.setVisible(false);
            showMenu();
            setHeader("Anagrafica");
        });

        apSubSearch.setChangeHandler(() -> {
            if (apSubSearch.getSelected() == null) {
                return;
            }
            apEditor.edit(apSubSearch.getSelected());
            editor.setVisible(false);
            setHeader(String.format("Anagrafica:Pubblicazione:Edit:%s-%s",
                                    apSubSearch.getSelected().getCaptionIntestatario(),
                                    apSubSearch.getSelected().getCaptionPubblicazione()));
        });

        apEditor.setChangeHandler(() -> {
            apSubSearch.onSearch();
            setHeader(String.format("Anagrafica:Edit:%s", search.getSelected().getCaption()));
            editor.setVisible(true);
            apEditor.setVisible(false);
        });


    }

}
