package it.arsinfo.smd.ui.print;

import com.vaadin.server.Sizeable;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import it.arsinfo.smd.data.Provincia;
import it.arsinfo.smd.data.TitoloAnagrafica;
import it.arsinfo.smd.dto.Indirizzo;
import it.arsinfo.smd.entity.Anagrafica;

public abstract class StampaIndirizzoUI extends UI{
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public static Layout stampaA(Indirizzo indirizzo) {
    	StringBuffer html = new StringBuffer("<div><p>");
    	html.append("<div class=\"gap-30\"></div>\n");
    	html.append("<div class=\"gap-30\"></div>\n");
    	html.append("<div class=\"gap-30\"></div>\n");
		html.append(indirizzo.getIntestazione());
    	if (indirizzo.getSottoIntestazione() != null && !indirizzo.getSottoIntestazione().equals("")) {
    		html.append("<br/>\n");
    		html.append(indirizzo.getSottoIntestazione());
    	}
		html.append("<br/>\n");
		html.append(indirizzo.getIndirizzo());
		html.append("<br/>\n");
		html.append(indirizzo.getCap());
		html.append(" ");
		html.append(indirizzo.getCitta()); ;
		html.append("(");
		html.append(indirizzo.getProvincia().name());
		html.append(")");
		html.append("<br/>\n");
		html.append(indirizzo.getPaese().getNome());
		html.append("</p></div>\n");
		HorizontalLayout lay = new HorizontalLayout();
		lay.setWidth(260,Sizeable.Unit.MM);
		lay.setHeight(190,Sizeable.Unit.MM);
		lay.setSpacing(false);
		lay.setMargin(false);
		VerticalLayout left = new VerticalLayout();
		left.setMargin(false);
		left.setSpacing(false);
		VerticalLayout right = new VerticalLayout();
		right.setMargin(false);
		right.setSpacing(false);
		lay.addComponent(left);
		lay.addComponent(right);
		lay.setExpandRatio(left, 100*1.0f);
		lay.setExpandRatio(right,80*1.0f);
		
		Label label = new Label(html.toString(), ContentMode.HTML);
		right.addComponent(label);


		return lay;
    }

	public static Layout stampaB(Indirizzo indirizzo) {
    	StringBuffer html = new StringBuffer();
    	html.append("<div class=\"gap-10\"></div>\n");
    	html.append("<div id=\"busta_2\"><p>");
		html.append(indirizzo.getIntestazione());
    	if (indirizzo.getSottoIntestazione() != null && !indirizzo.getSottoIntestazione().equals("")) {
    		html.append("<br/>\n");
    		html.append(indirizzo.getSottoIntestazione());
    	}
		html.append("<br/>\n");
		html.append(indirizzo.getIndirizzo());
		html.append("<br/>\n");
		html.append(indirizzo.getCap());
		html.append(" ");
		html.append(indirizzo.getCitta()); ;
		html.append("(");
		html.append(indirizzo.getProvincia().name());
		html.append(")");
		html.append("<br/>\n");
		html.append(indirizzo.getPaese().getNome());
    	html.append("<div class=\"hgap-30\"></div>\n");
		html.append("</p></div>\n");
		VerticalLayout lay = new VerticalLayout();
		lay.setWidth(110,Sizeable.Unit.MM);
		lay.setHeight(165,Sizeable.Unit.MM);
		lay.setSpacing(false);
		lay.setMargin(false);
		HorizontalLayout top = new HorizontalLayout();
		top.setMargin(false);
		top.setSpacing(false);
		HorizontalLayout bottom = new HorizontalLayout();
		bottom.setMargin(false);
		bottom.setSpacing(false);
		lay.addComponent(top);
		lay.addComponent(bottom);
		lay.setExpandRatio(top, 165*6.f);
		lay.setExpandRatio(bottom,165*10.50f);
		Label label = new Label(html.toString(), ContentMode.HTML);
		bottom.addComponent(label);


		return lay;
    }

	public static Layout stampaC(Indirizzo indirizzo) {
    	StringBuffer html = new StringBuffer("<div><p>");
    	html.append("<div class=\"gap-30\"></div>\n");
    	html.append("<div class=\"gap-30\"></div>\n");
		html.append(indirizzo.getIntestazione());
    	if (indirizzo.getSottoIntestazione() != null && !indirizzo.getSottoIntestazione().equals("")) {
    		html.append("<br/>\n");
    		html.append(indirizzo.getSottoIntestazione());
    	}
		html.append("<br/>\n");
		html.append(indirizzo.getIndirizzo());
		html.append("<br/>\n");
		html.append(indirizzo.getCap());
		html.append(" ");
		html.append(indirizzo.getCitta()); ;
		html.append("(");
		html.append(indirizzo.getProvincia().name());
		html.append(")");
		html.append("<br/>\n");
		html.append(indirizzo.getPaese().getNome());
		html.append("</p></div>\n");
		HorizontalLayout lay = new HorizontalLayout();
		lay.setWidth(220,Sizeable.Unit.MM);
		lay.setHeight(110,Sizeable.Unit.MM);
		lay.setSpacing(false);
		lay.setMargin(false);
		VerticalLayout left = new VerticalLayout();
		left.setMargin(false);
		left.setSpacing(false);
		VerticalLayout right = new VerticalLayout();
		right.setMargin(false);
		right.setSpacing(false);
		lay.addComponent(left);
		lay.addComponent(right);
		lay.setExpandRatio(left, 220*12.0f);
		lay.setExpandRatio(right,220*10.0f);
		
		Label label = new Label(html.toString(), ContentMode.HTML);
		right.addComponent(label);


		return lay;
    }

	@Override
	protected void init(VaadinRequest request) {
        getPage().getStyles().add("#busta_2 {\n" + 
        		"  -webkit-transform: rotate(90deg);\n" + 
        		"  -moz-transform: rotate(90deg);\n" + 
        		"  -o-transform: rotate(90deg);\n" + 
        		"  -ms-transform: rotate(90deg);\n" + 
        		"  transform: rotate(90deg);\n" + 
        		"}");	
        getPage().getStyles().add(".gap-10 { \n" + 
        		"				width:100%; \n" + 
        		"				height:10mm; \n" + 
        		"			} \n" + 
        		"			.gap-20 { \n" + 
        		"				width:100%; \n" + 
        		"				height:20mm; \n" + 
        		"			} \n" + 
        		"			.gap-30 { \n" + 
        		"				width:100%; \n" + 
        		"				height:30mm; \n" + 
        		"			} ");
        getPage().getStyles().add(".hgap-10 { \n" + 
        		"				height:10mm; \n" + 
        		"				width:10mm; \n" + 
        		"			} \n" + 
        		"			.hgap-20 { \n" + 
        		"				height:20mm; \n" + 
        		"				width:20mm; \n" + 
        		"			} \n" + 
        		"			.hgap-30 { \n" + 
        		"				height:30mm; \n" + 
        		"				width:30mm; \n" + 
        		"			} ");
	}

	protected static Indirizzo getTestIndirizzo() {
    	Anagrafica antonio = new Anagrafica();
    	antonio.setTitolo(TitoloAnagrafica.Rettore);
    	antonio.setDenominazione("Russo");
    	antonio.setNome("Antonio");
    	antonio.setIndirizzo("Largo Aldifreda 9");
    	antonio.setCap("81100");
    	antonio.setCitta("Caserta");
    	antonio.setProvincia(Provincia.CE);
    	return getIndirizzo(antonio);

	}

	protected static Indirizzo getIndirizzo(Anagrafica tizio) {
		return Indirizzo.getIndirizzo(tizio);
	}
}
