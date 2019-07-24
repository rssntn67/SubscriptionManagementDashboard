package it.arsinfo.smd.ui.vaadin;

import it.arsinfo.smd.entity.EstrattoConto;

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
