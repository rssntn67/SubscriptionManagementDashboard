package it.arsinfo.smd.vaadin;



import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class SmdHeader extends UI {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7884064928998716106L;
        private VerticalLayout layout = new VerticalLayout();

	
	@Override
	protected void init(VaadinRequest request) {
	    HorizontalLayout header = new HorizontalLayout();
	    header.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
	    header.addComponents( SmdUI.getPageLinks()
		);
		layout.addComponent(header);
		setContent(layout);

	}
		
	protected void addComponents(Component... components ) {
	    layout.addComponents(components);
	}

}
