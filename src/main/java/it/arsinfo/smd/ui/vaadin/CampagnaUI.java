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
                if (get().getStatoCampagna() == StatoCampagna.Chiusa) {
                    Notification.show("Non è possibile cancellare campagna che è stata chiusa",
                                      Notification.Type.ERROR_MESSAGE);
                    return;
                }
                if (get().getStatoCampagna() == StatoCampagna.InviatoEC) {
                    Notification.show("Non è possibile cancellare campagna che è stata inviato EC",
                                      Notification.Type.ERROR_MESSAGE);
                    return;
                }
                if (get().getStatoCampagna() == StatoCampagna.Inviata) {
                    Notification.show("Non è possibile cancellare campagna che è stata inviata",
                                      Notification.Type.ERROR_MESSAGE);
                    return;
                }
                try {
                    smdService.delete(get());
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
                    smdService.genera(get(), attivi);
                } catch (Exception e) {
                    Notification.show("Non è possibile generare campagna:"+e.getMessage(),
                                      Notification.Type.ERROR_MESSAGE);
                    return;
                }
                onChange();
            }

        };
        AbbonamentoConIndirizzoGrid abbonamentoGrid = new AbbonamentoConIndirizzoGrid("Abbonamenti");
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
            setHeader("Campagna");
            showMenu();
            add.setVisible(true);
            search.setVisible(true);
            grid.populate(search.find());
            abbonamentoGrid.setVisible(false);
        });

        campagnaItemEditor.setChangeHandler(() -> {
        });

        grid.addComponentColumn(campagna -> {
            final Button button = new Button("Visualizza Abbonamenti",VaadinIcons.ENVELOPES);
            button.addClickListener(click -> {
                add.setVisible(false);
                search.setVisible(false);
                editor.edit(campagna);
                grid.setVisible(false);
                abbonamentoGrid.populate(smdService.get(abbonamentoDao.findByAnno(campagna.getAnno())));
                setHeader("Campagna::Abbonamenti");
            });
            return button;
        });
        
        grid.addComponentColumn(campagna -> {
            final Button button = new Button(VaadinIcons.ENVELOPES);
            switch (campagna.getStatoCampagna()) {
            case Generata:
                button.setCaption("Invia Proposta Abbonamento Ccp");
                break;

            case Inviata:
                button.setCaption("Invia Estratto Conto");
                break;                    

            case InviatoEC:
                button.setCaption("Chiudi");
                break;                    

            case Chiusa:
                button.setCaption("Campagna Chiusa");
                button.setEnabled(false);
                break;

            default:
                button.setEnabled(false);
                break;
            }

            button.addClickListener(click -> {
                switch (campagna.getStatoCampagna()) {
                case Generata:
                    try {
                        smdService.invia(campagna);
                    } catch (Exception e) {
                        Notification.show("Non è possibile inviare campagna:"+e.getMessage(),
                                          Notification.Type.ERROR_MESSAGE);
                        return;
                    }
                    break;

                case Inviata:
                    try {
                        smdService.estratto(campagna);
                    } catch (Exception e) {
                        Notification.show("Non è possibile inviare Estratto Conto campagna:"+e.getMessage(),
                                          Notification.Type.ERROR_MESSAGE);
                        return;
                    }
                    break;                    

                case InviatoEC:
                    try {
                        smdService.chiudi(campagna);
                    } catch (Exception e) {
                        Notification.show("Non è possibile chiudere campagna:"+e.getMessage(),
                                          Notification.Type.ERROR_MESSAGE);
                        return;
                    }
                    break;                    

                case Chiusa:
                    break;

                default:
                    break;
                }
                grid.populate(search.find());
            });
            return button;
        });

        grid.addComponentColumn(campagna -> {
            final Button button = new Button("",VaadinIcons.ENVELOPES);
            switch (campagna.getStatoCampagna()) {
            case Generata:
                button.setCaption("Visualizza Abbonamenti Generati");
                break;
            case Inviata:
                button.setCaption("Visualizza Ccp Abbonamenti Inviati");
                break;
            case InviatoEC:
                button.setCaption("Visualizza Abbonamenti Inviato EC");
                break;
            case Chiusa:
                button.setCaption("Visualizza Abbonamenti Annullati");
                break;
            default:
                button.setEnabled(false);
                break;                
            }

            button.addClickListener(click -> {
                add.setVisible(false);
                search.setVisible(false);
                editor.edit(campagna);
                grid.setVisible(false);
                switch (campagna.getStatoCampagna()) {
                case Generata:
                    abbonamentoGrid.populate(smdService.get(abbonamentoDao.findByCampagna(campagna)));
                    setHeader("Campagna::Generata");
                    break;
                case Inviata:
                    abbonamentoGrid
                    .populate(
                    		smdService.get(
            				abbonamentoDao.findByCampagna(campagna)
                          .stream()
                          .filter(a -> a.getTotale().signum() > 0)
                          .collect(Collectors.toList())));
                    setHeader("Campagna::CCP Inviati");
                    break;
                case InviatoEC:
                    abbonamentoGrid
                    .populate(smdService.get(
                      abbonamentoDao.findByCampagnaAndStatoAbbonamento(campagna, StatoAbbonamento.SospesoInviatoEC)
                          .stream()
                          .collect(Collectors.toList())));
                    setHeader("Campagna::Abbonamenti Inviato EC");
                    break;
                case Chiusa:
                    abbonamentoGrid
                    .populate(smdService.get(
                      abbonamentoDao.findByCampagna(campagna)
                          .stream()
                          .filter(a -> a.getStatoAbbonamento() == StatoAbbonamento.Annullato)
                          .collect(Collectors.toList())));
                    setHeader("Campagna::Abbonamenti Annullati");
                    break;
                default:
                    break;                
                }
            });
            return button;
        });

        grid.populate(search.findAll());

    }

}
