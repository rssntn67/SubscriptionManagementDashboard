package it.arsinfo.smd.ui.distinta;

import it.arsinfo.smd.ui.service.api.DistintaVersamentoService;
import it.arsinfo.smd.entity.DistintaVersamento;
import it.arsinfo.smd.entity.Versamento;
import it.arsinfo.smd.ui.vaadin.SmdAddItem;
import it.arsinfo.smd.ui.vaadin.SmdButton;
import it.arsinfo.smd.ui.vaadin.SmdEntityEditor;
import it.arsinfo.smd.ui.vaadin.SmdEntityItemEditor;
import it.arsinfo.smd.ui.vaadin.SmdGrid;
import it.arsinfo.smd.ui.vaadin.SmdItemEditor;

public class DistintaEditor extends SmdEntityItemEditor<Versamento, DistintaVersamento>{

	public DistintaEditor(DistintaVersamentoService dao,
                          SmdAddItem<Versamento, DistintaVersamento> itemAdd, SmdButton itemDel, SmdButton itemSave,
                          SmdGrid<Versamento> itemGrid, SmdItemEditor<Versamento> itemEditor,
                          SmdEntityEditor<DistintaVersamento> editor) {
		super(dao, itemAdd, itemDel, itemSave, itemGrid, itemEditor, editor);
	}

}
