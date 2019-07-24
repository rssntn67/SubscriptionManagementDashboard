package it.arsinfo.smd.ui.vaadin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Title;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Button;

import it.arsinfo.smd.Smd;
import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.StatoStorico;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.EstrattoConto;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.entity.Storico;
import it.arsinfo.smd.repository.AbbonamentoDao;
import it.arsinfo.smd.repository.EstrattoContoDao;
import it.arsinfo.smd.repository.OperazioneDao;
import it.arsinfo.smd.repository.PubblicazioneDao;
import it.arsinfo.smd.repository.StoricoDao;

@SpringUI(path = SmdUI.URL_SPEDIZIONERE)
@Title("Ordini Spedizioniere ADP")
@Push
public class SpedizioniereUI extends SmdUI {

    /**
     * 
     */
    private static final long serialVersionUID = -4970387092690412856L;

    @Autowired
    private OperazioneDao operazioneDao;

    @Autowired
    private PubblicazioneDao pubblicazioneDao;

    @Autowired
    private AbbonamentoDao abbonamentoDao;

    @Autowired
    private StoricoDao storicoDao;

    @Autowired
    private EstrattoContoDao estrattoContoDao;

    @Override
    protected void init(VaadinRequest request) {
        super.init(request,"Spedizioniere");
        List<Pubblicazione> pubblicazioni =pubblicazioneDao.findAll();
        
        SmdProgressBar pb = new SmdProgressBar();
        SmdButton gss = new SmdButton("Genera Estratto Conto", VaadinIcons.CLOUD);
        SmdButton bss = new SmdButton("Aggiorna Stato Storici", VaadinIcons.CLOUD);

        OperazioneSearch search = new OperazioneSearch(operazioneDao, pubblicazioni);
        OperazioneGrid grid = new OperazioneGrid("Operazioni");
        OperazioneEditor editor = new OperazioneEditor(operazioneDao, pubblicazioni);
        SpedizioneGrid spedGrid = new SpedizioneGrid("Spedizioni");
        addSmdComponents(pb,spedGrid,gss,bss,editor,search,grid);
        
        
        pb.setVisible(false);
        spedGrid.setVisible(false);
        editor.setVisible(false);

        pb.setChangeHandler(() ->{});
        spedGrid.setChangeHandler(() ->{});
        
        bss.setChangeHandler(()-> {
            setHeader("Calcola Stato....");
            bss.getButton().setEnabled(false);
            pb.setVisible(true);
            new Thread(() -> {
                List<Abbonamento> abbonamenti = abbonamentoDao.findByAnno(Anno.getAnnoCorrente());
                List<Storico> storici = storicoDao.findAll();
                float delta = 1.0f/storici.size();
                pb.setValue(0.0f);
                storici.stream().forEach( s -> {
                    StatoStorico calcolato =  Smd.getStatoStorico(s, abbonamenti,estrattoContoDao.findAll());
                    if (s.getStatoStorico() != calcolato) {
                        s.setStatoStorico(calcolato);
                        storicoDao.save(s);
                    }
                    access(() -> {
                        pb.setValue(pb.getValue()+delta);
                        grid.populate(search.find());
                        this.push();
                    });
                });

                access(() -> {
                    pb.setValue(0.0f);
                    pb.setVisible(false);
                    bss.getButton().setEnabled(true);
                    setHeader("Storico");
                    grid.populate(search.find());
                    this.push();
                });

            }).start();            
        });

        gss.setChangeHandler(()-> {
            setHeader("Calcola Stato....");
            gss.getButton().setEnabled(false);
            pb.setVisible(true);
            new Thread(() -> {
                List<Abbonamento> abbonamenti = abbonamentoDao.findByAnno(Anno.getAnnoCorrente());
                List<Storico> storici = storicoDao.findAll();
                List<EstrattoConto> aggiornamenti = Smd.generaEstrattoConto(estrattoContoDao.findAll());
                if (aggiornamenti.isEmpty() && storici.isEmpty()) {
                    return;
                }
                float delta = 1.0f/(storici.size() + aggiornamenti.size());
                pb.setValue(0.0f);
                storici.stream().forEach( s -> {
                    StatoStorico calcolato =  Smd.getStatoStorico(s, abbonamenti,estrattoContoDao.findAll());
                    if (s.getStatoStorico() != calcolato) {
                        s.setStatoStorico(calcolato);
                        storicoDao.save(s);
                    }
                    access(() -> {
                        pb.setValue(pb.getValue()+delta);
                        grid.populate(search.find());
                        this.push();
                    });

                });
                aggiornamenti.stream().forEach(ec -> {
                    estrattoContoDao.save(ec);
                        access(() -> {
                            pb.setValue(pb.getValue()+delta);
                            grid.populate(search.find());
                            this.push();
                        });
                });
                access(() -> {
                    pb.setValue(0.0f);
                    pb.setVisible(false);
                    gss.getButton().setEnabled(true);
                    setHeader("Operazioni");
                    grid.populate(search.find());
                    this.push();
                });

            }).start();            
        });

        search.setChangeHandler(() -> grid.populate(search.find()));
        grid.setChangeHandler(()-> {
            if (grid.getSelected() == null) {
                return;
            }
            search.setVisible(false);
            gss.setVisible(false);
            grid.setVisible(false);
            editor.edit(grid.getSelected());   
        });
        
        editor.setChangeHandler(() -> {
            editor.setVisible(false);
            gss.setVisible(true);
            spedGrid.setVisible(false);
            search.setVisible(true);
            grid.populate(search.find());;            
        });

        grid.addComponentColumn(op -> {
            Button button = new Button("Spedizioniere",VaadinIcons.ENVELOPES);
            button.addClickListener(click -> {
                search.setVisible(false);
                grid.setVisible(false);
                editor.edit(op);
//                spedGrid.populate(Smd.listaSpedizioni(estrattoContoDao.findAll(), InvioSpedizione.Spedizioniere,op.getMese(),op.getAnno()));
            });
            return button;
        });
        
        grid.addComponentColumn(op -> {
            Button button = new Button("Adp Sede",VaadinIcons.ENVELOPES);
            button.addClickListener(click -> {
                search.setVisible(false);
                grid.setVisible(false);
                editor.edit(op);
//                spedGrid.populate(Smd.listaSpedizioni(estrattoContoDao.findAll(), InvioSpedizione.AdpSede,op.getMese(),op.getAnno()));
            });
            return button;
        });
        
        grid.populate(operazioneDao.findAll());

     }

}
