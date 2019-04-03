package it.arsinfo.smd;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import it.arsinfo.smd.data.Accettazione;
import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.Bollettino;
import it.arsinfo.smd.data.Cassa;
import it.arsinfo.smd.data.Ccp;
import it.arsinfo.smd.data.Cuas;
import it.arsinfo.smd.data.Diocesi;
import it.arsinfo.smd.data.Invio;
import it.arsinfo.smd.data.Mese;
import it.arsinfo.smd.data.Omaggio;
import it.arsinfo.smd.data.Regione;
import it.arsinfo.smd.data.Sostitutivo;
import it.arsinfo.smd.data.TipoPubblicazione;
import it.arsinfo.smd.data.TitoloAnagrafica;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Campagna;
import it.arsinfo.smd.entity.CampagnaItem;
import it.arsinfo.smd.entity.Incasso;
import it.arsinfo.smd.entity.Prospetto;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.entity.Spedizione;
import it.arsinfo.smd.entity.Storico;
import it.arsinfo.smd.entity.Versamento;
import it.arsinfo.smd.repository.AbbonamentoDao;
import it.arsinfo.smd.repository.AnagraficaDao;
import it.arsinfo.smd.repository.CampagnaDao;
import it.arsinfo.smd.repository.IncassoDao;
import it.arsinfo.smd.repository.ProspettoDao;
import it.arsinfo.smd.repository.PubblicazioneDao;
import it.arsinfo.smd.repository.SpedizioneDao;
import it.arsinfo.smd.repository.StoricoDao;
import it.arsinfo.smd.repository.VersamentoDao;

@SpringBootApplication
public class SmdApplication {

    private static final Logger log = LoggerFactory.getLogger(SmdApplication.class);
  
    public static List<Prospetto> generaProspetto(Pubblicazione pubblicazione, List<Abbonamento> abbonamenti, List<Spedizione> spedizioni, Anno anno) {
        final List<Prospetto> prospetti = new ArrayList<>();
        pubblicazione.getMesiPubblicazione().stream().forEach(mese -> {
            Prospetto prospetto = new Prospetto(pubblicazione, anno, mese);
            Integer conta = 0;
            for (Spedizione s: spedizioni) {
                if (s.getPubblicazione().getId() != pubblicazione.getId()) {
                    continue;
                }
                for (Abbonamento a: abbonamenti) {
                    if (s.getAbbonamento().getId() == a.getId() 
                            && a.getAnno() == anno 
                            && a.getInizio().getPosizione() <= mese.getPosizione() 
                            && a.getFine().getPosizione() >= mese.getPosizione() 
                      ) {
                            conta+=s.getNumero();
                    }
                }
            }
            prospetto.setStimato(conta);
            prospetti.add(prospetto);
        });
        return prospetti;
    }
    
    public static Versamento incassa(Versamento versamento, Abbonamento abbonamento) throws UnsupportedOperationException {
        if (versamento == null || abbonamento == null || abbonamento.getVersamento() != null) {
            throw new UnsupportedOperationException("Abbonamento e Versamento non sono associabili, valori null o abbonamento incassato");
        }
        if ((versamento.getResiduo().subtract(abbonamento.getTotale()).compareTo(BigDecimal.ZERO)) < 0) {
            throw new UnsupportedOperationException("Abbonamento e Versamento non sono associabili, non rimane abbastanza credito sul versamento");            
        }
        versamento.setResiduo(versamento.getResiduo().subtract(abbonamento.getTotale()));
        abbonamento.setVersamento(versamento);
        return versamento;
    }

    public static Versamento dissocia(Versamento versamento, Abbonamento abbonamento) throws UnsupportedOperationException {
        if (versamento == null || abbonamento == null || abbonamento.getVersamento() == null || abbonamento.getVersamento().getId() != versamento.getId()) {
            throw new UnsupportedOperationException("Abbonamento e Versamento non sono dissociabili, valori null");
        }        
        versamento.setResiduo(versamento.getResiduo().add(abbonamento.getTotale()));
        abbonamento.setVersamento(null);
        return versamento;
    }

    public static String getProgressivoVersamento(int i) {
        return String.format("%09d",i);
    }
    
    public static List<Spedizione> selectSpedizioni(List<Spedizione> spedizioni, Anno anno, Mese mese, Pubblicazione pubblicazione) {
        return spedizioni.stream()
                .filter(s -> 
                    s.getPubblicazione().getId() == pubblicazione.getId() 
                    && s.getAbbonamento().getAnno() == anno
                    && pubblicazione.getMesiPubblicazione().contains(mese)
                ).collect(Collectors.toList());
    }
     
    public static boolean pagamentoRegolare(Storico storico, List<Abbonamento> abbonamenti) {
        if (storico.getOmaggio() != Omaggio.No || storico.getOmaggio() != Omaggio.ConSconto) {
            return true;
        }
        for (Abbonamento abb: abbonamenti) {
            if (abb.getIntestatario().getId() == storico.getIntestatario().getId()) {
                continue;
            }
            if (abb.getAnno() != getAnnoCorrente() || abb.getAnno() != getAnnoPassato()) {
                continue;
            }
            for (Spedizione sped: abb.getSpedizioni()) {
                if (sped.getPubblicazione().getId() != storico.getPubblicazione().getId()
                        ||
                    sped.getDestinatario().getId() != storico.getDestinatario().getId()     ) {
                    continue;
                }
                if (abb.getCosto() != BigDecimal.ZERO && sped.getOmaggio() == storico.getOmaggio() && abb.getVersamento() == null) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public static void generaCampagna(Campagna campagna, List<Storico> storici, List<Abbonamento> vecchiabb) {
        Set<Long> campagnapubblicazioniIds = campagna.getCampagnaItems().stream().map(item -> item.getPubblicazione().getId()).collect(Collectors.toSet());
        Map<Cassa,List<Storico>> cassaStorico = new HashMap<>();
        Map<Long,Anagrafica>  intestatari = new HashMap<>();
        for (Storico storico: storici) {
            if (campagna.isRinnovaSoloAbbonatiInRegola() && !pagamentoRegolare(storico, vecchiabb)) {
                continue;
            }
            if (storico.isSospeso() || !campagnapubblicazioniIds.contains(storico.getPubblicazione().getId())) {
                continue;
            }
            if (!intestatari.containsKey(storico.getIntestatario().getId())) {
                intestatari.put(storico.getIntestatario().getId(), storico.getIntestatario());
            }
            if (!cassaStorico.containsKey(storico.getCassa())) {
                cassaStorico.put(storico.getCassa(), new ArrayList<>());
            }
            cassaStorico.get(storico.getCassa()).add(storico);
        }
        List<Abbonamento> abbonamenti = new ArrayList<>();
        for (Cassa cassa: cassaStorico.keySet()) {
            Map<Long,Abbonamento> abbti = new HashMap<>();
            for (Storico storico: cassaStorico.get(cassa)) {
                if (!abbti.containsKey(storico.getIntestatario().getId())) {
                    Abbonamento abb = generateAbbonamento(storico.getIntestatario(), campagna,cassa);
                    abbti.put(storico.getIntestatario().getId(), abb);
                }
                addSpedizione(abbti.get(storico.getIntestatario().getId()),storico);
            }
            abbonamenti.addAll(abbti.values());
        }
        abbonamenti.stream().forEach(abb -> calcoloCostoAbbonamento(abb));
        campagna.setAbbonamenti(abbonamenti);
    }
    
    public static Abbonamento generateAbbonamento(Anagrafica intestatario, Campagna campagna, Cassa cassa) {
        Abbonamento abb = new Abbonamento(intestatario);
        abb.setCampagna(campagna);
        abb.setAnno(campagna.getAnno());
        abb.setInizio(campagna.getInizio());
        abb.setFine(campagna.getFine());
        abb.setCassa(cassa);
        abb.setCampo(generateCampo(campagna.getAnno(), campagna.getInizio(), campagna.getFine()));
        return abb;
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

    public static Spedizione addSpedizione(Abbonamento abbonamento, Storico storico) {
        Spedizione spedizione = addSpedizione(abbonamento,storico.getPubblicazione(), storico.getDestinatario(), storico.getNumero());
        spedizione.setInvio(storico.getInvio());
        spedizione.setOmaggio(storico.getOmaggio());
        return spedizione;
    }
    
    public static Spedizione addSpedizione(Abbonamento abbonamento, 
            Pubblicazione pubblicazione,
            Anagrafica destinatario, 
            int numero) {
        if (abbonamento == null || pubblicazione == null || numero <= 0) {
            return new Spedizione();
        }
        Spedizione spedizione = new Spedizione(abbonamento, pubblicazione, destinatario, numero);
        abbonamento.addSpedizione(spedizione);
        return spedizione;
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
    
    public static Incasso generateIncasso(Set<String> versamenti,
            String riepilogo) throws ParseException {
        DateFormat formatter = new SimpleDateFormat("yyMMdd");
        final Incasso incasso = new Incasso();
        incasso.setCuas(Cuas.getCuas(Integer.parseInt(riepilogo.substring(0,
                                                                          1))));
        incasso.setCcp(Ccp.getByCcp(riepilogo.substring(1,
                                                                         13)));
        incasso.setDataContabile(formatter.parse(riepilogo.substring(13,
                                                                     19)));
//	    String filler = riepilogo.substring(19,33);
//	    String idriepilogo = riepilogo.substring(33,36);
        incasso.setDocumenti(Integer.parseInt(riepilogo.substring(36,
                                                                        44)));
        incasso.setImporto(new BigDecimal(riepilogo.substring(44, 54)
                + "." + riepilogo.substring(54, 56)));

        incasso.setEsatti(Integer.parseInt(riepilogo.substring(56,
                                                                        64)));
        incasso.setImportoEsatti(new BigDecimal(riepilogo.substring(64,
                                                                             74)
                + "." + riepilogo.substring(74, 76)));

        incasso.setErrati(Integer.parseInt(riepilogo.substring(76,
                                                                        84)));
        incasso.setImportoErrati(new BigDecimal(riepilogo.substring(84,
                                                                             94)
                + "." + riepilogo.substring(94, 96)));

        versamenti.stream().forEach(s -> {
            try {
                incasso.addVersamento(generateVersamento(incasso,s));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });
        return incasso;
    }

    private static Versamento generateVersamento(Incasso incasso,String value)
            throws ParseException {
        DateFormat formatter = new SimpleDateFormat("yyMMdd");
        Versamento versamento = new Versamento(incasso,new BigDecimal(value.substring(36, 44) + "." + value.substring(44, 46)));
        versamento.setBobina(value.substring(0, 3));
        versamento.setProgressivoBobina(value.substring(3, 8));
	versamento.setProgressivo(value.substring(8,15));
        versamento.setDataPagamento(formatter.parse(value.substring(27, 33)));
        versamento.setBollettino(Bollettino.getTipoBollettino(Integer.parseInt(value.substring(33,36))));
        versamento.setProvincia(value.substring(46, 49));
        versamento.setUfficio(value.substring(49, 52));
        versamento.setSportello(value.substring(52, 54));
//          value.substring(54,55);
        versamento.setDataContabile(formatter.parse(value.substring(55,61)));
        versamento.setCampo(value.substring(61,79));
        versamento.setAccettazione(Accettazione.getTipoAccettazione(value.substring(79,81)));
        versamento.setSostitutivo(Sostitutivo.getTipoAccettazione(value.substring(81,82)));
        return versamento;
    }

    static int startabbonamento = 0;

    /*
     * Codice Cliente (TD 674/896) si compone di 16 caratteri numerici
     * riservati al correntista che intende utilizzare tale campo 2 caratteri
     * numerici di controcodice pari al resto della divisione dei primi 16
     * caratteri per 93 (Modulo 93. Valori possibili dei caratteri di
     * controcodice: 00 - 92)
     */
    public static String generateCampo(Anno anno, Mese inizio, Mese fine) {
        // primi 4 caratteri anno
        String campo = anno.getAnnoAsString();
        // 5 e 6 inizio
        campo += inizio.getCode();
        // 7 e 8 fine
        campo += fine.getCode();
        // 9-16
        startabbonamento++;
        campo += String.format("%08d", startabbonamento);
        campo += String.format("%02d", Long.parseLong(campo) % 93);
        return campo;
    }

    public static int getNumeroPubblicazioni(Mese inizio, Mese fine, Mese pub, TipoPubblicazione tipo) {
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
    
    public static void calcoloCostoAbbonamento(Abbonamento abbonamento) {
        double costo = 0.0;
        Mese inizio = abbonamento.getInizio();
        Mese fine = abbonamento.getFine();
        for (Spedizione spedizione : abbonamento.getSpedizioni()) {
            costo+= generaCosto(inizio, fine, spedizione);
        }
        abbonamento.setCosto(BigDecimal.valueOf(costo));
    }
    
    public static double generaCosto(Mese inizio, Mese fine, Spedizione spedizione) { 
        return generaCosto(inizio, fine, spedizione.getPubblicazione(), spedizione.getOmaggio(), spedizione.getNumero());
    }

    public static double generaCosto(Mese inizio, Mese fine, Pubblicazione pubblicazione, Omaggio omaggio, Integer numero) {
        double costo = 0.0;
        switch (omaggio) {
        case No:
            costo = pubblicazione.getCostoUnitario().doubleValue()
                     * numero.doubleValue()
                     * getNumeroPubblicazioni(inizio,fine, pubblicazione.getMese(),pubblicazione.getTipo());
        break;
        
        case ConSconto:
            costo = pubblicazione.getCostoScontato().doubleValue()
                     * numero.doubleValue()
                     * getNumeroPubblicazioni(inizio,fine, pubblicazione.getMese(),pubblicazione.getTipo());  
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

    public static void main(String[] args) {
        SpringApplication.run(SmdApplication.class, args);
    }

    @Bean
    @Transactional
    public CommandLineRunner loadData(AnagraficaDao anagraficaDao, StoricoDao storicoDao,
            PubblicazioneDao pubblicazioneDao, AbbonamentoDao abbonamentoDao,
            SpedizioneDao spedizioneDao,
            CampagnaDao campagnaDao, IncassoDao incassoDao, VersamentoDao versamentoDao,
            ProspettoDao prospettoDao) {
        return (args) -> {
            Pubblicazione messaggio = new Pubblicazione("Messaggio",
                                                        TipoPubblicazione.MENSILE);
            messaggio.setActive(true);
            messaggio.setAutore("AAVV");
            messaggio.setCostoUnitario(new BigDecimal(1.25));
            messaggio.setCostoScontato(new BigDecimal(1.25));
            messaggio.setEditore("ADP");
            messaggio.setMese(Mese.GENNAIO);
            pubblicazioneDao.save(messaggio);

            Pubblicazione lodare = new Pubblicazione("Lodare e Servire",
                                                     TipoPubblicazione.MENSILE);
            lodare.setActive(true);
            lodare.setAutore("AAVV");
            lodare.setCostoUnitario(new BigDecimal(1.50));
            lodare.setCostoScontato(new BigDecimal(1.50));
            lodare.setEditore("ADP");
            lodare.setMese(Mese.GENNAIO);
            pubblicazioneDao.save(lodare);

            Pubblicazione blocchetti = new Pubblicazione("Blocchetti",
                                                         TipoPubblicazione.SEMESTRALE);
            blocchetti.setActive(true);
            blocchetti.setAutore("AAVV");
            blocchetti.setCostoUnitario(new BigDecimal(3.00));
            blocchetti.setCostoScontato(new BigDecimal(2.40));
            blocchetti.setEditore("ADP");
            blocchetti.setMese(Mese.MARZO);
            pubblicazioneDao.save(blocchetti);


            Pubblicazione estratti = new Pubblicazione("Estratti",
                                                       TipoPubblicazione.ANNUALE);
            estratti.setActive(true);
            estratti.setAutore("AAVV");
            estratti.setCostoUnitario(new BigDecimal(10.00));
            estratti.setCostoScontato(new BigDecimal(10.00));
            estratti.setEditore("ADP");
            estratti.setMese(Mese.LUGLIO);
            pubblicazioneDao.save(estratti);

            // save a couple of customers

            Anagrafica ar = new Anagrafica("Antonio", "Russo");
            ar.setDiocesi(Diocesi.DIOCESI116);
            ar.setIndirizzo("Piazza Duomo 1");
            ar.setCitta("Milano");
            ar.setCap("20100");
            ar.setEmail("ar@arsinfo.it");
            ar.setTelefono("+3902000009");
            ar.setTitolo(TitoloAnagrafica.Vescovo);
            ar.setRegioneVescovi(Regione.LOMBARDIA);
            anagraficaDao.save(ar);
            
            Storico arlodare = new Storico(ar, lodare, 1);
            arlodare.setOmaggio(Omaggio.CuriaDiocesiana);
            storicoDao.save(arlodare);
            
            Storico armessaggio = new Storico(ar, messaggio, 1);
            armessaggio.setOmaggio(Omaggio.CuriaDiocesiana);
            storicoDao.save(armessaggio);

            Storico arblocchetti = new Storico(ar, blocchetti, 1);
            arblocchetti.setOmaggio(Omaggio.CuriaDiocesiana);
            storicoDao.save(arblocchetti);

            Storico arestratti = new Storico(ar, estratti, 1);
            arestratti.setOmaggio(Omaggio.CuriaDiocesiana);
            storicoDao.save(arestratti);

            Anagrafica gp = new Anagrafica("Gabriele", "Pizzo");
            gp.setDiocesi(Diocesi.DIOCESI116);
            gp.setIndirizzo("Piazza Sant'Ambrogio 1");
            gp.setCitta("Milano");
            gp.setCap("20110");
            gp.setEmail("gp@arsinfo.it");
            gp.setTelefono("+3902000010");
            anagraficaDao.save(gp);

            Storico gpblocchetti = new Storico(gp, blocchetti, 10);
            gpblocchetti.setOmaggio(Omaggio.ConSconto);
            gpblocchetti.setCassa(Cassa.Contrassegno);
            storicoDao.save(gpblocchetti);
            
            Storico gpmessaggio = new Storico(gp, messaggio, 1);
            gpmessaggio.setCassa(Cassa.Contrassegno);
            storicoDao.save(gpmessaggio);

            Anagrafica mp = new Anagrafica("Matteo", "Paro");
            mp.setDiocesi(Diocesi.DIOCESI168);
            mp.setIndirizzo("Piazza del Gesu' 1");
            mp.setCitta("Roma");
            mp.setCap("00192");
            mp.setEmail("mp@arsinfo.it");
            mp.setTelefono("+3906000020");
            anagraficaDao.save(mp);
            
            Storico mpmessaggio = new Storico(mp, messaggio, 10);
            mpmessaggio.setOmaggio(Omaggio.Gesuiti);
            mpmessaggio.setInvio(Invio.AdpSede);
            storicoDao.save(mpmessaggio);

            Anagrafica dp = new Anagrafica("Davide", "Palma");
            dp.setDiocesi(Diocesi.DIOCESI168);
            dp.setIndirizzo("Piazza Navona 3, 00100 Roma");
            dp.setCitta("Roma");
            dp.setCap("00195");
            dp.setEmail("dp@arsinfo.it");
            dp.setTelefono("+3906000020");
            dp.setDirettoreDiocesiano(true);
            dp.setRegioneDirettoreDiocesano(Regione.LAZIO);
            anagraficaDao.save(dp);
            
            Storico dpmessaggio = new Storico(dp, messaggio, 1);
            dpmessaggio.setOmaggio(Omaggio.CuriaGeneralizia);
            dpmessaggio.setInvio(Invio.AdpSede);
            storicoDao.save(dpmessaggio);

            Anagrafica ms = new Anagrafica("Michele", "Santoro");
            ms.setDiocesi(Diocesi.DIOCESI126);
            ms.setIndirizzo("Via Duomo 10");
            ms.setCitta("Napoli");
            ms.setCap("80135");
            ms.setEmail("ms@arsinfo.it");
            ms.setTelefono("+39081400022");
            anagraficaDao.save(ms);

            Anagrafica ps = new Anagrafica("Pasqualina", "Santoro");
            ps.setDiocesi(Diocesi.DIOCESI126);
            ps.setIndirizzo("Piazza Dante 10");
            ps.setCitta("Napoli");
            ps.setCap("80135");
            ps.setEmail("arsinfo@adp.it");
            ps.setTelefono("+39081400023");
            anagraficaDao.save(ps);

            
            storicoDao.save(new Storico(ms, blocchetti, 10));
            storicoDao.save(new Storico(ms, ps, blocchetti, 5));
            
            Abbonamento abbonamentoMd = new Abbonamento(ms);
            addSpedizione(abbonamentoMd,blocchetti,ms,1);
            addSpedizione(abbonamentoMd,lodare,ms,1);
            addSpedizione(abbonamentoMd,estratti,ms,1);
            addSpedizione(abbonamentoMd,messaggio,ms,1);
            abbonamentoMd.setCampo(generateCampo(abbonamentoMd.getAnno(),
                                                 abbonamentoMd.getInizio(),
                                                 abbonamentoMd.getFine()));
            calcoloCostoAbbonamento(abbonamentoMd);
            abbonamentoDao.save(abbonamentoMd);

            Abbonamento abbonamentoCo = new Abbonamento(gp);
            addSpedizione(abbonamentoCo,blocchetti,gp,10);
            addSpedizione(abbonamentoCo,lodare,gp,10);
            addSpedizione(abbonamentoCo,estratti,gp,5);
            addSpedizione(abbonamentoCo,messaggio,gp,5);
            addSpedizione(abbonamentoCo,blocchetti,mp,10);
            addSpedizione(abbonamentoCo,blocchetti,ar,10);
            abbonamentoCo.setAnno(Anno.ANNO2018);
            abbonamentoCo.setCampo(generateCampo(abbonamentoCo.getAnno(),
                                                 abbonamentoCo.getInizio(),
                                                 abbonamentoCo.getFine()));
            calcoloCostoAbbonamento(abbonamentoCo);
            abbonamentoDao.save(abbonamentoCo);

            Abbonamento abbonamentoDp = new Abbonamento(dp);
            addSpedizione(abbonamentoDp,blocchetti,dp,10);
            abbonamentoDp.setInizio(Mese.MAGGIO);
            abbonamentoDp.setSpese(new BigDecimal("3.75"));
            abbonamentoDp.setCampo(generateCampo(abbonamentoDp.getAnno(),
                                                 abbonamentoDp.getInizio(),
                                                 abbonamentoDp.getFine()));
            calcoloCostoAbbonamento(abbonamentoDp);
            abbonamentoDao.save(abbonamentoDp);
            
            Abbonamento telematici001 = new Abbonamento(ar);
            addSpedizione(telematici001, blocchetti,ar,1);
            telematici001.setCosto(new BigDecimal(15));
            telematici001.setCampo("000000018000792609");
            abbonamentoDao.save(telematici001);
            
            Abbonamento venezia002 = new Abbonamento(ms);
            addSpedizione(venezia002, blocchetti,ms,1);
            venezia002.setCosto(new BigDecimal(15));
            venezia002.setCampo("000000018000854368");
            abbonamentoDao.save(venezia002);

            Abbonamento venezia003 = new Abbonamento(ms);
            addSpedizione(venezia003, blocchetti,ms,1);
            venezia003.setCosto(new BigDecimal(18));
            venezia003.setCampo("000000018000263519");
            abbonamentoDao.save(venezia003);

            Abbonamento venezia004 = new Abbonamento(ms);
            addSpedizione(venezia004, blocchetti,ms,2);
            venezia004.setCosto(new BigDecimal(30));
            venezia004.setCampo("000000018000254017");
            abbonamentoDao.save(venezia004);

            Abbonamento venezia005 = new Abbonamento(ms);
            addSpedizione(venezia005, blocchetti,ms,2);
            venezia005.setCosto(new BigDecimal(37));
            venezia005.setCampo("000000018000761469");
            abbonamentoDao.save(venezia005);

            Abbonamento venezia006 = new Abbonamento(ms);
            addSpedizione(venezia006, blocchetti,ms,3);
            venezia006.setCosto(new BigDecimal(48));
            venezia006.setCampo("000000018000253916");
            abbonamentoDao.save(venezia006);

            Abbonamento venezia007 = new Abbonamento(ms);
            addSpedizione(venezia007, blocchetti,ms,10);
            venezia007.setCosto(new BigDecimal(70));
            venezia007.setCampo("000000018000800386");
            abbonamentoDao.save(venezia007);
            
            Abbonamento venezia008 = new Abbonamento(ms);
            addSpedizione(venezia008, blocchetti,ms,15);
            venezia008.setCosto(new BigDecimal(84));
            venezia008.setCampo("000000018000508854");
            abbonamentoDao.save(venezia008);

            Abbonamento firenze009 = new Abbonamento(dp);
            addSpedizione(firenze009, estratti,dp,1);
            firenze009.setCosto(new BigDecimal(10));
            firenze009.setCampo("000000018000686968");
            abbonamentoDao.save(firenze009);
            
            Abbonamento firenze010 = new Abbonamento(dp);
            addSpedizione(firenze010, lodare,dp,1);
            firenze010.setCosto(new BigDecimal(15));
            firenze010.setCampo("000000018000198318");
            abbonamentoDao.save(firenze010);

            Abbonamento firenze011 = new Abbonamento(dp);
            addSpedizione(firenze011, lodare,dp,1);
            firenze011.setCosto(new BigDecimal(15));
            firenze011.setCampo("000000018000201449");
            abbonamentoDao.save(firenze011);

            Abbonamento firenze012 = new Abbonamento(dp);
            addSpedizione(firenze012, lodare,dp,3);
            firenze012.setCosto(new BigDecimal(33));
            firenze012.setCampo("000000018000633491");
            abbonamentoDao.save(firenze012);
            
            Abbonamento firenze013 = new Abbonamento(dp);
            addSpedizione(firenze013, lodare,dp,10);
            addSpedizione(firenze013, estratti,dp,10);
            addSpedizione(firenze013, blocchetti,dp,10);
            firenze013.setCosto(new BigDecimal(108));
            firenze013.setCampo("000000018000196500");
            abbonamentoDao.save(firenze013);
            
            Abbonamento bari014 = new Abbonamento(mp);
            addSpedizione(bari014, lodare,mp,1);
            bari014.setCosto(new BigDecimal(12));
            bari014.setCampo("000000018000106227");
            abbonamentoDao.save(bari014);

            Abbonamento bari015 = new Abbonamento(mp);
            addSpedizione(bari015, lodare,mp,3);
            bari015.setCosto(new BigDecimal(36));
            bari015.setCampo("000000018000077317");
            abbonamentoDao.save(bari015);

            Abbonamento bari016 = new Abbonamento(mp);
            addSpedizione(bari016, lodare,mp,5);
            bari016.setCosto(new BigDecimal(60));
            bari016.setCampo("000000018000125029");
            abbonamentoDao.save(bari016);

            Abbonamento bari017 = new Abbonamento(mp);
            addSpedizione(bari017, estratti,mp,10);
            bari017.setCosto(new BigDecimal(67));
            bari017.setCampo("000000018000065383");
            abbonamentoDao.save(bari017);


            Campagna campagna2018=new Campagna();
            campagna2018.setAnno(Anno.ANNO2018);
            campagna2018.addCampagnaItem(new CampagnaItem(campagna2018,messaggio));
            campagna2018.addCampagnaItem(new CampagnaItem(campagna2018,lodare));
            campagna2018.addCampagnaItem(new CampagnaItem(campagna2018,blocchetti));
            campagna2018.addCampagnaItem(new CampagnaItem(campagna2018,estratti));

            generaCampagna(campagna2018, storicoDao.findAll(), new ArrayList<>());
            campagnaDao.save(campagna2018);

            Campagna campagna2019=new Campagna();
            campagna2019.setAnno(Anno.ANNO2019);
            campagna2019.addCampagnaItem(new CampagnaItem(campagna2019,messaggio));
            campagna2019.addCampagnaItem(new CampagnaItem(campagna2019,lodare));
            campagna2019.addCampagnaItem(new CampagnaItem(campagna2019,blocchetti));
            campagna2019.addCampagnaItem(new CampagnaItem(campagna2019,estratti));

            generaCampagna(campagna2019, storicoDao.findAll(), new ArrayList<>());
            campagnaDao.save(campagna2019);

            Campagna campagna2020=new Campagna();
            campagna2020.setAnno(Anno.ANNO2020);
            campagna2020.addCampagnaItem(new CampagnaItem(campagna2020,messaggio));
            campagna2020.addCampagnaItem(new CampagnaItem(campagna2020,lodare));
            campagna2020.addCampagnaItem(new CampagnaItem(campagna2020,blocchetti));
            campagna2020.addCampagnaItem(new CampagnaItem(campagna2020,estratti));

            generaCampagna(campagna2020, storicoDao.findAll(), new ArrayList<>());
            campagnaDao.save(campagna2020);

            String riepilogo1="4000063470009171006              999000000010000000015000000000100000000150000000000000000000000                                                                                                        \n";
            Set<String> versamenti1= new HashSet<>();
            versamenti1.add("0000000000000010000634700091710046740000001500055111092171006000000018000792609CCN                                                                                                                      \n");
            Incasso incasso1 = generateIncasso(versamenti1, riepilogo1); 
            incassoDao.save(incasso1);
            
            String riepilogo2="3000063470009171006              999000000090000000367000000000700000003020000000002000000006500                                                                                                        \n";
            Set<String> versamenti2= new HashSet<>();
            versamenti2.add("0865737400000020000634700091710056740000001500074046022171006000000018000854368DIN                                                                                                                      \n");
            versamenti2.add("0865298400000030000634700091710056740000001800076241052171006000000018000263519DIN                                                                                                                      \n");
            versamenti2.add("0863439100000040000634700091710056740000003000023013042171006000000018000254017DIN                                                                                                                      \n");
            versamenti2.add("0854922500000050000634700091710046740000003700023367052171006000000018000761469DIN                                                                                                                      \n");
            versamenti2.add("0863439000000060000634700091710056740000004800023013042171006000000018000253916DIN                                                                                                                      \n");
            versamenti2.add("0865570900000070000634700091710056740000007000023247042171006000000018000800386DIN                                                                                                                      \n");
            versamenti2.add("0863569900000080000634700091710056740000008400074264032171006000000018000508854DIN                                                                                                                      \n");
            versamenti2.add("0856588699999990000634700091710041230000001500038124062171006727703812406007375DIN                                                                                                                      \n");
            versamenti2.add("0858313299999990000634700091710041230000005000098101062171006727709810106010156DIN                                                                                                                      \n");

            Incasso incasso2 = generateIncasso(versamenti2, riepilogo2);
            incassoDao.save(incasso2);
            
            String riepilogo3="5000063470009171006              999000000060000000201000000000500000001810000000001000000002000                                                                                                        \n";
            Set<String> versamenti3= new HashSet<>();
            versamenti3.add("0854174400000090000634700091710046740000001000055379072171006000000018000686968DIN                                                                                                                      \n");
            versamenti3.add("0860359800000100000634700091710056740000001500055239072171006000000018000198318DIN                                                                                                                      \n");
            versamenti3.add("0858363300000110000634700091710056740000001500055826052171006000000018000201449DIN                                                                                                                      \n");
            versamenti3.add("0860441300000120000634700091710056740000003300055820042171006000000018000633491DIN                                                                                                                      \n");
            versamenti3.add("0860565700000130000634700091710056740000010800055917062171006000000018000196500DIN                                                                                                                      \n");
            versamenti3.add("0855941199999990000634700091710041230000002000055681052171006727705568105003308DIN                                                                                                                      \n");

            Incasso incasso3 = generateIncasso(versamenti3, riepilogo3);
            incassoDao.save(incasso3);
            
            String riepilogo4="7000063470009171006              999000000070000000447500000000400000001750000000003000000027250                                                                                                        \n";
            Set<String> versamenti4= new HashSet<>();
            versamenti4.add("0873460200000140000634700091710056740000001200053057032171006000000018000106227DIN                                                                                                                      \n");
            versamenti4.add("0874263500000150000634700091710056740000003600009019032171006000000018000077317DIN                                                                                                                      \n");
            versamenti4.add("0875677100000160000634700091710056740000006000029079022171006000000018000125029DIN                                                                                                                      \n");
            versamenti4.add("0871026300000170000634700091710046740000006700040366032171006000000018000065383DIN                                                                                                                      \n");
            versamenti4.add("0862740599999990000634700091710044510000000750002066172171006727700206617006437DIN                                                                                                                      \n");
            versamenti4.add("0857504199999990000634700091710034510000004000040016062171006727604001606035576DIN                                                                                                                      \n");
            versamenti4.add("0866089199999990000634700091710044510000022500018160052171006727701816005010892DIN                                                                                                                      \n");
            
            Incasso incasso4=generateIncasso(versamenti4, riepilogo4);
            incassoDao.save(incasso4);
            
            log.info("Pubblicazioni found with findAll():");
            log.info("-------------------------------");
            for (Pubblicazione pubblicazione : pubblicazioneDao.findAll()) {
                log.info(pubblicazione.toString());
            }
            log.info("");

            log.info("Pubblicazione found with findByNameStartsWithIgnoreCase('Estratti'):");
            log.info("--------------------------------------------");
            for (Pubblicazione adp : pubblicazioneDao.findByNomeStartsWithIgnoreCase("Estratti")) {
                log.info(adp.toString());
            }
            log.info("");

            log.info("Pubblicazione found with findByTipo('MENSILE'):");
            log.info("--------------------------------------------");
            for (Pubblicazione mensile : pubblicazioneDao.findByTipo(TipoPubblicazione.MENSILE)) {
                log.info(mensile.toString());
            }
            log.info("");

            Pubblicazione first = pubblicazioneDao.findById(1L).get();
            log.info("Messaggio found with findOne(1L):");
            log.info("--------------------------------");
            log.info(first.toString());
            log.info("");

            Pubblicazione second = pubblicazioneDao.findById(2L).get();
            log.info("lodare found with findOne(2L):");
            log.info("--------------------------------");
            log.info(second.toString());
            log.info("");
            
            log.info("Anagrafica found with findAll():");
            log.info("-------------------------------");
            for (Anagrafica customer : anagraficaDao.findAll()) {
                log.info(customer.toString());
            }
            log.info("");

            log.info("Anagrafica Russo found with findOne(5L):");
            log.info("--------------------------------------------");
            Anagrafica russo = anagraficaDao.findById(5L).get();
            log.info(russo.toString());
            log.info("");
            

            log.info("Anagrafica found with findByLastNameStartsWithIgnoreCase('Russo'):");
            log.info("--------------------------------------------");
            for (Anagrafica ana : anagraficaDao.findByCognomeStartsWithIgnoreCase("Russo")) {
                log.info(ana.toString());
            }
            log.info("");

            log.info("Anagrafica found with findByDiocesi('ROMA'):");
            log.info("--------------------------------------------");
            for (Anagrafica roma : anagraficaDao.findByDiocesi(Diocesi.DIOCESI168)) {
                log.info(roma.toString());
            }
            log.info("");


            log.info("Storico found with findByIntestatario('michele santoro id=17'):");
            log.info("--------------------------------------------");
            for (Storico anp : storicoDao.findByIntestatario(ms)) {
                log.info(anp.toString());
            }
            log.info("");

            log.info("Storico found with findByDestinatario('davide palma id=15'):");
            log.info("--------------------------------------------");
            for (Storico anp : storicoDao.findByDestinatario(dp)) {
                log.info(anp.toString());
            }
            log.info("");

            log.info("Storico found with findByPubblicazione('blocchetti'):");
            log.info("--------------------------------------------");
            for (Storico anp : storicoDao.findByPubblicazione(blocchetti)) {
                log.info(anp.toString());
            }
            log.info("");

            log.info("Abbonamenti found with findAll():");
            log.info("-------------------------------");
            for (Abbonamento abbonamento : abbonamentoDao.findAll()) {
                log.info(abbonamento.toString());
            }
            log.info("");

            log.info("Abbonamenti found with findByIntestatario(ms):");
            log.info("-------------------------------");
            for (Abbonamento abbonamentomd : abbonamentoDao.findByIntestatario(ms)) {
                log.info(abbonamentomd.toString());
            }
            log.info("");

            log.info("Campagna found with findAll():");
            log.info("-------------------------------");
            for (Campagna campagna : campagnaDao.findAll()) {
                log.info(campagna.toString());
            }
            log.info("");

            log.info("Versamenti found with findAll():");
            log.info("-------------------------------");
            for (Versamento versamento : versamentoDao.findAll()) {
                log.info(versamento.toString());
            }
            log.info("");

            log.info("Incassi found with findAll():");
            log.info("-------------------------------");
            for (Incasso incasso : incassoDao.findAll()) {
                log.info(incasso.toString());
            }
            log.info("");
            
            log.info("Versamenti found by findByIncasso(incasso1):");
            log.info("-------------------------------");
            for (Versamento versamento : versamentoDao.findByIncasso(incasso1)) {
                log.info(versamento.toString());
            }
            log.info("");
            
            log.info("Versamenti found by findByImporto(new BigDecimal(\"40.00\"):");
            log.info("-------------------------------");
            for (Versamento versamento : versamentoDao.findByImporto(new BigDecimal("40.00"))) {
                log.info(versamento.toString());
            }
            log.info("");

            log.info("Versamenti found by data contabile 2017-ott-06:");
            log.info("-------------------------------");
            DateFormat formatter = new SimpleDateFormat("yyMMdd");
            for (Versamento versamento : versamentoDao.findByDataContabile(formatter.parse("171006"))) {
                log.info(versamento.toString());
            }
            log.info("");

            log.info("Versamenti found by data pagamento 2017-ott-03:");
            log.info("-------------------------------");
            for (Versamento versamento : versamentoDao.findByDataPagamento(formatter.parse("171003"))) {
                log.info(versamento.toString());
            }
            log.info("");

            log.info("Incassi found by CUAS.VENEZIA:");
            log.info("-------------------------------");
            for (Incasso incasso : incassoDao.findByCuas(Cuas.VENEZIA)) {
                log.info(incasso.toString());
            }
            log.info("");
            
            log.info("Abbonamenti found per Costo > 0 e Versamenti Not Null");
            log.info("-------------------------------");
            for (Abbonamento abb: abbonamentoDao.findByCostoGreaterThanAndVersamentoNotNull(BigDecimal.ZERO)) {
                log.info(abb.toString());
            }
            log.info("");

            log.info("Abbonamenti found per Costo > 0 e Versamenti Null");
            log.info("-------------------------------");
            for (Abbonamento abb: abbonamentoDao.findByCostoGreaterThanAndVersamentoNull(BigDecimal.ZERO)) {
                log.info(abb.toString());
            }
            log.info("");

            log.info("Abbonamenti found per Costo > 0 ");
            log.info("-------------------------------");
            for (Abbonamento abb: abbonamentoDao.findByCostoGreaterThan(BigDecimal.ZERO)) {
                log.info(abb.toString());
                if (abb.getVersamento() == null)
                    log.info("versamento:null");
                else
                    log.info(abb.getVersamento().getId().toString());
                
            }
            log.info("");

            log.info("versamenti found by incasso1");
            log.info("-------------------------------");
            for (Versamento versamento: versamentoDao.findByIncasso(incasso1)) {
                log.info(versamento.toString());
            }
            log.info("");

            log.info("Anno Scorso");
            log.info(getAnnoPassato().getAnnoAsString());
            log.info("Anno Corrente");
            log.info(getAnnoCorrente().getAnnoAsString());
            log.info("Anno Prossimo");
            log.info(getAnnoProssimo().getAnnoAsString());
            log.info("Mese Corrente");
            log.info(getMeseCorrente().getNomeBreve());
            log.info("");

            log.info("Numero: Mese.MARZO, Mese.DICEMBRE, Mese.MARZO, TipoPubblicazione.ANNUALE)");
            log.info(Integer.toString(getNumeroPubblicazioni(Mese.MARZO, Mese.DICEMBRE, Mese.MARZO, TipoPubblicazione.ANNUALE)));
            log.info("Numero: Mese.APRILE, Mese.DICEMBRE, Mese.MARZO, TipoPubblicazione.ANNUALE)");
            log.info(Integer.toString(getNumeroPubblicazioni(Mese.APRILE, Mese.DICEMBRE, Mese.MARZO, TipoPubblicazione.ANNUALE)));
            log.info("Numero: Mese.MARZO, Mese.DICEMBRE, Mese.MARZO, TipoPubblicazione.SEMESTRALE)");
            log.info(Integer.toString(getNumeroPubblicazioni(Mese.MARZO, Mese.DICEMBRE, Mese.MARZO, TipoPubblicazione.SEMESTRALE)));
            log.info("Numero: Mese.SETTEMBRE, Mese.DICEMBRE, Mese.MARZO, TipoPubblicazione.SEMESTRALE)");
            log.info(Integer.toString(getNumeroPubblicazioni(Mese.SETTEMBRE, Mese.DICEMBRE, Mese.MARZO, TipoPubblicazione.SEMESTRALE)));
            log.info("Numero: Mese.OTTOBRE, Mese.DICEMBRE, Mese.MARZO, TipoPubblicazione.SEMESTRALE)");
            log.info(Integer.toString(getNumeroPubblicazioni(Mese.OTTOBRE, Mese.DICEMBRE, Mese.MARZO, TipoPubblicazione.SEMESTRALE)));
            log.info("");
            
            
            Incasso incasso5 = new Incasso();
            incasso5.setCassa(Cassa.Contrassegno);
            incasso5.setDocumenti(1);
            incasso5.setErrati(0);
            incasso5.setEsatti(1);
            incasso5.setOperazione("Ricevuto Assegno");
            incasso5.setDataContabile(new Date());
            incasso5.setImportoErrati(BigDecimal.ZERO);
            incasso5.setImportoEsatti(abbonamentoDp.getTotale());
            incasso5.setImporto(abbonamentoDp.getTotale());
            
            Versamento versamentoIncasso5 = new Versamento(incasso5,abbonamentoDp.getTotale());
            versamentoIncasso5.setCampo(abbonamentoDp.getCampo());
            versamentoIncasso5.setDataContabile(incasso5.getDataContabile());
            versamentoIncasso5.setDataPagamento(incasso5.getDataContabile());
            incasso5.addVersamento(versamentoIncasso5);
            incassoDao.save(incasso5);

            log.info("Abbonamento Palma prima di essere incassato");
            log.info("-------------------------------");
            log.info(abbonamentoDp.toString());
            
            log.info("Versamento Incasso Palma prima di essere incassato");
            log.info("-------------------------------");
            log.info(versamentoIncasso5.toString());
            
            versamentoDao.save(
                   incassa(versamentoIncasso5, abbonamentoDp));
            abbonamentoDao.save(abbonamentoDp);
            
            log.info("Abbonamento Palma dopo essere stato incassato");
            log.info("-------------------------------");
            log.info(abbonamentoDp.toString());
            
            log.info("Versamento Incasso Palma dopo essere stato incassato");
            log.info("-------------------------------");
            log.info(versamentoIncasso5.toString());
            
            generaProspetto(estratti, abbonamentoDao.findByAnno(Anno.ANNO2018), spedizioneDao.findByPubblicazione(estratti),Anno.ANNO2018).stream().forEach(p -> {
                prospettoDao.save(p);
                log.info(p.toString());
            });
            generaProspetto(estratti, abbonamentoDao.findByAnno(Anno.ANNO2019), spedizioneDao.findByPubblicazione(estratti),Anno.ANNO2019).stream().forEach(p -> {
                prospettoDao.save(p);
                log.info(p.toString());
            });
            generaProspetto(estratti, abbonamentoDao.findByAnno(Anno.ANNO2020), spedizioneDao.findByPubblicazione(estratti),Anno.ANNO2020).stream().forEach(p -> {
                prospettoDao.save(p);
                log.info(p.toString());
            });
            
            generaProspetto(blocchetti, abbonamentoDao.findByAnno(Anno.ANNO2018), spedizioneDao.findByPubblicazione(blocchetti),Anno.ANNO2018).stream().forEach(p -> {
                prospettoDao.save(p);
                log.info(p.toString());
            });
            generaProspetto(blocchetti, abbonamentoDao.findByAnno(Anno.ANNO2019), spedizioneDao.findByPubblicazione(blocchetti),Anno.ANNO2019).stream().forEach(p -> {
                prospettoDao.save(p);
                log.info(p.toString());
            });
            generaProspetto(blocchetti, abbonamentoDao.findByAnno(Anno.ANNO2020), spedizioneDao.findByPubblicazione(blocchetti),Anno.ANNO2020).stream().forEach(p -> {
                prospettoDao.save(p);
                log.info(p.toString());
            });

            generaProspetto(lodare, abbonamentoDao.findByAnno(Anno.ANNO2018), spedizioneDao.findByPubblicazione(lodare),Anno.ANNO2018).stream().forEach(p -> {
                prospettoDao.save(p);
                log.info(p.toString());
            });
            generaProspetto(lodare, abbonamentoDao.findByAnno(Anno.ANNO2019), spedizioneDao.findByPubblicazione(lodare),Anno.ANNO2019).stream().forEach(p -> {
                prospettoDao.save(p);
                log.info(p.toString());
            });
            generaProspetto(lodare, abbonamentoDao.findByAnno(Anno.ANNO2020), spedizioneDao.findByPubblicazione(lodare),Anno.ANNO2020).stream().forEach(p -> {
                prospettoDao.save(p);
                log.info(p.toString());
            });

            generaProspetto(messaggio, abbonamentoDao.findByAnno(Anno.ANNO2018), spedizioneDao.findByPubblicazione(messaggio),Anno.ANNO2018).stream().forEach(p -> {
                prospettoDao.save(p);
                log.info(p.toString());
            });
            generaProspetto(messaggio, abbonamentoDao.findByAnno(Anno.ANNO2019), spedizioneDao.findByPubblicazione(messaggio),Anno.ANNO2019).stream().forEach(p -> {
                prospettoDao.save(p);
                log.info(p.toString());
            });
            generaProspetto(messaggio, abbonamentoDao.findByAnno(Anno.ANNO2020), spedizioneDao.findByPubblicazione(messaggio),Anno.ANNO2020).stream().forEach(p -> {
                prospettoDao.save(p);
                log.info(p.toString());
            });


        };
    }

}
