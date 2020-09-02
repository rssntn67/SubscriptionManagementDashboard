package it.arsinfo.smd.ui.campagna;

import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.data.Binder;
import com.vaadin.data.converter.StringToBigDecimalConverter;
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
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.entity.UserInfo;
import it.arsinfo.smd.ui.SmdEditorUI;
import it.arsinfo.smd.ui.vaadin.SmdEntityEditor;

public class CampagnaEditor extends SmdEntityEditor<Campagna> {

    private final ComboBox<Anno> anno = new ComboBox<Anno>("Anno",
                                                           EnumSet.allOf(Anno.class));

    private final ComboBox<StatoCampagna> statoCampagna = new ComboBox<StatoCampagna>("Stato",
            EnumSet.allOf(StatoCampagna.class));
    
    private final Label running = new Label("");
    private final CampagnaItemsEditor items;
    private final TextField numero = new TextField("Numero di Riviste Massimo da Sospendere");
    private final TextField residuo = new TextField("Importo Minimo Residuo da Visualizzare");

    private final AbbonamentoConRivisteGrid grid = new AbbonamentoConRivisteGrid("Abbonamenti");
    private final OperazioneCampagnaGrid operazioni =  new OperazioneCampagnaGrid("Operazioni");    
    private final OperazioneSospendiGrid sospensioni =  new OperazioneSospendiGrid("Sospensioni");    
    private final Button buttonV = new Button("Visualizza Abbonamenti Generati",VaadinIcons.ARCHIVE);
    private final Button buttonA = new Button("Abbonamenti Annullati",VaadinIcons.ARCHIVE);

    private final Button buttonWGenera = new Button("Genera",VaadinIcons.ENVELOPES);
    private final Button buttonWInvio = new Button("Invia",VaadinIcons.ENVELOPES);
    private final Button buttonWSospendi = new Button("Sospendi",VaadinIcons.ENVELOPES);
    private final ComboBox<Pubblicazione> pubblicazione= new ComboBox<Pubblicazione>();
    private final Button buttonWEstrattoConto = new Button("Estratto Conto",VaadinIcons.ENVELOPES);
    private final Button buttonWChiudi = new Button("Chiudi",VaadinIcons.ENVELOPES);

    private final CampagnaServiceDao repo;

    public CampagnaEditor(CampagnaServiceDao repo) {

        super(repo, new Binder<>(Campagna.class));
        this.repo=repo;
        List<Pubblicazione> pubblicazioni = repo.findPubblicazioni();
        pubblicazione.setItems(pubblicazioni);
        items = new CampagnaItemsEditor(pubblicazioni);
        buttonV.addClickListener(click -> {
        	switch (get().getStatoCampagna()) {
        		case Generata:
            		grid.populate(repo.findAbbonamentoConRivisteGenerati(get()));
        			break;
        		case Inviata:
                    grid.populate(repo.findAbbonamentoConRivisteInviati(get()));
                    break;
        		case InviatoEC:
    	            grid.populate(repo.findAbbonamentoConRivisteEstrattoConto(get()));
    	            break;
        		case Chiusa:
    				buttonV.setCaption("Visualizza Abbonamenti con Debito");
    	            grid.populate(repo.findAbbonamentoConDebito(get()));        			
    	            break;
    			default:
    				break;
        	}
        	
        });
        
        buttonA.addClickListener(click -> {
            grid.populate(repo.findAbbonamentoConRivisteAnnullati(get()));        	
        });

        buttonWGenera.addClickListener(click -> {
			Notification.show("Generazione Campagna Avviata", Notification.Type.TRAY_NOTIFICATION);
			BgGenera genera = new BgGenera(repo,UI.getCurrent());
			genera.start();
        	try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
            edit(get());
        });

        buttonWInvio.addClickListener(click -> {
			Notification.show("Invio Campagna Avviato", Notification.Type.TRAY_NOTIFICATION);
			BgInvia invia = new BgInvia(repo,UI.getCurrent());
			invia.start();
	       	try {
					Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
            edit(get());
        });

        buttonWSospendi.addClickListener(click -> {
        	Pubblicazione p = pubblicazione.getValue();
        	if (p == null) {
    			Notification.show("Sospendi Invio Pubblicazione Campagna Selezionare Pubblicazione da Sospendere", Notification.Type.TRAY_NOTIFICATION);
    			return;
        	}
			Notification.show("Sospendi Invio Pubblicazione Campagna Avviato", Notification.Type.TRAY_NOTIFICATION);
			BgSospendi sospendi = new BgSospendi(repo, p ,UI.getCurrent());
			sospendi.start();
	       	try {
					Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
            edit(get());
        });

        buttonWEstrattoConto.addClickListener(click -> {
			Notification.show("Estratto Conto Campagna Avviato", Notification.Type.TRAY_NOTIFICATION);
			BgEstrattoConto estratto = new BgEstrattoConto(repo,UI.getCurrent());
			estratto.start();
	       	try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
            edit(get());
        });

        buttonWChiudi.addClickListener(click -> {
			Notification.show("Chiudi Campagna Avviato", Notification.Type.TRAY_NOTIFICATION);
			BgChiudi bgchiudi = new BgChiudi(repo,UI.getCurrent());
			bgchiudi.start();
	       	try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
            edit(get());
        });

        getActions().addComponents(buttonWGenera,buttonWInvio,buttonWSospendi,pubblicazione,buttonWEstrattoConto,buttonWChiudi);
		HorizontalLayout stato = new HorizontalLayout(anno,statoCampagna,numero);
		
		HorizontalLayout riviste = new HorizontalLayout();
		riviste.addComponent(new Label("riviste in abbonamento"));
		riviste.addComponents(items.getComponents());

        setComponents(
        		getActions(),
        		running,
        		riviste,
        		stato,           		
        		residuo,
        		operazioni.getGrid(),
        		sospensioni.getGrid(),
        		new HorizontalLayout(buttonV,buttonA),
        	    new VerticalLayout(grid.getComponents())
		);
        
        grid.setVisible(false);
        residuo.setReadOnly(true);

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

        getBinder()
        .forField(residuo)
        .withConverter(new StringToBigDecimalConverter("Conversione in Eur"))
        .bind("residuo");
        
        getBinder().bindInstanceFields(this);
    }

    @Override
    public void focus(boolean persisted, Campagna campagna) {

        getSave().setVisible(false);
        numero.setVisible(false);
        residuo.setReadOnly(true);
    	buttonA.setVisible(false);
    	buttonV.setVisible(persisted);
        anno.setReadOnly(persisted);
        buttonWGenera.setEnabled(false);
		buttonWInvio.setEnabled(false);
		buttonWSospendi.setEnabled(false);
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
	        	operazioni.populate(repo.getOperazioni(campagna));
	        	sospensioni.populate(repo.getSospensioni(campagna));
	        	switch (get().getStatoCampagna()) {
				case Generata:
					buttonWInvio.setEnabled(true);
					buttonV.setCaption("Visualizza Abbonamenti Generati");
					break;
				case Inviata:
					buttonWEstrattoConto.setEnabled(true);
					buttonWSospendi.setEnabled(true);
					buttonV.setCaption("Visualizza Abbonamenti Inviati");
					break;
				case InviatoEC:
					numero.setVisible(true);
					buttonWChiudi.setEnabled(true);
    				buttonV.setCaption("Visualizza Abbonamenti Inviato Estratto Conto");
					break;
				case Chiusa:
			        residuo.setReadOnly(false);
					buttonA.setVisible(true);
    				buttonV.setCaption("Visualizza Abbonamenti con Debito");
					break;
				default:
					break;
				}
	        }
		}
        anno.focus();
    }

	@SuppressWarnings("unchecked")
	public UserInfo getOperatore() {
		return ((SmdEditorUI<Campagna>)UI.getCurrent()).getLoggedInUser();
	}

    private abstract class Bg extends Thread {
    	private final UI ui; 
    	
    	public Bg(UI ui) {
			super();
			this.ui=ui;
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
            repo.genera(c, getOperatore());
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
            repo.invia(c,getOperatore());
        }
    }

    private final class BgSospendi extends Bg {
    	private final CampagnaServiceDao repo;
    	private final Pubblicazione p;
    	
    	public BgSospendi(CampagnaServiceDao repo, final Pubblicazione p, final UI ui) {
			super(ui);
			this.repo = repo;
			this.p=p;
		}

		@Override
        public void exec(Campagna c) throws Exception
        {
            repo.sospendi(c,p,getOperatore());
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
            repo.estratto(c,getOperatore());
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
            repo.chiudi(c,getOperatore());
        }
    }
}
