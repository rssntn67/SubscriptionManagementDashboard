package it.arsinfo.smd.vaadin.ui;

import java.math.BigDecimal;
import java.util.EnumSet;

import com.vaadin.data.Binder;
import com.vaadin.data.converter.LocalDateToDateConverter;
import com.vaadin.data.converter.StringToBigDecimalConverter;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;

import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.ContoCorrentePostale;
import it.arsinfo.smd.data.Mese;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.repository.AbbonamentoDao;
import it.arsinfo.smd.repository.AnagraficaDao;
import it.arsinfo.smd.repository.PubblicazioneDao;
import it.arsinfo.smd.vaadin.model.SmdEditor;

public class AbbonamentoEditor extends SmdEditor<Abbonamento> {

    /**
     * 
     */
    private static final long serialVersionUID = 4673834235533544936L;

    private final ComboBox<Anagrafica> anagrafica = new ComboBox<Anagrafica>("Selezionare il cliente");
    private final ComboBox<Anagrafica> destinatario = new ComboBox<Anagrafica>("Selezionare il destinatario");
    private final TextField campo = new TextField("V Campo Poste Italiane");
    private final TextField cost = new TextField("Costo");

    private final CheckBox omaggio = new CheckBox("Omaggio");
    private final CheckBox pagato = new CheckBox("Pagato");
    private final DateField incasso = new DateField("Incassato");
    private final CheckBox estratti = new CheckBox("Abb. Ann. Estratti");
    private final CheckBox blocchetti = new CheckBox("Abb. Sem. Blocchetti");
    private final CheckBox lodare = new CheckBox("Abb. Men. Lodare e Service");
    private final CheckBox messaggio = new CheckBox("Abb. Men. Messaggio");
    private final TextField spese = new TextField("Spese Spedizione");

    private final ComboBox<Anno> anno = new ComboBox<Anno>("Selezionare Anno",
                                                           EnumSet.allOf(Anno.class));
    private final ComboBox<Mese> inizio = new ComboBox<Mese>("Selezionare Inizio",
                                                             EnumSet.allOf(Mese.class));
    private final ComboBox<Mese> fine = new ComboBox<Mese>("Selezionare Fine",
                                                           EnumSet.allOf(Mese.class));
    private final ComboBox<ContoCorrentePostale> contoCorrentePostale = new ComboBox<ContoCorrentePostale>("Selezionare ccp",
                                                                                                           EnumSet.allOf(ContoCorrentePostale.class));

    HorizontalLayout pri = new HorizontalLayout(anagrafica, destinatario,
                                                anno, inizio, fine);
    HorizontalLayout sec = new HorizontalLayout(campo, cost,
                                                contoCorrentePostale);
    HorizontalLayout che = new HorizontalLayout(estratti, blocchetti, lodare,
                                                messaggio, spese);
    HorizontalLayout pag = new HorizontalLayout(omaggio, pagato);
    HorizontalLayout pagfield = new HorizontalLayout(incasso);

    public AbbonamentoEditor(AbbonamentoDao repo, AnagraficaDao anagraficaDao,
            PubblicazioneDao pubblDao) {

        super(repo,new Binder<>(Abbonamento.class));

        addComponents(pri, sec, che, pag, pagfield, getActions());
        setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);

        anno.setItemCaptionGenerator(Anno::getAnnoAsString);

        inizio.setItemCaptionGenerator(Mese::getNomeBreve);
        fine.setItemCaptionGenerator(Mese::getNomeBreve);

        contoCorrentePostale.setItemCaptionGenerator(ContoCorrentePostale::getCcp);

        anagrafica.setItems(anagraficaDao.findAll());
        anagrafica.setItemCaptionGenerator(Anagrafica::getCaption);
        destinatario.setItems(anagraficaDao.findAll());
        destinatario.setItemCaptionGenerator(Anagrafica::getCaption);

        getBinder().forField(anagrafica).asRequired().withValidator(an -> an != null,
                                                               "Scegliere un Cliente").bind(Abbonamento::getIntestatario,
                                                                                            Abbonamento::setIntestatario);
        getBinder().forField(destinatario).bind("destinatario");
        getBinder().forField(anno).bind("anno");
        getBinder().forField(inizio).bind("inizio");
        getBinder().forField(fine).bind("fine");
        getBinder().forField(contoCorrentePostale).bind("contoCorrentePostale");

        getBinder().forField(campo).asRequired().withValidator(ca -> ca != null,
                                                          "Deve essere definito").bind(Abbonamento::getCampo,
                                                                                       Abbonamento::setCampo);
        getBinder().forField(cost).asRequired().withConverter(new StringToBigDecimalConverter("Conversione in Eur")).bind(Abbonamento::getCost,
                                                                                                                     Abbonamento::setCost);
        getBinder().forField(lodare).bind("lodare");
        getBinder().forField(messaggio).bind("messaggio");
        getBinder().forField(estratti).bind("estratti");
        getBinder().forField(blocchetti).bind("blocchetti");
        getBinder().forField(spese).asRequired().withConverter(new StringToBigDecimalConverter("Conversione in Eur")).bind(Abbonamento::getSpese,
                                                                                                                      Abbonamento::setSpese);
        getBinder().forField(pagato).bind("pagato");
        getBinder().forField(omaggio).bind("omaggio");
        getBinder().forField(incasso).withConverter(new LocalDateToDateConverter()).bind("incasso");

        // Configure and style components
        setSpacing(true);

        setVisible(false);

    }

    @Override
    public void focus(boolean persisted, Abbonamento abbonamento) {
        getCancel().setVisible(persisted);

        anagrafica.setReadOnly(persisted);
        destinatario.setReadOnly(persisted);

        estratti.setReadOnly(persisted);
        blocchetti.setReadOnly(persisted);
        lodare.setReadOnly(persisted);
        messaggio.setReadOnly(persisted);
        spese.setReadOnly(persisted);

        anno.setReadOnly(persisted);
        inizio.setReadOnly(persisted);
        fine.setReadOnly(persisted);
        cost.setVisible(persisted);
        cost.setReadOnly(persisted);
        campo.setVisible(persisted);
        campo.setReadOnly(persisted);

        if (persisted && abbonamento.getCost() == BigDecimal.ZERO) {
            getSave().setEnabled(false);
            getCancel().setEnabled(false);
            omaggio.setReadOnly(true);
            pagato.setVisible(false);
            pagato.setReadOnly(true);
            incasso.setVisible(false);
            incasso.setReadOnly(true);
            return;

        }

        if (persisted && abbonamento.isPagato()) {
            getSave().setEnabled(false);
            getCancel().setEnabled(false);
            omaggio.setReadOnly(true);
            pagato.setVisible(true);
            pagato.setReadOnly(true);
            incasso.setVisible(true);
            incasso.setReadOnly(true);
            return;
        }

        if (persisted) {
            getSave().setEnabled(true);
            getCancel().setEnabled(true);
            omaggio.setReadOnly(true);
            pagato.setVisible(true);
            incasso.setVisible(true);
            return;
        }

        getSave().setEnabled(true);
        getCancel().setEnabled(false);
        omaggio.setReadOnly(false);
        pagato.setVisible(false);
        incasso.setVisible(false);

        anagrafica.focus();

    }

}
