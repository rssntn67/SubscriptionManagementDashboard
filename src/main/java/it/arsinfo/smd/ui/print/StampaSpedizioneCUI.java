package it.arsinfo.smd.ui.print;

import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Layout;

import it.arsinfo.smd.dto.Indirizzo;
import it.arsinfo.smd.ui.SmdUI;

@SpringUI(path=SmdUI.URL_STAMPA_INDIRIZZO_SPEDIZIONE+"C")
public class StampaSpedizioneCUI extends StampaSpedizioneUI {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3428443647083120006L;
		
	@Override
    public Layout stampa(Indirizzo indirizzo) {
    	return stampaC(indirizzo);
    }
    
}
