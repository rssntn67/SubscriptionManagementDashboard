package it.arsinfo.smd.vaadin.ui;



import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import it.arsinfo.smd.vaadin.SmdUiHelper;

public abstract class AbstractUI extends com.vaadin.ui.UI {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7884064928998716106L;
        private VerticalLayout layout = new VerticalLayout();
        private HorizontalLayout header = new HorizontalLayout();
	
	protected void init(VaadinRequest request, String head) {
	    header.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
	    header.addComponentAsFirst(new Label(head));
	    header.addComponents(SmdUiHelper.getPageLinks()
		);
		layout.addComponent(header);
		setContent(layout);

	}
		
	protected void addComponents(Component... components ) {
	    layout.addComponents(components);
	}
	
	protected void hideHeader() {
	    header.setVisible(false);
	}
	
	protected void showHeader() {
	    header.setVisible(true);
	}

}
