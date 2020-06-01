package it.arsinfo.smd.ui.campagna;

import java.util.EnumSet;
import java.util.stream.Collectors;

import com.vaadin.data.Binder;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import it.arsinfo.smd.dao.CampagnaServiceDao;
import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.StatoCampagna;
import it.arsinfo.smd.entity.Campagna;
import it.arsinfo.smd.entity.CampagnaItem;
import it.arsinfo.smd.ui.vaadin.SmdEntityEditor;

public class CampagnaEditor extends SmdEntityEditor<Campagna> {

    private final ComboBox<Anno> anno = new ComboBox<Anno>("Anno",
                                                           EnumSet.allOf(Anno.class));

    private final ComboBox<StatoCampagna> statoCampagna = new ComboBox<StatoCampagna>("Stato",
            EnumSet.allOf(StatoCampagna.class));
    
    private final Label running = new Label("");
    private final CampagnaItemsEditor items;
    private final TextField numero = new TextField("Numero di Riviste Massimo da Sospendere");

    private final AbbonamentoConRivisteGrid grid = new AbbonamentoConRivisteGrid("Abbonamenti");
    
    private final Button buttonVGenerati = new Button("Abbonamenti Generati",VaadinIcons.ENVELOPES);
    private final Button buttonVInviati = new Button("Abbonamenti Inviati",VaadinIcons.ENVELOPES);
    private final Button buttonVEstrattiConto = new Button("Estratti Conto",VaadinIcons.ENVELOPES);
    private final Button buttonVAnnullati = new Button("Abbonamenti Annullati",VaadinIcons.ENVELOPES);


    private final Button buttonWGenera = new Button("Genera",VaadinIcons.ENVELOPES);
    private final Button buttonWInvio = new Button("Invia",VaadinIcons.ENVELOPES);
    private final Button buttonWEstrattoConto = new Button("Estratto Conto",VaadinIcons.ENVELOPES);
    private final Button buttonWChiudi = new Button("Chiudi",VaadinIcons.ENVELOPES);


    public CampagnaEditor(CampagnaServiceDao repo) {

        super(repo, new Binder<>(Campagna.class));
        items = new CampagnaItemsEditor(repo.findPubblicazioni());
        
        buttonVGenerati.addClickListener(click -> {
            grid.populate(repo.findAbbonamentoConRivisteGenerati(get()));
        });

        buttonVInviati.addClickListener(click -> {
            grid.populate(repo.findAbbonamentoConRivisteInviati(get()));
        });

        buttonVEstrattiConto.addClickListener(click -> {
            grid.populate(repo.findAbbonamentoConRivisteEstrattoConto(get()));
        });

        buttonVAnnullati.addClickListener(click -> {
            grid.populate(repo.findAbbonamentoConRivisteAnnullati(get()));
        });

        buttonWGenera.addClickListener(click -> {
			Notification.show("Generazione Campagna Avviata", Notification.Type.TRAY_NOTIFICATION);
			BgGenera genera = new BgGenera(repo, UI.getCurrent());
			genera.start();
        	try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
            edit(get());
            buttonWGenera.setEnabled(false);
        });

        buttonWInvio.addClickListener(click -> {
			Notification.show("Invio Campagna Avviato", Notification.Type.TRAY_NOTIFICATION);
			BgInvia invia = new BgInvia(repo, UI.getCurrent());
			invia.start();
	       	try {
					Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
            edit(get());
            buttonWInvio.setEnabled(false);
        });

        buttonWEstrattoConto.addClickListener(click -> {
			Notification.show("Estratto Conto Campagna Avviato", Notification.Type.TRAY_NOTIFICATION);
			BgEstrattoConto estratto = new BgEstrattoConto(repo, UI.getCurrent());
			estratto.start();
	       	try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
            edit(get());
            buttonWEstrattoConto.setEnabled(false);
        });

        buttonWChiudi.addClickListener(click -> {
			Notification.show("Chiudi Campagna Avviato", Notification.Type.TRAY_NOTIFICATION);
			BgChiudi bgchiudi = new BgChiudi(repo, UI.getCurrent());
			bgchiudi.start();
	       	try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
            edit(get());
            buttonWChiudi.setEnabled(false);
        });

        getActions().addComponents(buttonWGenera,buttonWInvio,buttonWEstrattoConto,buttonWChiudi);
		HorizontalLayout stato = new HorizontalLayout(anno,statoCampagna,numero);
		
		HorizontalLayout riviste = new HorizontalLayout();
		riviste.addComponent(new Label("riviste in abbonamento"));
		riviste.addComponents(items.getComponents());

        setComponents(
        		getActions(),
        		running,
        		riviste,
        		stato,
        	    new HorizontalLayout(buttonVGenerati,buttonVInviati,buttonVEstrattiConto,buttonVAnnullati),
        	    new VerticalLayout(grid.getComponents())
		);
        
        grid.setVisible(false);
        buttonWGenera.setEnabled(false);
        buttonWInvio.setEnabled(false);
        buttonWEstrattoConto.setEnabled(false);
        buttonWChiudi.setEnabled(false);

        anno.setItemCaptionGenerator(Anno::getAnnoAsString);

        statoCampagna.setReadOnly(true);
        numero.setVisible(false);
        
        items.setChangeHandler(() -> {
            items.edit(repo.findPubblicazioniValide().
            		stream().
            		map(p -> {
		                CampagnaItem ci = new CampagnaItem();
		                ci.setPubblicazione(p);
		                ci.setCampagna(get());
		                get().addCampagnaItem(ci);
		                return ci;
            }).collect(Collectors.toList()), false);
        	
        });
        
        grid.setChangeHandler(() -> {
            grid.setVisible(false);
        });

        getBinder()
        .forField(numero)
        .withConverter(new StringToIntegerConverter("Deve essere un numero"))
        .withValidator(num -> num != null && num > 0,"deve essere maggiore di 0")
        .bind(Campagna::getNumero, Campagna::setNumero);

        getBinder().bindInstanceFields(this);
    }

    @Override
    public void focus(boolean persisted, Campagna campagna) {

        getSave().setVisible(false);
        numero.setVisible(false);
    	buttonVAnnullati.setVisible(persisted);
    	buttonVEstrattiConto.setVisible(persisted);
    	buttonVGenerati.setVisible(persisted);
    	buttonVInviati.setVisible(persisted);
        anno.setReadOnly(persisted);
        buttonWGenera.setEnabled(false);
		buttonWInvio.setEnabled(false);
		buttonWEstrattoConto.setEnabled(false);
		buttonWChiudi.setEnabled(false);
        getCancel().setVisible(false);
        getDelete().setVisible(!campagna.isRunning() && persisted &&
    		campagna.getStatoCampagna() == StatoCampagna.Generata 
         && campagna.getAnno().getAnno() > Anno.getAnnoCorrente().getAnno()
        );
        statoCampagna.setVisible(persisted);
		if (campagna.isRunning()) {
			running.setValue("locked: running...");
		} else {
			running.setValue("");
	        if (!persisted) {
	        	buttonWGenera.setEnabled(true);
	        	items.onChange();
	        }  else {
	        	items.edit(campagna.getCampagnaItems(),true);
	        	switch (get().getStatoCampagna()) {
				case Generata:
					buttonWInvio.setEnabled(true);
					break;
				case Inviata:
					buttonWEstrattoConto.setEnabled(true);
					break;
				case InviatoEC:
					numero.setVisible(true);
					buttonWChiudi.setEnabled(true);
					break;
				default:
					break;
				}
	        }
		}
        anno.focus();
    }

    private abstract class Bg extends Thread {
    	private final UI ui; 
    	
    	public Bg(UI ui) {
			super();
			this.ui=ui;
			System.err.println(ui.getClass().getCanonicalName());
		}

    	public abstract void exec(Campagna campagna) throws Exception;

		@Override
        public void run()
        {
            try {
                exec(get());
            } catch (Exception e) {
                if (ui != null) {
                	ui.access(() -> Notification.show("Running Job Error", Notification.Type.ERROR_MESSAGE));
                }
            }
            
            if (ui != null) {
            	ui.access(() -> Notification.show("Running Job Finito", Notification.Type.TRAY_NOTIFICATION));
            }
        }
		
    }
    
    private final class BgGenera extends Bg {
    	private final CampagnaServiceDao repo;
    	
    	public BgGenera(CampagnaServiceDao repo, final UI ui) {
			super(ui);
			this.repo = repo;
		}


		@Override
        public void exec(Campagna c) throws Exception
        {
            repo.genera(c);
        }
    }

    
    private final class BgInvia extends Bg {
    	private final CampagnaServiceDao repo;
    	
    	public BgInvia(CampagnaServiceDao repo, final UI ui) {
			super(ui);
			this.repo = repo;
		}


		@Override
        public void exec(Campagna c) throws Exception
        {
            repo.invia(c);
        }
    }

    
    private final class BgEstrattoConto extends Bg {
    	private final CampagnaServiceDao repo;
    	
    	public BgEstrattoConto(CampagnaServiceDao repo, final UI ui) {
			super(ui);
			this.repo = repo;
		}


		@Override
        public void exec(Campagna c) throws Exception
        {
            repo.estratto(c);
        }
    }

    private final class BgChiudi extends Bg {
    	private final CampagnaServiceDao repo;
    	
    	public BgChiudi(CampagnaServiceDao repo, final UI ui) {
			super(ui);
			this.repo = repo;
		}

		@Override
        public void exec(Campagna c) throws Exception
        {
            repo.chiudi(get());
        }
    }
}
