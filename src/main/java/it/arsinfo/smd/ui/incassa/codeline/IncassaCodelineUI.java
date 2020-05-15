package it.arsinfo.smd.ui.incassa.codeline;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Title;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Notification;
import com.vaadin.ui.themes.ValoTheme;

import it.arsinfo.smd.dao.DistintaVersamentoServiceDao;
import it.arsinfo.smd.entity.DistintaVersamento;
import it.arsinfo.smd.ui.SmdUI;
import it.arsinfo.smd.ui.distinta.DistintaVersamentoGrid;
import it.arsinfo.smd.ui.distinta.DistintaVersamentoSearch;
import it.arsinfo.smd.ui.vaadin.SmdButton;
import it.arsinfo.smd.ui.versamento.VersamentoGrid;

@SpringUI(path = SmdUI.URL_INCASSA_CODELINE)
@Title(SmdUI.TITLE_INCASSA_CODELINE)
public class IncassaCodelineUI extends SmdUI {
    /**
     * 
     */
    private static final long serialVersionUID = 7884064928998716106L;

    @Autowired
    private DistintaVersamentoServiceDao dao;
    
    @Override
    protected void init(VaadinRequest request) {
        super.init(request,SmdUI.TITLE_INCASSA_CODELINE);
        SmdButton incassa = new SmdButton("Incassa con Code Line",VaadinIcons.AUTOMATION);
        incassa.getButton().addStyleName(ValoTheme.BUTTON_PRIMARY);
        SmdButton indietro = new SmdButton("Indietro",VaadinIcons.BACKSPACE);
        DistintaVersamentoSearch search = new DistintaVersamentoSearch(dao) {
        	@Override
        	public List<DistintaVersamento> find() {
        		return super.find().
        				stream().
        				filter(distinta -> distinta.getResiduo().signum() > 0).
        				collect(Collectors.toList());
        	}        	
        };
        DistintaVersamentoGrid grid = new DistintaVersamentoGrid("Distinte Versamenti");
        VersamentoGrid versGrid = new VersamentoGrid("Versamenti Incassati");
       
        addSmdComponents(search,incassa,indietro,versGrid,
                         grid
                         );

        versGrid.setVisible(false);
        indietro.setVisible(false);

        grid.setChangeHandler(() -> {
        });

        indietro.setChangeHandler(() -> {
        	search.setVisible(true);
        	incassa.setVisible(true);
        	grid.populate(search.find());
        	versGrid.setVisible(false);
        	indietro.setVisible(false);
        });

        incassa.setChangeHandler(() -> {
            try {
                versGrid.populate(dao.incassaCodeLine(search.find(),getLoggedInUser()));
            } catch (Exception e) {
                versGrid.setVisible(false);
                Notification.show("Incassa con Code Line. Errore: " +e.getMessage()+  ".",Notification.Type.ERROR_MESSAGE);
                return;
            }
        	search.setVisible(false);
        	incassa.setVisible(false);
        	grid.setVisible(false);
            indietro.setVisible(true);
        });
        
        grid.populate(search.find());
    }  
    
}
