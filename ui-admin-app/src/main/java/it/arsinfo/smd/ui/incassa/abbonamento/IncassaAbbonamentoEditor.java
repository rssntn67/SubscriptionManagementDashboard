package it.arsinfo.smd.ui.incassa.abbonamento;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

import com.vaadin.data.Binder;
import com.vaadin.data.ReadOnlyHasValue;
import com.vaadin.data.converter.LocalDateToDateConverter;
import it.arsinfo.smd.ui.EuroConverter;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;

import it.arsinfo.smd.entity.Anno;
import it.arsinfo.smd.entity.Cassa;
import it.arsinfo.smd.entity.Ccp;
import it.arsinfo.smd.entity.Cuas;
import it.arsinfo.smd.entity.Incassato;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Campagna;
import it.arsinfo.smd.ui.vaadin.SmdItemEditor;

public class IncassaAbbonamentoEditor extends SmdItemEditor<Abbonamento> {

    private boolean hasResiduo;

    private final ComboBox<Campagna> campagna = new ComboBox<>("Campagna");


    private final ComboBox<Cassa> cassa = new ComboBox<>("Cassa",
            EnumSet.allOf(Cassa.class));
    private final ComboBox<Ccp> ccp = new ComboBox<>("Conto Corrente",
            EnumSet.allOf(Ccp.class));
    private final ComboBox<Cuas> cuas = new ComboBox<>("Cuas",
            EnumSet.allOf(Cuas.class));
    private final TextField progressivo = new TextField("Progressivo");

    private final ComboBox<Incassato> statoIncasso = new ComboBox<>("Incassato",EnumSet.allOf(Incassato.class));
    
    private final DateField dataContabile = new DateField("Data contabile");
    private final DateField dataPagamento = new DateField("Data pagamento");
    
    public IncassaAbbonamentoEditor(List<Anagrafica> anagrafica, List<Campagna> campagne) {

        super(new Binder<>(Abbonamento.class));

        final ComboBox<Anagrafica> intestatario = new ComboBox<>("Intestatario");
        final ComboBox<Anno> anno = new ComboBox<>("Selezionare Anno",
                EnumSet.allOf(Anno.class));
        final TextField importo = new TextField("Importo");
        final TextField spese = new TextField("Spese");
        final TextField speseEstero = new TextField("Spese Estero");
        final TextField speseEstrattoConto = new TextField("Spese Estratto Conto");
        final TextField pregresso = new TextField("Pregresso");
        final TextField totale = new TextField("Totale");
        final TextField residuo = new TextField("Residuo");
        final TextField incassato = new TextField("Incassato");
        final TextField codeLine = new TextField("Code Line");

        HorizontalLayout anag = new HorizontalLayout(campagna,anno,codeLine);
        anag.addComponentsAndExpand(intestatario);

        HorizontalLayout status = new HorizontalLayout(statoIncasso);
        
        HorizontalLayout imp = new HorizontalLayout(importo,speseEstero,spese,pregresso,speseEstrattoConto);
        HorizontalLayout res =	new HorizontalLayout(totale,incassato,residuo);

        HorizontalLayout incss = new HorizontalLayout(dataContabile,dataPagamento,cassa,ccp,cuas);
        HorizontalLayout detai = new HorizontalLayout();
        detai.addComponentsAndExpand(progressivo);
        setComponents(anag, status,imp,res,incss,detai);


        intestatario.setItems(anagrafica);
        intestatario.setItemCaptionGenerator(Anagrafica::getCaption);
        intestatario.setEmptySelectionAllowed(false);


        campagna.setItems(campagne);
        campagna.setItemCaptionGenerator(Campagna::getCaption);
 
        anno.setItemCaptionGenerator(Anno::getAnnoAsString);
        anno.setEmptySelectionAllowed(false);
        
        campagna.setReadOnly(true);
        anno.setReadOnly(true);
        codeLine.setReadOnly(true);
        intestatario.setReadOnly(true);

        statoIncasso.setReadOnly(true);
        
        importo.setReadOnly(true);
        speseEstero.setReadOnly(true);
        spese.setReadOnly(true);
        pregresso.setReadOnly(true);
        speseEstrattoConto.setReadOnly(true);

        totale.setReadOnly(true);        
        incassato.setReadOnly(true);
        residuo.setReadOnly(true);

        cassa.setEmptySelectionAllowed(false);
        ccp.setEmptySelectionAllowed(false);
        ccp.setItemCaptionGenerator(Ccp::getCcp);
        cuas.setEmptySelectionAllowed(false);
        cuas.setItemCaptionGenerator(Cuas::getDenominazione);

        dataContabile.setDateFormat("dd/MM/yyyy");
        dataPagamento.setDateFormat("dd/MM/yyyy");

        getBinder().forField(codeLine).asRequired().withValidator(Objects::nonNull,
                "Deve essere definito").bind(Abbonamento::getCodeLine,
                                             Abbonamento::setCodeLine);
        getBinder().forField(intestatario)
            .asRequired()
            .withValidator(Objects::nonNull,"Scegliere un Cliente")
            .bind(Abbonamento::getIntestatario,Abbonamento::setIntestatario);

        getBinder().forField(campagna).bind(Abbonamento::getCampagna, Abbonamento::setCampagna);
        getBinder().forField(anno).asRequired().bind("anno");
        ReadOnlyHasValue<Abbonamento> stato = new ReadOnlyHasValue<>(abb->  statoIncasso.setValue(abb.getStatoIncasso(campagna.getValue())));
        getBinder().forField(stato).bind(abb->abb,null);


        getBinder()
        .forField(importo)
        .withConverter(new EuroConverter("Conversione in Eur"))
        .bind("importo");
        getBinder()
        .forField(spese)
        .withConverter(new EuroConverter("Conversione in Eur"))
        .withValidator(Objects::nonNull, "Spese non può essere null")
        .bind("spese");
        getBinder()
        .forField(speseEstero)
        .withConverter(new EuroConverter("Conversione in Eur"))
        .withValidator(Objects::nonNull, "Spese Estero non può essere null")
        .bind("speseEstero");
        getBinder()
        .forField(speseEstrattoConto)
        .withConverter(new EuroConverter("Conversione in Eur"))
        .withValidator(Objects::nonNull, "Spese Estratto conto non può essere null")
        .bind("speseEstrattoConto");
        getBinder()
        .forField(pregresso)
        .withConverter(new EuroConverter("Conversione in Eur"))
        .withValidator(Objects::nonNull, "Pregresso non può essere null")
        .bind("pregresso");
        getBinder()
        .forField(totale)
        .withConverter(new EuroConverter("Conversione in Eur"))
        .bind("totale");

        getBinder()
        .forField(incassato)
        .withConverter(new EuroConverter("Conversione in Eur"))
        .bind("incassato");
        getBinder()
            .forField(residuo)
            .withConverter(new EuroConverter("Conversione in Eur"))
            .bind("residuo");
               
        getBinder().forField(dataContabile).asRequired()
        .withConverter(new LocalDateToDateConverter())
        .bind("dataContabile");
        
        getBinder().forField(dataPagamento).asRequired()
        .withConverter(new LocalDateToDateConverter())
        .bind("dataPagamento");
        getBinder().forField(cassa).bind("cassa");
        getBinder().forField(ccp).bind("ccp");
        getBinder().forField(cuas).bind("cuas");                
        getBinder().forField(progressivo).asRequired().bind("progressivo");  
        
    }

    @Override
    public void focus(boolean persisted, Abbonamento abbonamento) {
        
        hasResiduo = abbonamento.getResiduo().signum() > 0; 

        
        dataContabile.setVisible(hasResiduo);
        dataPagamento.setVisible(hasResiduo); 
        cassa.setVisible(hasResiduo);
        ccp.setVisible(hasResiduo);
        cuas.setVisible(hasResiduo);
        progressivo.setVisible(hasResiduo);
                
        progressivo.focus();

    }
        
    public boolean incassare() {
        return hasResiduo;
    }
}
