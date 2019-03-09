package it.arsinfo.smd.vaadin.ui;

import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Grid;

import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.AnagraficaPubblicazione;
import it.arsinfo.smd.repository.AnagraficaPubblicazioneDao;
import it.arsinfo.smd.vaadin.model.SmdSearchKey;


public class AnagraficaPubblicazioneSubSearch extends SmdSearchKey<AnagraficaPubblicazione,Anagrafica> {

    /**
     * 
     */
    private static final long serialVersionUID = 4673834235533544936L;

    private final AnagraficaPubblicazioneDao anagraficaPubblicazioneDao;


    public AnagraficaPubblicazioneSubSearch(AnagraficaPubblicazioneDao anagraficaPubblicazioneDao) {
        super(new Grid<>(AnagraficaPubblicazione.class));
        this.anagraficaPubblicazioneDao=anagraficaPubblicazioneDao;

        setColumns("id","numero","captionPubblicazione","captionIntestatario","captionDestinatario","omaggio","cassa","invio");             

        addComponents(
                      getGrid()
                  );
        setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);

        // Configure and style components
        setSpacing(true);

        setVisible(false);

    }
    

    @Override
    public AnagraficaPubblicazione generate() {
        return new AnagraficaPubblicazione(getKey());
    }


    @Override
    public List<AnagraficaPubblicazione> searchByKey() {
        Anagrafica customer = getKey();
        List<AnagraficaPubblicazione> list = anagraficaPubblicazioneDao.findByIntestatario(customer);
        list.addAll(anagraficaPubblicazioneDao.findByDestinatario(customer)
                    .stream()
                    .filter(ap -> getKey().getId() != ap.getIntestatario().getId())
                    .collect(Collectors.toList()));
        return list;
    }

}
