package it.arsinfo.smd.ui.storico;

import it.arsinfo.smd.dao.StoricoServiceDao;
import it.arsinfo.smd.entity.Nota;
import it.arsinfo.smd.entity.Storico;
import it.arsinfo.smd.ui.vaadin.SmdButton;
import it.arsinfo.smd.ui.vaadin.SmdEntityItemEditor;

public class StoricoNotaEditor extends SmdEntityItemEditor<Nota,Storico> {


	public StoricoNotaEditor(
			StoricoServiceDao dao,
			NotaAdd itemAdd, SmdButton itemDel, SmdButton itemSave, NotaGrid itemGrid, NotaEditor itemEditor,
			StoricoEditor abbeditor) {
		super(dao,itemAdd,itemDel,itemSave, itemGrid, itemEditor, abbeditor);
	}

}
