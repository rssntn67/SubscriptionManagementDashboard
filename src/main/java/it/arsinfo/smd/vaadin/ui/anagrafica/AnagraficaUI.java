package it.arsinfo.smd.vaadin.ui.anagrafica;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;

import it.arsinfo.smd.repository.AnagraficaDao;
import it.arsinfo.smd.repository.AnagraficaPubblicazioneDao;
import it.arsinfo.smd.repository.PubblicazioneDao;
import it.arsinfo.smd.vaadin.SmdUiHelper;
import it.arsinfo.smd.vaadin.ui.AbstractUI;

@SpringUI(path = SmdUiHelper.URL_ANAGRAFICA)
@Title("Anagrafica Clienti ADP")
public class AnagraficaUI extends AbstractUI {

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
        super.init(request, "Anagrafica Clienti");
        Assert.notNull(anagraficaDao, "anagraficaDao must be not null");
        Assert.notNull(anagraficaPubblicazioneDao,
                       "anagraficaPubblicazioneDao must be not null");
        AnagraficaSearch anagraficaSearch = new AnagraficaSearch(anagraficaDao);
        AnagraficaEditor anagraficaEditor = new AnagraficaEditor(anagraficaDao);
        AnagraficaPubblicazioneSubSearch apSubSearch = new AnagraficaPubblicazioneSubSearch(anagraficaPubblicazioneDao);
        AnagraficaPubblicazioneEditor apEditor = new AnagraficaPubblicazioneEditor(anagraficaPubblicazioneDao,
                                                                                    pubblicazioneDao,
                                                                                    anagraficaDao);
        addComponents(apEditor, anagraficaEditor, apSubSearch, anagraficaSearch);

        anagraficaEditor.setWidth("100%");
        anagraficaSearch.setWidth("120%");
        apEditor.setWidth("100%");
        apSubSearch.setWidth("120%");

        anagraficaSearch.setChangeHandler(() -> {
            anagraficaEditor.edit(anagraficaSearch.getSelected());
            apSubSearch.setUpper(anagraficaSearch.getSelected());
            apSubSearch.onSearch();
            anagraficaEditor.setVisible(true);
            apSubSearch.setVisible(true);
            hideHeader();
        });


        anagraficaEditor.setChangeHandler(() -> {
            anagraficaSearch.onSearch();
            anagraficaSearch.setVisible(true);
            anagraficaEditor.setVisible(false);
            apEditor.setVisible(false);
            apSubSearch.setVisible(false);
            showHeader();
        });

        apSubSearch.setChangeHandler(() -> {
            apEditor.edit(apSubSearch.getSelected());
            anagraficaEditor.setVisible(true);
        });

        apEditor.setChangeHandler(() -> {
            apSubSearch.onSearch();
            anagraficaEditor.setVisible(true);
            apEditor.setVisible(false);
        });


    }

}
