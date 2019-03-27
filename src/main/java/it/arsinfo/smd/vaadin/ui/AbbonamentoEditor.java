package it.arsinfo.smd.vaadin.ui;

import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.List;

import com.vaadin.data.Binder;
import com.vaadin.data.converter.StringToBigDecimalConverter;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;

import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.Cassa;
import it.arsinfo.smd.data.Ccp;
import it.arsinfo.smd.data.Mese;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Campagna;
import it.arsinfo.smd.repository.AbbonamentoDao;
import it.arsinfo.smd.vaadin.model.SmdEditor;

public class AbbonamentoEditor extends SmdEditor<Abbonamento> {

    private final ComboBox<Anagrafica> intestatario = new ComboBox<Anagrafica>("Intestatario");
    private final ComboBox<Campagna> campagna = new ComboBox<Campagna>("Campagna");
    private final ComboBox<Anno> anno = new ComboBox<Anno>("Selezionare Anno",
            EnumSet.allOf(Anno.class));
    private final ComboBox<Mese> inizio = new ComboBox<Mese>("Selezionare Inizio",
            EnumSet.allOf(Mese.class));
    private final ComboBox<Mese> fine = new ComboBox<Mese>("Selezionare Fine",
          EnumSet.allOf(Mese.class));

    private final TextField costo = new TextField("Costo");
    private final ComboBox<Cassa> cassa = new ComboBox<Cassa>("Cassa",
            EnumSet.allOf(Cassa.class));
    private final TextField campo = new TextField("V Campo Poste Italiane");
    private final ComboBox<Ccp> ccp = new ComboBox<Ccp>("Selezionare ccp",
            EnumSet.allOf(Ccp.class));
    private final TextField spese = new TextField("Spese Spedizione");

    private final TextField incassato = new TextField("Incassato");
    public AbbonamentoEditor(AbbonamentoDao abbonamentoDao, List<Anagrafica> anagrafica, List<Campagna> campagne) {

        super(abbonamentoDao,new Binder<>(Abbonamento.class));

        HorizontalLayout pri = new HorizontalLayout(campagna,intestatario,
                                                    anno, inizio, fine);
        HorizontalLayout sec = new HorizontalLayout(incassato,costo,spese,cassa,campo,
                                                    ccp);
        

        setComponents(getActions(),pri, sec);
        
        campagna.setItems(campagne);
        campagna.setItemCaptionGenerator(Campagna::getCaption);

        intestatario.setItems(anagrafica);
        intestatario.setItemCaptionGenerator(Anagrafica::getCaption);
        intestatario.setEmptySelectionAllowed(false);

        anno.setItemCaptionGenerator(Anno::getAnnoAsString);
        anno.setEmptySelectionAllowed(false);

        inizio.setItemCaptionGenerator(Mese::getNomeBreve);
        inizio.setEmptySelectionAllowed(false);
        
        fine.setItemCaptionGenerator(Mese::getNomeBreve);
        fine.setEmptySelectionAllowed(false);

        costo.setReadOnly(true);
        campo.setReadOnly(true);
        cassa.setEmptySelectionAllowed(false);
        ccp.setItemCaptionGenerator(Ccp::getCcp);


        getBinder().forField(intestatario)
            .asRequired()
            .withValidator(an -> an != null,"Scegliere un Cliente")
            .bind(Abbonamento::getIntestatario,Abbonamento::setIntestatario);
        getBinder().forField(campagna).bind(Abbonamento::getCampagna, Abbonamento::setCampagna);
        getBinder().forField(anno).asRequired().bind("anno");
        getBinder().forField(inizio).asRequired().bind("inizio");
        getBinder().forField(fine).bind("fine");

        getBinder().forField(costo).asRequired().withConverter(new StringToBigDecimalConverter("Conversione in Eur")).bind(Abbonamento::getCosto,
                                                                                                                           Abbonamento::setCosto);
        getBinder().forField(cassa).bind("cassa");
        getBinder().forField(campo).asRequired().withValidator(ca -> ca != null,
                "Deve essere definito").bind(Abbonamento::getCampo,
                                             Abbonamento::setCampo);
        getBinder().forField(ccp).bind("ccp");
        

        getBinder().forField(spese).asRequired().withConverter(new StringToBigDecimalConverter("Conversione in Eur")).bind(Abbonamento::getSpese,
                                                                                                                      Abbonamento::setSpese);
        getBinder().forField(incassato).bind("incassato");

    }

    @Override
    public void focus(boolean persisted, Abbonamento abbonamento) {

        getDelete().setEnabled(persisted && abbonamento.getVersamento() != null);
        getSave().setEnabled(!persisted);
        getCancel().setEnabled(!persisted);
        intestatario.setReadOnly(persisted);
        anno.setReadOnly(persisted);
        inizio.setReadOnly(persisted);
        fine.setReadOnly(persisted);
        spese.setReadOnly(persisted);
        campo.setVisible(persisted);
        campo.setReadOnly(persisted);
        ccp.setReadOnly(persisted);
        campagna.setReadOnly(true);
        incassato.setVisible(persisted);
        cassa.setReadOnly(persisted);

        if (persisted && 
                abbonamento.getCosto().doubleValue() == BigDecimal.ZERO.doubleValue() && 
                abbonamento.getSpese().doubleValue() == BigDecimal.ZERO.doubleValue()) {
            cassa.setVisible(false);
            campo.setVisible(false);
            ccp.setVisible(false);
        } else if (!persisted ){
            cassa.setVisible(true);
            ccp.setVisible(true);
            intestatario.focus();
        }
        
    }
}
