package it.arsinfo.smd;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
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
import it.arsinfo.smd.data.Invio;
import it.arsinfo.smd.data.Mese;
import it.arsinfo.smd.data.Omaggio;
import it.arsinfo.smd.data.Sostitutivo;
import it.arsinfo.smd.data.StatoStorico;
import it.arsinfo.smd.data.TipoPubblicazione;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Campagna;
import it.arsinfo.smd.entity.CampagnaItem;
import it.arsinfo.smd.entity.Incasso;
import it.arsinfo.smd.entity.Operazione;
import it.arsinfo.smd.entity.Prospetto;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.entity.Spedizione;
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
    public static Campagna generaCampagna(final Campagna campagna, List<Anagrafica> anagrafiche, List<Storico> storici, List<Pubblicazione> pubblicazioni) {
        final Set<Long> campagnapubblicazioniIds = new HashSet<>();
        pubblicazioni.stream().forEach(p -> {
            CampagnaItem ci = new CampagnaItem();
            ci.setCampagna(campagna);
            ci.setPubblicazione(p);
            campagna.addCampagnaItem(ci);
            campagnapubblicazioniIds.add(p.getId());            
        });

        final List<Abbonamento> abbonamenti = new ArrayList<>();
        anagrafiche.stream().forEach(a -> {
            final Map<Cassa,List<Storico>> cassaStorico = new HashMap<>();
            storici.stream()
            .filter(
                storico -> 
                (storico.getIntestatario().getId() == a.getId() 
                   && campagnapubblicazioniIds.contains(storico.getPubblicazione().getId()) 
                   && (!campagna.isRinnovaSoloAbbonatiInRegola() 
                       || storico.regolare())
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
                abbonamento.setInizio(campagna.getInizio());
                abbonamento.setFine(campagna.getFine());
                abbonamento.setCassa(cassa);
                for (Storico storico: cassaStorico.get(cassa)) {
                    Spedizione spedizione = new Spedizione();
                    spedizione.setStorico(storico);
                    spedizione.setAbbonamento(abbonamento);
                    spedizione.setPubblicazione(storico.getPubblicazione());
                    spedizione.setDestinatario(storico.getDestinatario());
                    spedizione.setNumero(storico.getNumero());
                    spedizione.setInvio(storico.getInvio());
                    spedizione.setOmaggio(storico.getOmaggio());
                    abbonamento.addSpedizione(spedizione);
                }
                calcoloAbbonamento(abbonamento);
                abbonamenti.add(abbonamento);
            }
            
        });        
        campagna.setAbbonamenti(abbonamenti);
        return campagna;
    }

    public static List<Spedizione> spedizioneDaAggiornare(List<Spedizione> spedizioni) {
        return spedizioni
                .stream()
                .filter(
                    s -> s.getAbbonamento().getAnno().getAnno() == getAnnoCorrente().getAnno() && s.isSospesa() != getSpezioneSospesa(s)
                    ).collect(Collectors.toList());
    }
    
    public static boolean getSpezioneSospesa(Spedizione spedizione) {

        boolean sospendiSpedizione=spedizione.isSospesa();
        switch (spedizione.getOmaggio()) {
        case No:
            sospendiSpedizione = sospendiSpedizione(spedizione);
            break;

        case ConSconto:    
            sospendiSpedizione = sospendiSpedizione(spedizione);
            break;
        case CuriaDiocesiana:
            break;
        case CuriaGeneralizia:
            break;
        case Gesuiti:
            break;
        default:
            break;
        }
        return sospendiSpedizione;
    }

    public static StatoStorico getStatoStorico(Storico storico, List<Abbonamento> abbonamenti) {
        StatoStorico pagamentoRegolare = StatoStorico.S;
        switch (storico.getOmaggio()) {
        case No:
            pagamentoRegolare = checkVersamento(storico, abbonamenti);
            break;

        case ConSconto:    
            pagamentoRegolare = checkVersamento(storico, abbonamenti);
            break;

        case CuriaDiocesiana:
            pagamentoRegolare = StatoStorico.O;
            break;
        case CuriaGeneralizia:
            pagamentoRegolare = StatoStorico.O;
            break;
        case Gesuiti:
            pagamentoRegolare = StatoStorico.O;
            break;
        default:
            pagamentoRegolare = checkVersamento(storico, abbonamenti);
            break;
        }
        return pagamentoRegolare;
    }
    
    private static boolean sospendiSpedizione(Spedizione spedizione) {
        Abbonamento abbonamento = spedizione.getAbbonamento();
            if (abbonamento.getTotale().signum() > 0 &&  abbonamento.getVersamento() == null) {
                return true;
            }
            return false;
    }

    private static StatoStorico checkVersamento(Storico storico, List<Abbonamento> abbonamenti) {
        for (Abbonamento abb: abbonamenti) {
            if (abb.getIntestatario().getId() != storico.getIntestatario().getId() 
                    || abb.getCampagna() == null
                    || abb.getAnno().getAnno() != getAnnoCorrente().getAnno()) {
                continue;
            }
            for (Spedizione sped: abb.getSpedizioni()) {
                if (sped.getStorico().getId() != storico.getId()) {
                    continue;
                }
                if (abb.getTotale().signum() == 0 ) {
                    return StatoStorico.PR;
                }
                if (abb.getTotale().signum() > 0 &&  abb.getVersamento() == null) {
                    return StatoStorico.NPR;
                }
                if (abb.getTotale().signum() > 0 &&  abb.getVersamento() != null) {
                    return StatoStorico.PR;
                }
            }
        }
        return StatoStorico.S;
    }
    public static void calcoloAbbonamento(Abbonamento abbonamento) {
        double costo = 0.0;
        Mese inizio = abbonamento.getInizio();
        Mese fine = abbonamento.getFine();
        for (Spedizione spedizione : abbonamento.getSpedizioni()) {
            costo+= calcolaCosto(inizio, fine, spedizione.getPubblicazione(),spedizione.getOmaggio(),spedizione.getNumero());
        }
        abbonamento.setCosto(BigDecimal.valueOf(costo));
        abbonamento.setCampo(generaVCampo(abbonamento.getAnno(), abbonamento.getInizio(), abbonamento.getFine()));
    }
    
    /*
     * Codice Cliente (TD 674/896) si compone di 16 caratteri numerici
     * riservati al correntista che intende utilizzare tale campo 2 caratteri
     * numerici di controcodice pari al resto della divisione dei primi 16
     * caratteri per 93 (Modulo 93. Valori possibili dei caratteri di
     * controcodice: 00 - 92)
     */
    public static String generaVCampo(Anno anno, Mese inizio, Mese fine) {
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

    public static int calcolaNumeroPubblicazioni(Mese inizio, Mese fine, Mese pub, TipoPubblicazione tipo) {
        int numero = 0;
        switch (tipo) {
        case ANNUALE:
            if (inizio.getPosizione() <= pub.getPosizione()
                    && fine.getPosizione() >= pub.getPosizione()) {
                numero = 1;
            }
            break;
        case SEMESTRALE:
            if (inizio.getPosizione() <= pub.getPosizione()
                    && fine.getPosizione() >= pub.getPosizione()) {
                numero += 1;
            }
            if (fine.getPosizione() >= pub.getPosizione() + 6 && inizio.getPosizione() <= pub.getPosizione() + 6) {
                numero += 1;
            }
            break;
        case MENSILE:
            numero = fine.getPosizione()
                    - inizio.getPosizione() + 1;
            break;
        case UNICO:
            numero = 1;
            break;
        default:
            break;
        }
        return numero;
    }

    public static double calcolaCosto(
            Mese inizio, 
            Mese fine, 
            Pubblicazione pubblicazione, 
            Omaggio omaggio, 
            Integer numero) throws UnsupportedOperationException {
        if (inizio == null || fine == null || inizio.getPosizione() > fine.getPosizione() || pubblicazione == null || omaggio == null
                || numero == null || pubblicazione.getMese() ==  null || pubblicazione.getTipo() == null )
            throw new UnsupportedOperationException("Valori non ammissibili");
        double costo = 0.0;
        switch (omaggio) {
        case No:
            costo = pubblicazione.getCostoUnitario().doubleValue()
                     * numero.doubleValue()
                     * calcolaNumeroPubblicazioni(inizio,fine, pubblicazione.getMese(),pubblicazione.getTipo());
        break;
        
        case ConSconto:
            costo = pubblicazione.getCostoScontato().doubleValue()
                     * numero.doubleValue()
                     * calcolaNumeroPubblicazioni(inizio,fine, pubblicazione.getMese(),pubblicazione.getTipo());  
            break;
            
        case CuriaDiocesiana:
            break;
        
        case Gesuiti:
            break;
            
        case CuriaGeneralizia:
            break;
            
        default:
            break;
           
        }              
        return costo;
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

    public static List<Prospetto> generaProspetti(
            List<Pubblicazione> pubblicazioni, 
            List<Abbonamento> abbonamenti, 
            Anno anno, 
            Set<Mese> mesi,
            Set<Omaggio> omaggi) {

        List<Prospetto> prospetti = new ArrayList<>();
        pubblicazioni.stream().forEach(pubblicazione -> {
            omaggi.stream().forEach(omaggio -> 
            {
                pubblicazione.getMesiPubblicazione()
                    .stream()
                    .filter(mese -> mesi.contains(mese))
                    .forEach(mese -> 
                        prospetti.add(generaProspetto(pubblicazione, abbonamenti, anno, mese, omaggio))
                    );
            });
        });
        return prospetti;
    }

    public static Prospetto generaProspetto(Pubblicazione pubblicazione,
            List<Abbonamento> abbonamenti, Anno anno, Mese mese,
            Omaggio omaggio) {
        Prospetto prospetto = new Prospetto(pubblicazione, anno, mese,
                                            omaggio);
        Integer conta = 0;
        for (Abbonamento a : abbonamenti) {
            if (a.getAnno() == anno
                    && a.getInizio().getPosizione() <= mese.getPosizione()
                    && a.getFine().getPosizione() >= mese.getPosizione()) {
                for (Spedizione s : a.getSpedizioni()) {
                    if (s.getPubblicazione().getId() != pubblicazione.getId()
                            || s.getOmaggio() != omaggio) {
                        continue;
                    }
                    conta += s.getNumero();
                }
            }
        }
        prospetto.setStimato(conta);
        return prospetto;
    }

    public static List<Operazione> generaOperazioni(
            List<Pubblicazione> pubblicazioni, 
            List<Abbonamento> abbonamenti, 
            Anno anno,
            Set<Mese> mesi
        ) {
        List<Operazione> operazioni = new ArrayList<>();
        pubblicazioni.stream().forEach(pubblicazione -> {
            pubblicazione.getMesiPubblicazione()
                .stream()
                .filter(mese -> mesi.contains(mese))
                .forEach(mese -> operazioni.add(generaOperazione(pubblicazione, abbonamenti, anno, mese))
            );
        });
        return operazioni;
    }

    public static List<Spedizione> generaSpedizioniSped(List<Abbonamento> abbonamenti, Operazione operazione) {
        final List<Spedizione> spedizioni = new ArrayList<>();
        abbonamenti.stream().filter(a ->a.getAnno() == operazione.getAnno()
                        && a.getInizio().getPosizione() <= operazione.getMese().getPosizione() 
                        && a.getFine().getPosizione() >= operazione.getMese().getPosizione())
        .forEach(a -> {
           spedizioni.addAll(a.getSpedizioni().stream().filter(s -> 
                   !s.isSospesa()
               && s.getPubblicazione().getId() == operazione.getPubblicazione().getId() 
                && s.getInvio() != Invio.AdpSede
                ).collect(Collectors.toList())
           );
        });
        return spedizioni;
    }
    
    public static List<Spedizione> generaSpedizioniCassa(List<Abbonamento> abbonamenti, Operazione operazione) {
        final List<Spedizione> spedizioni = new ArrayList<>();
        abbonamenti.stream().filter(a ->a.getAnno() == operazione.getAnno()
                        && a.getInizio().getPosizione() <= operazione.getMese().getPosizione() 
                        && a.getFine().getPosizione() >= operazione.getMese().getPosizione())
        .forEach(a -> {
           spedizioni.addAll(a.getSpedizioni().stream().filter(s -> 
                   !s.isSospesa()
                && s.getPubblicazione().getId() == operazione.getPubblicazione().getId() 
                && s.getInvio() == Invio.AdpSede
                ).collect(Collectors.toList())
           );
        });
        return spedizioni;
    }

    
    public static Operazione generaOperazione(
            Pubblicazione pubblicazione, 
            List<Abbonamento> abbonamenti, 
            Anno anno, Mese mese) {
        final Operazione op = new Operazione(pubblicazione, anno, mese);
        op.setStimato(0);
        abbonamenti
            .stream()
            .filter(a ->
                        a.getAnno() == anno 
                        && a.getInizio().getPosizione() <= mese.getPosizione() 
                        && a.getFine().getPosizione() >= mese.getPosizione() 
                    )
            .forEach( a -> 
                      a.getSpedizioni()
                          .stream()
                          .filter( s ->
                                !s.isSospesa() 
                                && s.getPubblicazione().getId() == pubblicazione.getId()
                                  ).forEach( s -> op.setStimato(op.getStimato()+s.getNumero()))
             );
                        
        return op;        
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
}
