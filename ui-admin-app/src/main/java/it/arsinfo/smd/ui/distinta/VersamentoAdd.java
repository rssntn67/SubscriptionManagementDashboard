package it.arsinfo.smd.ui.distinta;

import it.arsinfo.smd.entity.DistintaVersamento;
import it.arsinfo.smd.entity.Versamento;
import it.arsinfo.smd.ui.vaadin.SmdAddItem;

public class VersamentoAdd extends SmdAddItem<Versamento,DistintaVersamento> {

    private DistintaVersamento incasso;

    public VersamentoAdd(String caption) {
        super(caption);
    }
    
    @Override
    public Versamento generate() {
        return new Versamento(incasso);
    }

	@Override
	public void set(DistintaVersamento t) {
        this.incasso = t;
		
	}

}
