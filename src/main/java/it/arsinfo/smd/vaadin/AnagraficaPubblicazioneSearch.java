package it.arsinfo.smd.vaadin;

import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.data.Binder;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import it.arsinfo.smd.data.CentroDiocesano;
import it.arsinfo.smd.data.Diocesi;
import it.arsinfo.smd.data.Regione;
import it.arsinfo.smd.data.TitoloAnagrafica;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.AnagraficaPubblicazione;
import it.arsinfo.smd.entity.Paese;
import it.arsinfo.smd.repository.AnagraficaDao;
import it.arsinfo.smd.repository.AnagraficaPubblicazioneDao;


public class AnagraficaPubblicazioneSearch extends SmdEditor {

    /**
     * 
     */
    private static final long serialVersionUID = 4673834235533544936L;

    private final AnagraficaPubblicazioneDao anagraficaPubblicazioneDao;

    /**
     * The currently edited customer
     */
    Grid<AnagraficaPubblicazione> grid;

    public AnagraficaPubblicazioneSearch(AnagraficaPubblicazioneDao anagraficaPubblicazioneDao) {

        this.anagraficaPubblicazioneDao=anagraficaPubblicazioneDao;

        grid = new Grid<>(AnagraficaPubblicazione.class);
        grid.setColumns("id","numero","captionPubblicazione","captionIntestatario","captionDestinatario","omaggio","cassa","invio");             
        grid.setWidth("80%");

        addComponents(
                      grid
                  );
        setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);

        // Configure and style components
        setSpacing(true);

        setVisible(false);

    }
    
    public final void list(Anagrafica customer) {
        List<AnagraficaPubblicazione> list = anagraficaPubblicazioneDao.findByIntestatario(customer);
        list.addAll(anagraficaPubblicazioneDao.findByDestinatario(customer)
                    .stream()
                    .filter(ap -> customer.getId() != ap.getIntestatario().getId())
                    .collect(Collectors.toList()));
        grid.setItems(list);
    }

}
