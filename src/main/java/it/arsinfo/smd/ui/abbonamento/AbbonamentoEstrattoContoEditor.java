package it.arsinfo.smd.ui.abbonamento;

import it.arsinfo.smd.dao.AbbonamentoServiceDao;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.EstrattoConto;
import it.arsinfo.smd.ui.vaadin.SmdButton;
import it.arsinfo.smd.ui.vaadin.SmdEntityItemEditor;

public class AbbonamentoEstrattoContoEditor extends SmdEntityItemEditor<EstrattoConto,Abbonamento> {


	public AbbonamentoEstrattoContoEditor(
			AbbonamentoServiceDao dao,
			EstrattoContoAdd itemAdd, SmdButton itemDel, SmdButton itemSave, EstrattoContoGrid itemGrid, EstrattoContoEditor itemEditor,
			AbbonamentoEditor abbeditor) {
		super(dao,itemAdd,itemDel,itemSave, itemGrid, itemEditor, abbeditor);
	}

}
