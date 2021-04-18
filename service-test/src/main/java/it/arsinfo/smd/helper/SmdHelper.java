package it.arsinfo.smd.helper;

import it.arsinfo.smd.data.*;
import it.arsinfo.smd.entity.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SmdHelper {

    public static List<SpesaSpedizione> getSpeseSpedizione() {
        List<SpesaSpedizione> sss = new ArrayList<>();
    
        SpesaSpedizione ss1 = new SpesaSpedizione();
        ss1.setRangeSpeseSpedizione(RangeSpeseSpedizione.Base);
        ss1.setAreaSpedizione(AreaSpedizione.Italia);
        ss1.setSpese(new BigDecimal("2.00"));
        ss1.setCor3gg(new BigDecimal("5.50"));
        ss1.setCor24h(new BigDecimal("7.50"));
        sss.add(ss1);
    
        SpesaSpedizione ss2 = new SpesaSpedizione();
        ss2.setRangeSpeseSpedizione(RangeSpeseSpedizione.Da100grA200gr);
        ss2.setAreaSpedizione(AreaSpedizione.Italia);
        ss2.setSpese(new BigDecimal("3.00"));
        ss2.setCor3gg(new BigDecimal("5.50"));
        ss2.setCor24h(new BigDecimal("7.50"));
        sss.add(ss2);
    
        SpesaSpedizione ss3 = new SpesaSpedizione();
        ss3.setRangeSpeseSpedizione(RangeSpeseSpedizione.Da200grA350gr);
        ss3.setAreaSpedizione(AreaSpedizione.Italia);
        ss3.setSpese(new BigDecimal("3.00"));
        ss3.setCor3gg(new BigDecimal("5.50"));
        ss3.setCor24h(new BigDecimal("7.50"));
        sss.add(ss3);
    
        SpesaSpedizione ss4 = new SpesaSpedizione();
        ss4.setRangeSpeseSpedizione(RangeSpeseSpedizione.Da350grA1Kg);
        ss4.setAreaSpedizione(AreaSpedizione.Italia);
        ss4.setSpese(new BigDecimal("5.00"));
        ss4.setCor3gg(new BigDecimal("5.50"));
        ss4.setCor24h(new BigDecimal("7.50"));
        sss.add(ss4);
    
        SpesaSpedizione ss5 = new SpesaSpedizione();
        ss5.setRangeSpeseSpedizione(RangeSpeseSpedizione.Da1KgA2Kg);
        ss5.setAreaSpedizione(AreaSpedizione.Italia);
        ss5.setSpese(new BigDecimal("5.50"));
        ss5.setCor3gg(new BigDecimal("5.50"));
        ss5.setCor24h(new BigDecimal("7.50"));
        sss.add(ss5);
    
        SpesaSpedizione ss6 = new SpesaSpedizione();
        ss6.setRangeSpeseSpedizione(RangeSpeseSpedizione.Da2KgA5Kg);
        ss6.setAreaSpedizione(AreaSpedizione.Italia);
        ss6.setSpese(new BigDecimal("7.00"));
        ss6.setCor3gg(new BigDecimal("7.00"));
        ss6.setCor24h(new BigDecimal("9.00"));
        sss.add(ss6);
    
        SpesaSpedizione ss7 = new SpesaSpedizione();
        ss7.setRangeSpeseSpedizione(RangeSpeseSpedizione.Da5KgA10Kg);
        ss7.setAreaSpedizione(AreaSpedizione.Italia);
        ss7.setSpese(new BigDecimal("8.00"));
        ss7.setCor3gg(new BigDecimal("8.00"));
        ss7.setCor24h(new BigDecimal("10.50"));
        sss.add(ss7);
        
        SpesaSpedizione ss8 = new SpesaSpedizione();
        ss8.setRangeSpeseSpedizione(RangeSpeseSpedizione.Da10KgA20Kg);
        ss8.setAreaSpedizione(AreaSpedizione.Italia);
        ss8.setSpese(new BigDecimal("9.00"));
        ss8.setCor3gg(new BigDecimal("9.00"));
        ss8.setCor24h(new BigDecimal("11.50"));
        sss.add(ss8);
    
        SpesaSpedizione ss9 = new SpesaSpedizione();
        ss9.setRangeSpeseSpedizione(RangeSpeseSpedizione.Da20KgA30Kg);
        ss9.setAreaSpedizione(AreaSpedizione.Italia);
        ss9.setSpese(new BigDecimal("10.00"));
        ss9.setCor3gg(new BigDecimal("10.00"));
        ss9.setCor24h(new BigDecimal("13.00"));
        sss.add(ss9);
    
        SpesaSpedizione ss9bis = new SpesaSpedizione();
        ss9bis.setRangeSpeseSpedizione(RangeSpeseSpedizione.Extra);
        ss9bis.setAreaSpedizione(AreaSpedizione.Italia);
        ss9bis.setSpese(new BigDecimal("16.00"));
        sss.add(ss9bis);
    
    
        SpesaSpedizione ss10 = new SpesaSpedizione();
        ss10.setRangeSpeseSpedizione(RangeSpeseSpedizione.Base);
        ss10.setAreaSpedizione(AreaSpedizione.EuropaBacinoMediterraneo);
        ss10.setSpese(new BigDecimal("4.00"));
        sss.add(ss10);
    
        SpesaSpedizione ss11 = new SpesaSpedizione();
        ss11.setRangeSpeseSpedizione(RangeSpeseSpedizione.Da100grA200gr);
        ss11.setAreaSpedizione(AreaSpedizione.EuropaBacinoMediterraneo);
        ss11.setSpese(new BigDecimal("5.70"));
        sss.add(ss11);
    
        SpesaSpedizione ss12 = new SpesaSpedizione();
        ss12.setRangeSpeseSpedizione(RangeSpeseSpedizione.Da200grA350gr);
        ss12.setAreaSpedizione(AreaSpedizione.EuropaBacinoMediterraneo);
        ss12.setSpese(new BigDecimal("6.50"));
        sss.add(ss12);
    
        SpesaSpedizione ss13 = new SpesaSpedizione();
        ss13.setRangeSpeseSpedizione(RangeSpeseSpedizione.Da350grA1Kg);
        ss13.setAreaSpedizione(AreaSpedizione.EuropaBacinoMediterraneo);
        ss13.setSpese(new BigDecimal("8.50"));
        sss.add(ss13);
    
        SpesaSpedizione ss14 = new SpesaSpedizione();
        ss14.setRangeSpeseSpedizione(RangeSpeseSpedizione.Da1KgA2Kg);
        ss14.setAreaSpedizione(AreaSpedizione.EuropaBacinoMediterraneo);
        ss14.setSpese(new BigDecimal("13.50"));
        sss.add(ss14);
    
        SpesaSpedizione ss15 = new SpesaSpedizione();
        ss15.setRangeSpeseSpedizione(RangeSpeseSpedizione.Base);
        ss15.setAreaSpedizione(AreaSpedizione.AmericaAfricaAsia);
        ss15.setSpese(new BigDecimal("5.00"));
        sss.add(ss15);
    
        SpesaSpedizione ss16 = new SpesaSpedizione();
        ss16.setRangeSpeseSpedizione(RangeSpeseSpedizione.Da100grA200gr);
        ss16.setAreaSpedizione(AreaSpedizione.AmericaAfricaAsia);
        ss16.setSpese(new BigDecimal("8.60"));
        sss.add(ss16);
    
        SpesaSpedizione ss17 = new SpesaSpedizione();
        ss17.setRangeSpeseSpedizione(RangeSpeseSpedizione.Da200grA350gr);
        ss17.setAreaSpedizione(AreaSpedizione.AmericaAfricaAsia);
        ss17.setSpese(new BigDecimal("9.00"));
        sss.add(ss17);
    
        SpesaSpedizione ss18 = new SpesaSpedizione();
        ss18.setRangeSpeseSpedizione(RangeSpeseSpedizione.Da350grA1Kg);
        ss18.setAreaSpedizione(AreaSpedizione.AmericaAfricaAsia);
        ss18.setSpese(new BigDecimal("13.50"));
        sss.add(ss18);
    
        SpesaSpedizione ss19 = new SpesaSpedizione();
        ss19.setRangeSpeseSpedizione(RangeSpeseSpedizione.Da1KgA2Kg);
        ss19.setAreaSpedizione(AreaSpedizione.AmericaAfricaAsia);
        ss19.setSpese(new BigDecimal("23.00"));
        sss.add(ss19);
    
        return sss;
    
    }

    public static Pubblicazione getMessaggio() {
        Pubblicazione p = new Pubblicazione("Messaggio",
                                                    TipoPubblicazione.MENSILE);
        p.setDescrizione("Il Messaggio del Cuore di Gesù");
        p.setActive(true);
        p.setAutore("AAVV");
        p.setEditore("ADP");
        p.setGrammi(80);
    
        p.setCostoUnitario(new BigDecimal("1.50"));
        p.setAbbonamento(new BigDecimal("15.00"));
        p.setAbbonamentoWeb(new BigDecimal("12.00"));
        p.setAbbonamentoConSconto(new BigDecimal("15.00"));
        p.setAbbonamentoSostenitore(new BigDecimal("50.00"));
    
        p.setGen(true);
        p.setFeb(true);
        p.setMar(true);
        p.setApr(true);
        p.setMag(true);
        p.setGiu(true);
        p.setLug(true);
        p.setAgo(false);
        p.setSet(true);
        p.setOtt(true);
        p.setNov(true);
        p.setDic(true);
    
            
        
        
        return p;
    }

    public static Pubblicazione getLodare() {
        Pubblicazione p = new Pubblicazione("Lodare",
                                                 TipoPubblicazione.MENSILE);
        p.setDescrizione("Lodare e Servire");
        p.setActive(true);
        p.setAutore("AAVV");
        p.setEditore("ADP");
        p.setGrammi(160);
        
        p.setCostoUnitario(new BigDecimal("2.00"));
        p.setAbbonamento(new BigDecimal("20.00"));
        p.setAbbonamentoWeb(new BigDecimal("18.00"));
        p.setAbbonamentoConSconto(new BigDecimal("18.00"));
        p.setAbbonamentoSostenitore(new BigDecimal("50.00"));
    
        p.setGen(true);
        p.setFeb(true);
        p.setMar(true);
        p.setApr(true);
        p.setMag(true);
        p.setGiu(true);
        p.setLug(true);
        p.setAgo(true);
        p.setSet(true);
        p.setOtt(true);
        p.setNov(true);
        p.setDic(true);
    
    
    
        
        
        return p;
        
        
        
    }

    public static Pubblicazione getBlocchetti() {
        Pubblicazione p = new Pubblicazione("Blocchetti",
                                                     TipoPubblicazione.SEMESTRALE);
        p.setDescrizione("Biglietti Mensili");
        p.setActive(true);
        p.setAutore("AAVV");
        p.setEditore("ADP");
        p.setGrammi(60);
    
        p.setCostoUnitario(new BigDecimal("3.50"));
        p.setAbbonamento(new BigDecimal("7.00"));
        p.setAbbonamentoWeb(new BigDecimal("5.00"));
        p.setAbbonamentoConSconto(new BigDecimal("5.60"));
        p.setAbbonamentoSostenitore(new BigDecimal("20.00"));
    
        p.setGen(true);
        p.setLug(true);
        p.setAnticipoSpedizione(3);
        return p;
    }

    public static Pubblicazione getEstratti() {
        Pubblicazione p = new Pubblicazione("Estratti",
                                                   TipoPubblicazione.ANNUALE);
        p.setDescrizione("Serie di dodici Manifesti del Messaggio");
        p.setActive(true);
        p.setAutore("AAVV");
        p.setEditore("ADP");
        p.setGrammi(100);
        
        p.setCostoUnitario(new BigDecimal("10.00"));
        p.setAbbonamento(new BigDecimal("10.00"));
        p.setAbbonamentoWeb(new BigDecimal("8.00"));
        p.setAbbonamentoConSconto(new BigDecimal("10.00"));
        p.setAbbonamentoSostenitore(new BigDecimal("10.00"));
    
        p.setGen(true);
        p.setAnticipoSpedizione(3);
    
        return p;
    }


    public static Anagrafica getAR() {
        Anagrafica ar = SmdHelper.getAnagraficaBy("Antonio", "Russo");
        ar.setDiocesi(Diocesi.DIOCESI116);
        ar.setIndirizzo("");
        ar.setCitta("");
        ar.setEmail("ar@arsinfo.it");
        ar.setPaese(Paese.IT);
        ar.setTelefono("+3902000009");
        ar.setTitolo(TitoloAnagrafica.Vescovo);
        ar.setRegioneVescovi(Regione.LOMBARDIA);
        return ar;
    }

    public static Anagrafica getDiocesiMi() {
        Anagrafica ar = SmdHelper.getAnagraficaBy("", "Arci Diocesi Milano");
        ar.setDiocesi(Diocesi.DIOCESI116);
        ar.setIndirizzo("Piazza Duomo 1");
        ar.setCitta("Milano");
        ar.setProvincia(Provincia.MI);
        ar.setCap("20100");
        ar.setEmail("milano@arsinfo.it");
        ar.setPaese(Paese.IT);
        ar.setTelefono("+3902000001");
        ar.setTitolo(TitoloAnagrafica.Diocesi);
        return ar;
    }

    public static Anagrafica getGP() {
        Anagrafica gp = SmdHelper.getAnagraficaBy("Gabriele", "Pizzo");
        gp.setDiocesi(Diocesi.DIOCESI116);
        gp.setIndirizzo("Piazza Sant'Ambrogio 1");
        gp.setCitta("Milano");
        gp.setProvincia(Provincia.MI);
        gp.setCap("20110");
        gp.setPaese(Paese.IT);
        gp.setEmail("gp@arsinfo.it");
        gp.setTelefono("+3902000010");
        return gp;
    }

    public static Storico getStoricoBy(
            Anagrafica intestatario, 
            Anagrafica destinatario, 
            Pubblicazione pubblicazione, 
            int numero, 
            boolean contrassegno,
            TipoAbbonamentoRivista omaggio,
            InvioSpedizione invioSpedizione
        ) {
        Storico storico = new Storico(); 
        storico.setIntestatario(intestatario);
        storico.setDestinatario(destinatario);
        storico.setPubblicazione(pubblicazione);
        storico.setNumero(numero);
        storico.setTipoAbbonamentoRivista(omaggio);
        storico.setContrassegno(contrassegno);
        storico.setInvioSpedizione(invioSpedizione);
        return storico;
    }

    public static Anagrafica getAnagraficaBy(String nome, String cognome) {
        Anagrafica anagrafica = new Anagrafica();
        anagrafica.setNome(nome);
        anagrafica.setDenominazione(cognome);
        anagrafica.setCodeLineBase(Anagrafica.generaCodeLineBase());
        return anagrafica;
    }

    public static Abbonamento getAbbonamentoBy(
            Anagrafica intestatario, 
            Anno anno, 
            boolean contrassegno
            ) {
    
        final Abbonamento abb = new Abbonamento();
        abb.setAnno(anno);
        abb.setContrassegno(contrassegno);
        abb.setIntestatario(intestatario);
        abb.setCodeLine(Abbonamento.generaCodeLine(anno));
        return abb;   
    }

}
