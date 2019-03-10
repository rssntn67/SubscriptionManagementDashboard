package it.arsinfo.smd.vaadin.ui;

import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Storico;
import it.arsinfo.smd.repository.StoricoDao;
import it.arsinfo.smd.vaadin.model.SmdSearchKey;


public class StoricoSubSearch extends SmdSearchKey<Storico,Anagrafica> {

    /**
     * 
     */
    private static final long serialVersionUID = 4673834235533544936L;

    private final StoricoDao anagraficaPubblicazioneDao;


    public StoricoSubSearch(StoricoDao anagraficaPubblicazioneDao) {
        super(new Grid<>(Storico.class));
        this.anagraficaPubblicazioneDao=anagraficaPubblicazioneDao;

        setColumns("numero","captionPubblicazione","captionIntestatario","captionDestinatario","omaggio","invio");
        setColumnCamptio("captionIntestatario", "Intestatario");
        setColumnCamptio("captionDestinatario", "Destinatario");
        setColumnCamptio("captionPubblicazione", "Pubblicazione");

        HorizontalLayout action = new HorizontalLayout(new Label("Storico"),getAdd());
        addComponents(action,
                      getGrid())
                  ;
        setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);

        // Configure and style components
        setSpacing(true);

    }
    

    @Override
    public Storico generate() {
        return new Storico(getKey());
    }


    @Override
    public List<Storico> searchByKey() {
        Anagrafica customer = getKey();
        List<Storico> list = anagraficaPubblicazioneDao.findByIntestatario(customer);
        list.addAll(anagraficaPubblicazioneDao.findByDestinatario(customer)
                    .stream()
                    .filter(ap -> getKey().getId() != ap.getIntestatario().getId())
                    .collect(Collectors.toList()));
        return list;
    }

}
