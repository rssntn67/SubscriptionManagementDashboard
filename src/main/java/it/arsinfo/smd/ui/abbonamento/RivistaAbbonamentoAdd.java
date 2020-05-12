package it.arsinfo.smd.ui.abbonamento;

import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.RivistaAbbonamento;
import it.arsinfo.smd.ui.vaadin.SmdAddItem;

public class RivistaAbbonamentoAdd extends SmdAddItem<RivistaAbbonamento, Abbonamento> {

	private Abbonamento abbonamento;
    public RivistaAbbonamentoAdd(String caption) {
        super(caption);
    }
    
    @Override
    public RivistaAbbonamento generate() {
        RivistaAbbonamento ec = new RivistaAbbonamento();
        ec.setAbbonamento(abbonamento);
        ec.setDestinatario(abbonamento.getIntestatario());
        ec.setAnnoInizio(abbonamento.getAnno());
        ec.setAnnoFine(abbonamento.getAnno());
        return ec;
    }

	@Override
	public void set(Abbonamento t) {
		abbonamento=t;
	}


}
