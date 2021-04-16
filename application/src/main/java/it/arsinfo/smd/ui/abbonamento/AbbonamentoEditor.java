package it.arsinfo.smd.ui.abbonamento;

import com.vaadin.data.Binder;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.server.FileResource;
import com.vaadin.ui.*;
import it.arsinfo.smd.ui.service.api.AbbonamentoService;
import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.Incassato;
import it.arsinfo.smd.data.StatoAbbonamento;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Campagna;
import it.arsinfo.smd.ui.EuroConverter;
import it.arsinfo.smd.ui.vaadin.SmdEntityEditor;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

public class AbbonamentoEditor extends SmdEntityEditor<Abbonamento> {

    private final ComboBox<Anagrafica> intestatario = new ComboBox<>("Intestatario");
    private final ComboBox<Campagna> campagna = new ComboBox<>("Campagna");

    private final ComboBox<Anno> anno = new ComboBox<>("Anno",
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

    private final Button stampaBollettino = new Button("Bollettino prestampato", VaadinIcons.PRINT);

    private final ComboBox<Incassato> statoIncasso = new ComboBox<>("Incassato",EnumSet.allOf(Incassato.class));

    private final AbbonamentoService dao;
    public AbbonamentoEditor(AbbonamentoService dao, List<Anagrafica> anagrafica, List<Campagna> campagne) {

        super(dao,new Binder<>(Abbonamento.class));
        this.dao=dao;
        CheckBox sollecitato = new CheckBox("Sollecitato");
        CheckBox inviatoEC = new CheckBox("InviatoEC");
        ComboBox<StatoAbbonamento> statoAbbonamento = new ComboBox<>("Stato",
                EnumSet.allOf(StatoAbbonamento.class));

        HorizontalLayout anag = new HorizontalLayout(campagna,
                anno,codeLine);
        anag.addComponentsAndExpand(intestatario);

        HorizontalLayout status = 
        		new HorizontalLayout(contrassegno,sollecitato,inviatoEC,statoIncasso,statoAbbonamento);
        
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

        sollecitato.setReadOnly(true);
        inviatoEC.setReadOnly(true);
        
        importo.setReadOnly(true);
        totale.setReadOnly(true);
        
        incassato.setReadOnly(true);
        residuo.setReadOnly(true);
        speseEstrattoConto.setReadOnly(true);

        stampaBollettino.addClickListener(c-> dao.getBollettino(get(),true));
        getBinder().forField(codeLine).asRequired().withValidator(Objects::nonNull,
                "Deve essere definito").bind(Abbonamento::getCodeLine,
                                             Abbonamento::setCodeLine);
        getBinder().forField(intestatario)
            .asRequired()
            .withValidator(Objects::nonNull,"Scegliere un Cliente")
            .bind(Abbonamento::getIntestatario,Abbonamento::setIntestatario);

        getBinder().forField(campagna).bind(Abbonamento::getCampagna, Abbonamento::setCampagna);
        getBinder().forField(anno).asRequired().bind("anno");
        getBinder().forField(statoIncasso).bind("statoIncasso");
        getBinder().forField(statoAbbonamento)
        .asRequired().bind(Abbonamento::getStatoAbbonamento,Abbonamento::setStatoAbbonamento);


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
               
        getBinder().forField(contrassegno).bind("contrassegno");
        getBinder().forField(sollecitato).bind("sollecitato");
        getBinder().forField(inviatoEC).bind("inviatoEC");

        getActions().addComponent(stampaBollettino);
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
        stampaBollettino.setVisible(persisted);

        if (persisted) {
            BrowserWindowOpener opener = new BrowserWindowOpener(new FileResource(dao.getBollettino(abbonamento,false)));
            opener.setWindowName("_blank_"+abbonamento.getCodeLine());
            opener.extend(stampaBollettino);
        }
        intestatario.focus();

    }
            
}
