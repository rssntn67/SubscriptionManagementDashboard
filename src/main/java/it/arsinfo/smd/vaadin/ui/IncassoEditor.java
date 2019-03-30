package it.arsinfo.smd.vaadin.ui;

import java.util.EnumSet;

import com.vaadin.data.Binder;
import com.vaadin.data.converter.LocalDateToDateConverter;
import com.vaadin.data.converter.StringToBigDecimalConverter;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;

import it.arsinfo.smd.data.Cassa;
import it.arsinfo.smd.data.Ccp;
import it.arsinfo.smd.data.Cuas;
import it.arsinfo.smd.entity.Incasso;
import it.arsinfo.smd.repository.IncassoDao;
import it.arsinfo.smd.vaadin.model.SmdEditor;

public class IncassoEditor extends SmdEditor<Incasso> {
    
    private final ComboBox<Cassa> cassa = new ComboBox<Cassa>("Cassa",EnumSet.allOf(Cassa.class));
    private final ComboBox<Cuas> cuas = new ComboBox<Cuas>("Cuas",EnumSet.allOf(Cuas.class));
    private final ComboBox<Ccp> ccp = new ComboBox<Ccp>("Ccp",EnumSet.allOf(Ccp.class));
    private final TextField operazione = new TextField("Operazione");
    private final TextField documenti = new TextField("Numero Documenti");
    private final TextField importo = new TextField("Importo");

    private final TextField esatti = new TextField("Numero Documenti Esatti");
    private final TextField importoEsatti = new TextField("Importo Esatti");

    private final TextField errati = new TextField("Numero Documenti Errati");
    private final TextField importoErrati = new TextField("Importo Errati");
    private final DateField dataContabile = new DateField("Data contabile");
    public IncassoEditor(IncassoDao incassoDao) {
        super(incassoDao, new Binder<>(Incasso.class));

        setComponents(
                      getActions(), 
                      new HorizontalLayout(cassa,cuas,ccp,operazione),
                      new HorizontalLayout(importo,importoEsatti,importoErrati),
                      new HorizontalLayout(documenti,esatti,errati)
                      );
        
        ccp.setItemCaptionGenerator(Ccp::getCcp);
        importo.setReadOnly(true);
        documenti.setReadOnly(true);
        importoErrati.setReadOnly(true);
        importoEsatti.setReadOnly(true);
        esatti.setReadOnly(true);
        errati.setReadOnly(true);
        getBinder().forField(documenti)
            .withConverter(new StringToIntegerConverter(""))
            .bind(Incasso::getDocumenti, Incasso::setDocumenti);
        
        getBinder().forField(esatti)
            .withConverter(new StringToIntegerConverter(""))
            .bind(Incasso::getEsatti, Incasso::setEsatti);
        getBinder().forField(errati)
            .withConverter(new StringToIntegerConverter(""))
            .bind(Incasso::getErrati, Incasso::setErrati);

        getBinder().forField(importo)
            .withConverter(new StringToBigDecimalConverter("Conversione in Eur"))
            .bind(Incasso::getImporto,Incasso::setImporto);
        getBinder().forField(importoEsatti)
            .withConverter(new StringToBigDecimalConverter("Conversione in Eur"))
            .bind(Incasso::getImportoEsatti,Incasso::setImportoEsatti);
        getBinder().forField(importoErrati)
            .withConverter(new StringToBigDecimalConverter("Conversione in Eur"))
            .bind(Incasso::getImportoErrati,Incasso::setImportoErrati);
        getBinder().forField(dataContabile)
            .withConverter(new LocalDateToDateConverter()).bind("dataContabile");
        getBinder().bindInstanceFields(this);
        
    }


    @Override
    public void focus(boolean persisted, Incasso incasso) {
        dataContabile.setReadOnly(persisted);
        cassa.setReadOnly(persisted);
        cuas.setReadOnly(persisted);
        ccp.setReadOnly(persisted);
        operazione.setReadOnly(persisted);
        getSave().setEnabled(!persisted);
        getCancel().setEnabled(!persisted);
    }

}
