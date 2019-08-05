package it.arsinfo.smd;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import it.arsinfo.smd.data.Accettazione;
import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.AreaSpedizione;
import it.arsinfo.smd.data.Bollettino;
import it.arsinfo.smd.data.Cassa;
import it.arsinfo.smd.data.Ccp;
import it.arsinfo.smd.data.Cuas;
import it.arsinfo.smd.data.Invio;
import it.arsinfo.smd.data.InvioSpedizione;
import it.arsinfo.smd.data.Mese;
import it.arsinfo.smd.data.RangeSpeseSpedizione;
import it.arsinfo.smd.data.Sostitutivo;
import it.arsinfo.smd.data.StatoAbbonamento;
import it.arsinfo.smd.data.StatoCampagna;
import it.arsinfo.smd.data.StatoSpedizione;
import it.arsinfo.smd.data.StatoStorico;
import it.arsinfo.smd.data.TipoEstrattoConto;
import it.arsinfo.smd.data.TipoPubblicazione;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Campagna;
import it.arsinfo.smd.entity.CampagnaItem;
import it.arsinfo.smd.entity.EstrattoConto;
import it.arsinfo.smd.entity.Incasso;
import it.arsinfo.smd.entity.Operazione;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.entity.Spedizione;
import it.arsinfo.smd.entity.SpedizioneItem;
import it.arsinfo.smd.entity.SpesaSpedizione;
import it.arsinfo.smd.entity.Storico;
import it.arsinfo.smd.entity.Versamento;

@Configuration
public class Smd {

    private static final Logger log = LoggerFactory.getLogger(Smd.class);
    private static final DateFormat formatter = new SimpleDateFormat("yyMMddH");
    private static final DateFormat unformatter = new SimpleDateFormat("yyMMdd");    

    public static final BigDecimal contrassegno=new BigDecimal(4.50);
    @Bean
    public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
    }
    
    public static EstrattoConto generaECDaStorico(Abbonamento abb,Storico storico) {
        final EstrattoConto ec = new EstrattoConto();
        ec.setStorico(storico);
        ec.setAbbonamento(abb);
        ec.setPubblicazione(storico.getPubblicazione());
        ec.setNumero(storico.getNumero());
        ec.setTipoEstrattoConto(storico.getTipoEstrattoConto());
        ec.setMeseInizio(Mese.GENNAIO);
        ec.setAnnoInizio(abb.getAnno());
        ec.setMeseFine(Mese.DICEMBRE);
        ec.setAnnoFine(abb.getAnno());
        ec.setInvio(storico.getInvio());
        ec.setInvioSpedizione(storico.getInvioSpedizione());
        ec.setDestinatario(storico.getDestinatario());
        return ec;
    }

    public static List<SpedizioneItem> aggiornaEC(Abbonamento abb, 
            EstrattoConto ec,
            List<Spedizione> spedizioni,
            List<SpesaSpedizione> spese)
    throws UnsupportedOperationException {      
        if (abb.getStatoAbbonamento() == StatoAbbonamento.Annullato) {
            throw new UnsupportedOperationException("Aggiona EC non consentita per Abbonamenti Annullati");
        }
        log.info("aggiornaEC: intestatario: {}", abb.getIntestatario().getCaption());
        log.info("aggiornaEC: area: {}", abb.getIntestatario().getAreaSpedizione());
        log.info("aggiornaEC: pubbli.: {}", ec.getPubblicazione().getNome());
        log.info("aggiornaEC: meseInizio: {}", ec.getMeseInizio().getNomeBreve());
        log.info("aggiornaEC: annoInizio: {}", ec.getAnnoInizio().getAnnoAsString());
        log.info("aggiornaEC: meseFine: {}", ec.getMeseFine().getNomeBreve());
        log.info("aggiornaEC: annoFine: {}", ec.getAnnoFine().getAnnoAsString());
        log.info("aggiornaEC: quantità: {}", ec.getNumero());
        
        final List<SpedizioneItem> oldItems = new ArrayList<>();
        
        spedizioni
        .stream()
        .forEach(sped -> {
            sped.getSpedizioneItems()
            .stream()
            .filter(item -> item.getEstrattoConto() == ec || 
               (item.getEstrattoConto().getId() != null && ec.getId() != null && item.getEstrattoConto().getId().longValue() == ec.getId().longValue()))
            .forEach(item -> {
                if (ec.getPubblicazione() == item.getPubblicazione() || 
                  (ec.getPubblicazione().getId() != null && item.getPubblicazione().getId() != null && 
                          ec.getPubblicazione().getId().longValue() == item.getPubblicazione().getId().longValue())) {
                    oldItems.add(item);
                } else {
                    throw new UnsupportedOperationException("Aggiona EC non consente di modificare la pubblicazione");
                }
            });
        });

        abb.setImporto(abb.getImporto().subtract(ec.getImporto()));
        
        
        final List<SpedizioneItem> invItems = new ArrayList<>(); 
        final List<SpedizioneItem> rimItems = new ArrayList<>();
        
        int numeroTotaleRiviste=0;
        for (SpedizioneItem oldItem: oldItems) {
            log.info("aggiornaEC parsing old: {}", oldItem.toString());
            switch (oldItem.getSpedizione().getStatoSpedizione()) {
            case INVIATA:
                invItems.add(oldItem);
                numeroTotaleRiviste+=oldItem.getNumero();
                break;
            case PROGRAMMATA:
                oldItem.getSpedizione().deleteSpedizioneItem(oldItem);
                rimItems.add(oldItem);
                break;
            case SOSPESA:
                oldItem.getSpedizione().deleteSpedizioneItem(oldItem);
                rimItems.add(oldItem);
                break;                        
            default:
                break;
            }
        }

        List<SpedizioneItem> items = generaECItems(ec);
        for (SpedizioneItem invItem: invItems) {
            log.info("aggiornaEC parsing old inviata: {}" , invItem.toString());
            for (SpedizioneItem item: items) {
                if (invItem.stessaPubblicazione(item)) {
                    log.info("aggiornaEC found new: {}", item.toString());
                    if (invItem.getNumero() < item.getNumero()) {
                        item.setNumero(item.getNumero() - invItem.getNumero());
                    } else {
                        item.setNumero(0);
                    }
                }
            }
        }

        Map<Integer, Spedizione> spedMap = Spedizione.getSpedizioneMap(spedizioni);
        for (SpedizioneItem item: items) {
            if (item.getNumero() == 0) {
                continue;
            }
            numeroTotaleRiviste+=item.getNumero();
            aggiungiItemSpedizione(abb, ec, spedMap, item);
        }
        ec.setNumeroTotaleRiviste(numeroTotaleRiviste);
        calcoloImportoEC(ec);
        abb.setImporto(abb.getImporto().add(ec.getImporto()));

        spedizioni = spedMap.values().stream().collect(Collectors.toList());
                
        calcolaPesoESpesePostali(abb, spedizioni, spese);

        //Updated status
        spedizioni.stream()
        .filter(sped -> sped.getStatoSpedizione() != StatoSpedizione.INVIATA)
        .forEach(sped -> {
            switch (abb.getStatoAbbonamento()) {
            case Nuovo:
                sped.setStatoSpedizione(StatoSpedizione.PROGRAMMATA);
                break;
            case Proposto:
                sped.setStatoSpedizione(StatoSpedizione.PROGRAMMATA);
                break;
            case Valido:
                sped.setStatoSpedizione(StatoSpedizione.PROGRAMMATA);
                break;
            case Sospeso:
                sped.setStatoSpedizione(StatoSpedizione.SOSPESA);
                break;
            case Annullato:
                break;

            default:
                break;
            }
        });
        return rimItems;
    }

    public static List<SpedizioneItem> rimuoviEC(
            Abbonamento abb, 
            EstrattoConto ec, 
            List<Spedizione> spedizioni,
            List<SpesaSpedizione> spese) 
    {
        abb.setImporto(abb.getImporto().subtract(ec.getImporto()));
        abb.setSpese(BigDecimal.ZERO);
        final List<SpedizioneItem> inviati = new ArrayList<>(); 
        final List<SpedizioneItem> rimossi = new ArrayList<>(); 
        for (Spedizione sped:spedizioni) {
            sped.getSpedizioneItems()
            .stream()
            .filter(item -> 
                item.getEstrattoConto() == ec 
                || 
                (ec.getId() != null && item.getEstrattoConto().getId() != null &&
                    item.getEstrattoConto().getId().longValue() == ec.getId().longValue()
                    )
                )
            .forEach(item -> {
                switch (sped.getStatoSpedizione()) {
                case INVIATA:
                    inviati.add(item);
                    break;
                case PROGRAMMATA:
                    rimossi.add(item);
                    break;
                case SOSPESA:
                    rimossi.add(item);
                    break;    
                default:
                    break;
                }
            });
        }
        
        for (SpedizioneItem rimosso:rimossi) {
            log.info("rimuoviEC: rimosso" + rimosso);
            rimosso.getSpedizione().deleteSpedizioneItem(rimosso);
        }
        
        int numeroinviato=0;
        for (SpedizioneItem inviata:inviati) {
            numeroinviato+=inviata.getNumero();
        }
        ec.setNumero(0);
        ec.setNumeroTotaleRiviste(numeroinviato);
        calcolaPesoESpesePostali(abb, spedizioni, spese);
        calcoloImportoEC(ec);
        abb.setImporto(abb.getImporto().add(ec.getImporto())); 
        return rimossi;
    }

    public static List<SpedizioneItem> generaECItems(EstrattoConto ec) throws UnsupportedOperationException {
        log.info("generaECItems: tipo: "+ ec.getTipoEstrattoConto());
        log.info("generaECItems: pubbli.: "+ ec.getPubblicazione().getNome());
        log.info("generaECItems: meseInizio: "+ ec.getMeseInizio().getNomeBreve());
        log.info("generaECItems: annoInizio: "+ ec.getAnnoInizio().getAnnoAsString());
        log.info("generaECItems: meseFine: "+ ec.getMeseFine().getNomeBreve());
        log.info("generaECItems: annoFine: "+ ec.getAnnoFine().getAnnoAsString());
        log.info("generaECItems: quantità: "+ ec.getNumero());
        List<SpedizioneItem> items = new ArrayList<>();
        Map<Anno, EnumSet<Mese>> mappaPubblicazioni = EstrattoConto.getAnnoMeseMap(ec);
        for (Anno anno: mappaPubblicazioni.keySet()) {
            mappaPubblicazioni.get(anno).stream().forEach(mese -> {
                SpedizioneItem item = new SpedizioneItem();
                item.setEstrattoConto(ec);
                item.setAnnoPubblicazione(anno);
                item.setMesePubblicazione(mese);
                item.setNumero(ec.getNumero());
                item.setPubblicazione(ec.getPubblicazione());
                items.add(item);
            });
        }
      if (items.isEmpty()) {
          throw new UnsupportedOperationException("Nessuna spedizione per estratto conto");
      }
      return items; 
    }

    public static void aggiungiItemSpedizione(Abbonamento abb, EstrattoConto ec,Map<Integer,Spedizione> spedMap, SpedizioneItem item) {
        Anagrafica destinatario = ec.getDestinatario();
        Invio invio = ec.getInvio();
        InvioSpedizione invioSpedizione =ec.getInvioSpedizione();
        log.info("aggiungiItemSpedizione: " + destinatario + destinatario.getAreaSpedizione() + invio + invioSpedizione);
        log.info("aggiungiItemSpedizione: item" + item);
        InvioSpedizione isped = invioSpedizione;
        Mese mesePubblicazione = item.getMesePubblicazione();
        Anno annoPubblicazione = item.getAnnoPubblicazione();
        boolean posticipata=false;
        int anticipoSpedizione = ec.getPubblicazione().getAnticipoSpedizione();
        Mese spedMese;
        Anno spedAnno;

        if (mesePubblicazione.getPosizione() - anticipoSpedizione <= 0) {
            spedMese = Mese.getByPosizione(12
                    + mesePubblicazione.getPosizione()
                    - anticipoSpedizione);
            spedAnno = Anno.getAnnoPrecedente(annoPubblicazione);
        } else {
            spedMese = Mese.getByPosizione(mesePubblicazione.getPosizione()
                    - anticipoSpedizione);
            spedAnno = annoPubblicazione;
        }
        log.info("geneneraSpedizioni: teorico: " + spedMese.getNomeBreve()+spedAnno.getAnnoAsString()+isped);

        if (spedAnno.getAnno() < Anno.getAnnoCorrente().getAnno()
                || (spedAnno == Anno.getAnnoCorrente()
                        && spedMese.getPosizione() <= Mese.getMeseCorrente().getPosizione())) {
            spedMese = Mese.getMeseCorrente();
            spedAnno = Anno.getAnnoCorrente();
            isped = InvioSpedizione.AdpSede;
            posticipata=true;
            log.info("geneneraSpedizioni: spedizione anticipata");

        }
        if (destinatario.getAreaSpedizione() != AreaSpedizione.Italia) {
            isped = InvioSpedizione.AdpSede;
            log.info("geneneraSpedizioni: spedizione estero");
        }
        log.info("geneneraSpedizioni: effettivo: " + spedMese.getNomeBreve()+spedAnno.getAnnoAsString()+isped);
        Spedizione spedizione = new Spedizione();
        spedizione.setMeseSpedizione(spedMese);
        spedizione.setAnnoSpedizione(spedAnno);
        spedizione.setInvioSpedizione(isped);
        spedizione.setAbbonamento(abb);
        spedizione.setDestinatario(destinatario);
        spedizione.setInvio(invio);
        if (!spedMap.containsKey(spedizione.hashCode())) {
            spedMap.put(spedizione.hashCode(), spedizione);
        }
        Spedizione sped = spedMap.get(spedizione.hashCode());
        item.setPosticipata(posticipata);
        item.setSpedizione(sped);
        sped.addSpedizioneItem(item);        
    }
    
    public static List<Spedizione> generaSpedizioni(Abbonamento abb,
            EstrattoConto ec, 
            List<Spedizione> spedizioni, 
            List<SpesaSpedizione> spese) throws UnsupportedOperationException {

        log.info("generaSpedizioni: intestatario: "+ abb.getIntestatario().getCaption());
        log.info("generaSpedizioni: area: "+ abb.getIntestatario().getAreaSpedizione());
        ec.setAbbonamento(abb);
        List<SpedizioneItem> items = generaECItems(ec);
        ec.setNumeroTotaleRiviste(ec.getNumero()*items.size());
        calcoloImportoEC(ec);
        abb.setImporto(abb.getImporto().add(ec.getImporto()));
        final Map<Integer, Spedizione> spedMap = Spedizione.getSpedizioneMap(spedizioni);

        for (SpedizioneItem item : items) {
            aggiungiItemSpedizione(abb, ec, spedMap, item);
        }
        calcolaPesoESpesePostali(abb, spedMap.values(), spese);
        return spedMap.values().stream().collect(Collectors.toList());
    }

    private static void calcolaPesoESpesePostali(Abbonamento abb, Collection<Spedizione> spedizioni, List<SpesaSpedizione> spese) {
        abb.setSpese(BigDecimal.ZERO);
        for (Spedizione sped: spedizioni) {
            int pesoStimato=0;
            for (SpedizioneItem item: sped.getSpedizioneItems()) {
                pesoStimato+=item.getNumero()*item.getPubblicazione().getGrammi();
            }
            sped.setPesoStimato(pesoStimato);
            switch (sped.getDestinatario().getAreaSpedizione()) {
            case Italia:
                if( sped.getInvioSpedizione() == InvioSpedizione.AdpSede 
                    && !sped.getSpedizioniPosticipate().isEmpty()) {
                    calcolaSpesePostali(sped, spese);
                }
                break;
            case EuropaBacinoMediterraneo:
                calcolaSpesePostali(sped, spese);
                break;

            case AmericaAfricaAsia:
                calcolaSpesePostali(sped, spese);
                break;
            default:
                break;
            }
            abb.setSpese(abb.getSpese().add(sped.getSpesePostali()));
        }
        if (abb.getCassa() == Cassa.Contrassegno) {
            abb.setSpese(abb.getSpese().add(contrassegno));                
        }

    }
    
    public static SpesaSpedizione getSpesaSpedizione(List<SpesaSpedizione> ss,AreaSpedizione area, RangeSpeseSpedizione range) {
        return ss.stream().filter( s-> s.getArea() == area && s.getRange() == range).collect(Collectors.toList()).iterator().next();
    }
    
    public static void calcolaSpesePostali(Spedizione sped, List<SpesaSpedizione> spese) throws UnsupportedOperationException {
        SpesaSpedizione spesa = 
                getSpesaSpedizione(
                           spese, 
                           sped.getDestinatario().getAreaSpedizione(), 
                           RangeSpeseSpedizione.getByPeso(sped.getPesoStimato())
                           );
        sped.setSpesePostali(spesa.getSpese());
    }
    
    public static List<EstrattoConto> 
        generaEstrattoContoAbbonamentiCampagna(final Campagna campagna,final Abbonamento abbonamento, List<Storico> storici) 
        throws UnsupportedOperationException {
        if (abbonamento.getCampagna() != campagna) {
            throw new UnsupportedOperationException("Campagna ed abbonamento non matchano");
        }
        if (abbonamento.getStatoAbbonamento() != StatoAbbonamento.Nuovo || campagna.getStatoCampagna() != StatoCampagna.Generata) {
            throw new UnsupportedOperationException("Campagna ed abbonamento non nuovi");
        }
        final List<EstrattoConto> ecs = new ArrayList<>();
        storici
        .stream()
        .filter(storico -> 
            storico.attivo() &&
            storico.getIntestatario().getId().longValue() == abbonamento.getIntestatario().getId().longValue()
            && 
            campagna.hasPubblicazione(storico.getPubblicazione())
            &&
            abbonamento.getCassa() == storico.getCassa()
                ).forEach(storico ->
            generaECDaStorico(abbonamento, storico));
        return ecs;
    }
    
    public static List<Abbonamento> generaAbbonamentiCampagna(final Campagna campagna, List<Anagrafica> anagrafiche, List<Storico> storici, List<Pubblicazione> pubblicazioni) {
        final Map<Integer,Pubblicazione> campagnapubblicazioniIds = new HashMap<>();
        pubblicazioni.stream()
        .filter(p -> p.isActive() && p.getTipo() != TipoPubblicazione.UNICO)
        .forEach(p -> {
            CampagnaItem ci = new CampagnaItem();
            ci.setCampagna(campagna);
            ci.setPubblicazione(p);
            campagna.addCampagnaItem(ci);
            campagnapubblicazioniIds.put(p.hashCode(),p);            
        });

        final List<Abbonamento> abbonamenti = new ArrayList<>();
        anagrafiche.stream().forEach(a -> {
            final Map<Cassa,List<Storico>> cassaStorico = new HashMap<>();
            storici.stream()
            .filter(
                storico -> 
                    storico.attivo()
                 && campagnapubblicazioniIds.containsKey(storico.getPubblicazione().hashCode()) 
                 && (
                         storico.getIntestatario() == a 
                    || (    storico.getIntestatario().getId() != null 
                         && a.getId() != null 
                         && storico.getIntestatario().getId().longValue() == a.getId().longValue()
                         )
                    ) 
            )
            .forEach(storico -> { 
                if (!cassaStorico.containsKey(storico.getCassa())) {
                    cassaStorico.put(storico.getCassa(), new ArrayList<>());
                }
                cassaStorico.get(storico.getCassa()).add(storico);
            });
            for (Cassa cassa: cassaStorico.keySet()) {
                Abbonamento abbonamento = new Abbonamento();
                abbonamento.setIntestatario(a);
                abbonamento.setCampagna(campagna);
                abbonamento.setAnno(campagna.getAnno());
                abbonamento.setCassa(cassa);
                abbonamento.setCodeLine(Abbonamento.generaCodeLine(abbonamento.getAnno(),a));
                abbonamento.setStatoAbbonamento(StatoAbbonamento.Nuovo);
                abbonamenti.add(abbonamento);
            }
            
        });        
        return abbonamenti;

    }

    public static void calcoloImportoEC(EstrattoConto ec) throws UnsupportedOperationException {
        BigDecimal costo=BigDecimal.ZERO;
        switch (ec.getTipoEstrattoConto()) {
        case Ordinario:
            costo = ec.getPubblicazione().getAbbonamento().multiply(new BigDecimal(ec.getNumero()));
            if (!ec.isAbbonamentoAnnuale() || ec.getNumero() == 0) {
                costo = ec.getPubblicazione().getCostoUnitario().multiply(new BigDecimal(ec.getNumeroTotaleRiviste()));
            }
            break;

        case Web:
            if (!ec.isAbbonamentoAnnuale()) {
                    throw new UnsupportedOperationException("Valori mesi inizio e fine non ammissibili per " + TipoEstrattoConto.Web);
            }
            costo = ec.getPubblicazione().getAbbonamentoWeb().multiply(new BigDecimal(ec.getNumero()));
            break;

        case Scontato:
            if (!ec.isAbbonamentoAnnuale()) {
                throw new UnsupportedOperationException("Valori mesi inizio e fine non ammissibili per " + TipoEstrattoConto.Web);
            }
            costo = ec.getPubblicazione().getAbbonamentoConSconto().multiply(new BigDecimal(ec.getNumero()));
            break;

        case Sostenitore:
            if (!ec.isAbbonamentoAnnuale()) {
                throw new UnsupportedOperationException("Valori mesi inizio e fine non ammissibili per " + TipoEstrattoConto.Web);
            }
            costo = ec.getPubblicazione().getAbbonamentoSostenitore().multiply(new BigDecimal(ec.getNumero()));
            break;
                
        case OmaggioCuriaDiocesiana:
            break;
        case OmaggioCuriaGeneralizia:
            break;
        case OmaggioDirettoreAdp:
            break;
        case OmaggioEditore:
            break;
        case OmaggioGesuiti:
            break;
        case DuplicatoSpedizione:
            break;
        default:
            break;

        }          
        ec.setImporto(costo);
    }
   
    public static StatoStorico getStatoStorico(Storico storico, List<Abbonamento> abbonamenti, List<EstrattoConto> estratticonto) {
        StatoStorico pagamentoRegolare = StatoStorico.Valido;
        switch (storico.getTipoEstrattoConto()) {
        case Ordinario:
            pagamentoRegolare = checkVersamento(storico, abbonamenti,estratticonto);
            break;
        case Scontato:    
            pagamentoRegolare = checkVersamento(storico, abbonamenti,estratticonto);
            break;
        case Sostenitore:    
            pagamentoRegolare = checkVersamento(storico, abbonamenti,estratticonto);
        case Web:    
            pagamentoRegolare = checkVersamento(storico, abbonamenti,estratticonto);
        case OmaggioCuriaDiocesiana:
            break;
        case OmaggioCuriaGeneralizia:
            break;
        case OmaggioGesuiti:
            break;
        case OmaggioDirettoreAdp:
            break;
        case OmaggioEditore:
            break;
        default:
            break;
        }
        return pagamentoRegolare;
    }
    
    private static StatoStorico checkVersamento(Storico storico, List<Abbonamento> abbonamenti, List<EstrattoConto> estrattiConto) {
        for (Abbonamento abb: abbonamenti) {
            if (abb.getIntestatario().getId().longValue() != storico.getIntestatario().getId().longValue() 
                    || abb.getCampagna() == null
                    || abb.getAnno().getAnno() != Anno.getAnnoCorrente().getAnno()) {
                continue;
            }
            for (EstrattoConto sped: estrattiConto) {
                if (sped.getStorico().getId() != storico.getId()) {
                    continue;
                }
                if (abb.getTotale().signum() == 0 ) {
                    return StatoStorico.Valido;
                }
                if (abb.getTotale().signum() > 0 &&  abb.getVersamento() == null) {
                    return StatoStorico.Sospeso;
                }
                if (abb.getTotale().signum() > 0 &&  abb.getVersamento() != null) {
                    return StatoStorico.Valido;
                }
            }
        }
        return StatoStorico.Sospeso;
    }
        
    public static Date getStandardDate(LocalDate localDate) {
        return getStandardDate(Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));       
    }

    public static Date getStandardDate(Date date) {
        return getStandardDate(unformatter.format(date));
    }

    public static Date getStandardDate(String yyMMdd) {
        try {
            return formatter.parse(yyMMdd+"8");
        } catch (ParseException e) {
            log.error(e.getMessage());
        }
        return null;
    };

    public static List<Operazione> generaOperazioni(
            List<Pubblicazione> pubblicazioni, 
            List<Spedizione> spedizioni
        ) {
        Anno anno = Anno.getAnnoCorrente();
        Mese mese = Mese.getMeseCorrente();
        List<Operazione> operazioni = new ArrayList<>();
        pubblicazioni.stream().forEach(p -> {
            Operazione operazione = generaOperazione(p, spedizioni,mese,anno);
            if (operazione.getStimatoSede() != 0 || operazione.getStimatoSped() != 0) {
                    operazioni.add(operazione);
            }
        }
        );
        return operazioni;
    }

    public static Operazione generaOperazione(
            Pubblicazione pubblicazione, 
            List<Spedizione> spedizioni,Mese mese, Anno anno) {
        final Operazione op = new Operazione(pubblicazione, anno, mese);
        int posizioneMese=mese.getPosizione()+pubblicazione.getAnticipoSpedizione();
        Mese mesePubblicazione = mese;
        Anno annoPubblicazione = anno;
        if (posizioneMese > 12) {
            mesePubblicazione = Mese.getByPosizione(posizioneMese-12);
            annoPubblicazione = Anno.getAnnoSuccessivo(anno);
        } else {
            annoPubblicazione=anno;
            mesePubblicazione=Mese.getByPosizione(posizioneMese);
        }

        if (!pubblicazione.getMesiPubblicazione().contains(mesePubblicazione)) {
            return op;
        }
        op.setMesePubblicazione(mesePubblicazione);
        op.setAnnoPubblicazione(annoPubblicazione);
        spedizioni
            .stream()
            .filter( sped -> 
                   sped.getStatoSpedizione() == StatoSpedizione.PROGRAMMATA 
                && sped.getAnnoSpedizione() == anno 
                && sped.getMeseSpedizione() == mese) 
            .forEach( sped -> 
                  sped
                  .getSpedizioneItems()
                  .stream()
                  .filter(item -> !item.isPosticipata() && item.getPubblicazione().hashCode() == pubblicazione.hashCode())
                  .forEach(item -> 
                  {
                      switch (sped.getInvioSpedizione()) {
                      case  Spedizioniere: 
                          op.setStimatoSped(op.getStimatoSped()+item.getNumero());
                          break;
                      case AdpSede:
                          op.setStimatoSede(op.getStimatoSede()+item.getNumero());
                          break;
                      default:
                        break;
                      }
                  })
              );                        
        return op;        
    }
            
    public static void incassa(Incasso incasso, Versamento versamento, Abbonamento abbonamento) throws UnsupportedOperationException {
        if (incasso == null ) {
            log.error("incassa: Incasso null");
            throw new UnsupportedOperationException("incassa: Incasso null");
        }
        if (versamento == null ) {
            log.error("incassa: Versamento null");
            throw new UnsupportedOperationException("incassa: Versamento null");
        }
        if (abbonamento == null ) {
            log.error("incassa: Abbonamento null");
            throw new UnsupportedOperationException("incassa: Abbonamento null");
        }
        if (versamento.getIncasso().getId().longValue() != incasso.getId().longValue()) {
            log.error(String.format("incassa: Incasso e Versamento non sono associati. Incasso=%s, Versamento=%s",incasso.toString(),versamento.toString()));
            throw new UnsupportedOperationException("incassa: Incasso e Versamento non sono associati");               
        }
        if (abbonamento.getVersamento() != null) {
            log.error("incassa: Abbonamento e Versamento non sono associabili, abbonamento incassato");
            throw new UnsupportedOperationException("incassa: Abbonamento e Versamento non sono associabili, abbonamento incassato");
        }
        if (versamento.getResiduo().compareTo(BigDecimal.ZERO) == 0) {
            log.error("incassa: Versamento con residuo 0, abbonamento non incassato");
            throw new UnsupportedOperationException("incassa: Versamento con residuo 0, abbonamento non incassato");            
        }
        abbonamento.setVersamento(versamento);
        if ((versamento.getResiduo()).compareTo(abbonamento.getTotale()) < 0) {
            abbonamento.setIncassato(versamento.getResiduo());
            versamento.setIncassato(versamento.getIncassato().add(versamento.getResiduo()));
        } else {
            abbonamento.setIncassato(abbonamento.getTotale());
            versamento.setIncassato(versamento.getIncassato().add(abbonamento.getTotale()));
        }
        incasso.setIncassato(incasso.getIncassato().add(abbonamento.getIncassato()));
    }

    public static void dissocia(Incasso incasso, Versamento versamento, Abbonamento abbonamento) throws UnsupportedOperationException {
        if (incasso == null ) {
            log.error("dissocia: Incasso null");
            throw new UnsupportedOperationException("dissocia: Incasso null");
        }
        if (versamento == null ) {
            log.error("dissocia: Versamento null");
            throw new UnsupportedOperationException("dissocia: Versamento null");
        }
        if (abbonamento == null ) {
            log.error("dissocia: Abbonamento null");
            throw new UnsupportedOperationException("dissocia: Abbonamento null");
        }
        if (abbonamento.getVersamento() == null ) {
            log.error("dissocia: Abbonamento non incassato");
            throw new UnsupportedOperationException("dissocia: Abbonamento non incassato");
        }
        if (versamento.getIncasso().getId().longValue() != incasso.getId().longValue()) {
            log.error(String.format("dissocia: Incasso e Versamento non sono associati. Incasso=%s, Versamento=%s",incasso.toString(),versamento.toString()));
            throw new UnsupportedOperationException("incassa: Incasso e Versamento non sono associati");               
        }
        if (abbonamento.getVersamento().getId().longValue() != versamento.getId().longValue() ) {
            log.error(String.format("dissocia: Abbonamento e Versamento non sono associati. Abbonamento=%s, Versamento=%s",abbonamento.toString(),versamento.toString()));
            throw new UnsupportedOperationException("dissocia: Abbonamento e Versamento non sono associati");
        }
        versamento.setIncassato(versamento.getIncassato().subtract(abbonamento.getIncassato()));
        incasso.setIncassato(incasso.getIncassato().subtract(abbonamento.getIncassato()));
        abbonamento.setIncassato(BigDecimal.ZERO);
        abbonamento.setVersamento(null);
    }
    
    public static boolean isVersamento(String versamento) {        
        return (
                versamento != null && versamento.length() == 200 && versamento.trim().length() == 82);
    }
    
    public static boolean isRiepilogo(String riepilogo) {
        return ( riepilogo != null &&
                 riepilogo.length() == 200 &&
                 riepilogo.trim().length() == 96 &&
                 riepilogo.substring(19,33).trim().length() == 0 &&
                 riepilogo.substring(33,36).equals("999")
                );
    }
    
    public static Incasso generaIncasso(Set<String> versamenti,
            String riepilogo) {
        final Incasso incasso = new Incasso();
        incasso.setCassa(Cassa.Ccp);
        incasso.setCuas(Cuas.getCuas(Integer.parseInt(riepilogo.substring(0,1))));
        incasso.setCcp(Ccp.getByCcp(riepilogo.substring(1,13)));
        incasso.setDataContabile(Smd.getStandardDate(riepilogo.substring(13,19)));
//          String filler = riepilogo.substring(19,33);
//          String idriepilogo = riepilogo.substring(33,36);
        incasso.setDocumenti(Integer.parseInt(riepilogo.substring(36,44)));
        incasso.setImporto(new BigDecimal(riepilogo.substring(44,54)
                + "." + riepilogo.substring(54,56)));

        incasso.setEsatti(Integer.parseInt(riepilogo.substring(56,64)));
        incasso.setImportoEsatti(new BigDecimal(riepilogo.substring(64,74)
                + "." + riepilogo.substring(74, 76)));

        incasso.setErrati(Integer.parseInt(riepilogo.substring(76,84)));
        incasso.setImportoErrati(new BigDecimal(riepilogo.substring(84,94)
                + "." + riepilogo.substring(94, 96)));

        versamenti.
            stream().
            forEach(s -> incasso.addVersamento(generateVersamento(incasso,s)));
        return incasso;
    }

    private static Versamento generateVersamento(Incasso incasso,String value)
            {
        Versamento versamento = new Versamento(incasso,new BigDecimal(value.substring(36, 44) + "." + value.substring(44, 46)));
        versamento.setBobina(value.substring(0, 3));
        versamento.setProgressivoBobina(value.substring(3, 8));
        versamento.setProgressivo(value.substring(8,15));
        versamento.setDataPagamento(Smd.getStandardDate(value.substring(27,33)));
        versamento.setBollettino(Bollettino.getTipoBollettino(Integer.parseInt(value.substring(33,36))));
        versamento.setProvincia(value.substring(46, 49));
        versamento.setUfficio(value.substring(49, 52));
        versamento.setSportello(value.substring(52, 54));
//          value.substring(54,55);
        versamento.setDataContabile(Smd.getStandardDate(value.substring(55,61)));
        versamento.setCodeLine(value.substring(61,79));
        versamento.setAccettazione(Accettazione.getTipoAccettazione(value.substring(79,81)));
        versamento.setSostitutivo(Sostitutivo.getTipoAccettazione(value.substring(81,82)));
        return versamento;
    }
    
    public static void calcoloImportoIncasso(Incasso incasso) {
        BigDecimal importo = BigDecimal.ZERO;
        for (Versamento versamento: incasso.getVersamenti()) {
            importo=importo.add(versamento.getImporto());
        }
        incasso.setImporto(importo);
        incasso.setDocumenti(incasso.getVersamenti().size());
        incasso.setErrati(0);
        incasso.setEsatti(incasso.getDocumenti());
        incasso.setImportoErrati(BigDecimal.ZERO);
        incasso.setImportoEsatti(incasso.getImporto());
    }

    public static List<EstrattoConto> generaEstrattoConto(
            List<EstrattoConto> findAll) {
        // TODO Auto-generated method stub
        return null;
    }
    
}
