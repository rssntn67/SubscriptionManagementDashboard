package it.arsinfo.smd.ui.print;

import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Layout;

import it.arsinfo.smd.dto.Indirizzo;

@SpringUI(path="/printtestA")
public  final class StampaTestAUI extends StampaTestUI {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3428443647083120006L;

	@Override
	public Layout stampa(Indirizzo indirizzo) {
		return stampaA(indirizzo);
	}   

		
}
