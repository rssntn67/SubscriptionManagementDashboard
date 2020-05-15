package it.arsinfo.smd.ui.distinta;

import java.time.ZoneId;
import java.util.EnumSet;

import com.vaadin.data.Binder;
import com.vaadin.data.converter.LocalDateToDateConverter;
import com.vaadin.data.converter.StringToBigDecimalConverter;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;

import it.arsinfo.smd.dao.repository.DistintaVersamentoDao;
import it.arsinfo.smd.data.Cassa;
import it.arsinfo.smd.data.Ccp;
import it.arsinfo.smd.data.Cuas;
import it.arsinfo.smd.entity.DistintaVersamento;
import it.arsinfo.smd.ui.vaadin.SmdRepositoryDaoEditor;

public class DistintaVersamentoEditor extends SmdRepositoryDaoEditor<DistintaVersamento> {
    
    private final ComboBox<Cassa> cassa = new ComboBox<Cassa>("Cassa",EnumSet.allOf(Cassa.class));
    private final ComboBox<Cuas> cuas = new ComboBox<Cuas>("Cuas",EnumSet.allOf(Cuas.class));
    private final ComboBox<Ccp> ccp = new ComboBox<Ccp>("Conto Corrente",EnumSet.allOf(Ccp.class));
    private final TextField documenti = new TextField("Numero Documenti");
    private final TextField importo = new TextField("Importo");
    private final TextField incassato = new TextField("Incassato");
    private final TextField residuo = new TextField("Residuo");

    private final TextField esatti = new TextField("Numero Documenti Esatti");
    private final TextField importoEsatti = new TextField("Importo Esatti");

    private final TextField errati = new TextField("Numero Documenti Errati");
    private final TextField importoErrati = new TextField("Importo Errati");
    private final DateField dataContabile = new DateField("Data contabile");
    
    public DistintaVersamentoEditor(DistintaVersamentoDao incassoDao) {
        super(incassoDao, new Binder<>(DistintaVersamento.class));

        setComponents(
                      getActions(), 
                      new HorizontalLayout(importo,incassato,residuo,dataContabile),
                      new HorizontalLayout(cassa,cuas,ccp),
                      new HorizontalLayout(documenti,esatti,errati,importoEsatti,importoErrati)
                      );
        
        importo.setReadOnly(true);
        incassato.setReadOnly(true);
        residuo.setReadOnly(true);
        documenti.setReadOnly(true);
        importoErrati.setReadOnly(true);
        importoEsatti.setReadOnly(true);
        esatti.setReadOnly(true);
        errati.setReadOnly(true);

        dataContabile.setDateFormat("dd/MM/yyyy");
        cuas.setEmptySelectionAllowed(false);
        cuas.setItemCaptionGenerator(Cuas::getDenominazione);
        cassa.setEmptySelectionAllowed(false);
        ccp.setEmptySelectionAllowed(false);
        ccp.setItemCaptionGenerator(Ccp::getCcp);

        getBinder().forField(documenti)
            .withConverter(new StringToIntegerConverter(""))
            .bind(DistintaVersamento::getDocumenti, DistintaVersamento::setDocumenti);
        
        getBinder().forField(esatti)
            .withConverter(new StringToIntegerConverter(""))
            .bind(DistintaVersamento::getEsatti, DistintaVersamento::setEsatti);
        getBinder().forField(errati)
            .withConverter(new StringToIntegerConverter(""))
            .bind(DistintaVersamento::getErrati, DistintaVersamento::setErrati);

        getBinder().forField(importo)
            .withConverter(new StringToBigDecimalConverter("Conversione in Eur"))
            .bind(DistintaVersamento::getImporto,DistintaVersamento::setImporto);
        getBinder().forField(incassato)
        .withConverter(new StringToBigDecimalConverter("Conversione in Eur"))
        .bind(DistintaVersamento::getIncassato,DistintaVersamento::setIncassato);
        getBinder().forField(residuo)
        .withConverter(new StringToBigDecimalConverter("Conversione in Eur"))
        .bind(DistintaVersamento::getResiduo,null);
       getBinder().forField(importoEsatti)
            .withConverter(new StringToBigDecimalConverter("Conversione in Eur"))
            .bind(DistintaVersamento::getImportoEsatti,DistintaVersamento::setImportoEsatti);
        getBinder().forField(importoErrati)
            .withConverter(new StringToBigDecimalConverter("Conversione in Eur"))
            .bind(DistintaVersamento::getImportoErrati,DistintaVersamento::setImportoErrati);
        getBinder().forField(dataContabile)
            .withConverter(new LocalDateToDateConverter(ZoneId.systemDefault())).bind("dataContabile");
        getBinder().bindInstanceFields(this);
        
    }


    @Override
    public void focus(boolean persisted, DistintaVersamento incasso) {
        incassato.setVisible(persisted);
        residuo.setVisible(persisted);
        cassa.setReadOnly(persisted);
        cuas.setReadOnly(persisted);
        ccp.setReadOnly(persisted);
        getCancel().setEnabled(!persisted);
    }

}
