package it.arsinfo.smd.ui.print;

import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.Layout;
import com.vaadin.ui.UI;

import it.arsinfo.smd.data.Provincia;
import it.arsinfo.smd.data.TitoloAnagrafica;
import it.arsinfo.smd.dto.Indirizzo;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.ui.SmdUI;

@SpringUI(path="/printtest")
public class StampaIndirizzoTestUI extends UI {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3428443647083120006L;

	@Override
    protected void init(VaadinRequest request) {
	       setContent(stampa());
	       
	       JavaScript.getCurrent().execute(
	               "setTimeout(function() {" +
	               "  print(); self.close();}, 0);");
    }   
		
    private Layout stampa() {
    	Anagrafica antonio = new Anagrafica();
    	antonio.setTitolo(TitoloAnagrafica.Rettore);
    	antonio.setDenominazione("Russo");
    	antonio.setNome("Antonio");
    	antonio.setIndirizzo("Largo Aldifreda 9");
    	antonio.setCap("81100");
    	antonio.setCitta("Caserta");
    	antonio.setProvincia(Provincia.CE);
    	return SmdUI.stampa(Indirizzo.getIndirizzo(antonio));
    }

}
