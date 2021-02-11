package it.arsinfo.smd.ui.print;

import com.vaadin.server.Sizeable;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import it.arsinfo.smd.data.Provincia;
import it.arsinfo.smd.data.TitoloAnagrafica;
import it.arsinfo.smd.dto.Indirizzo;
import it.arsinfo.smd.entity.Anagrafica;

public abstract class StampaUI extends UI{
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static String getHtml(Indirizzo indirizzo) {
		StringBuffer html = new StringBuffer();
		html.append(indirizzo.getIntestazione());
    	if (indirizzo.getSottoIntestazione() != null && !indirizzo.getSottoIntestazione().equals("")) {
    		html.append("<br/>\n");
    		html.append(indirizzo.getSottoIntestazione());
    	}
		html.append("<br/>\n");
		html.append(indirizzo.getIndirizzo());
		html.append("<br/>\n");
		html.append(indirizzo.getCap());
		html.append("&nbsp;");
		html.append(indirizzo.getCitta()); ;
		html.append("&nbsp;&nbsp;");
		html.append("(");
		html.append(indirizzo.getProvincia().name());
		html.append(")");
		html.append("<br/>\n");
		html.append(indirizzo.getPaese().getNome());
		return html.toString();
	}

	public static Layout stampaA(Indirizzo indirizzo) {
    	StringBuffer html = new StringBuffer("<div><p>");
    	html.append("<div class=\"gap-a\"></div>\n");
    	html.append(getHtml(indirizzo));
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
    	html.append("<div id=\"busta_2\"><p>");
    	html.append(getHtml(indirizzo));
		html.append("</p></div>\n");
		HorizontalLayout lay = new HorizontalLayout();
		lay.setWidth(210,Sizeable.Unit.MM);
		lay.setHeight(297,Sizeable.Unit.MM);
		lay.setSpacing(false);
		lay.setMargin(false);
		VerticalLayout left = new VerticalLayout();
		left.setSpacing(false);
		left.setMargin(false);
		lay.addComponent(left);
		VerticalLayout right = new VerticalLayout();
		right.setSpacing(false);
		right.setMargin(false);
		right.addComponent(new Label("<div class=\"gap-b\"></div>\n", ContentMode.HTML));
		right.addComponent(new Label(html.toString(), ContentMode.HTML));
		lay.addComponent(right);
		lay.setExpandRatio(left, 210*0.37f);
		lay.setExpandRatio(right, 210*0.63f);


		return lay;
    }

	public static Layout stampaC(Indirizzo indirizzo) {
    	StringBuffer html = new StringBuffer();
    	html.append("<div id=\"busta_2\"><p>");
    	html.append(getHtml(indirizzo));
		html.append("</p></div>\n");
		HorizontalLayout lay = new HorizontalLayout();
		lay.setWidth(210,Sizeable.Unit.MM);
		lay.setHeight(297,Sizeable.Unit.MM);
		lay.setSpacing(false);
		lay.setMargin(false);
		VerticalLayout left = new VerticalLayout();
		left.setSpacing(false);
		left.setMargin(false);
		lay.addComponent(left);
		VerticalLayout right = new VerticalLayout();
		right.setSpacing(false);
		right.setMargin(false);
		right.addComponent(new Label("<div class=\"gap-c\"></div>\n", ContentMode.HTML));
		right.addComponent(new Label(html.toString(), ContentMode.HTML));
		lay.addComponent(right);
		lay.setExpandRatio(left, 210*0.4f);
		lay.setExpandRatio(right, 210*0.6f);


		return lay;
    }

	@Override
	protected void init(VaadinRequest request) {
        getPage().getStyles().add("#busta_2 {\n" + 
        		"  -webkit-transform: rotate(-90deg);\n" + 
        		"  -moz-transform: rotate(270deg);\n" + 
        		"  -o-transform: rotate(270deg);\n" + 
        		"  -ms-transform: rotate(270deg);\n" + 
        		"  transform: rotate(270deg);\n" + 
        		"}");	
        getPage().getStyles().add(".gap-a { \n" + 
        		"				width:100%; \n" + 
        		"				height:110mm; \n" + 
        		"			} \n" + 
        		"			.gap-b { \n" + 
        		"				width:100%; \n" + 
        		"				height:35mm; \n" + 
        		"			} \n" + 
        		"			.gap-c { \n" + 
        		"				width:100%; \n" + 
        		"				height:50mm; \n" + 
        		"			}" 
        		);
        
	}
	
	protected void print() {
	       JavaScript.getCurrent().execute(
	               "setTimeout(function() {" +
	               "  print(); self.close();}, 0);");
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
		return Indirizzo.getIndirizzo(antonio);

	}

    public abstract Layout stampa(Indirizzo indirizzo);

}
