package it.arsinfo.smd.ui.abbonamento;

import java.util.EnumSet;
import java.util.List;

import com.vaadin.data.Binder;
import com.vaadin.data.converter.StringToBigDecimalConverter;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;

import it.arsinfo.smd.dao.AbbonamentoServiceDao;
import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.Incassato;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Campagna;
import it.arsinfo.smd.ui.vaadin.SmdEntityEditor;

public class AbbonamentoEditor extends SmdEntityEditor<Abbonamento> {

    private final ComboBox<Anagrafica> intestatario = new ComboBox<Anagrafica>("Intestatario");
    private final ComboBox<Campagna> campagna = new ComboBox<Campagna>("Campagna");

    private final ComboBox<Anno> anno = new ComboBox<Anno>("Anno",
            EnumSet.allOf(Anno.class));

    private final TextField importo = new TextField("Importo");
    private final TextField spese = new TextField("Spese");
    private final TextField speseEstero = new TextField("Spese Estero");
    private final TextField speseEstrattoConto = new TextField("Spese Estratto Conto");
    private final TextField pregresso = new TextField("Pregresso");
    private final TextField totale = new TextField("Totale");
    private final TextField residuo = new TextField("Residuo");
    private final TextField incassato = new TextField("Incassato");
    private final CheckBox contrassegno = new CheckBox("Contrassegno");
    private final TextField codeLine = new TextField("Code Line");

    private final ComboBox<Incassato> statoIncasso = new ComboBox<Incassato>("Incassato",EnumSet.allOf(Incassato.class));
        
    public AbbonamentoEditor(AbbonamentoServiceDao dao, List<Anagrafica> anagrafica, List<Campagna> campagne) {

        super(dao,new Binder<>(Abbonamento.class));
        
        HorizontalLayout anag = new HorizontalLayout(campagna,
                anno,codeLine);
        anag.addComponentsAndExpand(intestatario);

        HorizontalLayout status = new HorizontalLayout(contrassegno,statoIncasso);
        
        HorizontalLayout imp = new HorizontalLayout(importo,speseEstero,spese,pregresso,speseEstrattoConto);
        HorizontalLayout res =	new HorizontalLayout(totale,incassato,residuo);

        setComponents(getActions(),anag, status,imp,res);

        intestatario.setItems(anagrafica);
        intestatario.setItemCaptionGenerator(Anagrafica::getCaption);
        intestatario.setEmptySelectionAllowed(false);

        codeLine.setReadOnly(true);

        campagna.setItems(campagne);
        campagna.setItemCaptionGenerator(Campagna::getCaption);
        campagna.setReadOnly(true);

        anno.setItemCaptionGenerator(Anno::getAnnoAsString);
        anno.setEmptySelectionAllowed(false);

        
        importo.setReadOnly(true);
        totale.setReadOnly(true);
        
        incassato.setReadOnly(true);
        residuo.setReadOnly(true);
        speseEstrattoConto.setReadOnly(true);

        getBinder().forField(codeLine).asRequired().withValidator(ca -> ca != null,
                "Deve essere definito").bind(Abbonamento::getCodeLine,
                                             Abbonamento::setCodeLine);
        getBinder().forField(intestatario)
            .asRequired()
            .withValidator(an -> an != null,"Scegliere un Cliente")
            .bind(Abbonamento::getIntestatario,Abbonamento::setIntestatario);

        getBinder().forField(campagna).bind(Abbonamento::getCampagna, Abbonamento::setCampagna);
        getBinder().forField(anno).asRequired().bind("anno");
        getBinder().forField(statoIncasso).bind("statoIncasso");


        getBinder()
        .forField(importo)
        .withConverter(new StringToBigDecimalConverter("Conversione in Eur"))
        .bind("importo");
        getBinder()
        .forField(spese)
        .withConverter(new StringToBigDecimalConverter("Conversione in Eur"))
        .withValidator(sp -> sp != null, "Spese non può essere null")
        .bind("spese");
        getBinder()
        .forField(speseEstero)
        .withConverter(new StringToBigDecimalConverter("Conversione in Eur"))
        .withValidator(sp -> sp != null, "Spese Estero non può essere null")
        .bind("speseEstero");
        getBinder()
        .forField(speseEstrattoConto)
        .withConverter(new StringToBigDecimalConverter("Conversione in Eur"))
        .withValidator(sp -> sp != null, "Spese Estratto conto non può essere null")
        .bind("speseEstrattoConto");
        getBinder()
        .forField(pregresso)
        .withConverter(new StringToBigDecimalConverter("Conversione in Eur"))
        .withValidator(sp -> sp != null, "Pregresso non può essere null")
        .bind("pregresso");
        getBinder()
        .forField(totale)
        .withConverter(new StringToBigDecimalConverter("Conversione in Eur"))
        .bind("totale");

        getBinder()
        .forField(incassato)
        .withConverter(new StringToBigDecimalConverter("Conversione in Eur"))
        .bind("incassato");
        getBinder()
            .forField(residuo)
            .withConverter(new StringToBigDecimalConverter("Conversione in Eur"))
            .bind("residuo");
               
        getBinder().forField(contrassegno).bind("contrassegno");
        
    }

    @Override
    public void focus(boolean persisted, Abbonamento abbonamento) {

        getDelete().setEnabled(abbonamento.getCampagna() == null && abbonamento.getIncassato().signum() == 0);
        
        codeLine.setVisible(persisted);
        intestatario.setReadOnly(persisted);
        
        campagna.setVisible(persisted);
        anno.setReadOnly(persisted);
        statoIncasso.setVisible(persisted);

        importo.setVisible(persisted);
        spese.setVisible(persisted);
        speseEstero.setVisible(persisted);
        speseEstrattoConto.setVisible(persisted);
        pregresso.setVisible(persisted);
        totale.setVisible(persisted);
        incassato.setVisible(persisted);
        residuo.setVisible(persisted);
        
        contrassegno.setVisible(persisted);
        contrassegno.setEnabled(!persisted);
                
        intestatario.focus();

    }
            
}
