package it.arsinfo.smd.ui.campagna;

import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import it.arsinfo.smd.dao.SmdService;
import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.StatoCampagna;
import it.arsinfo.smd.entity.Campagna;
import it.arsinfo.smd.entity.CampagnaItem;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.entity.UserInfo;
import it.arsinfo.smd.ui.SmdEditorUI;
import it.arsinfo.smd.ui.vaadin.SmdEntityEditor;

public class CampagnaEditor extends SmdEntityEditor<Campagna> {


    private static final Logger log = LoggerFactory.getLogger(SmdService.class);
	private final ComboBox<Anno> anno = new ComboBox<Anno>("Anno",EnumSet.allOf(Anno.class));

    private final ComboBox<StatoCampagna> statoCampagna = 
    		new ComboBox<StatoCampagna>("Stato",EnumSet.allOf(StatoCampagna.class));
    
    private final Label running = new Label("");
    private final CampagnaItemsEditor items;
    private final TextField numero = new TextField("Numero di Riviste Massimo da Sospendere");
    private final TextField residuo = new TextField("Importo Minimo Residuo per Debitori");

    private final AbbonamentoConRivisteGrid grid = new AbbonamentoConRivisteGrid("Abbonamenti");
    private final OperazioneCampagnaGrid operazioni =  new OperazioneCampagnaGrid("Operazioni");    
    private final OperazioneSospendiGrid sospensioni =  new OperazioneSospendiGrid("Sospensioni");    
   
    private final Button buttonVGenera = new Button("Generati",VaadinIcons.ARCHIVE);
    private final Button buttonVInvio = new Button("Proposti",VaadinIcons.ARCHIVE);
    private final Button buttonVSollecita = new Button("Sollecito",VaadinIcons.ARCHIVE);
    private final Button buttonVEstrattoConto = new Button("Estratto Conto",VaadinIcons.ARCHIVE);
    private final Button buttonVDebito = new Button("Debitori",VaadinIcons.ARCHIVE);

    private final Button buttonWGenera = new Button("Genera",VaadinIcons.ENVELOPES);
    private final Button buttonWInvio = new Button("Invia",VaadinIcons.ENVELOPES);
    private final Button buttonWSollecita = new Button("Sollecita",VaadinIcons.ENVELOPES);
    private final Button buttonWSospendi = new Button("Sospendi",VaadinIcons.ENVELOPES);
    private final Button buttonWEstrattoConto = new Button("Estratto Conto",VaadinIcons.ENVELOPES);
    private final Button buttonWChiudi = new Button("Chiudi",VaadinIcons.ENVELOPES);

    private final ComboBox<Pubblicazione> pubblicazione= new ComboBox<Pubblicazione>();

    private final CampagnaServiceDao repo;

    public CampagnaEditor(CampagnaServiceDao repo) {

        super(repo, new Binder<>(Campagna.class));
        this.repo=repo;
        List<Pubblicazione> pubblicazioni = repo.findPubblicazioni();
        pubblicazione.setItems(pubblicazioni);
        pubblicazione.setItemCaptionGenerator(Pubblicazione::getNome);
        items = new CampagnaItemsEditor(pubblicazioni);
        
        operazioni.getGrid().setHeightByRows(5.0);
        sospensioni.getGrid().setHeightByRows(4.0);
        buttonVGenera.addClickListener(click -> {
    		grid.populate(repo.findAbbonamentoConRivisteGenerati(get()));
        });
        buttonVInvio.addClickListener(click -> {
            grid.populate(repo.findAbbonamentoConRivisteInviati(get()));
        });
        buttonVSollecita.addClickListener(click -> {
            grid.populate(repo.findAbbonamentoConRivisteSollecito(get()));
        });
        buttonVEstrattoConto.addClickListener(click -> {
            grid.populate(repo.findAbbonamentoConRivisteEstrattoConto(get()));
        });
        buttonVDebito.addClickListener(click -> {
            grid.populate(repo.findAbbonamentoConDebito(get()));        			
        });
       
        buttonWGenera.addClickListener(click -> {
			Notification.show("Generazione Campagna Avviata", Notification.Type.TRAY_NOTIFICATION);
			@SuppressWarnings("unchecked")
			BgGenera genera = new BgGenera(repo,(SmdEditorUI<Campagna>)UI.getCurrent());
			genera.start();
        	try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
            edit(get());
        });

        buttonWInvio.addClickListener(click -> {
			Notification.show("Invio Campagna Avviato", Notification.Type.TRAY_NOTIFICATION);
			@SuppressWarnings("unchecked")
			BgInvia invia = new BgInvia(repo,(SmdEditorUI<Campagna>)UI.getCurrent());
			invia.start();
	       	try {
					Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
            edit(get());
        });

        buttonWSollecita.addClickListener(click -> {
			Notification.show("Sollecito Campagna Avviato", Notification.Type.TRAY_NOTIFICATION);
			@SuppressWarnings("unchecked")
			BgSollecita sollecita = new BgSollecita(repo,(SmdEditorUI<Campagna>)UI.getCurrent());
			sollecita.start();
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
			@SuppressWarnings("unchecked")
			BgSospendi sospendi = new BgSospendi(repo, p ,(SmdEditorUI<Campagna>)UI.getCurrent());
			sospendi.start();
	       	try {
					Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
            edit(get());
        });

        buttonWEstrattoConto.addClickListener(click -> {
			Notification.show("Estratto Conto Campagna Avviato", Notification.Type.TRAY_NOTIFICATION);
			@SuppressWarnings("unchecked")
			BgEstrattoConto estratto = new BgEstrattoConto(repo,(SmdEditorUI<Campagna>)UI.getCurrent());
			estratto.start();
	       	try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
            edit(get());
        });

        buttonWChiudi.addClickListener(click -> {
			Notification.show("Chiudi Campagna Avviato", Notification.Type.TRAY_NOTIFICATION);
			@SuppressWarnings("unchecked")
			BgChiudi bgchiudi = new BgChiudi(repo,(SmdEditorUI<Campagna>)UI.getCurrent());
			bgchiudi.start();
	       	try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
            edit(get());
        });

        getActions().addComponents(buttonWGenera,buttonWInvio,buttonWSollecita,buttonWSospendi,pubblicazione,buttonWEstrattoConto,buttonWChiudi);
		HorizontalLayout stato = new HorizontalLayout(anno,statoCampagna,numero,residuo);
		
		HorizontalLayout riviste = new HorizontalLayout();
		riviste.addComponent(new Label("riviste in abbonamento"));
		riviste.addComponents(items.getComponents());

        setComponents(
        		getActions(),
        		running,
        		riviste,
        		stato,           		
        		new HorizontalLayout(buttonVGenera,buttonVInvio,buttonVSollecita,buttonVEstrattoConto,buttonVDebito),
        	    new VerticalLayout(grid.getComponents()),
        		operazioni.getGrid(),
        		sospensioni.getGrid()
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
    	
        buttonVGenera.setVisible(persisted);
    	buttonVInvio.setVisible(persisted);
    	buttonVInvio.setEnabled(false);
    	buttonVSollecita.setVisible(persisted);
    	buttonVSollecita.setEnabled(false);
    	buttonVEstrattoConto.setVisible(persisted);
    	buttonVEstrattoConto.setEnabled(false);
    	buttonVDebito.setVisible(persisted);
        
    	anno.setReadOnly(persisted);
        
        buttonWGenera.setEnabled(false);
		buttonWInvio.setEnabled(false);
		buttonWSollecita.setEnabled(false);
		pubblicazione.setEnabled(false);
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
					break;
				case Inviata:
					buttonVInvio.setEnabled(true);
					buttonWSollecita.setEnabled(true);
					break;
				case InviatoSollecito:
					buttonVSollecita.setEnabled(true);
					buttonWSospendi.setEnabled(true);
					pubblicazione.setEnabled(true);
					break;
				case InviatoSospeso:
					buttonWSospendi.setEnabled(true);
					pubblicazione.setEnabled(true);
					buttonWEstrattoConto.setEnabled(true);
					break;
				case InviatoEC:
					buttonVEstrattoConto.setEnabled(true);
					buttonWChiudi.setEnabled(true);
					break;
				case Chiusa:
					numero.setReadOnly(true);
					residuo.setReadOnly(true);
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
    	private final UserInfo operatore;
    	
    	public Bg(UI ui, UserInfo operatore) {
			super();
			this.ui=ui;
			this.operatore=operatore;
		}

    	public abstract void exec(Campagna campagna) throws Exception;

		@Override
        public void run()
        {
            try {
                exec(get());
            } catch (Exception e) {
            	log.error("Bg:run: {}", e.getMessage(),e);
                if (ui != null) {
                	ui.access(() -> Notification.show("Errore" + e.getMessage(), Notification.Type.ERROR_MESSAGE));
                }
                return;
            }
            
            if (ui != null) {
            	ui.access(() -> Notification.show("Esecuzione Completata", Notification.Type.TRAY_NOTIFICATION));
            }
        }
		
		public UserInfo getOperatore() {
			return operatore;
		}
				
    }
    
    private final class BgGenera extends Bg {
    	private final CampagnaServiceDao repo;
    	
    	public BgGenera(CampagnaServiceDao repo, final SmdEditorUI<Campagna> ui) {
			super(ui, ui.getLoggedInUser());
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
    	
    	public BgInvia(CampagnaServiceDao repo, final SmdEditorUI<Campagna> ui) {
			super(ui, ui.getLoggedInUser());
			this.repo = repo;
		}


		@Override
        public void exec(Campagna c) throws Exception
        {
            repo.invia(c,getOperatore());
        }
    }

    private final class BgSollecita extends Bg {
    	private final CampagnaServiceDao repo;
    	
    	public BgSollecita(CampagnaServiceDao repo, final SmdEditorUI<Campagna> ui) {
			super(ui, ui.getLoggedInUser());
			this.repo = repo;
		}


		@Override
        public void exec(Campagna c) throws Exception
        {
            repo.sollecita(c,getOperatore());
        }
    }

    private final class BgSospendi extends Bg {
    	private final CampagnaServiceDao repo;
    	private final Pubblicazione p;
    	
    	public BgSospendi(CampagnaServiceDao repo, final Pubblicazione p, final SmdEditorUI<Campagna> ui) {
			super(ui, ui.getLoggedInUser());
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
    	
    	public BgEstrattoConto(CampagnaServiceDao repo, final SmdEditorUI<Campagna> ui) {
			super(ui, ui.getLoggedInUser());
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
    	
    	public BgChiudi(CampagnaServiceDao repo, final SmdEditorUI<Campagna> ui) {
			super(ui, ui.getLoggedInUser());
			this.repo = repo;
		}

		@Override
        public void exec(Campagna c) throws Exception
        {
            repo.chiudi(c,getOperatore());
        }
    }
}
