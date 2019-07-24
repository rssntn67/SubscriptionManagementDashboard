package it.arsinfo.smd.ui.vaadin;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Title;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;

import it.arsinfo.smd.SmdService;
import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.StatoAbbonamento;
import it.arsinfo.smd.data.StatoCampagna;
import it.arsinfo.smd.data.TipoPubblicazione;
import it.arsinfo.smd.entity.CampagnaItem;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.repository.AbbonamentoDao;
import it.arsinfo.smd.repository.CampagnaDao;
import it.arsinfo.smd.repository.CampagnaItemDao;
import it.arsinfo.smd.repository.PubblicazioneDao;

@SpringUI(path = SmdUI.URL_CAMPAGNA)
@Title("Campagna Abbonamenti ADP")
public class CampagnaUI extends SmdUI {

    /**
     * 
     */
    private static final long serialVersionUID = 7884064928998716106L;

    @Autowired
    private CampagnaDao campagnaDao;

    @Autowired
    private CampagnaItemDao campagnaItemDao;

    @Autowired
    private PubblicazioneDao pubblicazioneDao;

    @Autowired
    private SmdService smdService;
    
    @Autowired
    private AbbonamentoDao abbonamentoDao; 

    @Override
    protected void init(VaadinRequest request) {
        super.init(request, "Campagna");
        List<Pubblicazione> attivi = pubblicazioneDao.findAll().stream().filter(p -> p.isActive()
                                                                                && p.getTipo() != TipoPubblicazione.UNICO).collect(Collectors.toList());
        CampagnaItemsEditor campagnaItemEditor = new CampagnaItemsEditor(attivi);
        CampagnaAdd add = new CampagnaAdd("Genera una nuova Campagna");
        CampagnaSearch search = new CampagnaSearch(campagnaDao);
        CampagnaGrid grid = new CampagnaGrid("Campagne");
        CampagnaEditor editor = new CampagnaEditor(campagnaDao) {
            @Override
            public void delete() {
                if (get().getStatoCampagna() == StatoCampagna.Inviata) {
                    Notification.show("Non è possibile cancellare campagna che è stata inviata",
                                      Notification.Type.ERROR_MESSAGE);
                    return;
                }
                if (get().getAnno() == Anno.getAnnoCorrente()) {
                    Notification.show("Non è possibile cancellare campagna dell'anno corrente",
                                      Notification.Type.ERROR_MESSAGE);
                    return;

                }
                if (get().getAnno() == Anno.getAnnoPassato()) {
                    Notification.show("Non è possibile cancellare campagna dell'anno passato",
                                      Notification.Type.ERROR_MESSAGE);
                    return;
                }
                try {
                    smdService.deleteCampagnaAbbonamenti(get());
                } catch (Exception e) {
                    Notification.show("Non è possibile cancellare campagna:"+e.getMessage(),
                                      Notification.Type.ERROR_MESSAGE);
                }
                onChange();
            }

            @Override
            public void save() {
                if (get().getId() != null) {
                    Notification.show("Impossibile Rigenerare Campagna",
                                      Notification.Type.WARNING_MESSAGE);
                    return;
                }
                if (get().getAnno() == null) {
                    Notification.show("Selezionare Anno Prima di Salvare",
                                      Notification.Type.WARNING_MESSAGE);
                    return;
                }
                if (campagnaDao.findByAnno(get().getAnno()) != null) {
                    Notification.show("E' stata già generata la Campagna per Anno "
                            + get().getAnno()
                            + ". Solo una Campagna per Anno",
                                      Notification.Type.WARNING_MESSAGE);
                    return;
                }
                if (get().getAnno().getAnno() < Anno.getAnnoCorrente().getAnno()) {
                    Notification.show("Anno deve essere almeno anno successivo",
                                      Notification.Type.WARNING_MESSAGE);
                    return;
                }
                try {
                    smdService.generaCampagnaAbbonamenti(get(), attivi);
                } catch (Exception e) {
                    Notification.show("Non è possibile generare campagna:"+e.getMessage(),
                                      Notification.Type.ERROR_MESSAGE);
                    return;
                }
                onChange();
            }

        };
        AbbonamentoGrid abbonamentoGrid = new AbbonamentoGrid("Abbonamenti Associati");

        addSmdComponents(campagnaItemEditor, editor, abbonamentoGrid, add,
                         search, grid);
        editor.setVisible(false);
        abbonamentoGrid.setVisible(false);
        add.setChangeHandler(() -> {
            setHeader(String.format("Campagna:Add"));
            hideMenu();
            add.setVisible(false);
            search.setVisible(false);
            grid.setVisible(false);
            editor.edit(add.generate());
            campagnaItemEditor.edit(attivi.stream().map(p -> {
                CampagnaItem ci = new CampagnaItem();
                ci.setPubblicazione(p);
                ci.setCampagna(editor.get());
                return ci;
            }).collect(Collectors.toList()), false);
        });

        search.setChangeHandler(() -> grid.populate(search.find()));
        grid.setChangeHandler(() -> {
            if (grid.getSelected() == null) {
                return;
            }
            setHeader("Campagna::Edit");
            hideMenu();
            add.setVisible(false);
            search.setVisible(false);
            grid.setVisible(false);
            editor.edit(grid.getSelected());
            campagnaItemEditor.edit(campagnaItemDao.findByCampagna(grid.getSelected()),
                                    true);
            abbonamentoGrid.populate(abbonamentoDao.findByCampagna(grid.getSelected()));
        });

        abbonamentoGrid.setChangeHandler(() -> {
            abbonamentoGrid.setVisible(false);
            add.setVisible(true);
            search.setVisible(true);
            grid.populate(search.find());
        });

        editor.setChangeHandler(() -> {
            editor.setVisible(false);
            campagnaItemEditor.setVisible(false);
            abbonamentoGrid.setVisible(false);
            setHeader("Campagna");
            showMenu();
            add.setVisible(true);
            search.setVisible(true);
            grid.populate(search.find());
        });

        campagnaItemEditor.setChangeHandler(() -> {
        });

        grid.addComponentColumn(campagna -> {
            Button button = new Button("Invia Proposta Abbonamento Ccp", VaadinIcons.ENVELOPES);
            button.setEnabled(campagna.getStatoCampagna() == StatoCampagna.Generata);
            button.addClickListener(click -> {
                try {
                    smdService.inviaCampagna(campagna);
                } catch (Exception e) {
                    Notification.show("Non è possibile inviare campagna:"+e.getMessage(),
                                      Notification.Type.ERROR_MESSAGE);
                    return;
                }
                setHeader("Campagna::Ccp");
                add.setVisible(false);
                search.setVisible(false);
                editor.edit(campagna);
                grid.setVisible(false);
            });
            return button;
        });

        
        grid.addComponentColumn(campagna -> {
            Button button = new Button("Visualizza Proposta Abbonamento Inviata", VaadinIcons.ENVELOPES);
            button.setEnabled(campagna.getStatoCampagna() == StatoCampagna.Inviata);
            button.addClickListener(click -> {
                setHeader("Campagna::Ccp");
                add.setVisible(false);
                search.setVisible(false);
                editor.edit(campagna);
                abbonamentoGrid
                .populate(
                  abbonamentoDao.findByCampagna(campagna)
                      .stream()
                      .filter(a -> a.getTotale().signum() > 0)
                      .collect(Collectors.toList()));
                grid.setVisible(false);
            });
            return button;
        });

        grid.addComponentColumn(campagna -> {
            Button button = new Button("Invia Estratto Conto", VaadinIcons.ENVELOPES);
            button.setEnabled(campagna.getStatoCampagna() == StatoCampagna.Inviata);
            button.addClickListener(click -> {
                try {
                    smdService.inviaEstrattoConto(campagna);
                } catch (Exception e) {
                    Notification.show("Non è possibile inviare Estratto Conto campagna:"+e.getMessage(),
                                      Notification.Type.ERROR_MESSAGE);
                    return;
                }
                setHeader("Campagna::EC");
                add.setVisible(false);
                search.setVisible(false);
                grid.setVisible(false);
                editor.edit(campagna);
            });
            return button;
        });

        grid.addComponentColumn(campagna -> {
            Button button = new Button("Visualizza Estratto Conto", VaadinIcons.ENVELOPES);
            button.setEnabled(campagna.getStatoCampagna() == StatoCampagna.Chiusa);
            button.addClickListener(click -> {
                setHeader("Campagna::Ccp");
                add.setVisible(false);
                search.setVisible(false);
                editor.edit(campagna);
                abbonamentoGrid
                .populate(
                  abbonamentoDao.findByCampagna(campagna)
                      .stream()
                      .filter(a -> a.getStatoAbbonamento() == StatoAbbonamento.InviatoEC)
                      .collect(Collectors.toList()));
                grid.setVisible(false);
            });
            return button;
        });

        grid.populate(search.findAll());

    }

}
