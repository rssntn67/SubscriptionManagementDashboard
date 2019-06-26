package it.arsinfo.smd.ui.vaadin;

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
import it.arsinfo.smd.entity.EstrattoConto;
import it.arsinfo.smd.repository.AbbonamentoDao;
import it.arsinfo.smd.repository.AnagraficaDao;
import it.arsinfo.smd.repository.CampagnaDao;
import it.arsinfo.smd.repository.PubblicazioneDao;
import it.arsinfo.smd.repository.EstrattoContoDao;

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
        AbbonamentoGrid grid = new AbbonamentoGrid("Abbonamenti");
        AbbonamentoEditor editor = new AbbonamentoEditor(abbonamentoDao,anagrafica,campagne) {
            @Override
            public void save() {                
                if (get().getId() == null && get().getAnno() == null) {
                    Notification.show("Selezionare Anno Prima di Salvare", Notification.Type.ERROR_MESSAGE);
                    return;
                }
                if (get().getId() == null && get().getAnno().getAnno() < Smd.getAnnoCorrente().getAnno()) {
                    Notification.show("Anno deve essere anno corrente o successivi", Notification.Type.ERROR_MESSAGE);
                    return;
                }
                if (get().getId() == null && get().getEstrattiConto().isEmpty()) {
                    Notification.show("Aggiungere Estratto Conto Prima di Salvare", Notification.Type.WARNING_MESSAGE);
                    return;
                }
                super.save();
            }
        };

        EstrattoContoAdd estrattoContoAdd = new EstrattoContoAdd("Aggiungi EC");
        EstrattoContoGrid estrattoContoGrid = new EstrattoContoGrid("Estratti Conto");
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
                Smd.calcoloImportoEC(get());
                get().setAbbonamento(editor.get());
                editor.get().addEstrattoConto(get());
                onChange();
            };
            
            @Override 
            public void delete() {
                editor.get().deleteEstrattoConto(get());
                onChange();
            };
        };

        addSmdComponents(estrattoContoEditor,editor,estrattoContoAdd,estrattoContoGrid, add,search, grid);

        editor.setVisible(false);
        estrattoContoEditor.setVisible(false);
        estrattoContoAdd.setVisible(false);
        estrattoContoGrid.setVisible(false);
        
        add.setChangeHandler(() -> {
            setHeader("Abbonamento:Nuovo");
            hideMenu();
            add.setVisible(false);
            search.setVisible(false);
            grid.setVisible(false);
            editor.edit(add.generate());
            estrattoContoAdd.setAbbonamento(editor.get());
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
            estrattoContoAdd.setVisible(false);
            estrattoContoEditor.setVisible(false);
            estrattoContoGrid.populate(estrattoContoDao.findByAbbonamento(grid.getSelected()));;
        });

        editor.setChangeHandler(() -> {
            editor.setVisible(false);
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
            setHeader(String.format("%s:Spedizione:Nuova",editor.get().getHeader()));
            hideMenu();
            estrattoContoEditor.edit(estrattoContoAdd.generate());
            editor.setVisible(false);
            estrattoContoAdd.setVisible(false);
        });
        
        estrattoContoEditor.setChangeHandler(() -> {
            setHeader("Abbonamento:Nuovo");
            estrattoContoAdd.setVisible(editor.get().getId() == null);
            estrattoContoEditor.setVisible(false);
            editor.edit(estrattoContoEditor.get().getAbbonamento());
            estrattoContoGrid.populate(editor.get().getEstrattiConto());
        });
        
        estrattoContoGrid.setChangeHandler(() -> {
            if (estrattoContoGrid.getSelected() == null) {
                return;
            }
            if (editor.get().getId() == null) {
                setHeader(estrattoContoGrid.getSelected().getHeader());
                estrattoContoEditor.edit(estrattoContoGrid.getSelected());
                add.setVisible(false);
                search.setVisible(false);
                editor.setVisible(false);
                estrattoContoAdd.setVisible(false);
            }
        });

        grid.populate(search.findAll());

    }

    public List<EstrattoConto> findByAbbonamaneto(Abbonamento abbonamento) {
        return estrattoContoDao.findByAbbonamento(abbonamento);
    }
}
