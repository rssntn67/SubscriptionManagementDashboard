package it.arsinfo.smd.ui.spedizione;

import java.util.EnumSet;
import java.util.List;

import com.vaadin.data.Binder;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;

import it.arsinfo.smd.dao.repository.SpedizioneItemDao;
import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.Mese;
import it.arsinfo.smd.data.StatoSpedizione;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.entity.SpedizioneItem;
import it.arsinfo.smd.ui.vaadin.SmdRepositoryDaoEditor;

public class SpedizioneItemEditor
        extends SmdRepositoryDaoEditor<SpedizioneItem> {

	private final ComboBox<Pubblicazione> pubblicazione = new ComboBox<Pubblicazione>("Pubblicazione");
    private final ComboBox<StatoSpedizione> statoSpedizione = 
    		new ComboBox<StatoSpedizione>("Stato",EnumSet.allOf(StatoSpedizione.class));

    private final ComboBox<Anno> annoPubblicazione = 
    		new ComboBox<Anno>("Anno Pubblicazione",EnumSet.allOf(Anno.class));
    private final ComboBox<Mese> mesePubblicazione = new ComboBox<Mese>("Mese Pubblicazione",EnumSet.allOf(Mese.class));

    private final CheckBox posticipata = new CheckBox("Posticipata");
    private final TextField numero = new TextField("Quantità");
    public SpedizioneItemEditor(
            SpedizioneItemDao spedizioneItemDao, List<Pubblicazione> pubblicazioni) {

        super(spedizioneItemDao, new Binder<>(SpedizioneItem.class) );

        pubblicazione.setItemCaptionGenerator(Pubblicazione::getNome);
        pubblicazione.setReadOnly(true);
        pubblicazione.setItems(pubblicazioni);
        
        annoPubblicazione.setItemCaptionGenerator(Anno::getAnnoAsString);
        annoPubblicazione.setReadOnly(true);

        mesePubblicazione.setItemCaptionGenerator(Mese::getNomeBreve);
        mesePubblicazione.setReadOnly(true);

        statoSpedizione.setEmptySelectionAllowed(false);
        
        posticipata.setReadOnly(true);
        numero.setReadOnly(true);
        
        setComponents(getActions(), 
                  new HorizontalLayout(statoSpedizione,pubblicazione,numero,mesePubblicazione,annoPubblicazione,posticipata)
                      );
   
        getBinder()
            .forField(numero)
            .asRequired()
            .withConverter(new StringToIntegerConverter("Inserire un Numero"))
            .withValidator(num -> num != null && num > 0,"deve essere maggiore di 0")
            .bind(SpedizioneItem::getNumero,SpedizioneItem::setNumero);

        getBinder().forField(pubblicazione)
        .asRequired().bind(SpedizioneItem::getPubblicazione,SpedizioneItem::setPubblicazione);
        
        getBinder().forField(statoSpedizione)
        .asRequired().bind(SpedizioneItem::getStatoSpedizione,SpedizioneItem::setStatoSpedizione);

        getBinder().forField(mesePubblicazione)
        .asRequired().bind(SpedizioneItem::getMesePubblicazione,SpedizioneItem::setMesePubblicazione);

        getBinder().forField(annoPubblicazione)
        .asRequired().bind(SpedizioneItem::getAnnoPubblicazione,SpedizioneItem::setAnnoPubblicazione);
        
        getBinder().forField(posticipata).asRequired().bind(SpedizioneItem::isPosticipata,SpedizioneItem::setPosticipata);

    }

	@Override
    public void focus(boolean persisted, SpedizioneItem obj) {
        getSave().setEnabled(obj.getStatoSpedizione() != StatoSpedizione.INVIATA); 
        getDelete().setEnabled(false);        
    }    
}
