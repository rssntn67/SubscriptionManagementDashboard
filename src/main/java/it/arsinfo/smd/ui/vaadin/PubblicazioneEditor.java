package it.arsinfo.smd.ui.vaadin;

import java.util.EnumSet;

import com.vaadin.data.Binder;
import com.vaadin.data.converter.StringToBigDecimalConverter;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;

import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.Mese;
import it.arsinfo.smd.data.TipoPubblicazione;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.repository.PubblicazioneDao;

public class PubblicazioneEditor extends SmdEditor<Pubblicazione> {

    private final TextField nome = new TextField("Nome");
    private final TextField autore = new TextField("Autore");
    private final TextField editore = new TextField("Editore");
    private final ComboBox<TipoPubblicazione> tipo = new ComboBox<TipoPubblicazione>("Tipo",EnumSet.allOf(TipoPubblicazione.class));
    private final ComboBox<Mese> mese = new ComboBox<Mese>("Mese Pubblicazione",EnumSet.allOf(Mese.class));
    private final ComboBox<Anno> anno = new ComboBox<Anno>("Anno Pubblicazione", EnumSet.allOf(Anno.class));
    private final TextField costoUnitario = new TextField("Costo Unitario");
    private final TextField costoScontato = new TextField("Costo Scontato");

    private final CheckBox active = new CheckBox("Active");

    HorizontalLayout basic = new HorizontalLayout(nome, tipo, autore,
                                                  editore);
    HorizontalLayout costi = new HorizontalLayout(costoUnitario, costoScontato,
                                                  mese,anno);
    HorizontalLayout check = new HorizontalLayout(active);

    public PubblicazioneEditor(PubblicazioneDao repo) {

        super(repo,new Binder<>(Pubblicazione.class));
        setComponents(getActions(),basic, costi, check);
        
        getBinder().forField(nome)
            .asRequired("Il Nome della Pubblicazione e' abbligatorio")
            .withValidator(nm -> nm != null || "".equals(nm), "il nome non può essere nullo")
            .bind(Pubblicazione::getNome,
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
        
        getBinder().forField(mese).bind(Pubblicazione::getMese,
                                                 Pubblicazione::setMese);
        getBinder().forField(anno).bind(Pubblicazione::getAnno,
                                        Pubblicazione::setAnno);
       

        anno.setItemCaptionGenerator(Anno::getAnnoAsString);
        mese.setItemCaptionGenerator(Mese::getNomeBreve);
        tipo.setItemCaptionGenerator(TipoPubblicazione::getDescrizione);
    }

    @Override
    public void focus(boolean persisted, Pubblicazione obj) {
        nome.focus();

    }
}
