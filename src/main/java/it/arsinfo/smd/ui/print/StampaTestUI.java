package it.arsinfo.smd.ui.print;

import com.vaadin.server.VaadinRequest;

public abstract class StampaTestUI extends StampaUI {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3428443647083120006L;

	@Override
    protected void init(VaadinRequest request) {
		super.init(request);
	    setContent(stampa(getTestIndirizzo()));
	    print();
    }
		
}
