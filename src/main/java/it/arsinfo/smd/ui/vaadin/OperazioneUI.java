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
import it.arsinfo.smd.data.StatoStorico;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.entity.Spedizione;
import it.arsinfo.smd.entity.Storico;
import it.arsinfo.smd.repository.AbbonamentoDao;
import it.arsinfo.smd.repository.OperazioneDao;
import it.arsinfo.smd.repository.PubblicazioneDao;
import it.arsinfo.smd.repository.SpedizioneDao;
import it.arsinfo.smd.repository.StoricoDao;

@SpringUI(path = SmdUI.URL_OPERAZIONI)
@Title("Operazioni ADP")
@Push
public class OperazioneUI extends SmdUI {

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
    private SpedizioneDao spedizioneDao;

    @Override
    protected void init(VaadinRequest request) {
        super.init(request,"Operazioni");
        List<Pubblicazione> pubblicazioni =pubblicazioneDao.findAll();
        
        SmdProgressBar pb = new SmdProgressBar();
        SmdButton bss = new SmdButton("Aggiorna Stato Storici e Spedizioni", VaadinIcons.CLOUD);

        SmdButton generaShow = new SmdButton("Genera Operazioni",VaadinIcons.ARCHIVES);
        OperazioneGenera genera = new OperazioneGenera("Genera", VaadinIcons.ENVELOPES,operazioneDao, abbonamentoDao, pubblicazioni);
        OperazioneSearch search = new OperazioneSearch(operazioneDao, pubblicazioni);
        OperazioneGrid grid = new OperazioneGrid("Operazioni");
        OperazioneEditor editor = new OperazioneEditor(operazioneDao, pubblicazioni);
        SpedizioneGrid spedGrid = new SpedizioneGrid("Spedizioni");
        addSmdComponents(pb,spedGrid,generaShow,bss,genera,editor,search,grid);
        
        
        pb.setVisible(false);
        spedGrid.setVisible(false);
        genera.setVisible(false);
        editor.setVisible(false);

        pb.setChangeHandler(() ->{});
        spedGrid.setChangeHandler(() ->{});
        bss.setChangeHandler(()-> {
            setHeader("Calcola Stato....");
            bss.getButton().setEnabled(false);
            pb.setVisible(true);
            new Thread(() -> {
                List<Abbonamento> abbonamenti = abbonamentoDao.findByAnno(Smd.getAnnoCorrente());
                List<Storico> storici = storicoDao.findAll();
                List<Spedizione> aggiornamenti = Smd.spedizioneDaAggiornare(spedizioneDao.findAll());
                if (aggiornamenti.isEmpty() && storici.isEmpty()) {
                    return;
                }
                float delta = 1.0f/(storici.size() + aggiornamenti.size());
                pb.setValue(0.0f);
                storici.stream().forEach( s -> {
                    StatoStorico calcolato =  Smd.getStatoStorico(s, abbonamenti);
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
                aggiornamenti.stream().forEach(s -> {
                    s.setSospesa(!s.isSospesa());
                    spedizioneDao.save(s);
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
                    setHeader("Operazioni");
                    grid.populate(search.find());
                    this.push();
                });

            }).start();            
        });

        generaShow.setChangeHandler(() -> {
            generaShow.setVisible(false);
            bss.setVisible(false);
            search.setVisible(false);
            grid.setVisible(false);
            genera.edit();        
        }); 
        genera.setChangeHandler(() -> {
            generaShow.setVisible(true);
            spedGrid.setVisible(false);
            genera.setVisible(false);
            bss.setVisible(true);
            search.setVisible(true);
            grid.setVisible(true);
        }); 
        search.setChangeHandler(() -> grid.populate(search.find()));
        grid.setChangeHandler(()-> {
            if (grid.getSelected() == null) {
                return;
            }
            generaShow.setVisible(false);
            search.setVisible(false);
            bss.setVisible(false);
            grid.setVisible(false);
            editor.edit(grid.getSelected());   
        });
        
        editor.setChangeHandler(() -> {
            editor.setVisible(false);
            generaShow.setVisible(true);
            bss.setVisible(true);
            spedGrid.setVisible(false);
            search.setVisible(true);
            grid.populate(search.find());;            
        });

        grid.addComponentColumn(op -> {
            Button button = new Button("Rigenera",VaadinIcons.ENVELOPES);
            button.setEnabled(!op.chiuso());
            button.addClickListener(click -> {
                operazioneDao.save(
                   Smd.generaOperazione(op.getPubblicazione(), 
                                        abbonamentoDao.findByAnno(op.getAnno()),
                                        op.getAnno(), 
                                        op.getMese()));
                operazioneDao.delete(op);
                grid.populate(search.find());
            });
            return button;
        });

        grid.addComponentColumn(op -> {
            Button button = new Button("Spedizioniere",VaadinIcons.ENVELOPES);
            button.addClickListener(click -> {
                generaShow.setVisible(false);
                search.setVisible(false);
                grid.setVisible(false);
                editor.edit(op);
                spedGrid.populate(Smd.generaSpedizioniSped(abbonamentoDao.findByAnno(op.getAnno()), op));
            });
            return button;
        });
        
        grid.addComponentColumn(op -> {
            Button button = new Button("Adp Sede",VaadinIcons.ENVELOPES);
            button.addClickListener(click -> {
                generaShow.setVisible(false);
                search.setVisible(false);
                grid.setVisible(false);
                editor.edit(op);
                spedGrid.populate(Smd.generaSpedizioniCassa(abbonamentoDao.findByAnno(op.getAnno()), op));
            });
            return button;
        });
        
        grid.populate(operazioneDao.findAll());

     }

}