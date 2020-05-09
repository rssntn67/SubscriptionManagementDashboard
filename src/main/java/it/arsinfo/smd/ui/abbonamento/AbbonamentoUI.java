package it.arsinfo.smd.ui.abbonamento;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;

import it.arsinfo.smd.dao.AbbonamentoServiceDao;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Campagna;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.ui.SmdEditorUI;
import it.arsinfo.smd.ui.SmdUI;

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
        super.init(request, "Abbonamento");

        
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
        AbbonamentoEditor editor = new AbbonamentoEditor(dao,anagrafica,campagne);
        
        EstrattoContoGrid itemGrid = new EstrattoContoGrid("Estratti Conto");
        EstrattoContoAdd itemAdd = new EstrattoContoAdd("Aggiungi EC");
        EstrattoContoEditor itemEditor = new EstrattoContoEditor(pubblicazioni, anagrafica);
        AbbonamentoEstrattoContoEditor abbeditor = new AbbonamentoEstrattoContoEditor(dao, itemAdd, itemGrid, itemEditor, editor);
        init(request, add, search, abbeditor, grid, "Abbonamento");
    }

}
