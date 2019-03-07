package it.arsinfo.smd.vaadin;


import java.util.EnumSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.vaadin.annotations.Title;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;

import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.entity.Campagna;
import it.arsinfo.smd.repository.AnagraficaDao;
import it.arsinfo.smd.repository.CampagnaDao;
import it.arsinfo.smd.repository.PubblicazioneDao;

@SpringUI(path=SmdUI.URL_CAMPAGNA)
@Title("Campagna Abbonamenti ADP")
public class CampagnaUI extends SmdHeader {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7884064928998716106L;

	Grid<Campagna> grid;
	@Autowired
	CampagnaDao repo;

	@Autowired
	AnagraficaDao anadao;

	@Autowired
	PubblicazioneDao pubdao;

	@Override
	protected void init(VaadinRequest request) {
	    super.init(request,"Campagna Abbonamenti" );
		Assert.notNull(repo, "repo must be not null");
		Button addNewBtn = new Button("Genera Campagna Abbonamenti", VaadinIcons.PLUS);		
	    ComboBox<Anno> filterAnno = new ComboBox<Anno>("Selezionare Anno", EnumSet.allOf(Anno.class));
		
		grid = new Grid<>(Campagna.class);
		CampagnaEditor editor = new CampagnaEditor(repo,anadao,pubdao);
		HorizontalLayout actions = new HorizontalLayout(filterAnno,addNewBtn);
		addComponents(editor,actions,grid);
		
		filterAnno.setEmptySelectionAllowed(false);
		filterAnno.setPlaceholder("Cerca per Anno");
		grid.setColumns("id", "anno", "inizio","fine",
				"estratti","blocchetti","lodare","messaggio",
				"pagato","anagraficaFlagA","anagraficaFlagB","anagraficaFlagC");		
		grid.getColumn("id").setMaximumWidth(50);
		grid.setWidth("80%");

		editor.setWidth("80%");

		filterAnno.addSelectionListener(e-> listType(e.getSelectedItem().get()));

		grid.asSingleSelect().addValueChangeListener(e -> {
			editor.edit(e.getValue());
		});
		
		addNewBtn.addClickListener(e -> editor.edit(new Campagna()));

		editor.setChangeHandler(() -> {
			editor.setVisible(false);
			list(null);
		});
		list(null);

	}

	void list(String filterText) {
		grid.setItems(repo.findAll());
	}
	
	void listType(Anno anno) {
		if (anno != null ) {
			grid.setItems(repo.findByAnno(anno));
		} else {
			grid.setItems(repo.findAll());
		}
	}

}
