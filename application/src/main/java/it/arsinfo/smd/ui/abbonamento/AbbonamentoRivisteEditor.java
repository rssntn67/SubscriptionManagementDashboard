package it.arsinfo.smd.ui.abbonamento;

import it.arsinfo.smd.ui.service.api.AbbonamentoService;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.RivistaAbbonamento;
import it.arsinfo.smd.ui.vaadin.SmdButton;
import it.arsinfo.smd.ui.vaadin.SmdEntityItemEditor;

public class AbbonamentoRivisteEditor extends SmdEntityItemEditor<RivistaAbbonamento,Abbonamento> {


	public AbbonamentoRivisteEditor(
			AbbonamentoService dao,
			RivistaAbbonamentoAdd itemAdd, SmdButton itemDel, SmdButton itemSave, RivistaAbbonamentoGrid itemGrid, RivistaAbbonamentoEditor itemEditor,
			AbbonamentoEditor abbeditor) {
		super(dao,itemAdd,itemDel,itemSave, itemGrid, itemEditor, abbeditor);
	}

}
