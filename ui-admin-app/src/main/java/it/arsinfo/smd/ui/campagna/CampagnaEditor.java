package it.arsinfo.smd.ui.campagna;

import com.vaadin.data.Binder;
import com.vaadin.data.converter.StringToBigDecimalConverter;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import it.arsinfo.smd.entity.*;
import it.arsinfo.smd.service.api.CampagnaService;
import it.arsinfo.smd.service.api.SmdService;
import it.arsinfo.smd.ui.EuroConverter;
import it.arsinfo.smd.ui.SmdEditorUI;
import it.arsinfo.smd.ui.vaadin.SmdEntityEditor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

public class CampagnaEditor extends SmdEntityEditor<Campagna> {

    private static final Logger log = LoggerFactory.getLogger(SmdService.class);
	private final ComboBox<Anno> anno = new ComboBox<>("Anno",EnumSet.allOf(Anno.class));

    private final ComboBox<StatoCampagna> statoCampagna = 
    		new ComboBox<>("Stato",EnumSet.allOf(StatoCampagna.class));
    
    private final Label running = new Label("");
    private final CampagnaItemsEditor items;
    private final TextField numero = new TextField("Max num. Riviste");
	private final TextField limiteInvioEstratto = new TextField("Importo Minimo Debitori");
	private final TextField limiteInvioSollecito = new TextField("Importo Minimo Debitori");
	private final TextField speseEstrattoConto = new TextField("Spese");
	private final TextField speseSollecito = new TextField("Spese");

	private final TextField contrassegno = new TextField("Spese Contrassegno");

	private final TextField sogliaImportoTotale = new TextField("Soglia Importo");
	private final TextField minPercIncassato = new TextField("Fattore Minimo");
	private final TextField maxDebito = new TextField("Max Debito");

	private final AbbonamentoDtoGrid grid = new AbbonamentoDtoGrid("Abbonamenti");
    private final OperazioneCampagnaGrid operazioni =  new OperazioneCampagnaGrid("Operazioni");    
    private final OperazioneSospendiGrid sospensioni =  new OperazioneSospendiGrid("Sospensioni");
   
    private final Button buttonVisualizzaGenerati = new Button("Abb. Generati",VaadinIcons.ARCHIVE);
    private final Button buttonVisualizzaInviati = new Button("Abb. Proposti",VaadinIcons.ARCHIVE);
    private final Button buttonVisualizzaSollecitati = new Button("Abb. Sollecitati",VaadinIcons.ARCHIVE);
    private final Button buttonVisualizzaEstrattoConto = new Button("Abb. Inviato Estratto Conto",VaadinIcons.ARCHIVE);
    private final Button buttonVisualizzaDebitori = new Button("Abb. Debitori",VaadinIcons.ARCHIVE);

    private final Button buttonGenera = new Button("Genera",VaadinIcons.ENVELOPES);
    private final Button buttonInvio = new Button("Invia",VaadinIcons.ENVELOPES);
    private final Button buttonSollecita = new Button("Sollecita",VaadinIcons.ENVELOPES);
    private final Button buttonSospendi = new Button("Sospendi",VaadinIcons.ENVELOPES);
    private final Button buttonEstrattoConto = new Button("Estratto Conto",VaadinIcons.ENVELOPES);
    private final Button buttonChiudi = new Button("Chiudi",VaadinIcons.ENVELOPES);

    private final ComboBox<Pubblicazione> comboBoxPubblicazioneDaSospendere = new ComboBox<>();

    private final CampagnaService repo;

    public CampagnaEditor(CampagnaService repo) {

        super(repo, new Binder<>(Campagna.class));
        this.repo=repo;
        List<Pubblicazione> pubblicazioni = repo.findPubblicazioni();
        comboBoxPubblicazioneDaSospendere.setItems(pubblicazioni);
        comboBoxPubblicazioneDaSospendere.setItemCaptionGenerator(Pubblicazione::getNome);
        items = new CampagnaItemsEditor(pubblicazioni);
        
        operazioni.getGrid().setHeightByRows(5.0);
        sospensioni.getGrid().setHeightByRows(4.0);

		buttonGenera.addStyleName(ValoTheme.BUTTON_PRIMARY);
		buttonInvio.addStyleName(ValoTheme.BUTTON_PRIMARY);
		buttonSollecita.addStyleName(ValoTheme.BUTTON_PRIMARY);
		buttonSospendi.addStyleName(ValoTheme.BUTTON_PRIMARY);
		buttonEstrattoConto.addStyleName(ValoTheme.BUTTON_PRIMARY);
		buttonChiudi.addStyleName(ValoTheme.BUTTON_PRIMARY);

		buttonVisualizzaGenerati.addClickListener(click -> grid.populate(repo.findAbbonamentoConRivisteGenerati(get())));
        buttonVisualizzaInviati.addClickListener(click -> grid.populate(repo.findAbbonamentoConRivisteInviati(get())));
        buttonVisualizzaSollecitati.addClickListener(click -> grid.populate(repo.findAbbonamentoConRivisteSollecito(get())));
        buttonVisualizzaEstrattoConto.addClickListener(click -> grid.populate(repo.findAbbonamentoConRivisteEstrattoConto(get())));
        buttonVisualizzaDebitori.addClickListener(click -> grid.populate(repo.findAbbonamentoConDebito(get())));

        buttonGenera.addClickListener(click -> {
			Notification.show("Generazione Campagna Avviata", Notification.Type.TRAY_NOTIFICATION);
			@SuppressWarnings("unchecked")
			BgGenera genera = new BgGenera(repo,(SmdEditorUI<Campagna>)UI.getCurrent());
			genera.start();
        	try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				Notification.show("Generazione Campagna Sleep Error", Notification.Type.TRAY_NOTIFICATION);
			}
            edit(get());
        });

        buttonInvio.addClickListener(click -> {
			Notification.show("Invio Campagna Avviato", Notification.Type.TRAY_NOTIFICATION);
			@SuppressWarnings("unchecked")
			BgInvia invia = new BgInvia(repo,(SmdEditorUI<Campagna>)UI.getCurrent());
			invia.start();
	       	try {
					Thread.sleep(1000);
			} catch (InterruptedException e) {
				Notification.show("Invio Campagna Sleep Error", Notification.Type.TRAY_NOTIFICATION);
			}
            edit(get());
        });

        buttonSollecita.addClickListener(click -> {
			Notification.show("Sollecito Campagna Avviato", Notification.Type.TRAY_NOTIFICATION);
			@SuppressWarnings("unchecked")
			BgSollecita sollecita = new BgSollecita(repo,(SmdEditorUI<Campagna>)UI.getCurrent());
			sollecita.start();
	       	try {
					Thread.sleep(1000);
			} catch (InterruptedException e) {
				Notification.show("Sollecito Campagna Sleep Error", Notification.Type.TRAY_NOTIFICATION);
			}
            edit(get());
        });

        buttonSospendi.addClickListener(click -> {
        	Pubblicazione p = comboBoxPubblicazioneDaSospendere.getValue();
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
				Notification.show("Sospendi Invio Pubblicazione Campagna Sleep Error", Notification.Type.TRAY_NOTIFICATION);
			}
            edit(get());
        });

        buttonEstrattoConto.addClickListener(click -> {
			Notification.show("Estratto Conto Campagna Avviato", Notification.Type.TRAY_NOTIFICATION);
			@SuppressWarnings("unchecked")
			BgEstrattoConto estratto = new BgEstrattoConto(repo,(SmdEditorUI<Campagna>)UI.getCurrent());
			estratto.start();
	       	try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				Notification.show("Estratto Conto Campagna Sleep Error", Notification.Type.TRAY_NOTIFICATION);
			}
            edit(get());
        });

        buttonChiudi.addClickListener(click -> {
			Notification.show("Chiudi Campagna Avviato", Notification.Type.TRAY_NOTIFICATION);
			@SuppressWarnings("unchecked")
			BgChiudi bgchiudi = new BgChiudi(repo,(SmdEditorUI<Campagna>)UI.getCurrent());
			bgchiudi.start();
	       	try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				Notification.show("Chiudi Campagna Sleep Error", Notification.Type.TRAY_NOTIFICATION);
			}
            edit(get());
        });

        getActions().addComponents(buttonGenera, buttonInvio, buttonSollecita, buttonSospendi, comboBoxPubblicazioneDaSospendere, buttonEstrattoConto, buttonChiudi);

        HorizontalLayout stato = new HorizontalLayout(anno,statoCampagna);
        Label generatedLabel= new Label("Il valore contrassegno viene usato quando la campagna viene generata\n" +
				                             "Inserire il valore delle spese da pagare con contrassegno",
				ContentMode.PREFORMATTED);
		Label sollecitoLabel =
				new Label( "Questi valori vengono utilizzati quando si invia il sollecito.\n " +
						        "Inserire il valore minimo di debito e le spese da aggiungere:\n" +
						        "il sollecito è inviato solo per importi > minimo debito.",
				ContentMode.PREFORMATTED);
		Label ecLabel =
				new Label( "Questi valori vengono utilizzati quando si invia l'Est. Conto.\n" +
						        "Inserire il valore minimo di debito e le spese da aggiungere: \n" +
						        "l'EC viene inviato solo per importi > minimo debito",
						ContentMode.PREFORMATTED);

		Label closeLabel =
				new Label( "Questo valore viene utilizzato solo quando si chiude la campagna.\n" +
						         "Inserire il numero minimo di riviste per mantenere attivo lo storico nel caso di abbonati debitori.\n" +
                                 "Se il numero di riviste in abbonamento supera il valore di 'numero', lo storico rimane attivo per la prossima campagna",
						ContentMode.PREFORMATTED);

		Label incassoLabel =
				new Label(
				 "Gli abbonamenti sono validi se: \n" +
					  " **** importo >= Soglia Importo & incassato >= importo * Fattore Minimo\n" +
				      " **** importo < Soglia Importo & debito < Max Debito \n" +
                      "La modifica di questi valori aggiornerà lo stato delle riviste\n"+
					  "in base allo stato della Campagna"
				,ContentMode.PREFORMATTED);
		Panel generated = new Panel("Gestione Contrassegno");
		generated.setContent(new HorizontalLayout(contrassegno,generatedLabel));
		generated.setSizeUndefined();

		Panel sollecito = new Panel("Gestione Sollecito");
		sollecito.setContent(new HorizontalLayout(limiteInvioSollecito,speseSollecito,sollecitoLabel));
		sollecito.setSizeUndefined();

		Panel ec = new Panel("Gestione Estratto Conto");
		ec.setContent(new HorizontalLayout(limiteInvioEstratto,speseEstrattoConto,ecLabel));
		ec.setSizeUndefined();

		Panel close = new Panel("Gestione chiusura");
		close.setContent(new HorizontalLayout(numero,closeLabel));
		close.setSizeUndefined();
		Panel statoincasso = new Panel("Gestione Stato Incasso");
		statoincasso.setContent(new HorizontalLayout(sogliaImportoTotale,minPercIncassato,maxDebito,incassoLabel));
		statoincasso.setSizeUndefined();


		HorizontalLayout riviste = new HorizontalLayout();
		riviste.addComponent(new Label("riviste in abbonamento"));
		riviste.addComponents(items.getComponents());

        setComponents(
        		getActions(),
        		running,
        		riviste,
				stato,
				new HorizontalLayout(buttonVisualizzaGenerati, buttonVisualizzaInviati, buttonVisualizzaSollecitati, buttonVisualizzaEstrattoConto, buttonVisualizzaDebitori),
				new VerticalLayout(grid.getComponents()),
				operazioni.getGrid(),
				sospensioni.getGrid(),
        		statoincasso,
				generated,
        		sollecito,
        		ec,
        		close
		);
        
        grid.setVisible(false);
        anno.setItemCaptionGenerator(Anno::getAnnoAsString);
        statoCampagna.setReadOnly(true);
        
        items.setChangeHandler(() -> items.edit(repo.findPubblicazioniValide().
				stream().
				map(p -> {
					CampagnaItem ci = new CampagnaItem();
					ci.setPubblicazione(p);
					ci.setCampagna(get());
					get().addCampagnaItem(ci);
					return ci;
		}).collect(Collectors.toList()), false));
        
        grid.setChangeHandler(() -> grid.setVisible(false));

        getBinder()
        .forField(numero)
        .withConverter(new StringToIntegerConverter("Deve essere un numero"))
        .withValidator(num -> num != null && num > 0,"deve essere maggiore di 0")
        .bind(Campagna::getNumero, Campagna::setNumero);

		getBinder()
				.forField(speseEstrattoConto)
				.withConverter(new EuroConverter("Conversione in Eur"))
				.bind("speseEstrattoConto");

		getBinder()
				.forField(speseSollecito)
				.withConverter(new EuroConverter("Conversione in Eur"))
				.bind("speseSollecito");

		getBinder()
				.forField(limiteInvioEstratto)
				.withConverter(new EuroConverter("Conversione in Eur"))
				.bind("limiteInvioEstratto");

		getBinder()
				.forField(limiteInvioSollecito)
				.withConverter(new EuroConverter("Conversione in Eur"))
				.bind("limiteInvioSollecito");

		getBinder()
				.forField(contrassegno)
				.withConverter(new EuroConverter("Conversione in Eur"))
				.bind("contrassegno");

		getBinder()
				.forField(sogliaImportoTotale)
				.withConverter(new EuroConverter("Conversione in Eur"))
				.bind("sogliaImportoTotale");

		getBinder()
				.forField(minPercIncassato)
				.withConverter(new StringToBigDecimalConverter("Conversione Decimale"))
				.withValidator(value-> value.signum() >0 && value.subtract(BigDecimal.ONE).signum()<0,"Deve essere un decimale essere compreso fra 0 e 1" )
				.bind("minPercIncassato");

		getBinder()
				.forField(maxDebito)
				.withConverter(new EuroConverter("Conversione in Eur"))
				.bind("maxDebito");


		getBinder().bindInstanceFields(this);
    }

    @Override
	public void save() {
		Notification.show("Salva Campagna Avviato", Notification.Type.TRAY_NOTIFICATION);
		@SuppressWarnings("unchecked")
		BgSave bgsave = new BgSave(repo,(SmdEditorUI<Campagna>)UI.getCurrent());
		bgsave.start();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			Notification.show("Chiudi Campagna Sleep Error", Notification.Type.TRAY_NOTIFICATION);
		}
		edit(get());

	}

    @Override
    public void focus(boolean persisted, Campagna campagna) {
        buttonVisualizzaGenerati.setVisible(persisted);
    	buttonVisualizzaInviati.setVisible(persisted);
    	buttonVisualizzaSollecitati.setVisible(persisted);
    	buttonVisualizzaEstrattoConto.setVisible(persisted);
    	buttonVisualizzaDebitori.setVisible(persisted);

        buttonGenera.setVisible(!persisted);
		buttonInvio.setVisible(false);
		buttonSollecita.setVisible(false);
		buttonSospendi.setVisible(false);
		comboBoxPubblicazioneDaSospendere.setVisible(false);
		buttonEstrattoConto.setVisible(false);
		buttonChiudi.setVisible(false);

		anno.setReadOnly(persisted);
		statoCampagna.setVisible(persisted);

		getSave().setEnabled(persisted);
		getCancel().setEnabled(persisted);
        getDelete().setEnabled(!campagna.isRunning() && persisted &&
    		campagna.getStatoCampagna() == StatoCampagna.Generata 
         && campagna.getAnno().getAnno() > Anno.getAnnoCorrente().getAnno()
        );

        if (campagna.isRunning()) {
			running.setValue("locked: running...");
			getSave().setEnabled(false);
			getCancel().setEnabled(false);
			getDelete().setEnabled(false);
			return;
		}
        running.setValue("");
		if (!persisted) {
			items.onChange();
			anno.focus();
			return;
		}
		contrassegno.setReadOnly(false);
		limiteInvioSollecito.setReadOnly(false);
		speseSollecito.setReadOnly(false);
		limiteInvioEstratto.setReadOnly(false);
		speseEstrattoConto.setReadOnly(false);
		sogliaImportoTotale.setReadOnly(false);
		minPercIncassato.setReadOnly(false);
		maxDebito.setReadOnly(false);

		switch (get().getStatoCampagna()) {
		case Generata:
			buttonInvio.setVisible(true);
			break;
		case Inviata:
			buttonSollecita.setVisible(true);
			contrassegno.setReadOnly(true);
			break;
		case InviatoSollecito:
		case InviatoSospeso:
			buttonSospendi.setVisible(true);
			comboBoxPubblicazioneDaSospendere.setVisible(true);
			buttonEstrattoConto.setVisible(true);
			limiteInvioSollecito.setReadOnly(true);
			speseSollecito.setReadOnly(true);
			contrassegno.setReadOnly(true);
			break;
		case InviatoEC:
			buttonChiudi.setVisible(true);
			limiteInvioSollecito.setReadOnly(true);
			speseSollecito.setReadOnly(true);
			contrassegno.setReadOnly(true);
			limiteInvioEstratto.setReadOnly(true);
			speseEstrattoConto.setReadOnly(true);
			break;
		case Chiusa:
			limiteInvioSollecito.setReadOnly(true);
			speseSollecito.setReadOnly(true);
			contrassegno.setReadOnly(true);
			limiteInvioEstratto.setReadOnly(true);
			speseEstrattoConto.setReadOnly(true);
			sogliaImportoTotale.setReadOnly(true);
			minPercIncassato.setReadOnly(true);
			maxDebito.setReadOnly(true);
			numero.setReadOnly(true);
			getSave().setEnabled(false);
			getCancel().setEnabled(false);
			break;
		default:
			break;
		}

		items.edit(campagna.getCampagnaItems(),true);
		operazioni.populate(repo.getOperazioni(campagna));
		sospensioni.populate(repo.getSospensioni(campagna));

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

	private final class BgSave extends Bg {
		private final CampagnaService repo;

		public BgSave(CampagnaService repo, final SmdEditorUI<Campagna> ui) {
			super(ui, ui.getLoggedInUser());
			this.repo = repo;
		}


		@Override
		public void exec(Campagna c) throws Exception
		{
			repo.save(c);
		}
	}

	private final class BgGenera extends Bg {
    	private final CampagnaService repo;
    	
    	public BgGenera(CampagnaService repo, final SmdEditorUI<Campagna> ui) {
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
    	private final CampagnaService repo;
    	
    	public BgInvia(CampagnaService repo, final SmdEditorUI<Campagna> ui) {
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
    	private final CampagnaService repo;
    	
    	public BgSollecita(CampagnaService repo, final SmdEditorUI<Campagna> ui) {
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
    	private final CampagnaService repo;
    	private final Pubblicazione p;
    	
    	public BgSospendi(CampagnaService repo, final Pubblicazione p, final SmdEditorUI<Campagna> ui) {
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
    	private final CampagnaService repo;
    	
    	public BgEstrattoConto(CampagnaService repo, final SmdEditorUI<Campagna> ui) {
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
    	private final CampagnaService repo;
    	
    	public BgChiudi(CampagnaService repo, final SmdEditorUI<Campagna> ui) {
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
