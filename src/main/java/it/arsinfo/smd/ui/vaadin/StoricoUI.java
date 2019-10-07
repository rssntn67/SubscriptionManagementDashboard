package it.arsinfo.smd.ui.vaadin;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Title;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.themes.ValoTheme;

import it.arsinfo.smd.SmdService;
import it.arsinfo.smd.data.StatoStorico;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Nota;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.repository.AnagraficaDao;
import it.arsinfo.smd.repository.NotaDao;
import it.arsinfo.smd.repository.PubblicazioneDao;
import it.arsinfo.smd.repository.StoricoDao;

@SpringUI(path = SmdUI.URL_STORICO)
@Title("Storico Anagrafica Pubblicazioni ADP")
@Push
public class StoricoUI extends SmdUI {

    /**
     * 
     */
    private static final long serialVersionUID = 7884064928998716106L;

    private static final Logger log = LoggerFactory.getLogger(StoricoUI.class);

    @Autowired
    PubblicazioneDao pubblicazioneDao;

    @Autowired
    AnagraficaDao anagraficaDao;

    @Autowired
    StoricoDao storicoDao;

    @Autowired
    NotaDao notaDao;

    @Autowired
    SmdService smdService;

    @Override
    protected void init(VaadinRequest request) {
        super.init(request, "Storico");
        List<Anagrafica> anagrafica = anagraficaDao.findAll();
        List<Pubblicazione> pubblicazioni = pubblicazioneDao.findAll();
        StoricoAdd add = new StoricoAdd("Aggiungi Storico");
        StoricoSearch search = new StoricoSearch(storicoDao,anagrafica,pubblicazioni);
        StoricoGrid grid = new StoricoGrid("Storico");
        
        StoricoEditor editor = 
                new StoricoEditor(
                                  storicoDao, 
                                  pubblicazioni, 
                                  anagrafica) {
            @Override
            public void save() {
                if (getIntestatario().isEmpty()) {
                    Notification.show("Intestatario deve essere valorizzato", Type.WARNING_MESSAGE);
                    return;                    
                }
                if (getDestinatario().isEmpty()) {
                    Notification.show("Destinatario deve essere valorizzato", Type.WARNING_MESSAGE);
                    return;                    
                }
                if (getPubblicazione().isEmpty()) {
                    Notification.show("Pubblicazione deve essere valorizzata", Type.WARNING_MESSAGE);
                    return;
                }
                Nota nota = new Nota(get());
                nota.setOperatore(getLoggedInUser().getUsername());
                if (get().getId() == null) {
                    nota.setDescription("Nuovo: " + get().toString());
                } else {
                    nota.setDescription("Aggiornato: " + get().toString());                    
                }
                try {
                    smdService.save(get(), nota);
                    log.info("save: {}" + get());
                    if (!getNota().isEmpty()) {
                        Nota unota = new Nota(get());
                        unota.setOperatore(getLoggedInUser().getUsername());
                        unota.setDescription(getNota().getValue());
                        notaDao.save(unota);
                        getNota().clear();
                    }
                    onChange();
                } catch (Exception e) {
                    log.warn("save failed for : {} ", get(),e);
                    Notification.show("Non è possibile salvare questo record: ",
                                      Notification.Type.ERROR_MESSAGE);
                    return;
                }
                                
            }
            
            @Override
            public void delete() {
                smdService.delete(get());
            }
        };
        
        NotaGrid notaGrid = new NotaGrid("Note");
        notaGrid.getGrid().setColumns("operatore","data","description");
        notaGrid.getGrid().setHeight("200px");

        SmdButton update = new SmdButton("Aggiorna Abbonamento Campagna ", VaadinIcons.ARCHIVES);
        update.getButton().addStyleName(ValoTheme.BUTTON_PRIMARY);

        addSmdComponents(add,update,editor,notaGrid,search, grid);
        editor.setVisible(false);
        notaGrid.setVisible(false);
        update.setVisible(false);
        
        search.setChangeHandler(()-> {
            grid.populate(search.find());
        });
        
        grid.setChangeHandler(() -> {
            if (grid.getSelected() == null) {
                return;
            }
            hideMenu();
            search.setVisible(false);
            editor.edit(grid.getSelected());
            update.setVisible(true);
            notaGrid.populate(notaDao.findByStorico(grid.getSelected()));
            setHeader(grid.getSelected().getHeader());
            add.setVisible(false);
        });

        editor.setChangeHandler(() -> {
            grid.populate(search.find());
            showMenu();
            search.setVisible(true);
            setHeader("Storico");
            editor.setVisible(false);
            notaGrid.setVisible(false);
            update.setVisible(false);
            add.setVisible(true);
        });

        add.setChangeHandler(() -> {
            editor.edit(add.generate());
            setHeader(String.format("Storico:Nuovo"));
            add.setVisible(false);
        });

        add.getButton().addClickListener(event ->
            search.setVisible(false));
        
        notaGrid.setChangeHandler(() -> {});

        update.setChangeHandler(() -> {
            if (editor.get().getStatoStorico() == StatoStorico.Sospeso) {
                Notification.show("Abbonamento non aggiornato storico Sospeso:" , Type.WARNING_MESSAGE);
                return;                                    
            }
            try {
                smdService.aggiornaAbbonamentoDaStorico(editor.get());
            } catch (Exception e) {
                log.warn("update failed for :" + editor.get().toString() +". Error log: " + e.getMessage(),e);
                Notification.show("Abbonamento non aggiornato:" + e.getMessage(), Type.ERROR_MESSAGE);
                return;                    
            }
            grid.populate(search.find());
            showMenu();
            search.setVisible(true);
            setHeader("Storico");
            editor.setVisible(false);
            notaGrid.setVisible(false);
            update.setVisible(false);
            add.setVisible(true);
        });

        grid.populate(search.find());

    }
}
