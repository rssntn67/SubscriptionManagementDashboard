package it.arsinfo.smd.vaadin.model;



import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public abstract class SmdUI extends UI {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7884064928998716106L;
        private VerticalLayout layout = new VerticalLayout();
        private HorizontalLayout menu = new HorizontalLayout();
        private Label header = new Label("");
	
	protected void init(VaadinRequest request, String head) {
	    
	    menu.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
	    menu.addComponents(SmdUIHelper.getPageLinks());
	    header.setValue(head);
	    layout.addComponent(menu);
	    layout.addComponent(header);
	    setContent(layout);

	}
	protected void setExpandRatio(Component component, float ratio) {
	    layout.setExpandRatio(component, ratio);
	}
	
	protected void addComponents(Component... components ) {
	    layout.addComponents(components);
	}
	
	protected void hideMenu() {
	    menu.setVisible(false);
	}
	
	protected void showMenu() {
	    menu.setVisible(true);
	}

	protected void setHeader(String head) {
	    header.setValue(head);
	}
}
