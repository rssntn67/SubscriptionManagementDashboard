package it.arsinfo.smd.ui.abbonamento;

import it.arsinfo.smd.entity.EstrattoConto;
import it.arsinfo.smd.ui.vaadin.SmdAdd;

public class EstrattoContoAdd extends SmdAdd<EstrattoConto> {

    public EstrattoContoAdd(String caption) {
        super(caption);
    }
    
    @Override
    public EstrattoConto generate() {
        EstrattoConto ec = new EstrattoConto();
        return ec;
    }


}
