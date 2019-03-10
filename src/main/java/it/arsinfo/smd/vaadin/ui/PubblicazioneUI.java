package it.arsinfo.smd.vaadin.ui;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;

import it.arsinfo.smd.repository.PubblicazioneDao;
import it.arsinfo.smd.vaadin.model.SmdUI;
import it.arsinfo.smd.vaadin.model.SmdUIHelper;

@SpringUI(path=SmdUIHelper.URL_PUBBLICAZIONI)
@Title("Anagrafica Pubblicazioni ADP")
public class PubblicazioneUI extends SmdUI {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7884064928998716106L;

	@Autowired
	PubblicazioneDao repo;
	
	@Override
	protected void init(VaadinRequest request) {
	    super.init(request, "Pubblicazioni");
		Assert.notNull(repo, "repo must be not null");
		PubblicazioneSearch search = new PubblicazioneSearch(repo);
		PubblicazioneEditor editor = new PubblicazioneEditor(repo);
		addComponents(editor,search);
	        editor.setWidth("100%");
	        search.setWidth("120%");
	        editor.setVisible(false);

	        search.setChangeHandler(() -> {
	            if (search.getSelected() == null) {
	                return;
	            }
	            editor.edit(search.getSelected());
	            setHeader(String.format("Pubblicazioni:Edit:%s", search.getSelected().getCaption()));
	            hideMenu();
	        });

		editor.setChangeHandler(() -> {
		    search.onSearch();
	            showMenu();
	            setHeader("Pubblicazioni");
	            editor.setVisible(false);
	        });

	}

}
