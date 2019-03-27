package it.arsinfo.smd.vaadin.ui;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Binder;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;

import it.arsinfo.smd.data.TipoDocumentoBollettino;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Versamento;
import it.arsinfo.smd.repository.AbbonamentoDao;
import it.arsinfo.smd.repository.VersamentoDao;
import it.arsinfo.smd.vaadin.model.SmdEditor;

public class VersamentoEditor extends SmdEditor<Versamento> {

    private static final Logger log = LoggerFactory.getLogger(VersamentoEditor.class);

    private Grid<Abbonamento> abbonamentiAssociabili;
    private Grid<Abbonamento> abbonamentiAssociati;

    private Label residuo = new Label();

    private Label avviso = new Label();

    private final AbbonamentoDao abbonamentoDao;

    public VersamentoEditor(VersamentoDao versamentoDao,
            AbbonamentoDao abbonamentoDao) {
        super(versamentoDao, new Binder<>(Versamento.class));
        this.abbonamentoDao = abbonamentoDao;

        HorizontalLayout info = new HorizontalLayout(avviso, residuo);
        abbonamentiAssociati = new Grid<>(Abbonamento.class);
        abbonamentiAssociabili = new Grid<>(Abbonamento.class);

        setComponents(info, abbonamentiAssociati, abbonamentiAssociabili);

        abbonamentiAssociati.setColumns("intestatario.cognome",
                                        "intestatario.nome",
                                        "ccp.ccp", "costo",
                                        "campo", "incasso", "incassato");
        abbonamentiAssociati.addComponentColumn(abbonamento -> {
            Button button = new Button("Dissocia");
            button.addClickListener(click -> dissocia(abbonamento));
            button.setEnabled(get().getTipoDocumento() != TipoDocumentoBollettino.TIPO674);
            return button;
        });
        abbonamentiAssociati.setWidth("100%");
        abbonamentiAssociati.setVisible(false);

        abbonamentiAssociabili.setColumns("intestatario.cognome",
                                          "intestatario.nome",
                                          "ccp.ccp", "costo",
                                          "campo", "incasso", "incassato");
        abbonamentiAssociabili.addComponentColumn(abbonamento -> {
            Button button = new Button("Associa");
            button.addClickListener(click -> incassa(abbonamento));
            button.setEnabled(get().getTipoDocumento() != TipoDocumentoBollettino.TIPO674);
            return button;
        });
        abbonamentiAssociabili.setWidth("100%");
        abbonamentiAssociabili.setVisible(false);
        residuo.addStyleName(ValoTheme.LABEL_H3);
        residuo.setVisible(false);
        avviso.addStyleName(ValoTheme.LABEL_H3);
        avviso.setVisible(false);
    }

    private void dissocia(Abbonamento abbonamento) {
        abbonamento.setVersamento(null);
        abbonamentoDao.save(abbonamento);
        edit(get());
    }

    private void incassa(Abbonamento abbonamento) {
        abbonamento.setVersamento(get());
        abbonamentoDao.save(abbonamento);
        edit(get());
    }

    @Override
    public void focus(boolean persisted, Versamento versamento) {
        if (versamento == null) {
            abbonamentiAssociabili.setVisible(false);
            abbonamentiAssociati.setVisible(false);
            residuo.setVisible(false);
            avviso.setVisible(false);
            return;
        }
        List<Abbonamento> matching;
        if (versamento.getTipoDocumento() == TipoDocumentoBollettino.TIPO674) {
            matching = abbonamentoDao.findByCampo(versamento.getCampo());
            abbonamentiAssociabili.setVisible(false);
        } else {
            matching = abbonamentoDao.findByVersamento(versamento);
            abbonamentiAssociabili.setItems(abbonamentoDao.findByCostoGreaterThanAndVersamentoNotNull(BigDecimal.ZERO));
            abbonamentiAssociabili.setVisible(true);
        }
        avviso.setVisible(true);
        residuo.setVisible(true);

        if (matching.size() == 0) {
            residuo.setValue("Residuo EUR: "
                    + versamento.getImporto().toString());
            avviso.setValue("Nessun Abbonamento Trovato Per il Versamento Selezionato");
            abbonamentiAssociati.setVisible(false);
            return;
        }

        abbonamentiAssociati.setItems(matching);
        abbonamentiAssociati.setVisible(true);
        matching.stream().filter(abbonamento -> abbonamento.getVersamento() ==  null && abbonamento.getCosto() != BigDecimal.ZERO).forEach(abbonamento -> {
            log.info("incasso");
            log.info(abbonamento.toString());
            incassa(abbonamento);
        });
        BigDecimal diff = versamento.getImporto();
        for (Abbonamento abbonamento : matching) {
            diff = diff.subtract(abbonamento.getCosto());
        }
        residuo.setValue("Residuo EUR: " + diff.toString());

        if (matching.size() == 1) {
            avviso.setValue("Trovato " + matching.size()
                    + " Abbonamento Per il Versamento Selezionato");
        } else {
            avviso.setValue("Trovati " + matching.size()
                    + " Abbonamenti Per il Versamento Selezionato");
        }
    }

}
