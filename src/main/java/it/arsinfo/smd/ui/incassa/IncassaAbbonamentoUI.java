package it.arsinfo.smd.ui.incassa;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Title;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Notification;

import it.arsinfo.smd.dao.AbbonamentoServiceDao;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Campagna;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.ui.SmdUI;
import it.arsinfo.smd.ui.abbonamento.AbbonamentoGrid;
import it.arsinfo.smd.ui.abbonamento.AbbonamentoSearch;
import it.arsinfo.smd.ui.vaadin.SmdButton;
import it.arsinfo.smd.ui.vaadin.SmdButtonTextField;

@SpringUI(path = SmdUI.URL_INCASSA_ABB)
@Title("Incassa da Abbonamenti ADP")
public class IncassaAbbonamentoUI extends SmdUI {

    /**
     * 
     */
    private static final long serialVersionUID = 3429323584726379968L;

    @Autowired
    private AbbonamentoServiceDao dao;
    
    @Override
    protected void init(VaadinRequest request) {
        super.init(request, "Incassa Abbonamento");
        
        List<Anagrafica> anagrafica = dao.getAnagrafica();
        List<Pubblicazione> pubblicazioni = dao.getPubblicazioni();
        List<Campagna> campagne = dao.getCampagne();

        SmdButton back = new SmdButton("Indietro",null);

        OperazioneIncassoGrid versamentoGrid = new OperazioneIncassoGrid("Operazioni su Versamenti Associate");
        
        AbbonamentoSearch search = new AbbonamentoSearch(dao,campagne,pubblicazioni,anagrafica) {
        	@Override
        	public List<Abbonamento> find() {
        		return super.find().stream().filter(a -> a.getTotale().longValue() > 0).collect(Collectors.toList());
        	}; 
        };
        AbbonamentoGrid grid = new AbbonamentoGrid("Abbonamenti");
        SmdButtonTextField incassa = new SmdButtonTextField("Inserisci Importo da Incassare","Incassa", VaadinIcons.CASH);
        
        IncassaAbbonamentoEditor editor = new IncassaAbbonamentoEditor(anagrafica,campagne);
        editor.getActions().addComponent(back.getButton());

        addSmdComponents(back,editor,incassa,versamentoGrid, search, grid);
        
        back.setVisible(false);
        editor.setVisible(false);
        versamentoGrid.setVisible(false);
        incassa.setVisible(false);
        setHeader("Incassa da Abbonamento");
                
        
        search.setChangeHandler(() -> grid.populate(search.find()));
        back.setChangeHandler(() -> editor.onChange());

        grid.setChangeHandler(() -> {
            if (grid.getSelected() == null) {
                return;
            }
            setHeader(grid.getSelected().getHeader());
            hideMenu();
            search.setVisible(false);
            back.setVisible(true);
            editor.edit(grid.getSelected());
            incassa.setVisible(editor.incassare());
            grid.setVisible(false);
            versamentoGrid.populate(dao.getOperazioneIncassoAssociate(editor.get()));
        });

        editor.setChangeHandler(() -> {
        	back.setVisible(false);
            editor.setVisible(false);
            incassa.setVisible(false);
            versamentoGrid.setVisible(false);
            showMenu();
            search.setVisible(true);
            grid.populate(search.find());
        });
                
        versamentoGrid.setChangeHandler(() -> {
        	
        });

        incassa.setChangeHandler(() -> {
	    	try {
	    		dao.incassa(editor.get(), incassa.getValue(), getLoggedInUser());
	        } catch (Exception e) {
	            Notification.show(e.getMessage(),
	                              Notification.Type.ERROR_MESSAGE);
	        }
        	incassa.setVisible(false);
        	editor.edit(editor.get());
        	versamentoGrid.populate(dao.getOperazioneIncassoAssociate(editor.get()));
        });

        grid.populate(search.find());

    }

}
