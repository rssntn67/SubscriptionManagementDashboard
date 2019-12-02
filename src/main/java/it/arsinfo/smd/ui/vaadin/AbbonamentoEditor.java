package it.arsinfo.smd.ui.vaadin;

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

import it.arsinfo.smd.Smd;
import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.Cassa;
import it.arsinfo.smd.data.Ccp;
import it.arsinfo.smd.data.Cuas;
import it.arsinfo.smd.data.Incassato;
import it.arsinfo.smd.data.StatoAbbonamento;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Campagna;
import it.arsinfo.smd.entity.EstrattoConto;
import it.arsinfo.smd.repository.AbbonamentoDao;

public class AbbonamentoEditor extends SmdEditor<Abbonamento> {

    private boolean noOmaggio;
    private boolean hasResiduo;

    private final ComboBox<Anagrafica> intestatario = new ComboBox<Anagrafica>("Intestatario");
    private final ComboBox<Campagna> campagna = new ComboBox<Campagna>("Campagna");
    private final ComboBox<StatoAbbonamento> statoAbbonamento = new ComboBox<StatoAbbonamento>("Stato",
            EnumSet.allOf(StatoAbbonamento.class));

    private final ComboBox<Anno> anno = new ComboBox<Anno>("Selezionare Anno",
            EnumSet.allOf(Anno.class));

    private final TextField importo = new TextField("Importo");
    private final TextField spese = new TextField("Spese");
    private final TextField speseEstero = new TextField("Spese Estero");
    private final TextField pregresso = new TextField("Pregresso");
    private final TextField totale = new TextField("Totale");
    private final TextField residuo = new TextField("Residuo");
    private final TextField incassato = new TextField("Incassato");
    private final ComboBox<Cassa> cassa = new ComboBox<Cassa>("Cassa",
            EnumSet.allOf(Cassa.class));
    private final TextField codeLine = new TextField("Code Line");
    private final ComboBox<Ccp> ccp = new ComboBox<Ccp>("Conto Corrente",
            EnumSet.allOf(Ccp.class));
    private final ComboBox<Cuas> cuas = new ComboBox<Cuas>("Cuas",
            EnumSet.allOf(Cuas.class));
    private final TextField progressivo = new TextField("Progressivo");

    List<EstrattoConto> estrattiConto = new ArrayList<>();
    private final ComboBox<Incassato> statoIncasso = new ComboBox<Incassato>("Incassato",EnumSet.allOf(Incassato.class));
    
    private final DateField dataContabile = new DateField("Data contabile");
    private final DateField dataPagamento = new DateField("Data pagamento");

    public AbbonamentoEditor(AbbonamentoDao abbonamentoDao, List<Anagrafica> anagrafica, List<Campagna> campagne) {

        super(abbonamentoDao,new Binder<>(Abbonamento.class));
        
        HorizontalLayout anag = new HorizontalLayout(codeLine);
        anag.addComponentsAndExpand(intestatario);

        HorizontalLayout status = new HorizontalLayout(campagna,
                                                     anno,statoAbbonamento,statoIncasso);
        
        HorizontalLayout imp = new HorizontalLayout(importo,speseEstero,spese,pregresso,totale,incassato,residuo);

        HorizontalLayout incss = new HorizontalLayout(dataContabile,dataPagamento,cassa,ccp,cuas,progressivo);
        HorizontalLayout detai = new HorizontalLayout();
        detai.addComponentsAndExpand(progressivo);
        setComponents(getActions(),anag, status,imp,incss,detai);

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

        cassa.setEmptySelectionAllowed(false);
        ccp.setEmptySelectionAllowed(false);
        ccp.setItemCaptionGenerator(Ccp::getCcp);
        cuas.setEmptySelectionAllowed(false);
        cuas.setItemCaptionGenerator(Cuas::getDenominazione);

        dataContabile.setDateFormat("dd/MM/yyyy");
        dataPagamento.setDateFormat("dd/MM/yyyy");

        getBinder().forField(codeLine).asRequired().withValidator(ca -> ca != null,
                "Deve essere definito").bind(Abbonamento::getCodeLine,
                                             Abbonamento::setCodeLine);
        getBinder().forField(intestatario)
            .asRequired()
            .withValidator(an -> an != null,"Scegliere un Cliente")
            .bind(Abbonamento::getIntestatario,Abbonamento::setIntestatario);

        getBinder().forField(campagna).bind(Abbonamento::getCampagna, Abbonamento::setCampagna);
        getBinder().forField(anno).asRequired().bind("anno");
        getBinder().forField(statoAbbonamento).bind("statoAbbonamento");
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
               
        getBinder().forField(dataContabile).asRequired()
        .withConverter(new LocalDateToDateConverter())
        .bind("dataContabile");
        
        getBinder().forField(dataPagamento).asRequired()
        .withConverter(new LocalDateToDateConverter())
        .bind("dataPagamento");
        getBinder().forField(cassa).bind("cassa");
        getBinder().forField(ccp).bind("ccp");
        getBinder().forField(cuas).bind("cuas");                
        getBinder().forField(progressivo).asRequired().bind("progressivo");                

    }

    @Override
    public void focus(boolean persisted, Abbonamento abbonamento) {

        getDelete().setEnabled(abbonamento.getCampagna() == null && abbonamento.getStatoAbbonamento() == StatoAbbonamento.Nuovo);
        
        codeLine.setVisible(persisted);
        intestatario.setReadOnly(persisted);
        
        campagna.setVisible(persisted);
        anno.setReadOnly(persisted);
        statoAbbonamento.setReadOnly(abbonamento.getCampagna() != null);
        statoIncasso.setVisible(persisted);

        noOmaggio = Smd.getStatoIncasso(abbonamento) != Incassato.Omaggio;
        hasResiduo = abbonamento.getResiduo().signum() > 0; 

        importo.setVisible(noOmaggio);
        spese.setVisible(noOmaggio);
        pregresso.setVisible(noOmaggio);
        totale.setVisible(noOmaggio);
        incassato.setVisible(noOmaggio);
        residuo.setVisible(noOmaggio);
        
        dataContabile.setVisible(noOmaggio && hasResiduo);
        dataPagamento.setVisible(noOmaggio && hasResiduo); 
        cassa.setVisible(noOmaggio && hasResiduo);
        ccp.setVisible(noOmaggio && hasResiduo);
        cuas.setVisible(noOmaggio && hasResiduo);
        progressivo.setVisible(noOmaggio && hasResiduo);
                
        intestatario.focus();

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
    
    public boolean incassare() {
        return noOmaggio && hasResiduo;
    }
}
