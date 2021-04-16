package it.arsinfo.smd.ui.print;

import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Layout;
import com.vaadin.ui.VerticalLayout;
import it.arsinfo.smd.ui.service.api.SpedizioneService;
import it.arsinfo.smd.data.Stampa;
import it.arsinfo.smd.entity.Spedizione;
import it.arsinfo.smd.ui.SmdUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@SpringUI(path= SmdUI.URL_STAMPA_INDIRIZZO_SPEDIZIONE)
public class StampaSpedizioneUI extends StampaUI {

    @Autowired
    SpedizioneService dao;
	/**
	 * 
	 */
	private static final long serialVersionUID = 3428443647083120006L;
	private static final Logger log = LoggerFactory.getLogger(StampaSpedizioneUI.class);

	@Override
    protected void init(VaadinRequest request) {
		super.init(request);
		log.info("init: id {} ",request.getParameter("id") );
		log.info("init: type {} ",request.getParameter("type") );
		setContent(stampa(request.getParameter("id"),Stampa.valueOf(request.getParameter("type"))));
	   	print();

	}
		
    private Layout stampa(String id, Stampa type) {
    	VerticalLayout subContent = new VerticalLayout();
    	subContent.setMargin(true);
    	subContent.setSpacing(true);
    	if (id == null || type == null) {
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
    	return stampa(type,dao.getIndirizzo(sped),subContent);
    }
        
}
