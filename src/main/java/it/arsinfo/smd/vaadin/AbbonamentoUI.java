package it.arsinfo.smd.vaadin;


import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.repository.AbbonamentoDao;
import it.arsinfo.smd.repository.AnagraficaDao;
import it.arsinfo.smd.repository.PubblicazioneDao;

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
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;

@SpringUI(path=SmdUI.URL_ABBONAMENTI)
@Title("Abbonamenti ADP")
public class AbbonamentoUI extends SmdHeader {

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
	       super.init(request);

		Assert.notNull(repo, "repo must be not null");
		Label header = new Label("Abbonamenti Clienti");
		Button addNewBtn = new Button("Aggiungi Abbonamento", VaadinIcons.PLUS);		
		ComboBox<Anagrafica> filterAnagrafica = new ComboBox<Anagrafica>();
		
		grid = new Grid<>(Abbonamento.class);
		AbbonamentoEditor editor = new AbbonamentoEditor(repo,rana,pubb);
		HorizontalLayout actions = new HorizontalLayout(filterAnagrafica,addNewBtn);
		addComponents(header,editor,actions,grid);

		header.addStyleName(ValoTheme.LABEL_H2);
		
		filterAnagrafica.setEmptySelectionAllowed(false);
		filterAnagrafica.setPlaceholder("Cerca per Cliente");
		filterAnagrafica.setItems(rana.findAll());
		filterAnagrafica.setItemCaptionGenerator(Anagrafica::getCognome);
		grid.setColumns("id", "anagrafica.cognome", "anagrafica.nome","contoCorrentePostale.ccp","cost","campo","data");		
		grid.getColumn("id").setMaximumWidth(50);
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
