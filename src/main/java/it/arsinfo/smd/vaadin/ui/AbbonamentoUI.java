package it.arsinfo.smd.vaadin.ui;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Notification;

import it.arsinfo.smd.Smd;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Campagna;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.entity.Spedizione;
import it.arsinfo.smd.repository.AbbonamentoDao;
import it.arsinfo.smd.repository.AnagraficaDao;
import it.arsinfo.smd.repository.CampagnaDao;
import it.arsinfo.smd.repository.PubblicazioneDao;
import it.arsinfo.smd.repository.SpedizioneDao;
import it.arsinfo.smd.vaadin.model.SmdUI;

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
    SpedizioneDao spedizioneDao;

    @Autowired
    AnagraficaDao anagraficaDao;

    @Autowired
    PubblicazioneDao pubblicazioneDao;

    @Autowired
    CampagnaDao campagnaDao;

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
        AbbonamentoSearch search = new AbbonamentoSearch(abbonamentoDao,anagrafica,campagne);
        AbbonamentoGrid grid = new AbbonamentoGrid("");
        AbbonamentoEditor editor = new AbbonamentoEditor(abbonamentoDao,anagrafica,campagne) {
            @Override
            public void save() {                
                if (get().getId() == null && get().getInizio() == null ) {
                    Notification.show("Selezionare Mese Inizio Prima di Salvare", Notification.Type.ERROR_MESSAGE);
                    return;
                }
                if (get().getId() == null && get().getFine() == null ) {
                    Notification.show("Selezionare Mese Fine Prima di Salvare", Notification.Type.ERROR_MESSAGE);
                    return;
                }
                if (get().getId() == null && get().getAnno() == null) {
                    Notification.show("Selezionare Anno Prima di Salvare", Notification.Type.ERROR_MESSAGE);
                    return;
                }
                if (get().getId() == null && get().getAnno().getAnno() < Smd.getAnnoCorrente().getAnno()) {
                    Notification.show("Anno deve essere anno corrente o successivi", Notification.Type.ERROR_MESSAGE);
                    return;
                }
                if (get().getId() == null  && get().getInizio().getPosizione() > get().getFine().getPosizione()) {
                    Notification.show("Anno corrente: il Mese Inizio deve essere il corrente o successivo", Notification.Type.ERROR_MESSAGE);
                    return;
                }
                if (get().getId() == null && get().getAnno().getAnno() == Smd.getAnnoCorrente().getAnno() && get().getInizio().getPosizione() < Smd.getMeseCorrente().getPosizione()) {
                    Notification.show("Anno corrente: il Mese Inizio deve essere il corrente o successivo", Notification.Type.ERROR_MESSAGE);
                    return;
                }
                if (get().getId() == null && get().getSpedizioni().isEmpty()) {
                    Notification.show("Aggiungere Spedizione Prima di Salvare", Notification.Type.WARNING_MESSAGE);
                    return;
                }
                if (get().getId() == null) {
                    Smd.calcoloAbbonamento((get()));
                }
                super.save();
            }
        };

        SpedizioneAdd spedizioneAdd = new SpedizioneAdd("Aggiungi spedizione");
        SpedizioneGrid spedizioneGrid = new SpedizioneGrid("Spedizioni");
        SpedizioneEditor spedizioneEditor = new SpedizioneEditor(spedizioneDao, pubblicazioni, anagrafica) {
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
                editor.get().addSpedizione(get());
                Smd.calcoloAbbonamento(editor.get());
                onChange();
            };
            
            @Override 
            public void delete() {
                editor.get().deleteSpedizione(get());
                Smd.calcoloAbbonamento(editor.get());
                onChange();
            };
        };

        addSmdComponents(spedizioneEditor,editor,spedizioneAdd,spedizioneGrid, add,search, grid);

        editor.setVisible(false);
        spedizioneEditor.setVisible(false);
        spedizioneAdd.setVisible(false);
        spedizioneGrid.setVisible(false);
        
        add.setChangeHandler(() -> {
            setHeader("Abbonamento:Nuovo");
            hideMenu();
            add.setVisible(false);
            search.setVisible(false);
            grid.setVisible(false);
            editor.edit(add.generate());
            spedizioneAdd.setAbbonamento(editor.get());
            spedizioneAdd.setVisible(true);
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
            spedizioneAdd.setVisible(false);
            spedizioneEditor.setVisible(false);
            spedizioneGrid.populate(spedizioneDao.findByAbbonamento(grid.getSelected()));;
        });

        editor.setChangeHandler(() -> {
            editor.setVisible(false);
            spedizioneAdd.setVisible(false);
            spedizioneEditor.setVisible(false);
            spedizioneGrid.setVisible(false);
            setHeader("Abbonamento");
            showMenu();
            add.setVisible(true);
            search.setVisible(true);
            grid.populate(search.find());
        });
        
        spedizioneAdd.setChangeHandler(() -> {
            setHeader(String.format("%s:Spedizione:Nuova",editor.get().getHeader()));
            hideMenu();
            spedizioneEditor.edit(spedizioneAdd.generate());
            editor.setVisible(false);
            spedizioneAdd.setVisible(false);
        });
        
        spedizioneEditor.setChangeHandler(() -> {
            setHeader("Abbonamento:Nuovo");
            spedizioneAdd.setVisible(editor.get().getId() == null);
            spedizioneEditor.setVisible(false);
            Smd.calcoloAbbonamento(editor.get());
            editor.edit(spedizioneEditor.get().getAbbonamento());
            spedizioneGrid.populate(editor.get().getSpedizioni());
        });
        
        spedizioneGrid.setChangeHandler(() -> {
            if (spedizioneGrid.getSelected() == null) {
                return;
            }
            if (editor.get().getId() == null) {
                setHeader(spedizioneGrid.getSelected().getHeader());
                spedizioneEditor.edit(spedizioneGrid.getSelected());
                add.setVisible(false);
                search.setVisible(false);
                editor.setVisible(false);
                spedizioneAdd.setVisible(false);
            }
        });

        grid.populate(search.findAll());

    }

    public List<Spedizione> findByAbbonamaneto(Abbonamento abbonamento) {
        return spedizioneDao.findByAbbonamento(abbonamento);
    }
}
