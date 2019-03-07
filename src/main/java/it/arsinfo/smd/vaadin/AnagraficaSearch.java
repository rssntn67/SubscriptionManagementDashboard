package it.arsinfo.smd.vaadin;


import java.util.EnumSet;
import java.util.stream.Collectors;

import org.springframework.util.StringUtils;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;

import it.arsinfo.smd.data.Diocesi;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.repository.AnagraficaDao;

public class AnagraficaSearch extends SmdEditor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7884064928998716106L;

	private Grid<Anagrafica> grid;
	private AnagraficaDao anagraficaDao;

	private Diocesi searchDiocesi;
	private String searchCognome;
	private Anagrafica anagrafica;
	
	public  AnagraficaSearch(AnagraficaDao anagraficaDao) {
	    this.anagraficaDao = anagraficaDao;
		Button addNewBtn = new Button("Aggiungi ad Anagrafica", VaadinIcons.PLUS);		
		TextField filterCognome = new TextField();
		ComboBox<Diocesi> filterDiocesi = new ComboBox<Diocesi>(null,EnumSet.allOf(Diocesi.class));
		
		grid = new Grid<>(Anagrafica.class);
		HorizontalLayout actions = new HorizontalLayout(filterDiocesi,filterCognome,addNewBtn);
		addComponents(actions,grid);
		
		filterDiocesi.setEmptySelectionAllowed(false);
		filterDiocesi.setPlaceholder("Cerca per Diocesi");
		filterDiocesi.setItemCaptionGenerator(Diocesi::getDetails);

		filterCognome.setPlaceholder("Cerca per Cognome");
		
		
		grid.setColumns("id", "nome", "cognome","diocesi.details","indirizzo","citta","cap","paese","email","telefono","inRegola");		
		grid.getColumn("diocesi.details").setCaption("Diocesi");
		grid.getColumn("id").setMaximumWidth(100);
		
		grid.setWidth("80%");

		filterDiocesi.setEmptySelectionAllowed(true);
		filterDiocesi.setItemCaptionGenerator(Diocesi::getDetails);

		filterDiocesi.addSelectionListener(e-> {
		    if (e.getValue() == null) {
		        searchDiocesi = null;
		    } else {
		        searchDiocesi = e.getSelectedItem().get(); 
		    }
		    list() ; 
		});

		
		filterCognome.setValueChangeMode(ValueChangeMode.EAGER);
		filterCognome.addValueChangeListener(e -> {
		    searchCognome = e.getValue();
		    list();		    
		});		

		grid.asSingleSelect().addValueChangeListener(e -> {
		    anagrafica = e.getValue();
		    changeHandler.onChange();
		});
		
		addNewBtn.addClickListener(e -> {
		    anagrafica = new Anagrafica("", ""); 
                    changeHandler.onChange();
		});

		list();

	}

	public void list() {
		if (StringUtils.isEmpty(searchCognome) && searchDiocesi == null) {
			grid.setItems(anagraficaDao.findAll());
		} else if (!StringUtils.isEmpty(searchCognome) && searchDiocesi == null) {
			grid.setItems(anagraficaDao.findByCognomeStartsWithIgnoreCase(searchCognome));
		} else if (StringUtils.isEmpty(searchCognome) && searchDiocesi != null) {
			grid.setItems(anagraficaDao.findByDiocesi(searchDiocesi));
		} else {
			grid.setItems(
			   anagraficaDao.findByCognomeStartsWithIgnoreCase(searchCognome).stream().filter( tizio -> tizio.getDiocesi().equals(searchDiocesi)).collect(Collectors.toList()));
		}
	}

    public Anagrafica getAnagrafica() {
        return anagrafica;
    }

}
