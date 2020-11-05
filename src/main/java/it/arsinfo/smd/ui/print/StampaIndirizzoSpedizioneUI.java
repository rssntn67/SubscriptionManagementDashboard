package it.arsinfo.smd.ui.print;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.Layout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import it.arsinfo.smd.dao.SpedizioneServiceDao;
import it.arsinfo.smd.entity.Spedizione;
import it.arsinfo.smd.ui.SmdUI;

@SpringUI(path=SmdUI.URL_STAMPA_INDIRIZZO_SPEDIZIONE)
public class StampaIndirizzoSpedizioneUI extends UI {

    @Autowired
    SpedizioneServiceDao dao;
	/**
	 * 
	 */
	private static final long serialVersionUID = 3428443647083120006L;

	@Override
    protected void init(VaadinRequest request) {
	       setContent(stampa(request.getParameter("id")));
	       
	       JavaScript.getCurrent().execute(
	               "setTimeout(function() {" +
	               "  print(); self.close();}, 0);");
    }   
		
    private Layout stampa(String id) {
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
    	return SmdUI.stampa(dao.getIndirizzo(sped));
    }

}
