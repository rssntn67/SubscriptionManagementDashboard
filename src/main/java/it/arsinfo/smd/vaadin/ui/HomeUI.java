package it.arsinfo.smd.vaadin.ui;



import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import it.arsinfo.smd.vaadin.SmdUiHelper;

@SpringUI
@Title("Gestione Abbonamenti ADP")
public class HomeUI extends UI {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7884064928998716106L;

	@Override
	protected void init(VaadinRequest request) {
		VerticalLayout layout = new VerticalLayout();
		layout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
		layout.addComponents(new Label("Benvenuti nel programma gestione abbonamenti ADP")
		);
		layout.addComponents(SmdUiHelper.getPageLinks());
		setContent(layout);

	}

}
