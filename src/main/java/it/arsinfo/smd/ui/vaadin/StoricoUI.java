package it.arsinfo.smd.ui.vaadin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Nota;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.repository.AbbonamentoDao;
import it.arsinfo.smd.repository.AnagraficaDao;
import it.arsinfo.smd.repository.EstrattoContoDao;
import it.arsinfo.smd.repository.NotaDao;
import it.arsinfo.smd.repository.PubblicazioneDao;
import it.arsinfo.smd.repository.SpedizioneDao;
import it.arsinfo.smd.repository.SpesaSpedizioneDao;
import it.arsinfo.smd.repository.StoricoDao;

@SpringUI(path = SmdUI.URL_STORICO)
@Title("Storico Anagrafica Pubblicazioni ADP")
@Push
public class StoricoUI extends SmdUI {

    /**
     * 
     */
    private static final long serialVersionUID = 7884064928998716106L;

    @Autowired
    PubblicazioneDao pubblicazioneDao;

    @Autowired
    AnagraficaDao anagraficaDao;

    @Autowired
    StoricoDao storicoDao;

    @Autowired
    NotaDao notaDao;

    @Autowired
    AbbonamentoDao abbonamentoDao;
    
    @Autowired
    EstrattoContoDao estrattoContoDao;
    
    @Autowired
    SpedizioneDao spedizioneDao;

    @Autowired
    SpesaSpedizioneDao spesaSpedizioneDao;

    @Override
    protected void init(VaadinRequest request) {
        super.init(request, "Storico");
        SmdProgressBar pb = new SmdProgressBar();
        List<Anagrafica> anagrafica = anagraficaDao.findAll();
        List<Pubblicazione> pubblicazioni = pubblicazioneDao.findAll();
        StoricoAdd add = new StoricoAdd("Aggiungi Storico");
        StoricoSearch search = new StoricoSearch(storicoDao,anagrafica,pubblicazioni);
        StoricoGrid grid = new StoricoGrid("Storico");
        StoricoEditor editor = 
                new StoricoEditor(
                                  storicoDao, 
                                  abbonamentoDao,
                                  estrattoContoDao,
                                  spedizioneDao,
                                  pubblicazioni, 
                                  anagrafica,
                                  spesaSpedizioneDao.findAll()) {
            @Override
            public void save() {
                if (getIntestatario().isEmpty()) {
                    Notification.show("Intestatario deve essere valorizzato", Type.ERROR_MESSAGE);
                    return;                    
                }
                if (getDestinatario().isEmpty()) {
                    Notification.show("Destinatario deve essere valorizzato", Type.ERROR_MESSAGE);
                    return;                    
                }
                if (getPubblicazione().isEmpty()) {
                    Notification.show("Pubblicazione deve essere valorizzata", Type.ERROR_MESSAGE);
                    return;
                }
                super.save();
                if (!getNota().isEmpty()) {
                    Nota nota = new Nota(get());
                    nota.setDescription(getNota().getValue());
                    notaDao.save(nota);
                    getNota().clear();
                }
            }
        };
        
        NotaGrid notaGrid = new NotaGrid("Note");
        notaGrid.getGrid().setColumns("data","description");
        notaGrid.getGrid().setHeight("200px");

        addSmdComponents(pb,add,editor,notaGrid,search, grid);
        pb.setVisible(false);
        editor.setVisible(false);
        notaGrid.setVisible(false);

        pb.setChangeHandler(() ->{});
        
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
            add.setVisible(true);
        });

        add.setChangeHandler(() -> {
            editor.edit(add.generate());
            setHeader(String.format("Storico:Nuovo"));
            add.setVisible(false);
        });

        notaGrid.setChangeHandler(() -> {});

        grid.populate(search.find());

    }
}
