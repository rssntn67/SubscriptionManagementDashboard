package it.arsinfo.smd.vaadin;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;

import it.arsinfo.smd.repository.AnagraficaDao;
import it.arsinfo.smd.repository.AnagraficaPubblicazioneDao;
import it.arsinfo.smd.repository.PubblicazioneDao;

@SpringUI(path=SmdUI.URL_ANAGRAFICA)
@Title("Anagrafica Clienti ADP")
public class AnagraficaUI extends SmdHeader {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7884064928998716106L;

	@Autowired
	AnagraficaDao anagraficaDao;
        @Autowired
        PubblicazioneDao pubblicazioneDao;
        @Autowired
        AnagraficaPubblicazioneDao anagraficaPubblicazioneDao;
	
	@Override
	protected void init(VaadinRequest request) {
            super.init(request,"Anagrafica Clienti");
		Assert.notNull(anagraficaDao, "anagraficaDao must be not null");
                Assert.notNull(anagraficaPubblicazioneDao, "anagraficaPubblicazioneDao must be not null");
                AnagraficaSearch anasrc = new AnagraficaSearch(anagraficaDao);
		AnagraficaEditor anaedt = new AnagraficaEditor(anagraficaDao);
		AnagraficaPubblicazioneSearch anapubsrc = new AnagraficaPubblicazioneSearch(anagraficaPubblicazioneDao);
                AnagraficaPubblicazioneEditor anapubedt = new AnagraficaPubblicazioneEditor(anagraficaPubblicazioneDao, pubblicazioneDao, anagraficaDao);
		addComponents(anapubedt,anaedt,anapubsrc,anasrc);
		

		anaedt.setWidth("100%");
                anasrc.setWidth("120%");
                anapubedt.setWidth("100%");
                anapubsrc.setWidth("120%");

		anaedt.setChangeHandler(() -> {
                    anasrc.list();
                    anaedt.setVisible(false);
                    anasrc.setVisible(true);
                    anapubedt.setVisible(false);
                    anapubsrc.setVisible(false);
                    showHeader();
		});
		
                anasrc.setChangeHandler(() -> {
                    anaedt.edit(anasrc.getAnagrafica());
                    anaedt.setVisible(true);
                    anasrc.setVisible(false);
                    anapubedt.setVisible(false);
                    anapubsrc.setVisible(true);
                    hideHeader();
               });
                
                anapubedt.setChangeHandler(() -> {
                    anapubsrc.list(anasrc.getAnagrafica());
                    anaedt.setVisible(true);
                    anasrc.setVisible(false);
                    anapubedt.setVisible(false);
                    anapubsrc.setVisible(true);
                    hideHeader();
                });
                
                anapubsrc.setChangeHandler(() -> {
                    anaedt.setVisible(true);
                    anasrc.setVisible(false);
                    hideHeader();
                });


	}

}
