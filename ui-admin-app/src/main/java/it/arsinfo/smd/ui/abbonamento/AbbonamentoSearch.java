package it.arsinfo.smd.ui.abbonamento;

import java.util.EnumSet;
import java.util.List;

import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;

import it.arsinfo.smd.service.api.AbbonamentoService;
import it.arsinfo.smd.entity.Anno;
import it.arsinfo.smd.entity.Incassato;
import it.arsinfo.smd.entity.StatoRivista;
import it.arsinfo.smd.entity.TipoAbbonamentoRivista;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Campagna;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.ui.vaadin.SmdSearch;

public class AbbonamentoSearch extends SmdSearch<Abbonamento> {

    private String searchCodeLine;
    private Anagrafica intestatario;
    private Anagrafica beneficiario;
    private Anno anno;
    private Campagna campagna;
    private final ComboBox<Incassato> filterIncassato= new ComboBox<>();
    private final CheckBox checkContrassegno = new CheckBox("Contrassegno");
    private final CheckBox checkSollecitato = new CheckBox("Sollecitato");
    private final CheckBox checkInviatoEC = new CheckBox("InviatoEC");
    private final CheckBox checkResiduo = new CheckBox("Residuo > 0");
    private final CheckBox checkNotResiduo = new CheckBox("Residuo < 0");
    private final CheckBox checkResiduoZero = new CheckBox("Residuo = 0");
    private final ComboBox<StatoRivista> filterStatoRivista= new ComboBox<>();
    // this are used on RivistaAbbonamento
    private Pubblicazione pubblicazione;
    private final ComboBox<TipoAbbonamentoRivista> filterTipoAbbonamentoRivista = new ComboBox<>();
    
    private final AbbonamentoService dao;

    public AbbonamentoSearch(AbbonamentoService dao, List<Campagna> campagne, List<Pubblicazione> pubblicazioni,
                             List<Anagrafica> anagrafica) {
        super(dao);

        this.dao=dao;
        ComboBox<Anagrafica> filterIntestatario = new ComboBox<>();
        ComboBox<Anagrafica> filterBeneficiario = new ComboBox<>();
        
        ComboBox<Anno> filterAnno = new ComboBox<>();
        ComboBox<Campagna> filterCampagna = new ComboBox<>();
        ComboBox<Pubblicazione> filterPubblicazione = new ComboBox<>();
        TextField filterCodeLine = new TextField();

        HorizontalLayout anag = new HorizontalLayout(filterCodeLine);
        anag.addComponentsAndExpand(filterIntestatario,filterBeneficiario);
        HorizontalLayout riv = new HorizontalLayout(filterPubblicazione);
        riv.addComponentsAndExpand(filterTipoAbbonamentoRivista);
        HorizontalLayout tipo = new HorizontalLayout(filterAnno,filterCampagna,filterIncassato);
        HorizontalLayout check = new HorizontalLayout(
        							checkSollecitato,checkInviatoEC,checkContrassegno,
        							checkNotResiduo,checkResiduoZero,checkResiduo);
        setComponents(anag,riv,tipo,check);
        
        filterCodeLine.setPlaceholder("Cerca Code Line");
        filterCodeLine.setValueChangeMode(ValueChangeMode.LAZY);
        filterCodeLine.addValueChangeListener(e -> {
            searchCodeLine = e.getValue();
            onChange();
        });

        filterIntestatario.setEmptySelectionAllowed(true);
        filterIntestatario.setPlaceholder("Cerca per Intestatario");
        filterIntestatario.setItems(anagrafica);
        filterIntestatario.setItemCaptionGenerator(Anagrafica::getCaption);
        filterIntestatario.addSelectionListener(e -> {
            if (e.getValue() == null) {
                intestatario = null;
            } else {
                intestatario = e.getSelectedItem().orElse(null);
            }
            onChange();
        });

        filterBeneficiario.setEmptySelectionAllowed(true);
        filterBeneficiario.setPlaceholder("Cerca per Beneficiario");
        filterBeneficiario.setItems(anagrafica);
        filterBeneficiario.setItemCaptionGenerator(Anagrafica::getCaption);
        filterBeneficiario.addSelectionListener(e -> {
            if (e.getValue() == null) {
                beneficiario = null;
            } else {
                beneficiario = e.getSelectedItem().orElse(null);
            }
            onChange();
        });

        filterPubblicazione.setEmptySelectionAllowed(true);
        filterPubblicazione.setPlaceholder("Cerca Abb. per Riviste");
        filterPubblicazione.setItems(pubblicazioni);
        filterPubblicazione.setItemCaptionGenerator(Pubblicazione::getNome);
        filterPubblicazione.addSelectionListener(e -> {
            if (e.getValue() == null) {
                pubblicazione = null;
            } else {
                pubblicazione = e.getSelectedItem().orElse(null);
            }
            onChange();
        });

        filterStatoRivista.setPlaceholder("Cerca Abb. Stato Riviste");
        filterStatoRivista.setItems(EnumSet.allOf(StatoRivista.class));
        filterStatoRivista.addSelectionListener(e ->onChange());

        filterTipoAbbonamentoRivista.setPlaceholder("Cerca Abb per TipoAbbonamento Riviste");
        filterTipoAbbonamentoRivista.setItems(EnumSet.allOf(TipoAbbonamentoRivista.class));
        filterTipoAbbonamentoRivista.addSelectionListener(e ->onChange());

        filterAnno.setEmptySelectionAllowed(true);
        filterAnno.setPlaceholder("Cerca per Anno");
        filterAnno.setItems(EnumSet.allOf(Anno.class));
        filterAnno.setItemCaptionGenerator(Anno::getAnnoAsString);
        filterAnno.addSelectionListener(e -> {
            if (e.getValue() == null) {
                anno = null;
            } else {
                anno=e.getSelectedItem().orElse(null);
            }
            onChange();
        });

        filterCampagna.setEmptySelectionAllowed(true);
        filterCampagna.setPlaceholder("Cerca per Campagna");
        filterCampagna.setItems(campagne);
        filterCampagna.setItemCaptionGenerator(Campagna::getCaption);
        filterCampagna.addSelectionListener(e -> {
            if (e.getValue() == null) {
                campagna = null;
            } else {
                campagna=e.getSelectedItem().orElse(null);
            }
            onChange();
        });

        filterIncassato.setEmptySelectionAllowed(true);
        filterIncassato.setPlaceholder("Cerca per Incassato");
        filterIncassato.setItems(EnumSet.allOf(Incassato.class));
        filterIncassato.addSelectionListener(e -> onChange());
        
        checkContrassegno.addValueChangeListener(e -> onChange());
        checkSollecitato.addValueChangeListener(e -> onChange());
        checkInviatoEC.addValueChangeListener(e -> onChange());
        checkResiduo.addValueChangeListener(e -> onChange());
        checkResiduoZero.addValueChangeListener(e -> onChange());
        checkNotResiduo.addValueChangeListener(e -> onChange());
    }

    @Override
    public List<Abbonamento> find() {
    	return dao.searchBy(
    					campagna,
    					intestatario,
    					beneficiario,
    					anno,
    					pubblicazione,
    					filterTipoAbbonamentoRivista.getValue(),
    					filterStatoRivista.getValue(),
    					filterIncassato.getValue(),
    					searchCodeLine,
    					checkContrassegno.getValue(),
    					checkResiduo.getValue(),
    					checkNotResiduo.getValue(),
    					checkResiduoZero.getValue(),
    					checkSollecitato.getValue(),
    					checkInviatoEC.getValue()
    			);
    }

}
