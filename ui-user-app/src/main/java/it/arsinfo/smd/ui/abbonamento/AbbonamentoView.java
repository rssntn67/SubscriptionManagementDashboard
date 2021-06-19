package it.arsinfo.smd.ui.abbonamento;

import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.service.api.StoricoService;
import it.arsinfo.smd.ui.MainLayout;
import it.arsinfo.smd.ui.storico.StoricoView;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

@Route(value="adp/abbonamento", layout = MainLayout.class)
@PageTitle("Campagna Anno Corrente| ADP Portale")
public class AbbonamentoView extends StoricoView {

    public AbbonamentoView(@Autowired StoricoService service) {
        super(service);
    }

    @PostConstruct
    public void init() {
        super.init(Anno.getAnnoCorrente());
    }
 }
