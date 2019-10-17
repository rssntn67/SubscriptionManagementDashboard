package it.arsinfo.smd.ui.vaadin;

import java.math.BigDecimal;
import java.util.ArrayList;
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
import it.arsinfo.smd.data.Ccp;
import it.arsinfo.smd.data.Incassato;
import it.arsinfo.smd.data.StatoAbbonamento;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Campagna;
import it.arsinfo.smd.entity.EstrattoConto;
import it.arsinfo.smd.entity.Versamento;
import it.arsinfo.smd.repository.AbbonamentoDao;
import it.arsinfo.smd.repository.VersamentoDao;

public class AbbonamentoEditor extends SmdEditor<Abbonamento> {

    private final ComboBox<Anagrafica> intestatario = new ComboBox<Anagrafica>("Intestatario");
    private final ComboBox<Campagna> campagna = new ComboBox<Campagna>("Campagna");
    private final ComboBox<StatoAbbonamento> statoAbbonamento = new ComboBox<StatoAbbonamento>("Stato",
            EnumSet.allOf(StatoAbbonamento.class));

    private final ComboBox<Anno> anno = new ComboBox<Anno>("Selezionare Anno",
            EnumSet.allOf(Anno.class));

    private final TextField importo = new TextField("Importo");
    private final TextField spese = new TextField("Spese");
    private final TextField pregresso = new TextField("Pregresso");
    private final TextField totale = new TextField("Totale");
    private final TextField residuo = new TextField("Residuo");
    private final TextField incassato = new TextField("Incassato");
    private final ComboBox<Cassa> cassa = new ComboBox<Cassa>("Cassa",
            EnumSet.allOf(Cassa.class));
    private final TextField codeLine = new TextField("Code Line");
    private final ComboBox<Ccp> ccp = new ComboBox<Ccp>("Selezionare ccp",
            EnumSet.allOf(Ccp.class));

    List<EstrattoConto> estrattiConto = new ArrayList<>();
    private final ComboBox<Incassato> statoIncasso = new ComboBox<Incassato>("Incassato",EnumSet.allOf(Incassato.class));
    
    private final DateField dataContabile = new DateField("Data contabile");
    private final DateField dataPagamento = new DateField("Data pagamento");

    private final VersamentoDao versamentoDao;
    public AbbonamentoEditor(VersamentoDao versamentoDao, AbbonamentoDao abbonamentoDao, List<Anagrafica> anagrafica, List<Campagna> campagne) {

        super(abbonamentoDao,new Binder<>(Abbonamento.class));
        this.versamentoDao = versamentoDao;
        HorizontalLayout anag = new HorizontalLayout(intestatario,statoAbbonamento,campagna,
                                                    anno);
        HorizontalLayout stato = new HorizontalLayout(statoIncasso,cassa,codeLine,
                                                    ccp);
        
        HorizontalLayout imp = new HorizontalLayout(importo,spese,pregresso,totale);

        HorizontalLayout incss = new HorizontalLayout(incassato,residuo,dataContabile,dataPagamento);

        setComponents(getActions(),anag, stato,imp, incss);
        
        campagna.setItems(campagne);
        campagna.setItemCaptionGenerator(Campagna::getCaption);

        intestatario.setItems(anagrafica);
        intestatario.setItemCaptionGenerator(Anagrafica::getCaption);
        intestatario.setEmptySelectionAllowed(false);

        anno.setItemCaptionGenerator(Anno::getAnnoAsString);
        anno.setEmptySelectionAllowed(false);

        statoAbbonamento.setReadOnly(true);
        campagna.setReadOnly(true);
        importo.setReadOnly(true);
        totale.setReadOnly(true);
        spese.setReadOnly(false);
        pregresso.setReadOnly(false);
        incassato.setReadOnly(true);
        residuo.setReadOnly(true);
        codeLine.setReadOnly(true);
        cassa.setEmptySelectionAllowed(false);
        ccp.setItemCaptionGenerator(Ccp::getCcp);

        dataContabile.setReadOnly(true);
        dataPagamento.setReadOnly(true);

        getBinder().forField(intestatario)
            .asRequired()
            .withValidator(an -> an != null,"Scegliere un Cliente")
            .bind(Abbonamento::getIntestatario,Abbonamento::setIntestatario);
        getBinder().forField(campagna).bind(Abbonamento::getCampagna, Abbonamento::setCampagna);
        getBinder().forField(anno).asRequired().bind("anno");
        getBinder().forField(cassa).bind("cassa");
        getBinder().forField(codeLine).asRequired().withValidator(ca -> ca != null,
                "Deve essere definito").bind(Abbonamento::getCodeLine,
                                             Abbonamento::setCodeLine);
        getBinder().forField(ccp).bind("ccp");
        

        getBinder()
        .forField(importo)
        .withConverter(new StringToBigDecimalConverter("Conversione in Eur"))
        .bind("importo");
        getBinder()
        .forField(spese)
        .withConverter(new StringToBigDecimalConverter("Conversione in Eur"))
        .bind("spese");
        getBinder()
        .forField(pregresso)
        .withConverter(new StringToBigDecimalConverter("Conversione in Eur"))
        .bind("pregresso");
        getBinder()
        .forField(incassato)
        .withConverter(new StringToBigDecimalConverter("Conversione in Eur"))
        .bind("incassato");
        getBinder()
            .forField(totale)
            .withConverter(new StringToBigDecimalConverter("Conversione in Eur"))
            .bind("totale");
        getBinder()
            .forField(residuo)
            .withConverter(new StringToBigDecimalConverter("Conversione in Eur"))
            .bind("residuo");
        getBinder().forField(statoIncasso).bind("statoIncasso");
        getBinder().forField(statoAbbonamento).bind("statoAbbonamento");
        getBinder().forField(dataContabile)
        .withConverter(new LocalDateToDateConverter())
        .bind("dataContabile");
        getBinder().forField(dataPagamento)
        .withConverter(new LocalDateToDateConverter())
        .bind("dataPagamento");


    }

    @Override
    public void focus(boolean persisted, Abbonamento abbonamento) {

        getDelete().setEnabled(abbonamento.getStatoAbbonamento() == StatoAbbonamento.Nuovo);
        getSave().setEnabled(!persisted || abbonamento.getCampagna() == null || abbonamento.getVersamento() == null);
        getCancel().setEnabled(!persisted || abbonamento.getCampagna() == null || abbonamento.getVersamento() == null);
        intestatario.setReadOnly(persisted);
        anno.setReadOnly(persisted);
        codeLine.setVisible(persisted);
        codeLine.setReadOnly(persisted);
        ccp.setReadOnly(persisted);
        statoIncasso.setVisible(persisted);
        cassa.setReadOnly(persisted);
        campagna.setVisible(persisted);
        statoAbbonamento.setReadOnly(abbonamento.getCampagna() != null);

        if (persisted && 
                abbonamento.getTotale().doubleValue() == BigDecimal.ZERO.doubleValue()) {
            cassa.setVisible(false);
            codeLine.setVisible(false);
            ccp.setVisible(false);
        } else if (!persisted ){
            cassa.setVisible(true);
            ccp.setVisible(true);
            intestatario.focus();
        }
        
        if (abbonamento.getVersamento() == null) {
            dataContabile.setVisible(false);
            dataPagamento.setVisible(false); 
        } else {
            Versamento versamento = versamentoDao.findById(abbonamento.getVersamento().getId()).get();
            abbonamento.setDataContabile(versamento.getDataContabile());
            abbonamento.setDataPagamento(versamento.getDataPagamento());
            dataContabile.setVisible(true);
            dataPagamento.setVisible(true); 
            pregresso.setReadOnly(true);
            spese.setReadOnly(true);
        }
    }

    public void addEstrattoConto(EstrattoConto estrattoConto) {
        if (estrattiConto.contains(estrattoConto)) {
            estrattiConto.remove(estrattoConto);
        }
        estrattiConto.add(estrattoConto);
    }
    
    public boolean remove(EstrattoConto estrattoconto) {
        if (estrattoconto.getId() != null) {
            return removeEstrattoContoById(estrattoconto.getId());
        }
        return estrattiConto.remove(estrattoconto);
    }
    
    private boolean removeEstrattoContoById(Long id) {
        List<EstrattoConto> ecs = new ArrayList<>();
        boolean match = false;
        for (EstrattoConto ec : estrattiConto) {
            if (ec.getId() != null && ec.getId().longValue() == id.longValue()) {
                match=true;
                continue;
            }
            ecs.add(ec);
        }
        
        return match;
    }
    public List<EstrattoConto> getEstrattiConto() {
        return estrattiConto;
    }

    public void setEstrattiConto(List<EstrattoConto> estrattiConto) {
        this.estrattiConto = estrattiConto;
    }
}
