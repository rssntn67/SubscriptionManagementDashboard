package it.arsinfo.smd.ui.abbonamento;

import it.arsinfo.smd.dao.AbbonamentoServiceDao;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.EstrattoConto;
import it.arsinfo.smd.ui.vaadin.SmdEntityItemEditor;

public class AbbonamentoEstrattoContoEditor extends SmdEntityItemEditor<EstrattoConto,Abbonamento> {

	public AbbonamentoEstrattoContoEditor(
			AbbonamentoServiceDao dao,
			EstrattoContoAdd itemAdd, EstrattoContoGrid itemGrid, EstrattoContoEditor itemEditor,
			AbbonamentoEditor editor) {
		super(dao,itemAdd, itemGrid, itemEditor, editor);
		addComponents(itemEditor.getComponents());
		addComponents(editor.getComponents());
		addComponents(itemGrid.getComponents());
	}

}
