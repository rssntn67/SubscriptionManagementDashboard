package it.arsinfo.smd.ui.vaadin;

import java.util.EnumSet;

import com.vaadin.data.Binder;
import com.vaadin.data.converter.StringToBigDecimalConverter;
import com.vaadin.data.converter.StringToIntegerConverter;
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
    private final TextField descrizione = new TextField("Descrizione");
    private final TextField autore = new TextField("Autore");
    private final TextField editore = new TextField("Editore");
    private final ComboBox<TipoPubblicazione> tipo = new ComboBox<TipoPubblicazione>("Tipo",EnumSet.allOf(TipoPubblicazione.class));
    private final ComboBox<Anno> anno = new ComboBox<Anno>("Anno Pubblicazione", EnumSet.allOf(Anno.class));
    private final TextField costoUnitario = new TextField("Costo Unitario");
    private final TextField abbonamentoItalia=new TextField("Abbonamento");
    private final TextField abbonamentoWeb=new TextField("Abbonamento Web");
    private final TextField abbonamentoEuropa=new TextField("Abbonamento Europa-Bac.Med.");
    private final TextField abbonamentoAmericaAsiaAfrica=new TextField("Abbonamento Ame-Asia-Afr");
    private final TextField abbonamentoSostenitore=new TextField("Abbonamento Sostenitore");
    private final TextField abbonamentoConSconto=new TextField("Abbonamento con Sconto");

    private final CheckBox gen = new CheckBox(Mese.GENNAIO.getNomeBreve());
    private final CheckBox feb = new CheckBox(Mese.FEBBRAIO.getNomeBreve());
    private final CheckBox mar = new CheckBox(Mese.MARZO.getNomeBreve());
    private final CheckBox apr = new CheckBox(Mese.APRILE.getNomeBreve());
    private final CheckBox mag = new CheckBox(Mese.MAGGIO.getNomeBreve());
    private final CheckBox giu = new CheckBox(Mese.GIUGNO.getNomeBreve());
    private final CheckBox lug = new CheckBox(Mese.LUGLIO.getNomeBreve());
    private final CheckBox ago = new CheckBox(Mese.AGOSTO.getNomeBreve());
    private final CheckBox set = new CheckBox(Mese.SETTEMBRE.getNomeBreve());
    private final CheckBox ott = new CheckBox(Mese.OTTOBRE.getNomeBreve());
    private final CheckBox nov = new CheckBox(Mese.NOVEMBRE.getNomeBreve());
    private final CheckBox dic = new CheckBox(Mese.DICEMBRE.getNomeBreve());

    private final TextField anticipoSpedizione = new TextField("Anticipo Spedizione Mesi");

    private final CheckBox active = new CheckBox("Active");

    HorizontalLayout basic = new HorizontalLayout(nome, tipo, descrizione, autore,
                                                  editore,anno,anticipoSpedizione);
    HorizontalLayout costi = new HorizontalLayout(costoUnitario,
                                                  abbonamentoItalia,
                                                  abbonamentoConSconto,
                                                  abbonamentoSostenitore,
                                                  abbonamentoAmericaAsiaAfrica,
                                                  abbonamentoEuropa,
                                                  abbonamentoWeb
                                                  );
    HorizontalLayout check = new HorizontalLayout(active,gen,feb,mar,apr,mag,giu,lug,ago,set,ott,nov,dic);

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
        getBinder().bind(descrizione, Pubblicazione::getDescrizione,
                         Pubblicazione::setDescrizione);
        getBinder().forField(anno).bind(Pubblicazione::getAnno,
                                             Pubblicazione::setAnno);

        getBinder().forField(anticipoSpedizione)
        .asRequired()
        .withConverter(new StringToIntegerConverter("Deve essere un intero"))
        .withValidator(i -> i > 0, "Deve essere maggiore di 0")
        .bind(Pubblicazione::getAnticipoSpedizione,
                                        Pubblicazione::setAnticipoSpedizione);

        getBinder().forField(costoUnitario)
            .asRequired()
            .withConverter(new StringToBigDecimalConverter("Conversione in Eur"))
            .withValidator(f -> f.signum() == 1,"Deve essere maggiore di 0")
            .bind(Pubblicazione::getCostoUnitario,Pubblicazione::setCostoUnitario);

        getBinder().forField(abbonamentoItalia)
        .asRequired()
        .withConverter(new StringToBigDecimalConverter("Conversione in Eur"))
        .withValidator(f -> f.signum() == 1,"Deve essere maggiore di 0")
        .bind(Pubblicazione::getAbbonamentoItalia,Pubblicazione::setAbbonamentoItalia);

        getBinder().forField(abbonamentoWeb)
        .asRequired()
        .withConverter(new StringToBigDecimalConverter("Conversione in Eur"))
        .withValidator(f -> f.signum() == 1,"Deve essere maggiore di 0")
        .bind(Pubblicazione::getAbbonamentoWeb,Pubblicazione::setAbbonamentoWeb);

        getBinder().forField(abbonamentoConSconto)
        .asRequired()
        .withConverter(new StringToBigDecimalConverter("Conversione in Eur"))
        .withValidator(f -> f.signum() == 1,"Deve essere maggiore di 0")
        .bind(Pubblicazione::getAbbonamentoConSconto,Pubblicazione::setAbbonamentoConSconto);

        getBinder().forField(abbonamentoEuropa)
        .asRequired()
        .withConverter(new StringToBigDecimalConverter("Conversione in Eur"))
        .withValidator(f -> f.signum() == 1,"Deve essere maggiore di 0")
        .bind(Pubblicazione::getAbbonamentoEuropa,Pubblicazione::setAbbonamentoEuropa);

        getBinder().forField(abbonamentoAmericaAsiaAfrica)
        .asRequired()
        .withConverter(new StringToBigDecimalConverter("Conversione in Eur"))
        .withValidator(f -> f.signum() == 1,"Deve essere maggiore di 0")
        .bind(Pubblicazione::getAbbonamentoAmericaAsiaAfrica,Pubblicazione::setAbbonamentoAmericaAsiaAfrica);

        getBinder().forField(abbonamentoSostenitore)
        .asRequired()
        .withConverter(new StringToBigDecimalConverter("Conversione in Eur"))
        .withValidator(f -> f.signum() == 1,"Deve essere maggiore di 0")
        .bind(Pubblicazione::getAbbonamentoSostenitore,Pubblicazione::setAbbonamentoSostenitore);

        getBinder().forField(active).bind(Pubblicazione::isActive,
                                          Pubblicazione::setActive);


        anno.setItemCaptionGenerator(Anno::getAnnoAsString);
        tipo.setItemCaptionGenerator(TipoPubblicazione::getDescrizione);
    }

    @Override
    public void focus(boolean persisted, Pubblicazione obj) {
        nome.focus();

    }
}
