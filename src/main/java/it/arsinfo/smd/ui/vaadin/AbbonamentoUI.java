package it.arsinfo.smd.ui.vaadin;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Title;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

import it.arsinfo.smd.SmdService;
import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.StatoAbbonamento;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Campagna;
import it.arsinfo.smd.entity.EstrattoConto;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.repository.AbbonamentoDao;
import it.arsinfo.smd.repository.AnagraficaDao;
import it.arsinfo.smd.repository.CampagnaDao;
import it.arsinfo.smd.repository.EstrattoContoDao;
import it.arsinfo.smd.repository.PubblicazioneDao;
import it.arsinfo.smd.repository.VersamentoDao;

@SpringUI(path = SmdUI.URL_ABBONAMENTI)
@Title("Abbonamenti ADP")
public class AbbonamentoUI extends SmdUI {

    /**
     * 
     */
    private static final long serialVersionUID = 3429323584726379968L;

    private static final Logger log = LoggerFactory.getLogger(AbbonamentoUI.class);

    @Autowired
    AbbonamentoDao abbonamentoDao;

    @Autowired
    EstrattoContoDao estrattoContoDao;

    @Autowired
    AnagraficaDao anagraficaDao;

    @Autowired
    PubblicazioneDao pubblicazioneDao;

    @Autowired
    CampagnaDao campagnaDao;

    @Autowired
    SmdService smdService;

    @Autowired
    VersamentoDao versamentoDao;
    

    @Override
    protected void init(VaadinRequest request) {
        super.init(request, "Abbonamento");

        
        List<Anagrafica> anagrafica = anagraficaDao.findAll();
        List<Pubblicazione> pubblicazioni = pubblicazioneDao.findAll();
        List<Campagna> campagne = campagnaDao.findAll();
        AbbonamentoAdd add = new AbbonamentoAdd("Aggiungi abbonamento");
        if (anagrafica.size() == 0) {
            add.setVisible(false);
        } else {
            add.setPrimoIntestatario(anagrafica.iterator().next());
        }
        EstrattoContoGrid estrattoContoGrid = new EstrattoContoGrid("Estratti Conto");
        AbbonamentoSearch search = new AbbonamentoSearch(abbonamentoDao,estrattoContoDao,pubblicazioni,anagrafica,campagne);
        AbbonamentoGrid grid = new AbbonamentoGrid("Abbonamenti");
        AbbonamentoEditor editor = new AbbonamentoEditor(versamentoDao,abbonamentoDao,anagrafica,campagne) {
            @Override
            public void delete() {
                if (get().getId() == null) {
                    Notification.show("Abbonamento non Salvato", Notification.Type.ERROR_MESSAGE);
                    return;
                }
                if (get().getCampagna() != null) {
                    Notification.show("Abbonamento associato a Campagna va gestito da Storico", Notification.Type.ERROR_MESSAGE);
                    return;
                }
                if (get().getStatoAbbonamento() != StatoAbbonamento.Nuovo) {
                    Notification.show("Stato Abbonamento diverso da Nuovo va gestito da Campagna", Notification.Type.ERROR_MESSAGE);
                    return;
                }
                try {
                    smdService.delete(get());
                    onChange();
                } catch (Exception e) {
                    log.error("save failed {} : {}", get(), e.getMessage(),e);
                    Notification.show("Abbonamento non eliminato:" + e.getMessage(), Type.ERROR_MESSAGE);
                    return;                    
                }
            }
            
            @Override
            public void save() {                
                if (get().getId() == null && get().getAnno() == null) {
                    Notification.show("Selezionare Anno Prima di Salvare", Notification.Type.ERROR_MESSAGE);
                    return;
                }
                if (get().getId() == null && get().getAnno().getAnno() < Anno.getAnnoCorrente().getAnno()) {
                    Notification.show("Anno deve essere anno corrente o successivi", Notification.Type.ERROR_MESSAGE);
                    return;
                }
                if (get().getId() == null && getEstrattiConto().size() == 0) {
                    Notification.show("Aggiungere Estratto Conto Prima di Salvare", Notification.Type.ERROR_MESSAGE);
                    return;
                }
                if (get().getId() == null) {
                    get().setCodeLine(Abbonamento.generaCodeLine(get().getAnno()));
                }
                if (get().getId() != null ) {
                    super.save();
                    return;
                }
                try {
                    abbonamentoDao.save(get());
                    smdService.genera(get(), getEstrattiConto().toArray(new EstrattoConto[getEstrattiConto().size()]));
                } catch (Exception e) {
                    log.warn("save failed {} : {}",get(),e.getMessage(),e);
                    Notification.show("Non è possibile salvare questo recordo è utilizzato da altri elementi.",
                                      Notification.Type.ERROR_MESSAGE);
                }
                onChange();
            }
        };

        SmdButton incassa = new SmdButton("Incassa", VaadinIcons.CASH);
        EstrattoContoAdd estrattoContoAdd = new EstrattoContoAdd("Aggiungi EC");
        EstrattoContoEditor estrattoContoEditor = new EstrattoContoEditor(estrattoContoDao, pubblicazioni, anagrafica) {
            @Override
            public void save() {
                if (get().getDestinatario() == null) {
                    Notification.show("Selezionare il Destinatario",Notification.Type.WARNING_MESSAGE);
                    return;
                }
                if (get().getPubblicazione() == null) {
                    Notification.show("Selezionare la Pubblicazione",Notification.Type.WARNING_MESSAGE);
                    return;
                }
                get().setAbbonamento(editor.get());
                if (get().getId() == null && editor.get().getId() == null) {
                    editor.addEstrattoConto(get());
                    onChange();
                    return;
                }
                if (get().getId() == null ) {
                    editor.addEstrattoConto(get());
                    try {
                      smdService.genera(editor.get(), get());  
                      onChange();
                      return;
                    } catch (Exception e) {
                        log.error("save failed {} : {}", get(), e.getMessage(),e);
                        Notification.show(e.getMessage(),Notification.Type.ERROR_MESSAGE);
                        return;
                    }
                }
                try {
                    smdService.aggiorna(get());
                    onChange();
                } catch (Exception e) {
                    log.error("save failed {} : {}", get(), e.getMessage(),e);
                    Notification.show(e.getMessage(),Notification.Type.ERROR_MESSAGE);
                    return;
                }
            };
            
            @Override 
            public void delete() {
                if (get().getId() == null ) {
                    if (!editor.remove(get())) {
                        Notification.show("Non posso rimuovere EC",Notification.Type.WARNING_MESSAGE);
                        return;
                    }
                    onChange();
                    return;
                }
                try {
                    smdService.rimuovi(get());
                    onChange();
                } catch (Exception e) {
                    log.error("save failed {} : {}", get(), e.getMessage(),e);
                    Notification.show(e.getMessage(),Notification.Type.WARNING_MESSAGE);
                }
                
            };
        };

        addSmdComponents(estrattoContoEditor,editor,incassa,estrattoContoAdd,estrattoContoGrid, add,search, grid);

        editor.setVisible(false);
        incassa.setVisible(false);
        estrattoContoEditor.setVisible(false);
        estrattoContoAdd.setVisible(false);
        estrattoContoGrid.setVisible(false);
        
        add.setChangeHandler(() -> {
            setHeader("Abbonamento:Nuovo");
            hideMenu();
            add.setVisible(false);
            search.setVisible(false);
            grid.setVisible(false);
            incassa.setVisible(false);
            editor.edit(add.generate());
            estrattoContoAdd.setVisible(true);
        });
        
        search.setChangeHandler(() -> grid.populate(search.find()));

        grid.setChangeHandler(() -> {
            if (grid.getSelected() == null) {
                return;
            }
            setHeader(grid.getSelected().getHeader());
            hideMenu();
            add.setVisible(false);
            search.setVisible(false);
            editor.edit(grid.getSelected());
            incassa.setVisible(editor.incassare());
            estrattoContoAdd.setVisible(grid.getSelected().getCampagna() == null);
            estrattoContoEditor.setVisible(false);
            estrattoContoGrid.populate(findByAbbonamento(grid.getSelected()));               
        });

        editor.setChangeHandler(() -> {
            editor.setVisible(false);
            incassa.setVisible(false);
            estrattoContoAdd.setVisible(false);
            estrattoContoEditor.setVisible(false);
            estrattoContoGrid.setVisible(false);
            setHeader("Abbonamento");
            showMenu();
            add.setVisible(true);
            search.setVisible(true);
            grid.populate(search.find());
        });
        
        estrattoContoAdd.setChangeHandler(() -> {
            setHeader(String.format("%s:Estratto Conto:Nuovo",editor.get().getHeader()));
            hideMenu();
            estrattoContoEditor.edit(estrattoContoAdd.generate());
            incassa.setVisible(false);
            editor.setVisible(false);
            estrattoContoAdd.setVisible(false);
        });
        
        estrattoContoEditor.setChangeHandler(() -> {
            setHeader("Abbonamento:Nuovo");
            estrattoContoAdd.setVisible(estrattoContoEditor.get().getStorico() == null);
            estrattoContoEditor.setVisible(false);
            editor.edit(estrattoContoEditor.get().getAbbonamento());
            if (estrattoContoEditor.get().getId() == null) {
                estrattoContoGrid.populate(editor.getEstrattiConto());
            } else {
                estrattoContoGrid.populate(findByAbbonamento(editor.get()));
            }
            
        });
        
        estrattoContoGrid.setChangeHandler(() -> {
            if (estrattoContoGrid.getSelected() == null) {
                return;
            }
            setHeader(estrattoContoGrid.getSelected().getHeader());
            estrattoContoEditor.edit(estrattoContoGrid.getSelected());
            add.setVisible(false);
            search.setVisible(false);
            editor.setVisible(false);
            incassa.setVisible(false);
            estrattoContoAdd.setVisible(false);
        });

        incassa.setChangeHandler(() -> {
            try {
                smdService.incassa(editor.get());
                incassa.setVisible(false);
                editor.edit(editor.get());
            } catch (Exception e) {
                Notification.show(e.getMessage(),
                                  Notification.Type.ERROR_MESSAGE);
            }
        });
        grid.populate(search.findAll());

    }

    public List<EstrattoConto> findByAbbonamento(Abbonamento abbonamento) {
        return estrattoContoDao.findByAbbonamento(
                                                  abbonamento);
    }
}
