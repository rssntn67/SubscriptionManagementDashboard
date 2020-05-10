package it.arsinfo.smd.ui.abbonamento;

import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.EstrattoConto;
import it.arsinfo.smd.ui.vaadin.SmdAddItem;

public class EstrattoContoAdd extends SmdAddItem<EstrattoConto, Abbonamento> {

	private Abbonamento abbonamento;
    public EstrattoContoAdd(String caption) {
        super(caption);
    }
    
    @Override
    public EstrattoConto generate() {
        EstrattoConto ec = new EstrattoConto();
        ec.setAbbonamento(abbonamento);
        ec.setDestinatario(abbonamento.getIntestatario());
        return ec;
    }

	@Override
	public void set(Abbonamento t) {
		abbonamento=t;
	}


}
