package it.arsinfo.smd;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import it.arsinfo.smd.data.Accettazione;
import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.Bollettino;
import it.arsinfo.smd.data.Cassa;
import it.arsinfo.smd.data.Ccp;
import it.arsinfo.smd.data.Cuas;
import it.arsinfo.smd.data.Incassato;
import it.arsinfo.smd.data.InvioSpedizione;
import it.arsinfo.smd.data.Mese;
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
import it.arsinfo.smd.entity.SpesaSpedizione;
import it.arsinfo.smd.entity.Storico;
import it.arsinfo.smd.entity.Versamento;

@Configuration
public class Smd {
    
    @Bean
    public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
    }
    
    private static final Logger log = LoggerFactory.getLogger(Smd.class);
    private static final DateFormat formatter = new SimpleDateFormat("yyMMddH");
    static final DateFormat unformatter = new SimpleDateFormat("yyMMdd");    
    public static String decodeForGrid(boolean status) {
        if (status) {
            return "si";
        }
        return "no";
    }
    
    public static Anno getAnnoCorrente() {
        return Anno.valueOf("ANNO"+new SimpleDateFormat("yyyy").format(new Date()));        
    }
    public static Anno getAnnoPassato() {
        Integer annoScorso = getAnnoCorrente().getAnno()-1;
        return Anno.valueOf("ANNO"+annoScorso);
    }
    public static Anno getAnnoProssimo() {
        Integer annoProssimo = getAnnoCorrente().getAnno()+1;
        return Anno.valueOf("ANNO"+annoProssimo);
    }
    public static Mese getMeseCorrente() {
        return Mese.getByCode(new SimpleDateFormat("MM").format(new Date()));        
    }
    public static String getProgressivoVersamento(int i) {
        return String.format("%09d",i);
    }

    public static boolean spedizionePosticipata(Spedizione spedizione, int anticipoSpedizione) {
        if (spedizione.getAnnoPubblicazione() == spedizione.getAnnoSpedizione()) {
            return !(spedizione.getMesePubblicazione().getPosizione() - spedizione.getMeseSpedizione().getPosizione() == anticipoSpedizione);
        }
        
        if (spedizione.getAnnoPubblicazione().getAnno() > spedizione.getAnnoSpedizione().getAnno()) {
            return !(12 - spedizione.getMeseSpedizione().getPosizione() + spedizione.getMesePubblicazione().getPosizione() == anticipoSpedizione);
        }
        return true;
    }
    
    public static Map<Anno, EnumSet<Mese>> getAnnoMeseMap(Mese inizioMese, Anno inizioAnno, Mese fineMese, Anno fineAnno, EnumSet<Mese> mesi) throws UnsupportedOperationException {
        if (inizioAnno.getAnno() > fineAnno.getAnno()) {
            throw new UnsupportedOperationException("data inizio maggiore di data fine");
        }
        if (inizioAnno == fineAnno && inizioMese.getPosizione() > fineMese.getPosizione()) {
            throw new UnsupportedOperationException("data inizio maggiore di data fine");
        }
        Map<Anno,EnumSet<Mese>> map = new HashMap<>();
        Anno anno = inizioAnno;
        while (anno.getAnno() <= fineAnno.getAnno()) {
            EnumSet<Mese> mesiin = EnumSet.noneOf(Mese.class);
            for (Mese mese: mesi) {
                if (anno == inizioAnno && anno == fineAnno) {
                    if (mese.getPosizione() >= inizioMese.getPosizione() && mese.getPosizione() <= fineMese.getPosizione()) {
                        mesiin.add(mese);
                    }
                } else if (anno == inizioAnno) {
                    if (mese.getPosizione() >= inizioMese.getPosizione()) {
                        mesiin.add(mese);
                    }
                } else if (anno == fineAnno) {
                    if (mese.getPosizione() <= fineMese.getPosizione()) {
                        mesiin.add(mese);
                    }
                } else {
                    mesiin.addAll(mesi);
                }
            }
            map.put(anno, mesiin);
            anno=Anno.getAnnoSuccessivo(anno);
        }
        
        return map;
    }

    public static EstrattoConto generaECDaStorico(Abbonamento abb,Storico storico) {
        final EstrattoConto ec = new EstrattoConto();
        ec.setStorico(storico);
        ec.setAbbonamento(abb);
        ec.setPubblicazione(storico.getPubblicazione());
        ec.setNumero(storico.getNumero());
        ec.setTipoEstrattoConto(storico.getTipoEstrattoConto());
        ec.setDestinatario(storico.getDestinatario());
        ec.setInvio(storico.getInvio());
        generaEC(abb, ec, storico.getInvioSpedizione(), Mese.GENNAIO , abb.getAnno(), Mese.DICEMBRE, abb.getAnno());
        return ec;
    }
    
    

    public static void generaEC(
            Abbonamento abb,
            EstrattoConto ec,
            InvioSpedizione invioSpedizione,
            Mese meseinizio,
            Anno annoinizio,
            Mese mesefine,
            Anno annofine
            ) throws UnsupportedOperationException {
        log.info("generaEC: "+ ec);
        log.info("generaEC: meseInizio:"+ meseinizio);
        log.info("generaEC: annoInizio:"+ annoinizio);
        log.info("generaEC: meseFine:"+ mesefine);
        log.info("generaEC: annoFine:"+ annofine);
        Map<Anno, EnumSet<Mese>> mappaPubblicazioni = getAnnoMeseMap(meseinizio, annoinizio, mesefine, annofine, ec.getPubblicazione().getMesiPubblicazione());
        for (Anno anno: mappaPubblicazioni.keySet()) {
            mappaPubblicazioni.get(anno).stream().forEach(mese -> {
                Spedizione spedizione = creaSpedizione(ec,mese, anno, invioSpedizione);
                spedizione.setEstrattoConto(ec);
                ec.addSpedizione(spedizione);
            });
        }
        if (ec.getSpedizioni().isEmpty()) {
            throw new UnsupportedOperationException("Nessuna spedizione per estratto conto");
        }
        calcoloImportoEC(ec);
        abb.setTotale(abb.getTotale().add(ec.getTotale()));
    }
    
    public static Spedizione creaSpedizione(EstrattoConto ec, Mese mesePubblicazione, Anno annoPubblicazione, InvioSpedizione invioSpedizione) throws UnsupportedOperationException {
        Mese spedMese = null;
        Anno spedAnno = null;
        if (ec == null ) {
            throw new UnsupportedOperationException("cannot create spedizione for null estratto conto: " +invioSpedizione+mesePubblicazione+annoPubblicazione);
        }
        if (ec.getPubblicazione() == null ) {
            throw new UnsupportedOperationException("cannot create spedizione for null pubblicazione: " +invioSpedizione+mesePubblicazione+annoPubblicazione);
        }
        if (!ec.getPubblicazione().getMesiPubblicazione().contains(mesePubblicazione)) {
            throw new UnsupportedOperationException("cannot create spedizione for month pubblicazione: " +invioSpedizione+mesePubblicazione+annoPubblicazione);            
        }
        int anticipoSpedizione = ec.getPubblicazione().getAnticipoSpedizione();
        if (mesePubblicazione.getPosizione()-anticipoSpedizione <= 0) {
            spedMese = Mese.getByPosizione(12+mesePubblicazione.getPosizione()-anticipoSpedizione);
            spedAnno = Anno.getAnnoPrecedente(annoPubblicazione);
        } else {
            spedMese = Mese.getByPosizione(mesePubblicazione.getPosizione()-anticipoSpedizione);
            spedAnno = annoPubblicazione;
        }
        if (spedAnno.getAnno() < getAnnoCorrente().getAnno() || (spedAnno == getAnnoCorrente() && spedMese.getPosizione() <= getMeseCorrente().getPosizione())) {
            spedMese = getMeseCorrente();
            spedAnno = getAnnoCorrente();
            invioSpedizione = InvioSpedizione.AdpSede;
        }
        Spedizione spedizione = new Spedizione();
        spedizione.setMesePubblicazione(mesePubblicazione);
        spedizione.setAnnoPubblicazione(annoPubblicazione);
        spedizione.setMeseSpedizione(spedMese);
        spedizione.setAnnoSpedizione(spedAnno);
        spedizione.setInvioSpedizione(invioSpedizione);
        spedizione.setEstrattoConto(ec);
        return spedizione;
    }
    
    public static List<Abbonamento> inviaPropostaAbbonamentoCampagna(final Campagna campagna, List<Abbonamento> abbonamenti) {
        campagna.setStatoCampagna(StatoCampagna.Inviata);
        return abbonamenti.stream()
                .filter(abb -> abb.getCampagna().getId().longValue() == campagna.getId().longValue())
                .map(abb -> {
                    if (abb.getStatoIncasso() ==  Incassato.Omaggio) {
                        abb.setStatoAbbonamento(StatoAbbonamento.Validato);
                    } else {
                        abb.setStatoAbbonamento(StatoAbbonamento.Proposto);
                    }
                    return abb;
                }).collect(Collectors.toList());
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
            storico.getIntestatario().getId() == abbonamento.getIntestatario().getId()
            && 
            campagna.hasPubblicazione(storico.getPubblicazione())
            &&
            abbonamento.getCassa() == storico.getCassa()
                ).forEach(storico ->
            generaECDaStorico(abbonamento, storico));
//        }
        return ecs;
    }
    
    public static List<Abbonamento> generaAbbonamentiCampagna(final Campagna campagna, List<Anagrafica> anagrafiche, List<Storico> storici, List<Pubblicazione> pubblicazioni) {
        final Map<Long,Pubblicazione> campagnapubblicazioniIds = new HashMap<>();
        pubblicazioni.stream()
        .filter(p -> p.isActive() && p.getTipo() != TipoPubblicazione.UNICO)
        .forEach(p -> {
            CampagnaItem ci = new CampagnaItem();
            ci.setCampagna(campagna);
            ci.setPubblicazione(p);
            campagna.addCampagnaItem(ci);
            campagnapubblicazioniIds.put(p.getId(),p);            
        });

        final List<Abbonamento> abbonamenti = new ArrayList<>();
        anagrafiche.stream().forEach(a -> {
            final Map<Cassa,List<Storico>> cassaStorico = new HashMap<>();
            storici.stream()
            .filter(
                storico -> 
                (storico.getIntestatario().getId() == a.getId() 
                   && campagnapubblicazioniIds.containsKey(storico.getPubblicazione().getId()) 
                   && storico.attivo())
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
                abbonamento.setCampo(generaVCampo(abbonamento.getAnno()));
                abbonamento.setStatoAbbonamento(StatoAbbonamento.Nuovo);
                abbonamenti.add(abbonamento);
            }
            
        });        
        return abbonamenti;

    }
    
    public static Abbonamento aggiornaAbbonamento(Abbonamento abbonamento,List<Pubblicazione> pubblicazioni, List<Storico> storici) {
        if (abbonamento.getAnno() != Smd.getAnnoProssimo()) {
            return abbonamento;
        }
        final Map<Long,Pubblicazione> campagnapubblicazioniIds = new HashMap<>();
        pubblicazioni.stream()
        .filter(p -> p.isActive() && p.getTipo() != TipoPubblicazione.UNICO)
        .forEach(p -> {
            campagnapubblicazioniIds.put(p.getId(),p);            
        });
        storici
        .stream()
        .filter( 
             s -> abbonamento.getCassa() == s.getCassa() && campagnapubblicazioniIds.containsKey(s.getPubblicazione().getId()) && s.attivo())
        .forEach(storico-> {
            generaECDaStorico(abbonamento, storico);
        });
        
        return abbonamento;
        
    }

    public static void calcoloImportoEC(EstrattoConto ec) throws UnsupportedOperationException {
        double costo=0.0;
        double spesePostali = 0.0;
        switch (ec.getTipoEstrattoConto()) {
        case Ordinario:
            costo = ec.getPubblicazione().getAbbonamento().doubleValue() * ec.getNumero().doubleValue();
            if (!ec.isAbbonamentoAnnuale() || ec.getNumeroSpedizioniConSpesePostali() > 0) {
              
                costo = ec.getPubblicazione().getCostoUnitario().doubleValue()
                     * ec.getNumero().doubleValue()
                     * Double.valueOf(ec.getSpedizioni().size());
            }
            SpesaSpedizione ss = ec.getPubblicazione().getSpesaSpedizioneBy(ec.getAbbonamento().getIntestatario().getAreaSpedizione(),ec.getNumero());
            switch (ec.getAbbonamento().getIntestatario().getAreaSpedizione()) {
            case Italia:
                if (ec.getNumeroSpedizioniConSpesePostali() == 0) {
                    break;
                }
                if (ss == null ) {
                    throw new UnsupportedOperationException("Aggiungere le Spese di Spedizione per Area Italia numero:" + ec.getNumero());                    
                }
                spesePostali = ss.getSpeseSpedizione().doubleValue()
                        * ec.getNumeroSpedizioniConSpesePostali();
                break;
            case EuropaBacinoMediterraneo:
                if (ss == null ) {
                    throw new UnsupportedOperationException("Aggiungere le Spese di Spedizione per Area EuropaBacinoMediterraneo numero:" + ec.getNumero());                    
                }
                spesePostali = ss.getSpeseSpedizione().doubleValue()
                        * ec.getSpedizioni().size();

            case AmericaAfricaAsia:
                if (ss == null ) {
                    throw new UnsupportedOperationException("Aggiungere le Spese di Spedizione per Area AmericaAsiaAfrica numero:" + ec.getNumero());                    
                }
                spesePostali = ss.getSpeseSpedizione().doubleValue()
                        * ec.getSpedizioni().size();
            default:
                break;
            }
            break;

        case Web:
            if (!ec.isAbbonamentoAnnuale()) {
                    throw new UnsupportedOperationException("Valori mesi inizio e fine non ammissibili per " + TipoEstrattoConto.Web);
            }
            costo = ec.getPubblicazione().getAbbonamentoWeb().doubleValue()
                    * ec.getNumero().doubleValue();  
            break;

        case Scontato:
            if (!ec.isAbbonamentoAnnuale()) {
                throw new UnsupportedOperationException("Valori mesi inizio e fine non ammissibili per " + TipoEstrattoConto.Web);
            }
            costo = ec.getPubblicazione().getAbbonamentoConSconto().doubleValue() * ec.getNumero().doubleValue();
            break;

        case Sostenitore:
            if (!ec.isAbbonamentoAnnuale()) {
                throw new UnsupportedOperationException("Valori mesi inizio e fine non ammissibili per " + TipoEstrattoConto.Web);
            }
            costo = ec.getPubblicazione().getAbbonamentoSostenitore().doubleValue()
                     * ec.getNumero().doubleValue();  
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
        default:
            break;

        }          
        ec.setSpesePostali(BigDecimal.valueOf(spesePostali));
        ec.setImporto(BigDecimal.valueOf(costo));
    }


    public static StatoStorico getStatoStorico(Storico storico, List<Abbonamento> abbonamenti, List<EstrattoConto> estratticonto) {
        StatoStorico pagamentoRegolare = StatoStorico.VALIDO;
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
            if (abb.getIntestatario().getId() != storico.getIntestatario().getId() 
                    || abb.getCampagna() == null
                    || abb.getAnno().getAnno() != getAnnoCorrente().getAnno()) {
                continue;
            }
            for (EstrattoConto sped: estrattiConto) {
                if (sped.getStorico().getId() != storico.getId()) {
                    continue;
                }
                if (abb.getTotale().signum() == 0 ) {
                    return StatoStorico.VALIDO;
                }
                if (abb.getTotale().signum() > 0 &&  abb.getVersamento() == null) {
                    return StatoStorico.SOSPESO;
                }
                if (abb.getTotale().signum() > 0 &&  abb.getVersamento() != null) {
                    return StatoStorico.VALIDO;
                }
            }
        }
        return StatoStorico.SOSPESO;
    }
        
    /*
     * Codice Cliente (TD 674/896) si compone di 16 caratteri numerici
     * riservati al correntista che intende utilizzare tale campo 2 caratteri
     * numerici di controcodice pari al resto della divisione dei primi 16
     * caratteri per 93 (Modulo 93. Valori possibili dei caratteri di
     * controcodice: 00 - 92)
     */
    public static String generaVCampo(Anno anno) {
        // primi 2 caratteri anno
        String campo = anno.getAnnoAsString().substring(2, 4);
        // 3-16
        campo += String.format("%014d", ThreadLocalRandom.current().nextLong(99999999999999l));
        campo += String.format("%02d", Long.parseLong(campo) % 93);
        return campo;
    }

    public static boolean checkCampo(String campo) {
        if (campo == null || campo.length() != 18) {
            return false;
            
        }
        
        String codice = campo.substring(0, 16);
        
        Long valorecodice = (Long.parseLong(codice) % 93);
        Integer codicecontrollo = Integer.parseInt(campo.substring(16,18));
        return codicecontrollo.intValue() == valorecodice.intValue();
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
            List<EstrattoConto> abbonamenti
        ) {
        Anno anno = getAnnoCorrente();
        Mese mese = getMeseCorrente();
        List<Operazione> operazioni = new ArrayList<>();
        pubblicazioni.stream().forEach(p -> {
            Operazione operazione = generaOperazione(p, abbonamenti,mese,anno);
            if (operazione.getStimatoSede() != 0 || operazione.getStimatoSped() != 0) {
                    operazioni.add(operazione);
            }
        }
        );
        return operazioni;
    }

    public static Operazione generaOperazione(
            Pubblicazione pubblicazione, 
            List<EstrattoConto> estratticonto,Mese mese, Anno anno) {
        final Operazione op = new Operazione(pubblicazione, anno, mese);
        estratticonto.stream()
          .filter(ec -> ec.getPubblicazione().getId() == pubblicazione.getId())
          .forEach(ec -> ec.getSpedizioni().stream()
              .filter( s ->s.getStatoSpedizione() == StatoSpedizione.PROGRAMMATA && s.getMeseSpedizione() == mese && s.getAnnoSpedizione() == anno)
                  .forEach( s -> 
                      {
                          switch (s.getInvioSpedizione()) {
                          case  Spedizioniere: 
                              op.setStimatoSped(op.getStimatoSped()+ec.getNumero());
                              break;
                          case AdpSede:
                              op.setStimatoSede(op.getStimatoSede()+ec.getNumero());
                              break;
                          default:
                            break;
                          }
                         }
                          )
             );
                        
        return op;        
    }

    public static List<Spedizione> listaSpedizioni(List<EstrattoConto> abbonamenti, InvioSpedizione invioSpedizione, Mese mese, Anno anno) {
        final List<Spedizione> spedizioni = new ArrayList<>();
        abbonamenti.stream()
                .forEach( ec -> {
                    spedizioni.addAll(ec.getSpedizioni().stream().filter(s -> 
                                s.getStatoSpedizione() == StatoSpedizione.PROGRAMMATA && s.getInvioSpedizione() == invioSpedizione
                                && s.getMeseSpedizione() == mese
                                && s.getAnnoSpedizione() == anno
                            ).collect(Collectors.toList()));
        });
        return spedizioni;
    }
        
    public static Versamento incassa(Incasso incasso, Versamento versamento, Abbonamento abbonamento) throws UnsupportedOperationException {
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
        if ((versamento.getResiduo().subtract(abbonamento.getTotale()).compareTo(BigDecimal.ZERO)) < 0) {
            throw new UnsupportedOperationException("incassa: Abbonamento e Versamento non sono associabili, non rimane abbastanza credito sul versamento");            
        }
        versamento.setIncassato(versamento.getIncassato().add(abbonamento.getTotale()));
        incasso.setIncassato(incasso.getIncassato().add(abbonamento.getTotale()));
        abbonamento.setVersamento(versamento);
        return versamento;
    }

    public static Versamento dissocia(Incasso incasso, Versamento versamento, Abbonamento abbonamento) throws UnsupportedOperationException {
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
        versamento.setIncassato(versamento.getIncassato().subtract(abbonamento.getTotale()));
        incasso.setIncassato(incasso.getIncassato().subtract(abbonamento.getTotale()));
        abbonamento.setVersamento(null);
        return versamento;
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
        versamento.setCampo(value.substring(61,79));
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
