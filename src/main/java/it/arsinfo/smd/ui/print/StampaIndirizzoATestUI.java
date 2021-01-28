package it.arsinfo.smd.ui.print;

import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.JavaScript;

@SpringUI(path="/printtestA")
public class StampaIndirizzoATestUI extends StampaIndirizzoUI {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3428443647083120006L;

	@Override
    protected void init(VaadinRequest request) {
		super.init(request);
	    setContent(stampaA(getTestIndirizzo()));
	       
	       JavaScript.getCurrent().execute(
	               "setTimeout(function() {" +
	               "  print(); self.close();}, 0);");
    }   
		
}
