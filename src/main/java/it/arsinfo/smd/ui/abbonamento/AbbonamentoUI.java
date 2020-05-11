package it.arsinfo.smd.ui.abbonamento;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Title;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.themes.ValoTheme;

import it.arsinfo.smd.dao.AbbonamentoServiceDao;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Campagna;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.ui.SmdEditorUI;
import it.arsinfo.smd.ui.SmdUI;
import it.arsinfo.smd.ui.vaadin.SmdButton;

@SpringUI(path = SmdUI.URL_ABBONAMENTI)
@Title("Abbonamenti ADP")
public class AbbonamentoUI extends SmdEditorUI<Abbonamento> {

    /**
     * 
     */
    private static final long serialVersionUID = 3429323584726379968L;

    @Autowired
    AbbonamentoServiceDao dao;
    
    @Override
    protected void init(VaadinRequest request) {
        List<Anagrafica> anagrafica = dao.getAnagrafica();
        List<Pubblicazione> pubblicazioni = dao.getPubblicazioni();
        List<Campagna> campagne = dao.getCampagne();
        AbbonamentoAdd add = new AbbonamentoAdd("Aggiungi abbonamento");
        if (anagrafica.size() == 0) {
            add.setVisible(false);
        } else {
            add.setPrimoIntestatario(anagrafica.iterator().next());
        }
        AbbonamentoSearch search = new AbbonamentoSearch(dao,campagne,pubblicazioni,anagrafica);
        AbbonamentoGrid grid = new AbbonamentoGrid("Abbonamenti");
        
        EstrattoContoAdd itemAdd = new EstrattoContoAdd("Aggiungi EC");
     	SmdButton itemDel = new SmdButton("Rimuovi Item", VaadinIcons.TRASH);
	    itemDel.getButton().addStyleName(ValoTheme.BUTTON_DANGER);
    	SmdButton itemSave = new SmdButton("Salva Item", VaadinIcons.CHECK);
	    itemSave.getButton().addStyleName(ValoTheme.BUTTON_PRIMARY);
	    AbbonamentoEditor abbeditor = new AbbonamentoEditor(dao,anagrafica,campagne);
	    abbeditor.getActions().addComponents(itemDel.getComponents());
		abbeditor.getActions().addComponents(itemSave.getComponents());
		abbeditor.getActions().addComponents(itemAdd.getComponents());
        EstrattoContoGrid itemGrid = new EstrattoContoGrid("Estratti Conto");
        EstrattoContoEditor itemEditor = new EstrattoContoEditor(pubblicazioni, anagrafica);
   	    
        AbbonamentoEstrattoContoEditor editor = new AbbonamentoEstrattoContoEditor(dao, itemAdd, itemDel, itemSave,itemGrid, itemEditor, abbeditor);
        editor.addComponents(itemEditor.getComponents());
        editor.addComponents(abbeditor.getComponents());
        editor.addComponents(itemGrid.getComponents());

        super.init(request, add, search, editor, grid, "Abbonamento");
        
        addSmdComponents(editor, 
                add,
                search, 
                grid);

        grid.populate(search.findAll());

    }

}
