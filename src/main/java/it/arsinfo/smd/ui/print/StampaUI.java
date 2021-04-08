package it.arsinfo.smd.ui.print;

import com.vaadin.server.Sizeable;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.*;
import it.arsinfo.smd.data.Stampa;
import it.arsinfo.smd.dto.Indirizzo;
import org.springframework.util.StringUtils;

public abstract class StampaUI extends UI{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static String getHtml(Indirizzo indirizzo) {
		StringBuffer html;
		html = new StringBuffer();
		html.append("<b>");
		html.append(indirizzo.getIntestazione());
    	if (StringUtils.hasLength(indirizzo.getSottoIntestazione())) {
    		html.append("<br/>\n");
    		html.append(indirizzo.getSottoIntestazione());
    	}
		html.append("<br/>\n");
		html.append(indirizzo.getIndirizzo());
		html.append("<br/>\n");
		html.append(indirizzo.getCap());
		html.append("&nbsp;");
		html.append(indirizzo.getCitta());
		html.append("&nbsp;&nbsp;");
		html.append("(");
		html.append(indirizzo.getProvincia().name());
		html.append(")");
		html.append("<br/>\n");
		html.append(indirizzo.getPaese().getNome());
		html.append("</b>");
		return html.toString();
	}

	public static Layout stampa(Stampa type, Indirizzo indirizzo, Layout defaultLayout) {
		switch (type) {
			case Busta:
				return stampaBusta(indirizzo);
			case Cartoncino:
				return stampaCartoncino(indirizzo);
			case BustaGialla:
				return stampaBustaGialla(indirizzo);
			default:
				break;
		}
		return defaultLayout;
	}

	public static Layout stampaBustaGialla(Indirizzo indirizzo) {
    	StringBuffer html;
		html = new StringBuffer("<div><p>");
		html.append("<div class=\"gap-a\"></div>\n");
    	html.append(getHtml(indirizzo));
		html.append("</p></div>\n");
		HorizontalLayout lay = new HorizontalLayout();
		lay.setWidth(297,Sizeable.Unit.MM);
		lay.setHeight(210,Sizeable.Unit.MM);
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
		lay.setExpandRatio(left, 297*0.9f);
		lay.setExpandRatio(right,297*1.1f);
		
		Label label = new Label(html.toString(), ContentMode.HTML);
		right.addComponent(label);


		return lay;
    }

	public static Layout stampaCartoncino(Indirizzo indirizzo) {
    	StringBuffer html;
		html = new StringBuffer();
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
		lay.setExpandRatio(left, 210*0.30f);
		lay.setExpandRatio(right, 210*0.70f);


		return lay;
    }

	public static Layout stampaBusta(Indirizzo indirizzo) {
    	StringBuffer html;
		html = new StringBuffer();
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
		lay.setExpandRatio(left, 210*0.35f);
		lay.setExpandRatio(right, 210*0.65f);


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
        		"				height:50mm; \n" + 
        		"			} \n" + 
        		"			.gap-c { \n" + 
        		"				width:100%; \n" + 
        		"				height:45mm; \n" + 
        		"			}" 
        		);
        
	}
	
	protected void print() {
	       JavaScript.getCurrent().execute(
	               "setTimeout(function() {" +
	               "  print(); self.close();}, 0);");
	}

}
