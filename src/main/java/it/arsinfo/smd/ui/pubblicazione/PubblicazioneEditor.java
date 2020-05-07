package it.arsinfo.smd.ui.pubblicazione;

import java.util.EnumSet;

import com.vaadin.data.Binder;
import com.vaadin.data.converter.StringToBigDecimalConverter;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;

import it.arsinfo.smd.dao.PubblicazioneServiceDao;
import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.Mese;
import it.arsinfo.smd.data.TipoPubblicazione;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.ui.vaadin.SmdServiceDaoEditor;

public class PubblicazioneEditor extends SmdServiceDaoEditor<Pubblicazione> {

     private TextField nome = new TextField("Nome");


    public PubblicazioneEditor(PubblicazioneServiceDao repo) {
        super(repo,new Binder<>(Pubblicazione.class));

        TextField descrizione = new TextField("Descrizione");
        TextField autore = new TextField("Autore");
        TextField editore = new TextField("Editore");
        ComboBox<TipoPubblicazione> tipo = new ComboBox<TipoPubblicazione>("Tipo",EnumSet.allOf(TipoPubblicazione.class));
        ComboBox<Anno> anno = new ComboBox<Anno>("Anno Pubblicazione", EnumSet.allOf(Anno.class));
        TextField grammi = new TextField("Peso in grammi");
        TextField costoUnitario = new TextField("Costo Unitario");
        TextField abbonamento=new TextField("Abbonamento");
        TextField abbonamentoWeb=new TextField("Abbonamento Web");
        TextField abbonamentoSostenitore=new TextField("Abbonamento Sostenitore");
        TextField abbonamentoConSconto=new TextField("Abbonamento con Sconto");

        CheckBox gen = new CheckBox(Mese.GENNAIO.getNomeBreve());
        CheckBox feb = new CheckBox(Mese.FEBBRAIO.getNomeBreve());
        CheckBox mar = new CheckBox(Mese.MARZO.getNomeBreve());
        CheckBox apr = new CheckBox(Mese.APRILE.getNomeBreve());
        CheckBox mag = new CheckBox(Mese.MAGGIO.getNomeBreve());
        CheckBox giu = new CheckBox(Mese.GIUGNO.getNomeBreve());
        CheckBox lug = new CheckBox(Mese.LUGLIO.getNomeBreve());
        CheckBox ago = new CheckBox(Mese.AGOSTO.getNomeBreve());
        CheckBox set = new CheckBox(Mese.SETTEMBRE.getNomeBreve());
        CheckBox ott = new CheckBox(Mese.OTTOBRE.getNomeBreve());
        CheckBox nov = new CheckBox(Mese.NOVEMBRE.getNomeBreve());
        CheckBox dic = new CheckBox(Mese.DICEMBRE.getNomeBreve());

        TextField anticipoSpedizione = new TextField("Anticipo Spedizione Mesi");

        CheckBox active = new CheckBox("Active");

        HorizontalLayout denominazione = new HorizontalLayout(nome);
        denominazione.addComponentsAndExpand(tipo, descrizione);
        HorizontalLayout dettagli = new HorizontalLayout(
                                               autore,editore,anno,anticipoSpedizione,grammi
                                               );
        HorizontalLayout costi = new HorizontalLayout(
                                               costoUnitario,
                                               abbonamento,
                                               abbonamentoConSconto,
                                               abbonamentoSostenitore,
                                               abbonamentoWeb
                                                  );
        HorizontalLayout checkAttivo = new HorizontalLayout(active);
        HorizontalLayout mesi = new HorizontalLayout(gen,feb,mar,apr,mag,giu,lug,ago,set,ott,nov,dic);

        setComponents(getActions(),denominazione,dettagli, costi, mesi,checkAttivo);
        
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

        getBinder().forField(grammi)
        .asRequired()
        .withConverter(new StringToIntegerConverter("Deve essere un intero"))
        .withValidator(i -> i > 0, "Deve essere maggiore di 0")
        .bind(Pubblicazione::getGrammi,
                                        Pubblicazione::setGrammi);

        getBinder().forField(costoUnitario)
            .asRequired()
            .withConverter(new StringToBigDecimalConverter("Conversione in Eur"))
            .withValidator(f -> f.signum() == 1,"Deve essere maggiore di 0")
            .bind(Pubblicazione::getCostoUnitario,Pubblicazione::setCostoUnitario);

        getBinder().forField(abbonamento)
        .asRequired()
        .withConverter(new StringToBigDecimalConverter("Conversione in Eur"))
        .withValidator(f -> f.signum() == 1,"Deve essere maggiore di 0")
        .bind(Pubblicazione::getAbbonamento,Pubblicazione::setAbbonamento);

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

        getBinder().forField(abbonamentoSostenitore)
        .asRequired()
        .withConverter(new StringToBigDecimalConverter("Conversione in Eur"))
        .withValidator(f -> f.signum() == 1,"Deve essere maggiore di 0")
        .bind(Pubblicazione::getAbbonamentoSostenitore,Pubblicazione::setAbbonamentoSostenitore);

        getBinder().forField(active).bind(Pubblicazione::isActive,
                                          Pubblicazione::setActive);

        getBinder().forField(gen).bind(Pubblicazione::isGen,
                                          Pubblicazione::setGen);
        getBinder().forField(feb).bind(Pubblicazione::isFeb,
                                       Pubblicazione::setFeb);
        getBinder().forField(mar).bind(Pubblicazione::isMar,
                                       Pubblicazione::setMar);
        getBinder().forField(apr).bind(Pubblicazione::isApr,
                                       Pubblicazione::setApr);
        getBinder().forField(mag).bind(Pubblicazione::isMag,
                                       Pubblicazione::setMag);
        getBinder().forField(giu).bind(Pubblicazione::isGiu,
                                       Pubblicazione::setGiu);
        getBinder().forField(lug).bind(Pubblicazione::isLug,
                                       Pubblicazione::setLug);
        getBinder().forField(ago).bind(Pubblicazione::isAgo,
                                       Pubblicazione::setAgo);
        getBinder().forField(set).bind(Pubblicazione::isSet,
                                       Pubblicazione::setSet);
        getBinder().forField(ott).bind(Pubblicazione::isOtt,
                                       Pubblicazione::setOtt);
        getBinder().forField(nov).bind(Pubblicazione::isNov,
                                       Pubblicazione::setNov);
        getBinder().forField(dic).bind(Pubblicazione::isDic,
                                       Pubblicazione::setDic);



        anno.setItemCaptionGenerator(Anno::getAnnoAsString);
        tipo.setItemCaptionGenerator(TipoPubblicazione::getDescrizione);
    }

    @Override
    public void focus(boolean persisted, Pubblicazione obj) {
        nome.focus();

    }
}
