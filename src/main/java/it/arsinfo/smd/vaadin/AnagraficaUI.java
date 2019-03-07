package it.arsinfo.smd.vaadin;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;

import it.arsinfo.smd.repository.AnagraficaDao;
import it.arsinfo.smd.repository.AnagraficaPubblicazioneDao;

@SpringUI(path=SmdUI.URL_ANAGRAFICA)
@Title("Anagrafica Clienti ADP")
public class AnagraficaUI extends SmdHeader {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7884064928998716106L;

	@Autowired
	AnagraficaDao anagraficaDao;
        @Autowired
        AnagraficaPubblicazioneDao anagraficaPubblicazioneDao;
	
	@Override
	protected void init(VaadinRequest request) {
            super.init(request,"Anagrafica Clienti");
		Assert.notNull(anagraficaDao, "anagraficaDao must be not null");
                Assert.notNull(anagraficaPubblicazioneDao, "anagraficaPubblicazioneDao must be not null");
                AnagraficaSearch search = new AnagraficaSearch(anagraficaDao);
		AnagraficaEditor editor = new AnagraficaEditor(anagraficaDao, anagraficaPubblicazioneDao);
		addComponents(editor,search);
		

		editor.setWidth("100%");
                search.setWidth("120%");

		editor.setChangeHandler(() -> {
                    search.list();
                    editor.setVisible(false);
                    search.setVisible(true);
                    showHeader();
		});
		
                search.setChangeHandler(() -> {
                    editor.edit(search.getAnagrafica());
                    editor.setVisible(true);
                    search.setVisible(false);
                    hideHeader();
               });

	}

}
