package it.arsinfo.smd.vaadin.ui;

import com.vaadin.data.Binder;
import com.vaadin.ui.Grid;

import it.arsinfo.smd.entity.Incasso;
import it.arsinfo.smd.entity.Versamento;
import it.arsinfo.smd.repository.IncassoDao;
import it.arsinfo.smd.vaadin.model.SmdEditor;

public class IncassoEditor extends SmdEditor<Incasso> {

    Grid<Versamento> gridVersamento;
   
    public IncassoEditor(IncassoDao incassoDao) {
        super(incassoDao, new Binder<>(Incasso.class));

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
         gridVersamento.setItems(incasso.getVersamenti());
         gridVersamento.setVisible(true); 
        
    }

}
