package it.arsinfo.smd.ui.vaadin;

import java.util.EnumSet;

import com.vaadin.data.Binder;
import com.vaadin.data.converter.LocalDateToDateConverter;
import com.vaadin.data.converter.StringToBigDecimalConverter;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;

import it.arsinfo.smd.data.Accettazione;
import it.arsinfo.smd.data.Bollettino;
import it.arsinfo.smd.data.Cassa;
import it.arsinfo.smd.data.Ccp;
import it.arsinfo.smd.data.Cuas;
import it.arsinfo.smd.data.Sostitutivo;
import it.arsinfo.smd.entity.Versamento;
import it.arsinfo.smd.repository.VersamentoDao;

public class VersamentoEditor extends SmdEditor<Versamento> {

    private final TextField  importo = new TextField("Importo");
    private final TextField  incassato = new TextField("Incassato");
    private final TextField  residuo = new TextField("residuo");    
    private final TextField  codeLine = new TextField("CodeLine");
    private final TextField  progressivo = new TextField("Progressivo");
    private final TextField  operazione = new TextField("Operazione");
    
    private final TextField  provincia = new TextField("Identificativo Provincia");
    private final TextField  ufficio = new TextField("Identificativo Ufficio");
    private final TextField  sportello = new TextField("Identificativo Sportello");
    private final TextField bobina = new TextField("Bobina");
    private final TextField progressivoBobina = new TextField("Progressivo Bobina");

    private final ComboBox<Ccp> ccp = new ComboBox<Ccp>("Conto Corrente", EnumSet.allOf(Ccp.class));
    private final ComboBox<Cassa> cassa = new ComboBox<Cassa>("Cassa", EnumSet.allOf(Cassa.class));
    private final ComboBox<Cuas> cuas = new ComboBox<Cuas>("Cuas", EnumSet.allOf(Cuas.class));
    
    private final ComboBox<Bollettino> bollettino = 
            new ComboBox<Bollettino>("Bollettino",EnumSet.allOf(Bollettino.class));
    private final ComboBox<Accettazione> accettazione =
            new ComboBox<Accettazione>("Accettazione",EnumSet.allOf(Accettazione.class));;
    private final ComboBox<Sostitutivo> sostitutivo =
            new ComboBox<Sostitutivo>("Sostitutivo",EnumSet.allOf(Sostitutivo.class));

    private final DateField dataContabile = new DateField("Data contabile");
    private final DateField dataPagamento = new DateField("Data pagamento");

    public VersamentoEditor(VersamentoDao versamentoDao) {
        super(versamentoDao, new Binder<>(Versamento.class));

        ccp.setReadOnly(true);
        ccp.setItemCaptionGenerator(Ccp::getCcp);
        cuas.setReadOnly(true);
        cuas.setItemCaptionGenerator(Cuas::getDenominazione);
        cassa.setReadOnly(true);

        incassato.setReadOnly(true);
        residuo.setReadOnly(true);
        dataContabile.setReadOnly(true);

        bollettino.setItemCaptionGenerator(Bollettino::getBollettino);
        accettazione.setItemCaptionGenerator(Accettazione::getDescr);
        sostitutivo.setItemCaptionGenerator(Sostitutivo::getDescr);
        setComponents(
                      getActions(),
                      new HorizontalLayout(importo,incassato,residuo,dataContabile,dataPagamento),
                      new HorizontalLayout(ccp,cassa,cuas),
                      new HorizontalLayout(operazione,codeLine,progressivo),
                      new HorizontalLayout(provincia,ufficio,sportello,bobina,progressivoBobina),
                      new HorizontalLayout(bollettino,accettazione,sostitutivo)
                  );

        getBinder().forField(importo)
            .asRequired()
            .withConverter(new StringToBigDecimalConverter("Conversione in Eur"))
            .bind(Versamento::getImporto,Versamento::setImporto);
        getBinder().forField(incassato)
            .withConverter(new StringToBigDecimalConverter("Conversione in Eur"))
            .bind(Versamento::getIncassato,Versamento::setIncassato);
        getBinder().forField(residuo)
        .withConverter(new StringToBigDecimalConverter("Conversione in Eur"))
        .bind(Versamento::getResiduo,null);
        getBinder().forField(progressivo)
            .asRequired()
            .withValidator(p -> p != null, "progressivo deve essere valorizzato")
            .bind("progressivo");
        getBinder().forField(dataContabile)
            .asRequired()
            .withConverter(new LocalDateToDateConverter())
            .bind("dataContabile");
        getBinder().forField(dataPagamento)
            .asRequired()
            .withConverter(new LocalDateToDateConverter())
            .bind("dataPagamento");

        getBinder().bindInstanceFields(this);

    }

    @Override
    public void focus(boolean persisted, Versamento versamento) {

        getDelete().setEnabled(!persisted);
        getSave().setEnabled(!persisted);
        getCancel().setEnabled(!persisted);
        
        ccp.setValue(versamento.getIncasso().getCcp());
        cuas.setValue(versamento.getIncasso().getCuas());
        cassa.setValue(versamento.getIncasso().getCassa());
        importo.setReadOnly(persisted);
        incassato.setVisible(persisted);
        residuo.setVisible(persisted);
        dataPagamento.setReadOnly(persisted);
        
        operazione.setReadOnly(persisted);
        codeLine.setReadOnly(persisted);
        progressivo.setReadOnly(persisted);
        
        provincia.setReadOnly(persisted);
        ufficio.setReadOnly(persisted);
        sportello.setReadOnly(persisted);
        bobina.setReadOnly(persisted);
        progressivoBobina.setReadOnly(persisted);
        
        bollettino.setReadOnly(persisted);
        sostitutivo.setReadOnly(persisted);
        accettazione.setReadOnly(persisted);
    }

}