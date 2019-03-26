package it.arsinfo.smd.vaadin.ui;

import com.vaadin.data.Binder;
import com.vaadin.ui.Grid;

import it.arsinfo.smd.entity.Incasso;
import it.arsinfo.smd.entity.Versamento;
import it.arsinfo.smd.repository.IncassoDao;
import it.arsinfo.smd.repository.VersamentoDao;
import it.arsinfo.smd.vaadin.model.SmdEditor;

public class IncassoEditor extends SmdEditor<Incasso> {

    private Grid<Versamento> gridVersamento;
    private final VersamentoDao versamentoDao;
    
    public IncassoEditor(IncassoDao incassoDao, VersamentoDao versamentoDao) {
        super(incassoDao, new Binder<>(Incasso.class));
        this.versamentoDao = versamentoDao;

        gridVersamento = new Grid<Versamento>(Versamento.class);

        gridVersamento.setColumns("bobina", "progressivoBobina",
                                  "progressivo","errore",
                                  "dataPagamento","dataContabile","importo",
                                  "tipoDocumento.bollettino","provincia","ufficio","sportello",
                                  "tipoAccettazione.tipo","tipoSostitutivo.descr"
                                  );
        gridVersamento.setVisible(false);
        gridVersamento.setWidth("80%");
        setComponents(getActions(),gridVersamento);

    }


    @Override
    public void focus(boolean persisted, Incasso incasso) {
        if (incasso == null) {
            gridVersamento.setVisible(false); 
            return;
         }
         gridVersamento.setItems(versamentoDao.findByIncasso(incasso));
         gridVersamento.setVisible(true); 
        
    }

}
