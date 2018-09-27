package it.arsinfo.smd.vaadin;


import it.arsinfo.smd.entity.Pubblicazione.Tipo;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.repository.PubblicazioneDao;

import java.util.EnumSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

@SpringUI(path=SmdUI.URL_PUBBLICAZIONI)
@Title("Anagrafica Pubblicazioni ADP")
public class PubblicazioneUI extends UI {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7884064928998716106L;

	Grid<Pubblicazione> grid;
	@Autowired
	PubblicazioneDao repo;
	
	@Override
	protected void init(VaadinRequest request) {
		Assert.notNull(repo, "repo must be not null");
		Label header = new Label("Anagrafica Pubblicazioni");
		TextField filterNome = new TextField();
		ComboBox<Tipo> filterTipo = new ComboBox<Tipo>(null,EnumSet.allOf(Tipo.class));
		
		grid = new Grid<>(Pubblicazione.class);
		PubblicazioneEditor editor = new PubblicazioneEditor(repo);
		HorizontalLayout actions = new HorizontalLayout(filterTipo,filterNome);
		VerticalLayout layout = new VerticalLayout();
		layout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
		layout.addComponents(header,editor,actions,grid);
		setContent(layout);

		header.addStyleName(ValoTheme.LABEL_H2);
		
		filterTipo.setEmptySelectionAllowed(false);
		filterTipo.setPlaceholder("Cerca per Tipo");
		
		filterNome.setPlaceholder("Cerca per Nome");
		
		grid.setColumns("id", "nome", "tipo","costo","active","abbonamento");		
		grid.getColumn("id").setMaximumWidth(50);
		grid.setWidth("80%");

		editor.setWidth("80%");

		filterTipo.addSelectionListener(e-> listCustomer(e.getSelectedItem().get()));

		filterNome.setValueChangeMode(ValueChangeMode.EAGER);
		filterNome.addValueChangeListener(e -> listCustomers(e.getValue()));		

		grid.asSingleSelect().addValueChangeListener(e -> {
			editor.edit(e.getValue());
		});
		
		editor.setChangeHandler(() -> {
			editor.setVisible(false);
			listCustomers(filterNome.getValue());
		});
		listCustomers(null);

	}

	void listCustomers(String filterText) {
		if (StringUtils.isEmpty(filterText)) {
			grid.setItems(repo.findAll());
		}
		else {
			grid.setItems(repo.findByNomeStartsWithIgnoreCase(filterText));
		}
	}
	
	void listCustomer(Tipo tipo) {
		if (tipo != null ) {
			grid.setItems(repo.findByTipo(tipo));
		} else {
			grid.setItems(repo.findAll());
		}
	}

}
