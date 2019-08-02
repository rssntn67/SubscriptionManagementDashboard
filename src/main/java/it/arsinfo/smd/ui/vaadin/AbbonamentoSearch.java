package it.arsinfo.smd.ui.vaadin;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.util.StringUtils;

import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;

import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.Cassa;
import it.arsinfo.smd.data.Ccp;
import it.arsinfo.smd.data.StatoAbbonamento;
import it.arsinfo.smd.data.TipoEstrattoConto;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Campagna;
import it.arsinfo.smd.entity.EstrattoConto;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.repository.AbbonamentoDao;
import it.arsinfo.smd.repository.EstrattoContoDao;

public class AbbonamentoSearch extends SmdSearch<Abbonamento> {

    private String searchCodeLine;
    private String searchCap;
    private Anagrafica customer;
    private Anno anno;
    private Campagna campagna;
    private final ComboBox<Ccp> filterCcp = new ComboBox<Ccp>();
    private final ComboBox<Cassa> filterCassa = new ComboBox<Cassa>();
    private final ComboBox<StatoAbbonamento> filterStatoAbbonamento= new ComboBox<StatoAbbonamento>();
    private Pubblicazione pubblicazione;
    private final ComboBox<TipoEstrattoConto> filterTipoEstrattoConto = new ComboBox<TipoEstrattoConto>();
    
    private final EstrattoContoDao estrattoContoDao;

    public AbbonamentoSearch(AbbonamentoDao abbonamentoDao, 
            EstrattoContoDao estrattoContoDao,
            List<Pubblicazione> pubblicazioni,
            List<Anagrafica> anagrafica, 
            List<Campagna> campagne) {
        super(abbonamentoDao);

        this.estrattoContoDao=estrattoContoDao;
        ComboBox<Anagrafica> filterAnagrafica = new ComboBox<Anagrafica>();
        ComboBox<Anno> filterAnno = new ComboBox<Anno>();
        ComboBox<Campagna> filterCampagna = new ComboBox<Campagna>();
        ComboBox<Pubblicazione> filterPubblicazione = new ComboBox<Pubblicazione>();
        
        TextField filterCodeLine = new TextField();
        TextField filterCap = new TextField();


        setComponents(new HorizontalLayout(filterAnagrafica,filterPubblicazione,filterStatoAbbonamento,filterAnno,filterCassa),
                      new HorizontalLayout(filterCap,filterCodeLine,filterCampagna,filterTipoEstrattoConto,filterCcp));

        filterCodeLine.setPlaceholder("Inserisci Code Line");
        filterCodeLine.setValueChangeMode(ValueChangeMode.EAGER);
        filterCodeLine.addValueChangeListener(e -> {
            searchCodeLine = e.getValue();
            onChange();
        });

        filterCap.setPlaceholder("Inserisci CAP");
        filterCap.setValueChangeMode(ValueChangeMode.EAGER);
        filterCap.addValueChangeListener(e -> {
            searchCap = e.getValue();
            onChange();
        });

        filterCampagna.setEmptySelectionAllowed(true);
        filterCampagna.setPlaceholder("Cerca per Campagna");
        filterCampagna.setItems(campagne);
        filterCampagna.setItemCaptionGenerator(Campagna::getCaption);
        filterCampagna.addSelectionListener(e -> {
            if (e.getValue() == null) {
                campagna = null;
            } else {
                campagna=e.getSelectedItem().get();
            }
            onChange();
        });

        filterPubblicazione.setEmptySelectionAllowed(true);
        filterPubblicazione.setPlaceholder("Cerca per Pubblicazioni");
        filterPubblicazione.setItems(pubblicazioni);
        filterPubblicazione.setItemCaptionGenerator(Pubblicazione::getNome);
        filterPubblicazione.addSelectionListener(e -> {
            if (e.getValue() == null) {
                pubblicazione = null;
            } else {
                pubblicazione = e.getSelectedItem().get();
            }
            onChange();
        });

        filterAnno.setEmptySelectionAllowed(true);
        filterAnno.setPlaceholder("Cerca per Anno");
        filterAnno.setItems(EnumSet.allOf(Anno.class));
        filterAnno.setItemCaptionGenerator(Anno::getAnnoAsString);
        filterAnno.addSelectionListener(e -> {
            if (e.getValue() == null) {
                anno = null;
            } else {
                anno=e.getSelectedItem().get();
            }
            onChange();
        });

        filterAnagrafica.setEmptySelectionAllowed(true);
        filterAnagrafica.setPlaceholder("Cerca per Anagrafica");
        filterAnagrafica.setItems(anagrafica);
        filterAnagrafica.setItemCaptionGenerator(Anagrafica::getCaption);
        filterAnagrafica.addSelectionListener(e -> {
            if (e.getValue() == null) {
                customer = null;
            } else {
                customer = e.getSelectedItem().get();
            }
            onChange();
        });
        
        filterCassa.setPlaceholder("Cerca per Cassa");
        filterCassa.setItems(EnumSet.allOf(Cassa.class));
        filterCassa.addSelectionListener(e ->onChange());

        filterStatoAbbonamento.setPlaceholder("Cerca per Stato");
        filterStatoAbbonamento.setItems(EnumSet.allOf(StatoAbbonamento.class));
        filterStatoAbbonamento.addSelectionListener(e ->onChange());

        filterCcp.setPlaceholder("Cerca per Cc");
        filterCcp.setItems(EnumSet.allOf(Ccp.class));
        filterCcp.setItemCaptionGenerator(Ccp::getCcp);
        filterCcp.addSelectionListener(e ->onChange());
        
        filterTipoEstrattoConto.setPlaceholder("Cerca per Tipo Estratto Conto");
        filterTipoEstrattoConto.setItems(EnumSet.allOf(TipoEstrattoConto.class));
        filterTipoEstrattoConto.addSelectionListener(e ->onChange());


    }

    private List<Abbonamento> findByTipoEstrattoConto(List<Abbonamento> abbonamenti, TipoEstrattoConto tec) {
        List<Long> approved = estrattoContoDao
                .findByTipoEstrattoConto(tec)
                .stream().map( ec -> ec.getAbbonamento().getId()).collect(Collectors.toList());
            return abbonamenti.stream().filter(abb -> approved.contains(abb.getId())).collect(Collectors.toList());

    }
    
    private List<Abbonamento> findByCustomer() {
       final Map<Long,Abbonamento> abbMap = 
                ((AbbonamentoDao) getRepo()).findByIntestatario(customer)
                .stream().collect(Collectors.toMap(Abbonamento::getId, Function.identity()));
        
        estrattoContoDao
            .findByDestinatario(customer)
            .stream()
            .filter(ec -> !abbMap.containsKey(ec.getAbbonamento().getId()))
            .forEach( ec -> {
                Abbonamento  abb = ((AbbonamentoDao) getRepo()).findById(ec.getAbbonamento().getId()).get();
                abbMap.put(abb.getId(), abb);
            });        
        return abbMap.values().stream().collect(Collectors.toList());
    }
    

    private List<Abbonamento> findByPubblicazione(List<Abbonamento> abbonamenti) {         
         List<Long> approved = estrattoContoDao
             .findByPubblicazione(pubblicazione)
             .stream().map( ec -> ec.getAbbonamento().getId()).collect(Collectors.toList());
         return abbonamenti.stream().filter(abb -> approved.contains(abb.getId())).collect(Collectors.toList());
     }

    private List<Abbonamento> findByCap(List<Abbonamento> abbonamenti) {
        List<Long> approved = abbonamenti
                 .stream()
                 .filter(
                         abb -> 
                     abb.getIntestatario().getCap() != null &&    
                     abb.getIntestatario().getCap().toLowerCase()
                     .contains(searchCap.toLowerCase()))
                 .map(abb -> abb.getId())
                 .collect(Collectors.toList());
         
      approved.addAll(estrattoContoDao
             .findAll()
             .stream()
             .filter(ec -> 
                 ec.getDestinatario().getCap() != null &&
                 ec.getDestinatario().getCap().toLowerCase()
                 .contains(searchCap.toLowerCase()))
             .map( ec -> 
                 ec.getAbbonamento().getId()).collect(Collectors.toList())
             );        
      return abbonamenti.stream().filter(abb -> approved.contains(abb.getId())).collect(Collectors.toList());
     }

    @Override
    public List<Abbonamento> find() {
        if (campagna == null && customer == null && anno == null) {
            return filterAll(findAll());            
        }
        if (campagna == null && anno == null) {
            return filterAll(findByCustomer());
        }
        if (customer == null && anno == null) {
            return filterAll(((AbbonamentoDao) getRepo()).findByCampagna(campagna));
        }
        if (customer == null && campagna == null) {
            return filterAll(((AbbonamentoDao) getRepo()).findByAnno(anno));
        }
        
        if (anno == null) {
           return filterAll(findByCustomer()
            .stream()
            .filter(a -> 
                a.getCampagna() != null
                && a.getCampagna().getId() == campagna.getId()
            )
            .collect(Collectors.toList()));
        }
        if (campagna == null) {
           return filterAll(findByCustomer()
            .stream()
            .filter(a -> 
                a.getAnno() == anno
                )
            .collect(Collectors.toList()));
        }
        if (customer == null) {
            return filterAll(((AbbonamentoDao) getRepo()).findByCampagna(campagna)
                .stream()
                    .filter(a -> 
                        a.getAnno() == anno
                        )
                    .collect(Collectors.toList()));
        }
        return filterAll(((AbbonamentoDao) getRepo()).findByIntestatario(customer)
                .stream()
                .filter(a -> 
                    a.getCampagna() != null
                    && a.getCampagna().getId() == campagna.getId()
                    && a.getAnno() == anno
                    )
                .collect(Collectors.toList()));
    }

    private List<Abbonamento> filterAll(List<Abbonamento> abbonamenti) {
        if (filterTipoEstrattoConto.getValue() != null) {
            abbonamenti = findByTipoEstrattoConto(abbonamenti, filterTipoEstrattoConto.getValue());
        }
        if (!StringUtils.isEmpty(searchCap)) {
            abbonamenti = findByCap(abbonamenti);
        }
        if (pubblicazione != null) {
            abbonamenti = findByPubblicazione(abbonamenti);
        }
        if (filterCcp.getValue() != null) {
            abbonamenti=abbonamenti.stream().filter(a -> a.getCcp() == filterCcp.getValue()).collect(Collectors.toList());      
        }
        if (filterCassa.getValue() != null) {
            abbonamenti=abbonamenti.stream().filter(a -> a.getCassa() == filterCassa.getValue()).collect(Collectors.toList());      
        }
        if (filterStatoAbbonamento.getValue() != null) {
            abbonamenti=abbonamenti.stream().filter(a -> a.getStatoAbbonamento() == filterStatoAbbonamento.getValue()).collect(Collectors.toList());      
        }
        if (!StringUtils.isEmpty(searchCodeLine)) {
            abbonamenti=abbonamenti.stream().filter(a -> a.getCodeLine().toLowerCase().contains(searchCodeLine.toLowerCase())).collect(Collectors.toList());                  
        }
        return abbonamenti;
    }
    
    public List<EstrattoConto> findEC() {
        if (pubblicazione != null) {
            return filterAllEC(estrattoContoDao.findByPubblicazione(pubblicazione));
        }
        return filterAllEC(estrattoContoDao.findAll());
    }

    private List<EstrattoConto> filterAllEC(List<EstrattoConto> estrattiConto) {
        if (customer != null) {
            estrattiConto = estrattiConto.stream().filter( s -> s.getDestinatario().getId() == customer.getId()).collect(Collectors.toList());
        }
        if (filterTipoEstrattoConto.getValue() != null) {
            estrattiConto=estrattiConto.stream().filter(s -> s.getTipoEstrattoConto() == filterTipoEstrattoConto.getValue()).collect(Collectors.toList());      
        }
        
        return estrattiConto;
    }


}
