package it.arsinfo.smd.vaadin.ui;


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

import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.repository.AbbonamentoDao;
import it.arsinfo.smd.repository.AnagraficaDao;
import it.arsinfo.smd.repository.PubblicazioneDao;
import it.arsinfo.smd.vaadin.model.SmdUI;
import it.arsinfo.smd.vaadin.model.SmdUIHelper;

@SpringUI(path=SmdUIHelper.URL_ABBONAMENTI)
@Title("Abbonamenti ADP")
public class AbbonamentoUI extends SmdUI {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7884064928998716106L;

	Grid<Abbonamento> grid;
	@Autowired
	AbbonamentoDao repo;

	@Autowired
	AnagraficaDao rana;

	@Autowired
	PubblicazioneDao pubb;

	@Override
	protected void init(VaadinRequest request) {
	       super.init(request,"Abbonamenti Clienti");

		Assert.notNull(repo, "repo must be not null");
		Button addNewBtn = new Button("Aggiungi Abbonamento", VaadinIcons.PLUS);		
		ComboBox<Anagrafica> filterAnagrafica = new ComboBox<Anagrafica>();
		
		grid = new Grid<>(Abbonamento.class);
		AbbonamentoEditor editor = new AbbonamentoEditor(repo,rana,pubb);
		HorizontalLayout actions = new HorizontalLayout(filterAnagrafica,addNewBtn);
		addComponents(editor,actions,grid);
		
		filterAnagrafica.setEmptySelectionAllowed(false);
		filterAnagrafica.setPlaceholder("Cerca per Cliente");
		filterAnagrafica.setItems(rana.findAll());
		filterAnagrafica.setItemCaptionGenerator(Anagrafica::getCaption);
		grid.setColumns("intestatario.caption", "cassa","costo","anno","data");		
		grid.setWidth("80%");

		editor.setWidth("80%");

		filterAnagrafica.addSelectionListener(e-> listType(e.getSelectedItem().get()));

		grid.asSingleSelect().addValueChangeListener(e -> {
			editor.edit(e.getValue());
		});
		
		addNewBtn.addClickListener(e -> editor.edit(new Abbonamento()));

		editor.setChangeHandler(() -> {
			editor.setVisible(false);
			list(null);
		});
		list(null);

	}

	void list(String filterText) {
		grid.setItems(repo.findAll());
	}
	
	void listType(Anagrafica anagrafica) {
		if (anagrafica != null ) {
			grid.setItems(repo.findByIntestatario(anagrafica));
		} else {
			grid.setItems(repo.findAll());
		}
	}

}
