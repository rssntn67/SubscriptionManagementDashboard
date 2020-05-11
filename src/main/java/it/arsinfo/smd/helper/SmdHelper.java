package it.arsinfo.smd.helper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.AreaSpedizione;
import it.arsinfo.smd.data.Cassa;
import it.arsinfo.smd.data.Ccp;
import it.arsinfo.smd.data.Diocesi;
import it.arsinfo.smd.data.Invio;
import it.arsinfo.smd.data.InvioSpedizione;
import it.arsinfo.smd.data.Mese;
import it.arsinfo.smd.data.Paese;
import it.arsinfo.smd.data.Provincia;
import it.arsinfo.smd.data.RangeSpeseSpedizione;
import it.arsinfo.smd.data.Regione;
import it.arsinfo.smd.data.TipoEstrattoConto;
import it.arsinfo.smd.data.TipoPubblicazione;
import it.arsinfo.smd.data.TitoloAnagrafica;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.EstrattoConto;
import it.arsinfo.smd.entity.Incasso;
import it.arsinfo.smd.entity.Nota;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.entity.SpesaSpedizione;
import it.arsinfo.smd.entity.Storico;
import it.arsinfo.smd.entity.Versamento;
import it.arsinfo.smd.service.Smd;

public class SmdHelper {

    public static Incasso getIncassoTelematici() {
        String riepilogo1="4000063470009171006              999000000010000000015000000000100000000150000000000000000000000                                                                                                        \n";
        Set<String> versamenti1= new HashSet<>();
        versamenti1.add("0000000000000010000634700091710046740000001500055111092171006000000018000792609CCN                                                                                                                      \n");
        return Smd.generaIncasso(versamenti1, riepilogo1); 
        
    }

    public static Incasso getIncassoVenezia() {
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
    
        return Smd.generaIncasso(versamenti2, riepilogo2);
    
    }

    public static Incasso getIncassoFirenze() {
        String riepilogo3="5000063470009171006              999000000060000000201000000000500000001810000000001000000002000                                                                                                        \n";
        Set<String> versamenti3= new HashSet<>();
        versamenti3.add("0854174400000090000634700091710046740000001000055379072171006000000018000686968DIN                                                                                                                      \n");
        versamenti3.add("0860359800000100000634700091710056740000001500055239072171006000000018000198318DIN                                                                                                                      \n");
        versamenti3.add("0858363300000110000634700091710056740000001500055826052171006000000018000201449DIN                                                                                                                      \n");
        versamenti3.add("0860441300000120000634700091710056740000003300055820042171006000000018000633491DIN                                                                                                                      \n");
        versamenti3.add("0860565700000130000634700091710056740000010800055917062171006000000018000196500DIN                                                                                                                      \n");
        versamenti3.add("0855941199999990000634700091710041230000002000055681052171006727705568105003308DIN                                                                                                                      \n");
    
        return Smd.generaIncasso(versamenti3, riepilogo3);
    }

    public static Incasso getIncassoBari() {
        
        String riepilogo4="7000063470009171006              999000000070000000447500000000400000001750000000003000000027250                                                                                                        \n";
        Set<String> versamenti4= new HashSet<>();
        versamenti4.add("0873460200000140000634700091710056740000001200053057032171006000000018000106227DIN                                                                                                                      \n");
        versamenti4.add("0874263500000150000634700091710056740000003600009019032171006000000018000077317DIN                                                                                                                      \n");
        versamenti4.add("0875677100000160000634700091710056740000006000029079022171006000000018000125029DIN                                                                                                                      \n");
        versamenti4.add("0871026300000170000634700091710046740000006700040366032171006000000018000065383DIN                                                                                                                      \n");
        versamenti4.add("0862740599999990000634700091710044510000000750002066172171006727700206617006437DIN                                                                                                                      \n");
        versamenti4.add("0857504199999990000634700091710034510000004000040016062171006727604001606035576DIN                                                                                                                      \n");
        versamenti4.add("0866089199999990000634700091710044510000022500018160052171006727701816005010892DIN                                                                                                                      \n");
        
        return Smd.generaIncasso(versamenti4, riepilogo4);
    }

    public static Incasso getIncassoByImportoAndCodeLine(BigDecimal importo,String codeLine) {
        Incasso incasso5 = new Incasso();
        incasso5.setCassa(Cassa.Contrassegno);
        incasso5.setCcp(Ccp.DUE);
        
        Versamento versamentoIncasso5 = new Versamento(incasso5,importo);
        versamentoIncasso5.setCodeLine(codeLine);
        versamentoIncasso5.setDataPagamento(incasso5.getDataContabile());
        incasso5.addVersamento(versamentoIncasso5);
        Smd.calcoloImportoIncasso(incasso5);
        return incasso5;
    }

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
        ss11.setRangeSpeseSpedizione(RangeSpeseSpedizione.Da100grA200gr);;
        ss11.setAreaSpedizione(AreaSpedizione.EuropaBacinoMediterraneo);
        ss11.setSpese(new BigDecimal("5.70"));
        sss.add(ss11);
    
        SpesaSpedizione ss12 = new SpesaSpedizione();
        ss12.setRangeSpeseSpedizione(RangeSpeseSpedizione.Da200grA350gr);;
        ss12.setAreaSpedizione(AreaSpedizione.EuropaBacinoMediterraneo);
        ss12.setSpese(new BigDecimal("6.50"));
        sss.add(ss12);
    
        SpesaSpedizione ss13 = new SpesaSpedizione();
        ss13.setRangeSpeseSpedizione(RangeSpeseSpedizione.Da350grA1Kg);;
        ss13.setAreaSpedizione(AreaSpedizione.EuropaBacinoMediterraneo);
        ss13.setSpese(new BigDecimal("8.50"));
        sss.add(ss13);
    
        SpesaSpedizione ss14 = new SpesaSpedizione();
        ss14.setRangeSpeseSpedizione(RangeSpeseSpedizione.Da1KgA2Kg);;
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

    public static Anagrafica getT001() {
        Anagrafica a = SmdHelper.getAnagraficaBy("Telematici", "001");
        a.setDiocesi(Diocesi.DIOCESI111);
        a.setIndirizzo("tel001");
        a.setCitta("tel001");
        a.setEmail("tel001@arsinfo.it");
        a.setPaese(Paese.IT);
        a.setTelefono("+3902000009");
        a.setTitolo(TitoloAnagrafica.Egregio);
        a.setRegioneVescovi(Regione.PIEMONTEVALLEDAOSTA);
        return a;        
    }

    public static Anagrafica getVe002() {
        Anagrafica a = SmdHelper.getAnagraficaBy("Venezia", "002");
        a.setDiocesi(Diocesi.DIOCESI003);
        a.setIndirizzo("ve002");
        a.setCitta("ve002");
        a.setEmail("ve002@arsinfo.it");
        a.setPaese(Paese.IT);
        a.setTelefono("+3902000009");
        a.setTitolo(TitoloAnagrafica.Egregio);
        a.setRegioneVescovi(Regione.TRIVENETO);
        return a;        
    }

    public static Anagrafica getVe003() {
        Anagrafica a = SmdHelper.getAnagraficaBy("Venezia", "003");
        a.setDiocesi(Diocesi.DIOCESI003);
        a.setIndirizzo("ve003");
        a.setCitta("ve003");
        a.setEmail("ve003@arsinfo.it");
        a.setPaese(Paese.IT);
        a.setTelefono("+3902000009");
        a.setTitolo(TitoloAnagrafica.Egregio);
        a.setRegioneVescovi(Regione.TRIVENETO);
        return a;        
    }

    public static Anagrafica getVe004() {
        Anagrafica a = SmdHelper.getAnagraficaBy("Venezia", "004");
        a.setDiocesi(Diocesi.DIOCESI003);
        a.setIndirizzo("ve004");
        a.setCitta("ve004");
        a.setEmail("ve004@arsinfo.it");
        a.setPaese(Paese.IT);
        a.setTelefono("+3902000009");
        a.setTitolo(TitoloAnagrafica.Egregio);
        a.setRegioneVescovi(Regione.TRIVENETO);
        return a;        
    }

    public static Anagrafica getVe005() {
        Anagrafica a = SmdHelper.getAnagraficaBy("Venezia", "005");
        a.setDiocesi(Diocesi.DIOCESI003);
        a.setIndirizzo("ve005");
        a.setCitta("ve005");
        a.setEmail("ve005@arsinfo.it");
        a.setPaese(Paese.IT);
        a.setTelefono("+3902000009");
        a.setTitolo(TitoloAnagrafica.Egregio);
        a.setRegioneVescovi(Regione.TRIVENETO);
        return a;        
    }

    public static Anagrafica getVe006() {
        Anagrafica a = SmdHelper.getAnagraficaBy("Venezia", "006");
        a.setDiocesi(Diocesi.DIOCESI003);
        a.setIndirizzo("ve006");
        a.setCitta("ve006");
        a.setEmail("ve006@arsinfo.it");
        a.setPaese(Paese.IT);
        a.setTelefono("+3902000009");
        a.setTitolo(TitoloAnagrafica.Egregio);
        a.setRegioneVescovi(Regione.TRIVENETO);
        return a;        
    }

    public static Anagrafica getVe007() {
        Anagrafica a = SmdHelper.getAnagraficaBy("Venezia", "007");
        a.setDiocesi(Diocesi.DIOCESI003);
        a.setIndirizzo("ve007");
        a.setCitta("ve007");
        a.setEmail("ve007@arsinfo.it");
        a.setPaese(Paese.IT);
        a.setTelefono("+3902000009");
        a.setTitolo(TitoloAnagrafica.Egregio);
        a.setRegioneVescovi(Regione.TRIVENETO);
        return a;        
    }

    public static Anagrafica getVe008() {
        Anagrafica a = SmdHelper.getAnagraficaBy("Venezia", "008");
        a.setDiocesi(Diocesi.DIOCESI003);
        a.setIndirizzo("ve008");
        a.setCitta("ve008");
        a.setEmail("ve008@arsinfo.it");
        a.setPaese(Paese.IT);
        a.setTelefono("+3902000009");
        a.setTitolo(TitoloAnagrafica.Egregio);
        a.setRegioneVescovi(Regione.TRIVENETO);
        return a;        
    }

    public static Anagrafica getFi009() {
        Anagrafica a = SmdHelper.getAnagraficaBy("Firenze", "009");
        a.setDiocesi(Diocesi.DIOCESI053);
        a.setIndirizzo("fi009");
        a.setCitta("fi009");
        a.setEmail("fi009@arsinfo.it");
        a.setPaese(Paese.IT);
        a.setTelefono("+3902000009");
        a.setTitolo(TitoloAnagrafica.Egregio);
        a.setRegioneVescovi(Regione.TOSCANA);
        return a;        
    }

    public static Anagrafica getFi010() {
        Anagrafica a = SmdHelper.getAnagraficaBy("Firenze", "010");
        a.setDiocesi(Diocesi.DIOCESI053);
        a.setIndirizzo("fi010");
        a.setCitta("fi010");
        a.setEmail("fi010@arsinfo.it");
        a.setPaese(Paese.IT);
        a.setTelefono("+3902000009");
        a.setTitolo(TitoloAnagrafica.Egregio);
        a.setRegioneVescovi(Regione.TOSCANA);
        return a;        
    }

    public static Anagrafica getFi011() {
        Anagrafica a = SmdHelper.getAnagraficaBy("Firenze", "011");
        a.setDiocesi(Diocesi.DIOCESI053);
        a.setIndirizzo("fi011");
        a.setCitta("fi011");
        a.setEmail("fi011@arsinfo.it");
        a.setPaese(Paese.IT);
        a.setTelefono("+3902000009");
        a.setTitolo(TitoloAnagrafica.Egregio);
        a.setRegioneVescovi(Regione.TOSCANA);
        return a;        
    }

    public static Anagrafica getFi012() {
        Anagrafica a = SmdHelper.getAnagraficaBy("Firenze", "012");
        a.setDiocesi(Diocesi.DIOCESI053);
        a.setIndirizzo("fi012");
        a.setCitta("fi012");
        a.setEmail("fi012@arsinfo.it");
        a.setPaese(Paese.IT);
        a.setTelefono("+3902000009");
        a.setTitolo(TitoloAnagrafica.Egregio);
        a.setRegioneVescovi(Regione.TOSCANA);
        return a;        
    }

    public static Anagrafica getFi013() {
        Anagrafica a = SmdHelper.getAnagraficaBy("Firenze", "013");
        a.setDiocesi(Diocesi.DIOCESI053);
        a.setIndirizzo("fi013");
        a.setCitta("fi013");
        a.setEmail("fi013@arsinfo.it");
        a.setPaese(Paese.IT);
        a.setTelefono("+3902000009");
        a.setTitolo(TitoloAnagrafica.Egregio);
        a.setRegioneVescovi(Regione.PUGLIA);
        return a;        
    }

    public static Anagrafica getBa014() {
        Anagrafica a = SmdHelper.getAnagraficaBy("Bari", "014");
        a.setDiocesi(Diocesi.DIOCESI053);
        a.setIndirizzo("ba014");
        a.setCitta("ba014");
        a.setEmail("ba014@arsinfo.it");
        a.setPaese(Paese.IT);
        a.setTelefono("+3902000009");
        a.setTitolo(TitoloAnagrafica.Egregio);
        a.setRegioneVescovi(Regione.PUGLIA);
        return a;        
    }

    public static Anagrafica getBa015() {
        Anagrafica a = SmdHelper.getAnagraficaBy("Bari", "015");
        a.setDiocesi(Diocesi.DIOCESI053);
        a.setIndirizzo("ba015");
        a.setCitta("ba015");
        a.setEmail("ba015@arsinfo.it");
        a.setPaese(Paese.IT);
        a.setTelefono("+3902000009");
        a.setTitolo(TitoloAnagrafica.Egregio);
        a.setRegioneVescovi(Regione.PUGLIA);
        return a;        
    }

    public static Anagrafica getBa016() {
        Anagrafica a = SmdHelper.getAnagraficaBy("Bari", "016");
        a.setDiocesi(Diocesi.DIOCESI053);
        a.setIndirizzo("ba016");
        a.setCitta("ba016");
        a.setEmail("ba016@arsinfo.it");
        a.setPaese(Paese.IT);
        a.setTelefono("+3902000009");
        a.setTitolo(TitoloAnagrafica.Egregio);
        a.setRegioneVescovi(Regione.PUGLIA);
        return a;        
    }

    public static Anagrafica getBa017() {
        Anagrafica a = SmdHelper.getAnagraficaBy("Bari", "017");
        a.setDiocesi(Diocesi.DIOCESI053);
        a.setIndirizzo("ba017");
        a.setCitta("ba017");
        a.setEmail("ba017@arsinfo.it");
        a.setPaese(Paese.IT);
        a.setTelefono("+3902000009");
        a.setTitolo(TitoloAnagrafica.Egregio);
        a.setRegioneVescovi(Regione.PUGLIA);
        return a;        
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

    public static Anagrafica getMP() {
        Anagrafica mp = SmdHelper.getAnagraficaBy("Matteo", "Paro");
        mp.setDiocesi(Diocesi.DIOCESI168);
        mp.setTitolo(TitoloAnagrafica.Religioso);
        mp.setIndirizzo("Piazza del Gesu' 1");
        mp.setCitta("Roma");
        mp.setProvincia(Provincia.RM);
        mp.setCap("00192");
        mp.setPaese(Paese.IT);
        mp.setEmail("mp@arsinfo.it");
        mp.setTelefono("+3906000020");
        return mp;
    }

    public static Anagrafica getDP() {
        Anagrafica dp = SmdHelper.getAnagraficaBy("Davide", "Palma");
        dp.setDiocesi(Diocesi.DIOCESI168);
        dp.setTitolo(TitoloAnagrafica.Sacerdote);
        dp.setIndirizzo("Piazza Navona 3, 00100 Roma");
        dp.setCitta("Roma");
        dp.setProvincia(Provincia.RM);
        dp.setCap("00195");
        dp.setPaese(Paese.IT);
        dp.setEmail("dp@arsinfo.it");
        dp.setTelefono("+3906000020");
        dp.setDirettoreDiocesiano(true);
        dp.setRegioneDirettoreDiocesano(Regione.LAZIO);
        return dp;
    }

    public static Anagrafica getMS() {
        Anagrafica ms = SmdHelper.getAnagraficaBy("Michele", "Santoro");
        ms.setDiocesi(Diocesi.DIOCESI126);
        ms.setIndirizzo("Via Duomo 10");
        ms.setCitta("Napoli");
        ms.setProvincia(Provincia.NA);
        ms.setCap("80135");
        ms.setPaese(Paese.IT);
        ms.setEmail("ms@arsinfo.it");
        ms.setTelefono("+39081400022");
        return ms;
    }

    public static Anagrafica getPS() {
        Anagrafica ps = SmdHelper.getAnagraficaBy("Pasqualina", "Santoro");
        ps.setDiocesi(Diocesi.DIOCESI126);
        ps.setIndirizzo("Piazza Dante 10");
        ps.setCitta("Napoli");
        ps.setProvincia(Provincia.NA);
        ps.setCap("80135");
        ps.setPaese(Paese.IT);
        ps.setEmail("arsinfo@adp.it");
        ps.setTelefono("+39081400023");
        return ps;
    }

    public static Storico getStoricoBy(
            Anagrafica intestatario, 
            Anagrafica destinatario, 
            Pubblicazione pubblicazione, 
            int numero, 
            Cassa cassa,
            TipoEstrattoConto omaggio,
            Invio invio,
            InvioSpedizione invioSpedizione
        ) {
        Storico storico = new Storico(); 
        storico.setIntestatario(intestatario);
        storico.setDestinatario(destinatario);
        storico.setPubblicazione(pubblicazione);
        storico.setNumero(numero);
        storico.setTipoEstrattoConto(omaggio);
        storico.setCassa(cassa);
        storico.setInvio(invio);
        storico.setInvioSpedizione(invioSpedizione);
        return storico;
    }

    public static Nota getNota(Storico storico) {
        Nota nota= new Nota(storico);
        nota.setDescription("Importato da Ad Hoc: " + storico.toString());
        return nota;
    }

    public static Anagrafica getAnagraficaBy(String nome, String cognome) {
        Anagrafica anagrafica = new Anagrafica();
        anagrafica.setNome(nome);
        anagrafica.setDenominazione(cognome);
        anagrafica.setCodeLineBase(Anagrafica.generaCodeLineBase());
        return anagrafica;
    }

    public static Abbonamento getAbbonamentoBy(Anagrafica intestatario) {
        Abbonamento abbonamento = new Abbonamento();
        abbonamento.setIntestatario(intestatario);
        return abbonamento;
    }

    public static Abbonamento getAbbonamentoBy(
            Anagrafica intestatario, 
            Anno anno, 
            Cassa cassa
            ) {
    
        final Abbonamento abb = new Abbonamento();
        abb.setAnno(anno);
        abb.setCassa(cassa);
        abb.setIntestatario(intestatario);
        abb.setCodeLine(Abbonamento.generaCodeLine(anno));
        return abb;   
    }

    public static EstrattoConto addEC(Abbonamento abb, Pubblicazione pubblicazione,
        Integer numero, BigDecimal importo) {
        EstrattoConto ec = new EstrattoConto();
        ec.setAbbonamento(abb);
        ec.setPubblicazione(pubblicazione);
        ec.setDestinatario(abb.getIntestatario());
        ec.setNumero(numero);
        ec.setImporto(importo);
        ec.setAnnoInizio(Anno.ANNO2017);
        ec.setAnnoFine(Anno.ANNO2017);
        ec.setMeseInizio(Mese.GENNAIO);
        ec.setMeseFine(Mese.DICEMBRE);
        return ec;
    }

}
