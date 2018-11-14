    package it.arsinfo.smd.vaadin;
    
    import java.util.ArrayList;
    import java.util.EnumSet;
    import java.util.List;
    
    import it.arsinfo.smd.SmdApplication;
    import it.arsinfo.smd.entity.Abbonamento;
    import it.arsinfo.smd.entity.Anagrafica;
    import it.arsinfo.smd.entity.Anno;
    import it.arsinfo.smd.entity.ContoCorrentePostale;
    import it.arsinfo.smd.entity.Mese;
    import it.arsinfo.smd.entity.Pubblicazione;
    import it.arsinfo.smd.repository.AbbonamentoDao;
    import it.arsinfo.smd.repository.AnagraficaDao;
    import it.arsinfo.smd.repository.PubblicazioneDao;
    
    import com.vaadin.data.Binder;
    import com.vaadin.data.converter.LocalDateToDateConverter;
    import com.vaadin.data.converter.StringToBigDecimalConverter;
    import com.vaadin.icons.VaadinIcons;
    import com.vaadin.ui.Alignment;
    import com.vaadin.ui.Button;
    import com.vaadin.ui.CheckBox;
    import com.vaadin.ui.ComboBox;
    import com.vaadin.ui.DateField;
    import com.vaadin.ui.HorizontalLayout;
    import com.vaadin.ui.TextField;
    import com.vaadin.ui.VerticalLayout;
    import com.vaadin.ui.themes.ValoTheme;
    
    public class AbbonamentoEditor extends VerticalLayout {
    
        /**
         * 
         */
        private static final long serialVersionUID = 4673834235533544936L;
    
        private final AbbonamentoDao repo;
        private final PubblicazioneDao pubbldao;
    
        /**
         * The currently edited customer
         */
        private Abbonamento abbonamento;
        private final ComboBox<Anagrafica> anagrafica = new ComboBox<Anagrafica>("Selezionare il cliente");
        private final ComboBox<Anagrafica> destinatario = new ComboBox<Anagrafica>("Selezionare il destinatario");
        private final TextField campo = new TextField("V Campo Poste Italiane");
        private final TextField cost = new TextField("Costo");
    
        private final CheckBox omaggio = new CheckBox("Omaggio");
        private final CheckBox pagato = new CheckBox("Pagato");
        private final DateField incasso = new DateField("Incassato");
        private final CheckBox estratti = new CheckBox("Abb. Ann. Estratti");
        private final CheckBox blocchetti = new CheckBox("Abb. Sem. Blocchetti");
        private final CheckBox lodare = new CheckBox("Abb. Men. Lodare e Service");
        private final CheckBox messaggio = new CheckBox("Abb. Men. Messaggio");
        private final TextField spese = new TextField("Spese Spedizione");
    
        private final ComboBox<Anno> anno = new ComboBox<Anno>("Selezionare Anno",
                                                               EnumSet.allOf(Anno.class));
        private final ComboBox<Mese> inizio = new ComboBox<Mese>("Selezionare Inizio",
                                                                 EnumSet.allOf(Mese.class));
        private final ComboBox<Mese> fine = new ComboBox<Mese>("Selezionare Fine",
                                                               EnumSet.allOf(Mese.class));
        private final ComboBox<ContoCorrentePostale> contoCorrentePostale = new ComboBox<ContoCorrentePostale>("Selezionare ccp",
                                                                                                               EnumSet.allOf(ContoCorrentePostale.class));
    
        Button save = new Button("Save", VaadinIcons.CHECK);
        Button cancel = new Button("Cancel");
        Button delete = new Button("Delete", VaadinIcons.TRASH);
    
        HorizontalLayout pri = new HorizontalLayout(anagrafica, destinatario,
                                                    anno, inizio, fine);
        HorizontalLayout sec = new HorizontalLayout(campo, cost,
                                                    contoCorrentePostale);
        HorizontalLayout che = new HorizontalLayout(estratti, blocchetti, lodare,
                                                    messaggio, spese);
        HorizontalLayout pag = new HorizontalLayout(omaggio, pagato);
        HorizontalLayout pagfield = new HorizontalLayout(incasso);
        HorizontalLayout actions = new HorizontalLayout(save, cancel, delete);
    
        Binder<Abbonamento> binder = new Binder<>(Abbonamento.class);
        private ChangeHandler changeHandler;
    
        public AbbonamentoEditor(AbbonamentoDao repo, AnagraficaDao anagraficaDao,
                PubblicazioneDao pubblDao) {
    
            this.repo = repo;
            this.pubbldao = pubblDao;
    
            addComponents(pri, sec, che, pag, pagfield, actions);
            setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
    
            anno.setItemCaptionGenerator(Anno::getAnnoAsString);
    
            inizio.setItemCaptionGenerator(Mese::getNomeBreve);
            fine.setItemCaptionGenerator(Mese::getNomeBreve);
    
            contoCorrentePostale.setItemCaptionGenerator(ContoCorrentePostale::getCcp);
    
            anagrafica.setItems(anagraficaDao.findAll());
            anagrafica.setItemCaptionGenerator(Anagrafica::getCaption);
            destinatario.setItems(anagraficaDao.findAll());
            destinatario.setItemCaptionGenerator(Anagrafica::getCaption);
    
            binder.forField(anagrafica).asRequired().withValidator(an -> an != null,
                                                                   "Scegliere un Cliente").bind(Abbonamento::getAnagrafica,
                                                                                                Abbonamento::setAnagrafica);
            binder.forField(destinatario).bind("destinatario");
            binder.forField(anno).bind("anno");
            binder.forField(inizio).bind("inizio");
            binder.forField(fine).bind("fine");
            binder.forField(contoCorrentePostale).bind("contoCorrentePostale");
    
            binder.forField(campo).asRequired().withValidator(ca -> ca != null,
                                                              "Deve essere definito").bind(Abbonamento::getCampo,
                                                                                           Abbonamento::setCampo);
            binder.forField(cost).asRequired().withConverter(new StringToBigDecimalConverter("Conversione in Eur")).bind(Abbonamento::getCost,
                                                                                                                         Abbonamento::setCost);
            binder.forField(lodare).bind("lodare");
            binder.forField(messaggio).bind("messaggio");
            binder.forField(estratti).bind("estratti");
            binder.forField(blocchetti).bind("blocchetti");
            binder.forField(spese).asRequired().withConverter(new StringToBigDecimalConverter("Conversione in Eur")).bind(Abbonamento::getSpese,
                                                                                                                          Abbonamento::setSpese);
            binder.forField(pagato).bind("pagato");
            binder.forField(omaggio).bind("omaggio");
            binder.forField(incasso).withConverter(new LocalDateToDateConverter()).bind("incasso");
    
            // Configure and style components
            setSpacing(true);
    
            save.addStyleName(ValoTheme.BUTTON_PRIMARY);
            delete.addStyleName(ValoTheme.BUTTON_DANGER);
    
            save.addClickListener(e -> save());
            delete.addClickListener(e -> delete());
            cancel.addClickListener(e -> edit(abbonamento));
            setVisible(false);
    
        }
    
        void delete() {
            repo.delete(abbonamento);
            changeHandler.onChange();
        }
    
        void save() {
            if (abbonamento.getIncasso() != null) {
                abbonamento.setPagato(true);
            } else {
                if (abbonamento.getAnagrafica().getOmaggio() != null) {
                    abbonamento.setOmaggio(true);
                } else if (!abbonamento.isOmaggio()) {
                    List<Pubblicazione> abbpub = new ArrayList<Pubblicazione>();
                    if (abbonamento.isBlocchetti()) {
                        abbpub.addAll(pubbldao.findByNomeStartsWithIgnoreCase("Blocchetti"));
                    }
                    if (abbonamento.isEstratti()) {
                        abbpub.addAll(pubbldao.findByNomeStartsWithIgnoreCase("Estratti"));
                    }
                    if (abbonamento.isLodare()) {
                        abbpub.addAll(pubbldao.findByNomeStartsWithIgnoreCase("Lodare"));
                    }
                    if (abbonamento.isMessaggio()) {
                        abbpub.addAll(pubbldao.findByNomeStartsWithIgnoreCase("Messaggio"));
                    }
                    abbonamento.setCost(SmdApplication.getCosto(abbonamento,
                                                                abbpub));
                    abbonamento.setCampo(SmdApplication.generateCampo(abbonamento.getAnno(),
                                                                      abbonamento.getInizio(),
                                                                      abbonamento.getFine()));
                }
            }
            repo.save(abbonamento);
            changeHandler.onChange();
        }
    
        public interface ChangeHandler {
            void onChange();
        }
    
        public void setChangeHandler(ChangeHandler h) {
            // ChangeHandler is notified when either save or delete
            // is clicked
            changeHandler = h;
        }
    
        public final void edit(Abbonamento c) {
            if (c == null) {
                setVisible(false);
                return;
            }
            final boolean persisted = c.getId() != null;
            if (persisted) {
                // Find fresh entity for editing
                abbonamento = repo.findById(c.getId()).get();
            } else {
                abbonamento = c;
            }
            setEditable(persisted);
    
            // Bind customer properties to similarly named fields
            // Could also use annotation or "manual binding" or programmatically
            // moving values from fields to entities before saving
            binder.setBean(abbonamento);
            setVisible(true);
    
            // Focus first name initially
            anagrafica.focus();
        }
    
        private void setEditable(boolean persisted) {
    
            cancel.setVisible(persisted);
    
            anagrafica.setReadOnly(persisted);
            destinatario.setReadOnly(persisted);
    
            estratti.setReadOnly(persisted);
            blocchetti.setReadOnly(persisted);
            lodare.setReadOnly(persisted);
            messaggio.setReadOnly(persisted);
            spese.setReadOnly(persisted);
    
            anno.setReadOnly(persisted);
            inizio.setReadOnly(persisted);
            fine.setReadOnly(persisted);
            cost.setVisible(persisted);
            cost.setReadOnly(persisted);
            campo.setVisible(persisted);
            campo.setReadOnly(persisted);
    
            if (persisted && abbonamento.isOmaggio()) {
                save.setEnabled(false);
                cancel.setEnabled(false);
                omaggio.setReadOnly(true);
                pagato.setVisible(false);
                pagato.setReadOnly(true);
                incasso.setVisible(false);
                incasso.setReadOnly(true);
                return;
                
            } 
            
            if (persisted && abbonamento.isPagato()) {
                save.setEnabled(false);
                cancel.setEnabled(false);
                omaggio.setReadOnly(true);
                pagato.setVisible(true);
                pagato.setReadOnly(true);
                incasso.setVisible(true);
                incasso.setReadOnly(true);
                return;
            } 
            
            if (persisted) {
                save.setEnabled(true);
                cancel.setEnabled(true);
                omaggio.setReadOnly(true);
                pagato.setVisible(true);
                incasso.setVisible(true);
                return;
            } 
            
            save.setEnabled(true);
            cancel.setEnabled(false);
            omaggio.setReadOnly(false);
            pagato.setVisible(false);
            incasso.setVisible(false);
    
        }
    
    }
