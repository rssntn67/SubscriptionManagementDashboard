package it.arsinfo.smd.vaadin.ui;

import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.List;

import com.vaadin.data.Binder;
import com.vaadin.data.converter.LocalDateToDateConverter;
import com.vaadin.data.converter.StringToBigDecimalConverter;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;

import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.Cassa;
import it.arsinfo.smd.data.ContoCorrentePostale;
import it.arsinfo.smd.data.Mese;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.repository.AbbonamentoDao;
import it.arsinfo.smd.vaadin.model.SmdEditor;

public class AbbonamentoEditor extends SmdEditor<Abbonamento> {

    private final ComboBox<Anagrafica> intestatario = new ComboBox<Anagrafica>("Intestatario");
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
    private final ComboBox<ContoCorrentePostale> contoCorrentePostale = new ComboBox<ContoCorrentePostale>("Selezionare ccp",
            EnumSet.allOf(ContoCorrentePostale.class));
    private final TextField spese = new TextField("Spese Spedizione");

    private final DateField incasso = new DateField("Incassato");

    public AbbonamentoEditor(AbbonamentoDao abbonamentoDao, List<Anagrafica> anagrafica) {

        super(abbonamentoDao,new Binder<>(Abbonamento.class));

        HorizontalLayout pri = new HorizontalLayout(intestatario,
                                                    anno, inizio, fine,incasso);
        HorizontalLayout sec = new HorizontalLayout(costo, cassa, campo,
                                                    contoCorrentePostale,spese);
        

        setComponents(getActions(),pri, sec);

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
        cassa.setEmptySelectionAllowed(false);
        campo.setReadOnly(true);
        contoCorrentePostale.setItemCaptionGenerator(ContoCorrentePostale::getCcp);


        getBinder().forField(intestatario)
            .asRequired()
            .withValidator(an -> an != null,"Scegliere un Cliente")
            .bind(Abbonamento::getIntestatario,Abbonamento::setIntestatario);
        getBinder().forField(anno).asRequired().bind("anno");
        getBinder().forField(inizio).asRequired().bind("inizio");
        getBinder().forField(fine).bind("fine");

        getBinder().forField(costo).asRequired().withConverter(new StringToBigDecimalConverter("Conversione in Eur")).bind(Abbonamento::getCosto,
                                                                                                                           Abbonamento::setCosto);
        getBinder().forField(cassa).bind("cassa");
        getBinder().forField(campo).asRequired().withValidator(ca -> ca != null,
                "Deve essere definito").bind(Abbonamento::getCampo,
                                             Abbonamento::setCampo);
        getBinder().forField(contoCorrentePostale).bind("contoCorrentePostale");
        

        getBinder().forField(spese).asRequired().withConverter(new StringToBigDecimalConverter("Conversione in Eur")).bind(Abbonamento::getSpese,
                                                                                                                      Abbonamento::setSpese);
        getBinder().forField(incasso).withConverter(new LocalDateToDateConverter()).bind("incasso");

    }

    @Override
    public void focus(boolean persisted, Abbonamento abbonamento) {

        getDelete().setEnabled(persisted);
        intestatario.setReadOnly(persisted);
        anno.setReadOnly(persisted);
        inizio.setReadOnly(persisted);
        fine.setReadOnly(persisted);
        spese.setReadOnly(persisted);
        campo.setVisible(persisted);
        campo.setReadOnly(persisted);

        if (persisted && abbonamento.getCosto() == BigDecimal.ZERO) {
            getSave().setEnabled(false);
            getCancel().setEnabled(false);
            incasso.setVisible(false);
            return;
        }

        if (persisted && abbonamento.getIncasso() == null) {
            getSave().setEnabled(true);
            getCancel().setEnabled(true);
            incasso.setVisible(true);
            return;
        }

        getSave().setEnabled(true);
        getCancel().setEnabled(true);
        incasso.setVisible(false);

        intestatario.focus();

    }
}
