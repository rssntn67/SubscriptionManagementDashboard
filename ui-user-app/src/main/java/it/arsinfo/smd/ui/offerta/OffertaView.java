package it.arsinfo.smd.ui.offerta;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.data.renderer.NumberRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.entity.Offerta;
import it.arsinfo.smd.service.Smd;
import it.arsinfo.smd.service.api.OffertaService;
import it.arsinfo.smd.ui.MainLayout;
import it.arsinfo.smd.ui.entity.EntityGridView;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Route(value="adp/offerta", layout = MainLayout.class)
@PageTitle("Offerta | ADP Portale")
public class OffertaView extends EntityGridView<Offerta> {

    private final OffertaService service;

    public OffertaView(@Autowired OffertaService service)  {
        this.service=service;
    }

    @PostConstruct
    public void init() {
        super.init(new Grid<>(Offerta.class));

        configureGrid("statoOperazioneIncasso","offerteCumulate.anno","versamento.codeLine","versamento.dataPagamento");
        getGrid().addColumn(new NumberRenderer<>(Offerta::getImporto, Smd.getEuroCurrency())).setHeader("Importo");

        Button paga = new Button("Esegui una donazione");
        add(
            new H2("Offerte Effettuate dal 01/01/"+Anno.getAnnoPassato().getAnnoAsString()),
            getContent(getGrid()),
            paga
        );
        getGrid().setHeightByRows(true);

        updateList();

    }



    @Override
    public List<Offerta> filter() {
        List<Offerta> offerte = new ArrayList<>();
        try {
            offerte.addAll(service.findByCommittente(getUserSession().getLoggedInIntestatario(),Anno.getAnnoPassato()));
            offerte.addAll(service.findByCommittente(getUserSession().getLoggedInIntestatario(),Anno.getAnnoCorrente()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return offerte;
    }
}
