package it.arsinfo.smd.ui.campagna;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import it.arsinfo.smd.entity.StatoWooCommerceOrder;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.UserInfo;
import it.arsinfo.smd.entity.WooCommerceOrder;
import it.arsinfo.smd.service.api.WooCommerceOrderService;
import it.arsinfo.smd.woocommerce.api.WooCommerceApiService;

import java.util.List;

public class CampagnaPaga extends Button {
    private final WooCommerceApiService api;
    private final WooCommerceOrderService service;
    private final Abbonamento abbonamento;
    private final UserInfo user;

    public CampagnaPaga(UserInfo user, Abbonamento abbonamento, WooCommerceOrderService service, WooCommerceApiService api) {
        super("Paga");
        this.user=user;
        this.service=service;
        this.abbonamento=abbonamento;
        this.api=api;
        this.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        this.setEnabled(abbonamento.getResiduo().signum() >0);
        this.setIcon(VaadinIcon.EURO.create());
        this.addClickListener(this::create);
    }

    public  void create(ClickEvent<Button> buttonClickEvent)  {
        WooCommerceOrder forder=null;
        List<WooCommerceOrder> wooCommerceOrders = service.findByAbbonamento(abbonamento);
        boolean found = false;
        for (WooCommerceOrder wo: wooCommerceOrders) {
            if (wo.getStatus() == StatoWooCommerceOrder.Completed)
                continue;
            if (wo.getStatus() == StatoWooCommerceOrder.Dismissed)
                continue;
            if (!found && wo.getPrice().subtract(abbonamento.getResiduo()).signum() == 0) {
                found = true;
                forder=wo;
            } else {
                wo.setStatus(StatoWooCommerceOrder.Dismissed);
                try {
                    api.delete(wo);
                    service.save(wo);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (forder == null) {
            try {
                forder = api.create(abbonamento,wooCommerceOrders.size());
                forder.setUserInfo(user);
                service.save(forder);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (forder!=null)
            UI.getCurrent().getPage().executeJs("window.open('"+forder.getPermalink()+"','_blank')");

    }

}
