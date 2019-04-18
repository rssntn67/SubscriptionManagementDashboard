package it.arsinfo.smd.ui.vaadin;



import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;

import it.arsinfo.smd.ui.security.SecurityUtils;

@SpringUI
@Title("Gestione Abbonamenti ADP")
public class HomeUI extends SmdUI {

    /**
     * 
     */
    private static final long serialVersionUID = 7884064928998716106L;

    @Override
    protected void init(VaadinRequest request) {
        HorizontalSplitPanel layout = new HorizontalSplitPanel();
        AbsoluteLayout left = new AbsoluteLayout();
        left.setWidth("1600px");
        left.setHeight("1000px");        
        left.addComponent(new Label("Benvenuto nel programma gestione abbonamenti ADP"), "left: 320px; top: 200px;");
        VerticalLayout right = new VerticalLayout();
        right.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
        right.addComponents(getPageLinks(SecurityUtils.getUsername()));
        layout.addComponents(left,
                             right);
        setContent(layout);

    }

}
