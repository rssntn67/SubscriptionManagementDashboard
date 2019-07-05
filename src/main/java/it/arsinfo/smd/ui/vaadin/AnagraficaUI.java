package it.arsinfo.smd.ui.vaadin;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;

import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Storico;
import it.arsinfo.smd.repository.AbbonamentoDao;
import it.arsinfo.smd.repository.AnagraficaDao;
import it.arsinfo.smd.repository.CampagnaDao;
import it.arsinfo.smd.repository.EstrattoContoDao;
import it.arsinfo.smd.repository.NotaDao;
import it.arsinfo.smd.repository.PubblicazioneDao;
import it.arsinfo.smd.repository.SpedizioneDao;
import it.arsinfo.smd.repository.StoricoDao;

@SpringUI(path = SmdUI.URL_ANAGRAFICA)
@Title("Anagrafica Clienti ADP")
public class AnagraficaUI extends SmdUI {

    /**
     * 
     */
    private static final long serialVersionUID = 7884064928998716106L;

    @Autowired
    AnagraficaDao anagraficaDao;
    @Autowired
    PubblicazioneDao pubblicazioneDao;
    @Autowired
    StoricoDao storicoDao;
    @Autowired
    AbbonamentoDao abbonamentoDao;
    @Autowired
    NotaDao notaDao;
    @Autowired
    CampagnaDao campagnaDao;
    @Autowired
    EstrattoContoDao estrattoContoDao;
    
    @Autowired
    SpedizioneDao spedizioneDao;

    @Override
    protected void init(VaadinRequest request) {
        super.init(request, "Anagrafica");
        AnagraficaAdd add = new AnagraficaAdd("Aggiungi ad Anagrafica");
        AnagraficaSearch search = new AnagraficaSearch(anagraficaDao,storicoDao);
        AnagraficaGrid grid = new AnagraficaGrid("Anagrafiche");
        AnagraficaEditor editor = new AnagraficaEditor(anagraficaDao);
        
        
        addSmdComponents(
                         editor, 
                         add,
                         search, 
                         grid);

        editor.setVisible(false);
        
        add.setChangeHandler(() -> {
            setHeader("Anagrafica:Nuova");
            hideMenu();
            add.setVisible(false);
            search.setVisible(false);
            grid.setVisible(false);
            editor.edit(add.generate());
        });

        search.setChangeHandler(() -> {
            grid.populate(search.find());
        });
        
        grid.setChangeHandler(() -> {
            if (grid.getSelected() == null) {
                return;
            }
            setHeader(grid.getSelected().getHeader());
            hideMenu();
            add.setVisible(false);
            search.setVisible(false);
            editor.edit(grid.getSelected());
        });

        editor.setChangeHandler(() -> {
            grid.populate(search.find());
            editor.setVisible(false);
            setHeader("Anagrafica");
            showMenu();
            add.setVisible(true);
            search.setVisible(true);
        });

        grid.populate(search.findAll());

    }
    
    public List<Storico> findByCustomer(Anagrafica customer) {
        List<Storico> list = storicoDao.findByIntestatario(customer);
        list.addAll(storicoDao.findByDestinatario(customer)
                    .stream()
                    .filter(ap -> customer.getId() != ap.getIntestatario().getId())
                    .collect(Collectors.toList()));
        return list;
    }

}
