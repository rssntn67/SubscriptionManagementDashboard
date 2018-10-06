package it.arsinfo.smd.vaadin;



import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Note;
import it.arsinfo.smd.repository.AnagraficaDao;
import it.arsinfo.smd.repository.NoteDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.vaadin.annotations.Title;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

@SpringUI(path=SmdUI.URL_NOTE)
@Title("Note Anagrafica ADP")
public class NoteUI extends UI {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7884064928998716106L;

	Grid<Note> grid;
	@Autowired
	NoteDao repo;

	@Autowired
	AnagraficaDao anadao;

	@Override
	protected void init(VaadinRequest request) {
		Assert.notNull(repo, "repo must be not null");
		Label header = new Label("Campagna Abbonamento");
		Button addNewBtn = new Button("Genera Nota", VaadinIcons.PLUS);	
		TextField filter = new TextField();
	    ComboBox<Anagrafica> filterAnagrafica = new ComboBox<Anagrafica>("Selezionare Cliente");
		
		grid = new Grid<>(Note.class);
		NoteEditor editor = new NoteEditor(repo,anadao);
		HorizontalLayout actions = new HorizontalLayout(filterAnagrafica,filter,addNewBtn);
		VerticalLayout layout = new VerticalLayout();
		layout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
		layout.addComponents(header,editor,actions,grid);
		setContent(layout);

		header.addStyleName(ValoTheme.LABEL_H2);
		
		filter.setPlaceholder("Cerca per Descrizione");
		filter.setValueChangeMode(ValueChangeMode.EAGER);
		filter.addValueChangeListener(e -> list(e.getValue()));		

		
		filterAnagrafica.setEmptySelectionAllowed(false);
		filterAnagrafica.setPlaceholder("Cerca per Cliente");
		filterAnagrafica.setItems(anadao.findAll());
		filterAnagrafica.setItemCaptionGenerator(Anagrafica::getCaption);
		grid.setColumns("id", "anagrafica.cognome", "anagrafica.nome","data","description");		
		grid.getColumn("id").setMaximumWidth(50);
		grid.setWidth("80%");

		editor.setWidth("80%");

		filterAnagrafica.addSelectionListener(e-> listType(e.getSelectedItem().get()));

		grid.asSingleSelect().addValueChangeListener(e -> {
			editor.edit(e.getValue());
		});
		
		addNewBtn.addClickListener(e -> editor.edit(new Note()));

		editor.setChangeHandler(() -> {
			editor.setVisible(false);
			list(null);
		});
		list(null);

	}

	void list(String filterText) {
		if (StringUtils.isEmpty(filterText)) {
			grid.setItems(repo.findAll());
		} else {
			grid.setItems(repo.findByDescriptionStartsWithIgnoreCase(filterText));
		}
	}
	
	void listType(Anagrafica anagrafica) {
		if (anagrafica != null ) {
			grid.setItems(repo.findByAnagrafica(anagrafica));
		} else {
			grid.setItems(repo.findAll());
		}
	}

}
