package it.arsinfo.smd.ui.campagna;

import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.StatoStorico;
import it.arsinfo.smd.entity.Storico;
import it.arsinfo.smd.service.api.StoricoService;
import it.arsinfo.smd.ui.MainLayout;
import it.arsinfo.smd.ui.storico.StoricoView;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Route(value="adp/campagna", layout = MainLayout.class)
@PageTitle("Campagna | ADP Portale")
public class CampagnaView extends StoricoView {

    public CampagnaView(@Autowired StoricoService service) {
        super(service);
    }

    @PostConstruct
    public void init() {
        super.init(Anno.getAnnoProssimo());
    }

    @Override
    public List<Storico> filter() {
        return super.filter().stream().filter(s -> (s.getStatoStorico() != StatoStorico.Annullato || s.getStatoStorico() != StatoStorico.Sospeso) && s.getNumero() > 0).collect(Collectors.toList());
    }


}
