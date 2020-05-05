package it.arsinfo.smd.ui.vaadin;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Title;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

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
import it.arsinfo.smd.service.SmdService;

@SpringUI(path = SmdUI.URL_ABBONAMENTI)
@Title("Abbonamenti ADP")
public class AbbonamentoUI extends SmdUI {

    /**
     * 
     */
    private static final long serialVersionUID = 3429323584726379968L;

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
        OperazioneIncassoVersamentoGrid versamentoGrid = new OperazioneIncassoVersamentoGrid("Operazioni su Versamenti Associate");
        EstrattoContoGrid estrattoContoGrid = new EstrattoContoGrid("Estratti Conto");
        AbbonamentoSearch search = new AbbonamentoSearch(abbonamentoDao,estrattoContoDao,pubblicazioni,anagrafica,campagne);
        AbbonamentoGrid grid = new AbbonamentoGrid("Abbonamenti");
        AbbonamentoEditor editor = new AbbonamentoEditor(abbonamentoDao,anagrafica,campagne) {
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
                    smdService.genera(get(), getEstrattiConto().toArray(new EstrattoConto[getEstrattiConto().size()]));
                    onChange();
                } catch (Exception e) {
                    Notification.show("Non è possibile salvare questo recordo è utilizzato da altri elementi.",
                                      Notification.Type.ERROR_MESSAGE);
                }
            }
        };

        SmdButtonTextField incassa = new SmdButtonTextField("Inserisci Importo da Incassare","Incassa", VaadinIcons.CASH);
        
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
                        Notification.show(e.getMessage(),Notification.Type.ERROR_MESSAGE);
                        return;
                    }
                }
                try {
                    smdService.aggiorna(get());
                    onChange();
                } catch (Exception e) {
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
                    Notification.show(e.getMessage(),Notification.Type.WARNING_MESSAGE);
                }
                
            };
        };

        addSmdComponents(estrattoContoEditor,editor);
        HorizontalLayout lay = new HorizontalLayout(estrattoContoAdd.getComponents());
        lay.addComponents(new Label("     "));
        lay.addComponents(incassa.getComponents());
        addComponents(lay);
        addSmdComponents(versamentoGrid,estrattoContoGrid, add,search, grid);

        editor.setVisible(false);
        incassa.setVisible(false);
        estrattoContoEditor.setVisible(false);
        estrattoContoAdd.setVisible(false);
        versamentoGrid.setVisible(false);
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
            versamentoGrid.populate(smdService.getAssociati(editor.get()));
            estrattoContoGrid.populate(estrattoContoDao.findByAbbonamento(
                    grid.getSelected()));               
        });

        editor.setChangeHandler(() -> {
            editor.setVisible(false);
            incassa.setVisible(false);
            estrattoContoAdd.setVisible(false);
            estrattoContoEditor.setVisible(false);
            estrattoContoGrid.setVisible(false);
            versamentoGrid.setVisible(false);
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
            versamentoGrid.setVisible(false);
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
                estrattoContoGrid.populate(estrattoContoDao.findByAbbonamento(
                        editor.get()));
            }
            versamentoGrid.populate(smdService.getAssociati(editor.get()));
            estrattoContoAdd.setVisible(true);
        });
        
        versamentoGrid.setChangeHandler(() -> {
        	
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
        	if (incassa.getValue() == null) {
                Notification.show("Devi inserire l'importo da incassare",
                        Notification.Type.ERROR_MESSAGE);
                return;
        	}
            try {
            	new BigDecimal(incassa.getValue());
            } catch (Exception e) {
                Notification.show("Errore di conversione del valore dell'incasso: " +incassa.getValue(),
                                  Notification.Type.ERROR_MESSAGE);
                return;
            }
        	if (editor.get().getDataContabile() == null) {
                Notification.show("Devi inserire la data contabile",
                        Notification.Type.ERROR_MESSAGE);
                return;
        	}
        	if (editor.get().getDataPagamento() == null) {
                Notification.show("Devi inserire la data pagamento",
                        Notification.Type.ERROR_MESSAGE);
                return;
        	}
        	if (editor.get().getProgressivo() == null) {
                Notification.show("Aggiungere Riferimento nel Campo Progressivo",
                        Notification.Type.ERROR_MESSAGE);
                return;
        	}
            try {
                smdService.incassa(editor.get(), new BigDecimal(incassa.getValue()),getLoggedInUser());
                incassa.setVisible(false);
                editor.edit(editor.get());
                versamentoGrid.populate(smdService.getAssociati(editor.get()));
            } catch (Exception e) {
                Notification.show(e.getMessage(),
                                  Notification.Type.ERROR_MESSAGE);
            }
        });

        grid.populate(search.findAll());

    }

}
