package it.arsinfo.smd.ui.versamento;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.data.renderer.NumberRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import it.arsinfo.smd.entity.Anno;
import it.arsinfo.smd.entity.SmdEntity;
import it.arsinfo.smd.entity.Versamento;
import it.arsinfo.smd.service.api.VersamentoService;
import it.arsinfo.smd.ui.MainLayout;
import it.arsinfo.smd.ui.entity.EntityGridView;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Route(value="adp/versamento", layout = MainLayout.class)
@PageTitle("Versamento | ADP Portale")
public class VersamentoView extends EntityGridView<Versamento> {

    private final VersamentoService service;

    public VersamentoView(@Autowired VersamentoService service)  {
        this.service=service;
    }

    @PostConstruct
    public void init() {
        super.init(new Grid<>(Versamento.class));

        configureGrid("codeLine","dataPagamento");
        setColumnCaption("distintaVersamento.cassa", "Cassa");
        setColumnCaption("distintaVersamento.cuas", "Cuas");
        setColumnCaption("distintaVersamento.ccp.ccp", "CC");

        getGrid().addColumn(new NumberRenderer<>(Versamento::getImporto, SmdEntity.getEuroCurrency())).setHeader("Importo");
        getGrid().addColumn(new NumberRenderer<>(Versamento::getIncassato, SmdEntity.getEuroCurrency())).setHeader("Incassato");
        getGrid().addColumn(new NumberRenderer<>(Versamento::getResiduo, SmdEntity.getEuroCurrency())).setHeader("Residuo");

        add(
            new H2("Versamenti Effettuati"),
            getContent(getGrid())
        );
        getGrid().setHeightByRows(true);

        updateList();

    }



    @Override
    public List<Versamento> filter() {
        try {
            return service.searchBy(getUserSession().getLoggedInIntestatario(),Anno.getAnnoCorrente());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}
