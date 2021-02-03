package it.arsinfo.smd.ui.print;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Layout;
import com.vaadin.ui.VerticalLayout;

import it.arsinfo.smd.dao.SpedizioneServiceDao;
import it.arsinfo.smd.entity.Spedizione;

public abstract class StampaSpedizioneUI extends StampaUI {

    @Autowired
    SpedizioneServiceDao dao;
	/**
	 * 
	 */
	private static final long serialVersionUID = 3428443647083120006L;

	@Override
    protected void init(VaadinRequest request) {
		   super.init(request);
	       setContent(stampa(request.getParameter("id")));
	       print();
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
    	return stampa(dao.getIndirizzo(sped));
    }
        
}
