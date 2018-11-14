package it.arsinfo.smd.vaadin;


import java.io.OutputStream;
import java.util.EnumSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.themes.ValoTheme;

import it.arsinfo.smd.entity.Cuas;
import it.arsinfo.smd.entity.Incasso;
import it.arsinfo.smd.repository.IncassoDao;

@SpringUI(path=SmdUI.URL_INCASSI)
@Title("Incassi ADP")
public class IncassoUI extends SmdHeader implements Receiver, SucceededListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7884064928998716106L;

	Grid<Incasso> grid;
	
	@Autowired
	IncassoDao repo;

	@Override
	protected void init(VaadinRequest request) {
	       super.init(request);

		Assert.notNull(repo, "repo must be not null");
		Label header = new Label("Incassi");
		ComboBox<Cuas> filterCuas = new ComboBox<Cuas>("Selezionare C.U.A.S.",EnumSet.allOf(Cuas.class));
		
		Upload upload = new Upload("Aggiungi Incasso", this);
		upload.setImmediateMode(false);
		upload.setButtonCaption("Avvia Download");
		
		grid = new Grid<>(Incasso.class);
		IncassoEditor editor = new IncassoEditor(repo);
		HorizontalLayout actions = new HorizontalLayout(filterCuas,upload);
		addComponents(header,editor,actions,grid);

		header.addStyleName(ValoTheme.LABEL_H2);
		
		filterCuas.setEmptySelectionAllowed(false);
		filterCuas.setPlaceholder("Cerca per CUAS");
		filterCuas.setItemCaptionGenerator(Cuas::getDenominazione);
		grid.setColumns("id", "cuas.denominazione", 
		                "ccp.ccp",
		                "dataContabile",
		                "totaleDocumenti","totaleImporto",
		                "documentiEsatti","importoDocumentiEsatti",
                                "documentiErrati","importoDocumentiErrati");		
		grid.getColumn("id").setMaximumWidth(50);
		grid.setWidth("80%");

		editor.setWidth("80%");

		filterCuas.addSelectionListener(e-> listType(e.getSelectedItem().get()));

		grid.asSingleSelect().addValueChangeListener(e -> {
			editor.edit(e.getValue());
		});
		
		editor.setChangeHandler(() -> {
			editor.setVisible(false);
			list(null);
		});
		list(null);

	}

	void list(String filterText) {
		grid.setItems(repo.findAll());
	}
	
	void listType(Cuas cuas) {
		if (cuas != null ) {
			grid.setItems(repo.findByCuas(cuas));
		} else {
			grid.setItems(repo.findAll());
		}
	}

    @Override
    public void uploadSucceeded(SucceededEvent event) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public OutputStream receiveUpload(String filename, String mimeType) {
        // TODO Auto-generated method stub
        return null;
    }

}
