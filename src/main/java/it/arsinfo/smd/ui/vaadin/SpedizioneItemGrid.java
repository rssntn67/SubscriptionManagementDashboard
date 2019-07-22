package it.arsinfo.smd.ui.vaadin;

import com.vaadin.ui.Grid;

import it.arsinfo.smd.entity.EstrattoConto;
import it.arsinfo.smd.entity.SpedizioneItem;

public class SpedizioneItemGrid extends SmdGrid<SpedizioneItem> {

    public SpedizioneItemGrid(String gridname) {
        super(new Grid<>(SpedizioneItem.class),gridname);
        setColumns("numero","pubblicazione.nome","mesePubblicazione","annoPubblicazione","posticipata");
        setColumnCaption("pubblicazione.nome","Pubblicazione");
        setColumnCaption("numero","Quan.tà");
    }

}
