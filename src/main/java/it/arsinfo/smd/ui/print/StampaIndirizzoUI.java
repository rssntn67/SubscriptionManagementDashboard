package it.arsinfo.smd.ui.print;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.server.Sizeable;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import it.arsinfo.smd.dao.SpedizioneServiceDao;
import it.arsinfo.smd.dto.Indirizzo;
import it.arsinfo.smd.entity.Spedizione;

@SpringUI(path="print")
public class StampaIndirizzoUI extends UI {

    @Autowired
    SpedizioneServiceDao dao;
	/**
	 * 
	 */
	private static final long serialVersionUID = 3428443647083120006L;

	@Override
    protected void init(VaadinRequest request) {
	       //setContent(stampa(request.getParameter("id")));
	       GridLayout lay = new GridLayout(3,1);
	       lay.setWidth(220,Sizeable.Unit.MM);
	       lay.setHeight(110,Sizeable.Unit.MM);
	       VerticalLayout left = new VerticalLayout(new Label("   "));
	       VerticalLayout right = new VerticalLayout(new Label("   "));
	       lay.addComponent(left, 0, 0);
	       lay.addComponent(stampa(request.getParameter("id")), 1, 0);
	       lay.addComponent(right, 2, 0);
	       setContent(lay);
	       
	       JavaScript.getCurrent().execute(
	               "setTimeout(function() {" +
	               "  print(); self.close();}, 0);");
    }   
	
	private Label templatestampahtml(Indirizzo indirizzo) {
		String html = "<p>";
		html+=indirizzo.getIntestazione();
    	if (indirizzo.getSottoIntestazione() != null && !indirizzo.getSottoIntestazione().equals("")) {
    		html+="<br/>";
    		html+=indirizzo.getSottoIntestazione();
    	}
		html+="<br/>";
		html+= indirizzo.getIndirizzo();
		html+="<br/>";
		html+=indirizzo.getCap() + " " + indirizzo.getCitta() + " ("+indirizzo.getProvincia().name()+")";
		html+="<br/>";
		html+=indirizzo.getPaese().getNome();
		html+="</p>";
		return new Label(html, ContentMode.HTML);
	}
	
    private VerticalLayout stampa(String id) {
    	VerticalLayout subContent = new VerticalLayout();
    	subContent.setMargin(true);
    	subContent.setSpacing(true);
    	if (id == null) {
    		return subContent;
    	}
    	try {
    		Long.parseLong(id);
		} catch (Exception e) {
			return subContent;
		}
    	Spedizione sped = dao.findById(Long.parseLong(id));
    	if (sped == null) {
    		return subContent;
    	}
    	subContent.addComponent(templatestampahtml(dao.stampa(sped)));
    	return subContent;
    }

}
