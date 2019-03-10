package it.arsinfo.smd.vaadin.ui;

import java.util.EnumSet;

import com.vaadin.data.Binder;
import com.vaadin.data.converter.StringToBigDecimalConverter;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;

import it.arsinfo.smd.data.Mese;
import it.arsinfo.smd.data.TipoPubblicazione;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.repository.PubblicazioneDao;
import it.arsinfo.smd.vaadin.model.SmdEditor;

public class PubblicazioneEditor extends SmdEditor<Pubblicazione> {

    /**
     * 
     */
    private static final long serialVersionUID = 4673834235533544936L;

    private final TextField nome = new TextField("Nome");
    private final TextField autore = new TextField("Autore");
    private final TextField editore = new TextField("Editore");
    private final ComboBox<TipoPubblicazione> tipo = new ComboBox<TipoPubblicazione>("Tipo",
                                                                                     EnumSet.allOf(TipoPubblicazione.class));
    private final ComboBox<Mese> primaPubblicazione = new ComboBox<Mese>("Prima Pubblicazione",
                                                                         EnumSet.allOf(Mese.class));
    private final TextField costoUnitario = new TextField("Costo Unitario");
    private final TextField costoScontato = new TextField("Costo Scontato");

    private final CheckBox active = new CheckBox("Active");
    private final CheckBox abbonamento = new CheckBox("Abbonamento");

    HorizontalLayout basic = new HorizontalLayout(nome, tipo, autore,
                                                  editore);
    HorizontalLayout costi = new HorizontalLayout(costoUnitario, costoScontato,
                                                  primaPubblicazione);
    HorizontalLayout check = new HorizontalLayout(active, abbonamento);

    public PubblicazioneEditor(PubblicazioneDao repo) {

        super(repo,new Binder<>(Pubblicazione.class));
        addComponents(basic, costi, check, getActions());
        setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);

        getBinder().forField(nome).asRequired("Il Nome della Pubblicazione e' abbligatorio").bind(Pubblicazione::getNome,
                                                                                             Pubblicazione::setNome);
        getBinder().forField(tipo).asRequired("Il Tipo di pubblicazione e' obbligatorio").bind(Pubblicazione::getTipo,
                                                                                          Pubblicazione::setTipo);
        getBinder().bind(autore, Pubblicazione::getAutore,
                    Pubblicazione::setAutore);
        getBinder().bind(editore, Pubblicazione::getEditore,
                    Pubblicazione::setEditore);
        getBinder().forField(costoUnitario).asRequired().withConverter(new StringToBigDecimalConverter("Conversione in Eur")).withValidator(f -> f.signum() == 1,
                                                                                                                               "Deve essere maggiore di 0").bind(Pubblicazione::getCostoUnitario,
                                                                                                                                                                 Pubblicazione::setCostoUnitario);
        getBinder().forField(costoScontato).asRequired().withConverter(new StringToBigDecimalConverter("Conversione in Eur")).withValidator(f -> f.signum() == 1,
                                                                                                                                       "Deve essere maggiore di 0").bind(Pubblicazione::getCostoScontato,
                                                                                                                                                                         Pubblicazione::setCostoScontato);
        getBinder().forField(active).bind(Pubblicazione::isActive,
                                     Pubblicazione::setActive);
        getBinder().forField(abbonamento).bind(Pubblicazione::isAbbonamento,
                                          Pubblicazione::setAbbonamento);
        getBinder().forField(primaPubblicazione).bind(Pubblicazione::getPrimaPubblicazione,
                                                 Pubblicazione::setPrimaPubblicazione);
        // Configure and style components
        setSpacing(true);

        primaPubblicazione.setItemCaptionGenerator(Mese::getNomeBreve);

    }

    @Override
    public void focus(boolean persisted, Pubblicazione obj) {
        nome.focus();

    }

}
