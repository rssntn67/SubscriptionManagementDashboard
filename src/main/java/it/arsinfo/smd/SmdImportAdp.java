package it.arsinfo.smd;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.AreaSpedizione;
import it.arsinfo.smd.data.Cassa;
import it.arsinfo.smd.data.Diocesi;
import it.arsinfo.smd.data.Invio;
import it.arsinfo.smd.data.InvioSpedizione;
import it.arsinfo.smd.data.Paese;
import it.arsinfo.smd.data.Provincia;
import it.arsinfo.smd.data.TipoEstrattoConto;
import it.arsinfo.smd.data.TipoPubblicazione;
import it.arsinfo.smd.data.TitoloAnagrafica;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Campagna;
import it.arsinfo.smd.entity.CampagnaItem;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.entity.Storico;
import it.arsinfo.smd.repository.AbbonamentoDao;
import it.arsinfo.smd.repository.AnagraficaDao;
import it.arsinfo.smd.repository.CampagnaDao;
import it.arsinfo.smd.repository.EstrattoContoDao;
import it.arsinfo.smd.repository.IncassoDao;
import it.arsinfo.smd.repository.NotaDao;
import it.arsinfo.smd.repository.OperazioneDao;
import it.arsinfo.smd.repository.PubblicazioneDao;
import it.arsinfo.smd.repository.SpedizioneDao;
import it.arsinfo.smd.repository.SpedizioneItemDao;
import it.arsinfo.smd.repository.SpesaSpedizioneDao;
import it.arsinfo.smd.repository.StoricoDao;
import it.arsinfo.smd.repository.VersamentoDao;

public class SmdImportAdp extends SmdLoadSampleData {
    public SmdImportAdp(SmdService smdService, AnagraficaDao anagraficaDao,
            StoricoDao storicoDao, NotaDao notaDao,
            PubblicazioneDao pubblicazioneDao,
            SpesaSpedizioneDao spesaSpedizioneDao,
            AbbonamentoDao abbonamentoDao, EstrattoContoDao estrattoContoDao,
            SpedizioneDao spedizioneDao, SpedizioneItemDao spedizioneItemDao,
            CampagnaDao campagnaDao, IncassoDao incassoDao,
            VersamentoDao versamentoDao, OperazioneDao operazioneDao
            ) {
        super(smdService, anagraficaDao, storicoDao, notaDao, pubblicazioneDao,
              spesaSpedizioneDao, abbonamentoDao, estrattoContoDao, spedizioneDao,
              spedizioneItemDao, campagnaDao, incassoDao, versamentoDao,
              operazioneDao);
    }
    private static final Logger log = LoggerFactory.getLogger(Smd.class);

    public static final String CAMPAGNA_2020 = "data/CA2020COMPLETA.xls";

    public static final String ARCHIVIO_CLIENTI = "data/ARCHIVIOCLIENTI2020.xls";
    public static final String ELENCO_ABBONATI = "data/ELENCOABBONATI2020.xls";
    public static final String ELENCO_BENEFICIARI_2020 = "data/BENEFICIARI2020.xls";
    public static final String ELENCO_ABBONATI_ESTERO = "data/ABBONATIESTERO2020.xls";
    public static final String ELENCO_ABBONATI_ITA_ESTERO = "data/ABBONATITALIABENEFESTERO2020.xls";
    
    public static final String ELENCO_OMAGGIO_MESSAGGIO = "data/ELENCOOMAGGIOMESSAGGIO2020.xls";
    public static final String ELENCO_OMAGGIO_GESUITI_MESSAGGIO = "data/ELENCOOMAGGIOGESUITIMESSAGGIO2020.xls";
    public static final String ELENCO_OMAGGIO_BLOCCHETTI = "data/ELENCOOMAGGIOBLOCCHETTI2020.xls";
    public static final String ELENCO_OMAGGIO_GESUITI_BLOCCHETTI = "data/ELENCOOMAGGIOGESUITIBLOCCHETTI2020.xls";
    public static final String ELENCO_OMAGGIO_LODARE = "data/ELENCOOMAGGIOLODARE2020.xls";
    public static final String ELENCO_OMAGGIO_GESUITI_MANIFESTI = "data/ELENCOOMAGGIOGESUITIMANIFESTI2020.xls";
    
    public static final String CATEGORIA_BM_CASSA = "data/CATEGORIABMCASSA.xls";

    public static List<Storico> getStoriciFromOmaggio(List<Row> omaggiorows,
                Map<String,Anagrafica> anagraficaMap,
                Map<String,Anagrafica> omaggioMap, Pubblicazione p, 
                InvioSpedizione invioSped,
                TipoEstrattoConto tipo) throws UnsupportedOperationException {
        final List<Storico> storici = new ArrayList<>();
        for (Row row: omaggiorows) {
            Anagrafica intestatario=null;
            String denominazione = getNominativoFromOmaggioRow(row);
            Provincia provincia = getProvinciaFromOmaggioRow(row);
            for (String cod: omaggioMap.keySet()) {
                Anagrafica a = omaggioMap.get(cod);
                if (denominazione.equals(a.getDenominazione()) 
                        && provincia == a.getProvincia()) {
                    intestatario = anagraficaMap.get(cod);
                    break;
                }
            }
            if (intestatario == null) {
                throw new UnsupportedOperationException("Intestatario non trovato");
            }
            int qnt = getQuantitaFromOmaggioRow(row);
            storici.add(SmdHelper.getStoricoBy(intestatario, intestatario, p, qnt, Cassa.Ccp, tipo, Invio.Destinatario, invioSped));
        }
        return storici;
    }
    public static Map<String,BigDecimal> fixSpeseBeneficiari(
            List<Row> abrows,
            Pubblicazione messaggio,
            Pubblicazione lodare,
            Pubblicazione blocchetti, 
            Pubblicazione estratti) throws UnsupportedOperationException {

        Map<String,BigDecimal> speseMap = new HashMap<>();
        for (Row row : abrows) {
            String ancodice = getAncodiceFromBeneficiari(row);
            BigDecimal prezzo = getPrezzoFromBeneficiari(row);
            if (messaggio.getAbbonamento().compareTo(prezzo) == 0) {
                continue;
            } else if (blocchetti.getAbbonamento().compareTo(prezzo) == 0) {
                continue;
            } else if (lodare.getAbbonamento().compareTo(prezzo) == 0) {
                continue;
            } else if (estratti.getAbbonamento().compareTo(prezzo) == 0) {
                continue;
            } else if (blocchetti.getAbbonamentoConSconto().compareTo(prezzo) == 0) {
                continue;
            }
            String bancodice = getBancodiceFromBeneficiari(row);
            if (!bancodice.trim().equals("")) {
                speseMap.put(bancodice, prezzo);
            } else {
                speseMap.put(ancodice, prezzo);                
            }
        }
        return speseMap;
    }
    
    public static List<Storico> 
        getStoriciFromBeneficiari2010(
            List<Row> abrows,
            Map<String,Anagrafica> anagraficaMap,
            Pubblicazione messaggio,
            Pubblicazione lodare,
            Pubblicazione blocchetti, 
            Pubblicazione estratti, Set<String> bmCassa) throws UnsupportedOperationException {
        final List<Storico> storici = new ArrayList<>();
        for (Row row : abrows) {
            String ancodice = getAncodiceFromBeneficiari(row);
            int qnt = getQuantitaFromBeneficiari(row);
            BigDecimal prezzo = getPrezzoFromBeneficiari(row);
            Anagrafica destinatario = anagraficaMap.get(ancodice);
            Pubblicazione p;
            TipoEstrattoConto tipo= TipoEstrattoConto.Ordinario;           
            InvioSpedizione invioSped = InvioSpedizione.Spedizioniere;
            if (messaggio.getAbbonamento().compareTo(prezzo) == 0) {
                p=messaggio;
            } else if (blocchetti.getAbbonamento().compareTo(prezzo) == 0) {
                p=blocchetti;
                if (qnt >= 30) {
                    invioSped = InvioSpedizione.AdpSede;
                }
            } else if (lodare.getAbbonamento().compareTo(prezzo) == 0) {
                p=lodare;
            } else if (estratti.getAbbonamento().compareTo(prezzo) == 0) {
                p=estratti;
                if (qnt >= 2) {
                    invioSped = InvioSpedizione.AdpSede;
                }
            } else if (blocchetti.getAbbonamentoConSconto().compareTo(prezzo) == 0) {
                p=blocchetti;
                tipo = TipoEstrattoConto.Scontato;
                if (qnt >= 30) {
                    invioSped = InvioSpedizione.AdpSede;
                }
            } else {
                continue;
            }
            String bancodice = getBancodiceFromBeneficiari(row);
            Anagrafica intestatario = destinatario;
            if (!bancodice.trim().equals("")) {
                intestatario = anagraficaMap.get(bancodice);
            }
            if (bmCassa.contains(ancodice)) {
                invioSped = InvioSpedizione.AdpSede;
            }
            storici.add(SmdHelper.getStoricoBy(intestatario, destinatario, p, qnt, Cassa.Ccp, tipo, Invio.Destinatario, invioSped));
        }
        
        return storici;
    }
    
    public static Map<String,BigDecimal> fixSpeseEstero(Map<String,Row> aeRowMap){
        Map<String,BigDecimal> speseMap = new HashMap<>();
        for (String cod: aeRowMap.keySet()) {
            speseMap.put(cod, getSpeseAbbEstero(aeRowMap.get(cod)));
        }
        return speseMap;
    }
    
    public static List<Storico> 
        getStoriciFromEstero2010(
            Map<String,Row> aeRowMap,
            Map<String,Anagrafica> anagraficaMap,
            Pubblicazione messaggio,
            Pubblicazione lodare,
            Pubblicazione blocchetti, 
            Pubblicazione estratti) throws UnsupportedOperationException 
    {
        
        final List<Storico> storici = new ArrayList<>();
        for (String ancod: aeRowMap.keySet()) {
            Anagrafica intestatario = anagraficaMap.get(ancod);
            Row row = aeRowMap.get(ancod);
            if (getIdEstrattiAbbEstero(row) == 3) {
                int qnt = getQtaEstrattiAbbEstero(row);
                storici.add(SmdHelper.getStoricoBy(intestatario, intestatario, estratti, qnt, Cassa.Ccp, TipoEstrattoConto.Ordinario, Invio.Destinatario, InvioSpedizione.AdpSede));
            }
            if (getIdBlocchettiAbbEstero(row) == 2) {
                int qnt = getQtaBlocchettiAbbEstero(row);
                storici.add(SmdHelper.getStoricoBy(intestatario, intestatario, blocchetti, qnt, Cassa.Ccp, TipoEstrattoConto.Ordinario, Invio.Destinatario, InvioSpedizione.AdpSede));
            }
            if (getIdMessaggioAbbEstero(row) == 1) {
                int qnt = getQtaMessaggioAbbEstero(row);
                storici.add(SmdHelper.getStoricoBy(intestatario, intestatario, messaggio, qnt, Cassa.Ccp, TipoEstrattoConto.Ordinario, Invio.Destinatario, InvioSpedizione.AdpSede));
            }
        }
        return storici;
    }
    
    public static Map<String,BigDecimal> fixSpeseItaEstero(List<Row> rows){
        Map<String,BigDecimal> speseMap = new HashMap<>();
        for (Row row: rows) {
            String ancodInt = getAnCodiceIntestatarioAbbonatiItaEstero(row);
            if (!speseMap.containsKey(ancodInt)) {
                speseMap.put(ancodInt, BigDecimal.ZERO);
            }
            BigDecimal value = speseMap.get(ancodInt).add(getSpeseFromAbbonatiItaEstero(row));
            speseMap.put(ancodInt, value);
        }
        return speseMap;
    }
    
    public static List<Storico> 
        getStoriciFromItaEstero2010(
            List<Row> rows,
            Map<String,Anagrafica> eaMap,
            Pubblicazione messaggio,
            Pubblicazione lodare,
            Pubblicazione blocchetti, 
            Pubblicazione estratti) throws UnsupportedOperationException {
    final List<Storico> storici = new ArrayList<>();
    for (Row row: rows) {
        String pubstr = getTestataFromAbbonatiItaEstero(row);
        Pubblicazione p;
        if (pubstr.equalsIgnoreCase("messaggio")) {
            p=messaggio;
        } else if (pubstr.equalsIgnoreCase("blocchetti")) {
            p=blocchetti;
        } else if (pubstr.equalsIgnoreCase("lodare")) {
            p=lodare;
        } else if (pubstr.equalsIgnoreCase("estratti")) {
            p=estratti;
        } else {
            throw new UnsupportedOperationException("Pubblicazione non trovata: " + pubstr);
        }
        int qnt = getQuantFromAbbonatiItaEstero(row);
        String ancodInt = getAnCodiceIntestatarioAbbonatiItaEstero(row);
        String ancodDst = getAnCodiceDestinatatarioAbbonatiItaEstero(row);
        storici.add( 
            SmdHelper
                .getStoricoBy(
                              eaMap.get(ancodInt), 
                              eaMap.get(ancodDst), 
                              p, 
                              qnt, 
                              Cassa.Ccp, 
                              TipoEstrattoConto.Ordinario, 
                              Invio.Destinatario, 
                              InvioSpedizione.AdpSede)
            );
    }        
    return storici;
    }
    public static List<Storico> 
        getStoriciFromCampagna2010(
                Map<String,Row> campagnarowMap,Map<String,Anagrafica> eaMap,
                Pubblicazione messaggio,Pubblicazione lodare,Pubblicazione blocchetti, Pubblicazione estratti ) {
        final List<Storico> storici = new ArrayList<>();

        campagnarowMap.keySet().forEach(cod ->
        {
            Row row = campagnarowMap.get(cod);
            Anagrafica a = eaMap.get(cod);
            Integer num = processRowCampagnaMessaggioNum(row);
            if ( num > 0) {
                storici.add(SmdHelper.getStoricoBy(a, a, messaggio, num, Cassa.Ccp, TipoEstrattoConto.Ordinario, Invio.Destinatario, InvioSpedizione.Spedizioniere));
            }
            num = processRowCampagnaLodareNum(row);
            if ( num > 0) {
                storici.add(SmdHelper.getStoricoBy(a, a, lodare, num, Cassa.Ccp, TipoEstrattoConto.Ordinario, Invio.Destinatario, InvioSpedizione.Spedizioniere));
            }
            num = processRowCampagnaBlocchettiNum(row);
            BigDecimal costo = processRowCampagnaBlocchettiCosto(row);
            if ( num > 0) {
                if ( costo.doubleValue() != blocchetti.getAbbonamento().multiply(new BigDecimal(num)).doubleValue()) {
                    storici.add(SmdHelper.getStoricoBy(a, a, blocchetti, num, Cassa.Ccp, TipoEstrattoConto.Scontato, Invio.Destinatario, InvioSpedizione.Spedizioniere));
                } else {
                    storici.add(SmdHelper.getStoricoBy(a, a, blocchetti, num, Cassa.Ccp, TipoEstrattoConto.Ordinario, Invio.Destinatario, InvioSpedizione.Spedizioniere));
                }
            }
            num = processRowCampagnaManifestiNum(row);
            if ( num > 0) {
                storici.add(SmdHelper.getStoricoBy(a, a, estratti, num, Cassa.Ccp, TipoEstrattoConto.Ordinario, Invio.Destinatario, InvioSpedizione.Spedizioniere));
            }
        });                
        return storici;
    }
    
    public static Integer getRowInteger(String value) {
        value = value.trim();
        try {
            return Integer.parseInt(value.trim());
        } catch (Exception e) {
            return 0;
        }
        
    }
    
    public static BigDecimal getRowBigDecimal(String value) {
        value = value.trim().replace(",", ".");
        if (StringUtils.isEmpty(value)) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(value);
    }
    

    public static AreaSpedizione getAreaSpedizione(String annazion) {
        if (annazion.equals("RSM") || annazion.equals("ITA") || annazion.equals("CVC")) {
                return AreaSpedizione.Italia;
        } else if (annazion.equals("GBR") || annazion.equals("FRA")
                    || annazion.equals("ROM") || annazion.equals("CZE")
                    || annazion.equals("HRV") || annazion.equals("LTU")
                    || annazion.equals("LTU") || annazion.equals("NLD")
                    || annazion.equals("LUX") || annazion.equals("CHE")
                    || annazion.equals("POL") || annazion.equals("ALB")
                    || annazion.equals("DEU") || annazion.equals("ESP")
                    || annazion.equals("CYP") || annazion.equals("BEL")
                    || annazion.equals("SVN") || annazion.equals("IRL")
                    || annazion.equals("ISR") || annazion.equals("PRT")
                    || annazion.equals("SYR") || annazion.equals("EGY")
                    || annazion.equals("TUR") || annazion.equals("LBY")
                    || annazion.equals("GRC")) {
            return AreaSpedizione.EuropaBacinoMediterraneo;
        } 
        return AreaSpedizione.AmericaAfricaAsia;

    }
    public static String getCapFromAnlocali(String anlocali) {
        return anlocali.substring(0,5);
    }

    public static String getCittaFromAnlocali(String anlocali) {
        if (anlocali.contains("(")) {
            return anlocali.substring(6,anlocali.indexOf("(")-1);
        }
        return anlocali.substring(6);
    }

    public static String getProvinciaFromAnlocali(String anlocali) {
        if (anlocali.contains("(") && anlocali.contains(")")) {
            return anlocali.substring(anlocali.indexOf("(")+1,anlocali.indexOf(")"));
        }
        if (anlocali.contains("(")) {
            return anlocali.substring(anlocali.indexOf("(")+1);
        }
        return "";
    }

    public static Provincia getProvincia(String sigla) {
        try {
            return Provincia.valueOf(sigla);
        } catch (Exception e) {
            return Provincia.ND;
        }
    }

    public static Anagrafica getAnagraficaByAncodcon(String pncodcon) throws UnsupportedOperationException {
        Anagrafica a = new Anagrafica();
        if (pncodcon.length() == 10) {
            if (pncodcon.startsWith("E")) {
                a.setCodeLineBase("10000" + pncodcon.substring(1));
            } else {
                a.setCodeLineBase("0000" + pncodcon);
            }
        } else if (pncodcon.length() == 14){
            a.setCodeLineBase(pncodcon);
        } 
        String codeLine = Abbonamento.generaCodeLine(Anno.ANNO2019,
                                                     a);
        if (!Abbonamento.checkCodeLine(codeLine)) {
            log.debug("----- codeLine invalid ------");
            log.debug("ANCODICE: " + pncodcon);
            log.debug("------------------------------");
            throw new UnsupportedOperationException();
        }
        return a;
    }
    
    public static void populateAnagraficaEstero(Anagrafica a, String ancodice, String destitolo,String andescri, String andescr2, String anindiri, String anindir1,
            String anlocali,String annazion) {
        a.setTitolo(TitoloAnagrafica.getByIntestazione(destitolo));        
        if (a.getTitolo() == TitoloAnagrafica.Nessuno) {
            log.debug("-----Anagrafica Titolo error------");
            log.debug(a.getCodeLineBase());
            log.debug("------------------------------");
            
            throw new UnsupportedOperationException();
        }
        a.setDenominazione(andescri);
        a.setNome(andescr2);
        a.setIndirizzo(anindiri);
        a.setCitta(anlocali);
        a.setDiocesi(Diocesi.DIOCESI000);
        a.setPaese(Paese.getBySigla(annazion));
        if (a.getPaese() == Paese.ND) {
            log.debug("-----Paese non Definito------");
            log.debug(ancodice);
            log.debug(andescri);
            log.debug(anindiri);
            log.debug(anlocali);
            log.debug(annazion);
            log.debug("------------------------------");
            
            throw new UnsupportedOperationException();
        }
        a.setAreaSpedizione(getAreaSpedizione(annazion));
    }
        
    public static void populateAnagraficaCampagna(Anagrafica a, String destitolo,
            String andescri, String andescr2, String anindiri, String anindir1,
            String anlocali) throws UnsupportedOperationException {

        a.setTitolo(TitoloAnagrafica.getByIntestazione(destitolo));        
        
        if (a.getTitolo() == TitoloAnagrafica.Nessuno) {
            log.debug("-----Anagrafica Titolo error------");
            log.debug(a.getCodeLineBase());
            log.debug(a.getCitta());
            log.debug(a.getCap());
            log.debug(a.getProvincia().getNome());
            log.debug("------------------------------");
            
            throw new UnsupportedOperationException();
        }

        a.setDenominazione(andescri);
        a.setNome(andescr2);
        
        a.setCitta(getCittaFromAnlocali(anlocali));
        a.setCap(getCapFromAnlocali(anlocali));
        a.setProvincia(getProvincia(getProvinciaFromAnlocali(anlocali)));
        
        try {
            Integer.getInteger(a.getCap());
        } catch (Exception e) {
            log.debug("-----Anagrafica CAP error------");
            log.debug(a.getCodeLineBase());
            log.debug(a.getCitta());
            log.debug(a.getCap());
            log.debug(a.getProvincia().getNome());
            log.debug("------------------------------");
            
            throw new UnsupportedOperationException();
        }

        
        a.setIndirizzo(anindiri);
        if (!anindir1.equals("")) {
            if (a.getCodeLineBase().equals("00000000012956")) {
                a.setIndirizzoSecondaRiga(null);
            } else if (a.getCodeLineBase().equals("00000000066055")) {
                    a.setIndirizzo("FRAZ.VIGHIZZOLO - VIA S. GIOVANNI 255");
            } else if (a.getCodeLineBase().equals("00000000020992")) {
                a.setIndirizzo("LOC.S.PIETRO DI BARBOZZA-STR.CHIESA 2");
            } else if (a.getCodeLineBase().equals("00000000061880")) {
                a.setIndirizzo("LOC.MAGLIO DI SOPRA - VIA MARZOTTO 2");
            } else {
                a.setIndirizzoSecondaRiga(anindir1);
            }
            log.debug("-----Fixed ANINDIR1------");
            log.debug(a.getCodeLineBase());
            log.debug("ANINDIRI: " + anindiri);
            log.debug("ANINDIR1: " + anindir1);
            log.debug("Indirizzo1: " + a.getIndirizzo());
            log.debug("Indirizzo2: " + a.getIndirizzoSecondaRiga());
            log.debug("------------------------------");
            
        }
                       
        if (a.getProvincia() == Provincia.ND) {
            if (a.getCodeLineBase().equals("00000000015153")) {
                a.setProvincia(Provincia.RM);
                log.debug("-----Anagrafica Provincia RM------");
                log.debug(a.getCodeLineBase());
                log.debug(a.getCitta());
                log.debug(a.getCap());
                log.debug(a.getProvincia().getNome());
                log.debug("------------------------------");
                
            } else if (a.getCodeLineBase().equals("00000000070340")) {
                a.setProvincia(Provincia.TV);
                log.debug("-----Anagrafica Provincia TV------");
                log.debug(a.getCodeLineBase());
                log.debug(a.getCitta());
                log.debug(a.getCap());
                log.debug(a.getProvincia().getNome());
                log.debug("------------------------------");
                
            } else if (a.getCap().equals("87020")) {
                a.setProvincia(Provincia.CS);
                log.debug("-----Anagrafica Provincia CS------");
                log.debug(a.getCodeLineBase());
                log.debug(a.getCitta());
                log.debug(a.getCap());
                log.debug(a.getProvincia().getNome());
                log.debug("------------------------------");
                                
            } else if (a.getCap().equals("31048")) {
                a.setProvincia(Provincia.TV);
                log.debug("-----Anagrafica Provincia TV------");
                log.debug(a.getCodeLineBase());
                log.debug(a.getCitta());
                log.debug(a.getCap());
                log.debug(a.getProvincia().getNome());
                log.debug("------------------------------");
                                
            } else if (a.getCap().equals("35010")) {
                a.setProvincia(Provincia.PD);
                log.debug("-----Anagrafica Provincia PD------");
                log.debug(a.getCodeLineBase());
                log.debug(a.getCitta());
                log.debug(a.getCap());
                log.debug(a.getProvincia().getNome());
                log.debug("------------------------------");
                                
            } else if (a.getCap().equals("41033")) {
                a.setProvincia(Provincia.MO);
                log.debug("-----Anagrafica Provincia MO------");
                log.debug(a.getCodeLineBase());
                log.debug(a.getCitta());
                log.debug(a.getCap());
                log.debug(a.getProvincia().getNome());
                log.debug("------------------------------");
                                
            } else if (a.getCap().equals("47893")) {
                a.setPaese(Paese.SM);
                a.setCitta("BORGO MAGGIORE");
                a.setDiocesi(Diocesi.DIOCESI175);
                log.debug("-----Anagrafica Paese San Marino------");
                log.debug(a.getCodeLineBase());
                log.debug(a.getCitta());
                log.debug(a.getCap());
                log.debug(a.getPaese().getNome());
                log.debug(a.getDiocesi().getDetails());
                log.debug("------------------------------");
                                
            } else if (a.getCap().equals("50038")) {
                a.setProvincia(Provincia.FI);
                log.debug("-----Anagrafica Provincia FI------");
                log.debug(a.getCodeLineBase());
                log.debug(a.getCitta());
                log.debug(a.getCap());
                log.debug(a.getProvincia().getNome());
                log.debug("------------------------------");
                                
            } else if (a.getCap().equals("54028")) {
                a.setProvincia(Provincia.MS);
                log.debug("-----Anagrafica Provincia MS------");
                log.debug(a.getCodeLineBase());
                log.debug(a.getCitta());
                log.debug(a.getCap());
                log.debug(a.getProvincia().getNome());
                log.debug("------------------------------");
                                
            } else if (a.getCap().equals("56035")) {
                a.setProvincia(Provincia.PI);
                log.debug("-----Anagrafica Provincia PI------");
                log.debug(a.getCodeLineBase());
                log.debug(a.getCitta());
                log.debug(a.getCap());
                log.debug(a.getProvincia().getNome());
                log.debug("------------------------------");
                
            } else if (a.getCap().equals("87064")) {
                a.setProvincia(Provincia.CS);
                log.debug("-----Anagrafica Provincia CS------");
                log.debug(a.getCodeLineBase());
                log.debug(a.getCitta());
                log.debug(a.getCap());
                log.debug(a.getProvincia().getNome());
                log.debug("------------------------------");
                
            } else if (a.getCap().equals("60012")) {
                a.setProvincia(Provincia.AN);
                a.setIndirizzo(a.getIndirizzo()+ " FRAZ.BRUGNETTO");
                log.debug("-----Anagrafica Provincia AN------");
                log.debug(a.getCodeLineBase());
                log.debug(a.getIndirizzo());
                log.debug(a.getCitta());
                log.debug(a.getCap());
                log.debug(a.getProvincia().getNome());
                log.debug("------------------------------");
                                
            } else if (a.getCap().equals("80061")) {
                a.setProvincia(Provincia.NA);
                log.debug("-----Anagrafica Provincia NA------");
                log.debug(a.getCodeLineBase());
                log.debug(a.getIndirizzo());
                log.debug(a.getCitta());
                log.debug(a.getCap());
                log.debug(a.getProvincia().getNome());
                log.debug("------------------------------");
                                
            } else {
                log.debug("-----Anagrafica Provincia error------");
                log.debug(a.getCodeLineBase());
                log.debug(a.getCitta());
                log.debug(a.getCap());
                log.debug(a.getProvincia().getNome());
                log.debug("------------------------------");
                
                throw new UnsupportedOperationException();
            }
        }

    }
    
    public static Integer processRowCampagnaMessaggioNum(Row row) {
        DataFormatter dataFormatter = new DataFormatter();
        return getRowInteger(dataFormatter.formatCellValue(row.getCell(8)));
    }    
    public static BigDecimal processRowCampagnaMessaggioCosto(Row row) {
        DataFormatter dataFormatter = new DataFormatter();
        return getRowBigDecimal(dataFormatter.formatCellValue(row.getCell(9)));
    }
    
    public static Integer processRowCampagnaBlocchettiNum(Row row) {
        DataFormatter dataFormatter = new DataFormatter();
        return getRowInteger(dataFormatter.formatCellValue(row.getCell(10)));
    }
    
    public static BigDecimal processRowCampagnaBlocchettiCosto(Row row) {
        DataFormatter dataFormatter = new DataFormatter();
        return getRowBigDecimal(dataFormatter.formatCellValue(row.getCell(11)));
    }
    public static  Integer processRowCampagnaManifestiNum(Row row) {
        DataFormatter dataFormatter = new DataFormatter();
        return getRowInteger(dataFormatter.formatCellValue(row.getCell(12)));
    }
    public static BigDecimal processRowCampagnaManifestiCosto(Row row) {
        DataFormatter dataFormatter = new DataFormatter();
        return getRowBigDecimal(dataFormatter.formatCellValue(row.getCell(13)));
    }        
    public static Integer processRowCampagnaLodareNum(Row row) {
        DataFormatter dataFormatter = new DataFormatter();
        return getRowInteger(dataFormatter.formatCellValue(row.getCell(14)));
    }

    public static BigDecimal processRowCampagnaLodareCosto(Row row) {
        DataFormatter dataFormatter = new DataFormatter();
        return getRowBigDecimal(dataFormatter.formatCellValue(row.getCell(15)));
    }

    public static BigDecimal processRowCampagnaTotaleCosto(Row row) throws UnsupportedOperationException {
        DataFormatter dataFormatter = new DataFormatter();
        BigDecimal totimp = getRowBigDecimal(dataFormatter.formatCellValue(row.getCell(16)));
        BigDecimal totabb = getRowBigDecimal(dataFormatter.formatCellValue(row.getCell(22)));
        if ( totabb.compareTo(totimp) != 0) {
            throw new UnsupportedOperationException();
        }

        return totimp;
    }

    public static String processRowCampagnaCodeline(Row row,String pncodcon) throws UnsupportedOperationException {
        DataFormatter dataFormatter = new DataFormatter();
        String pndessup = dataFormatter.formatCellValue(row.getCell(7));
        if (pncodcon.equals("0000004967")) {
            pndessup="000000020000730517";
            log.debug("----- codeLine fix ------");
            log.debug("ANCODICE: " + pncodcon);
            log.debug("codeLine: " + pndessup);
            log.debug("------------------------------");
            
        } else if (pncodcon.equals("0000048374")) {
            pndessup="000000020000730416";
            log.debug("----- codeLine fix ------");
            log.debug("ANCODICE: " + pncodcon);
            log.debug("codeLine: " + pndessup);
            log.debug("------------------------------");
            
        }
        if (!Abbonamento.checkCodeLine(pndessup)) {
            log.debug("----- codeLine invalid ------");
            log.debug("ANCODICE: " + pncodcon);
            log.debug("------------------------------");
            
            throw new UnsupportedOperationException();
        }
        return pndessup;
        
    }
    
    private static List<Row> getOmaggi(File omaggi) throws IOException {
        DataFormatter dataFormatter = new DataFormatter();
        Workbook wbca2020 = new HSSFWorkbook(new FileInputStream(omaggi));
        List<Row> rows = new ArrayList<>();
        for (Row row: wbca2020.getSheetAt(0)) {
            String tot_quant = dataFormatter.formatCellValue(row.getCell(0));
            if (tot_quant.endsWith("TOT_QUANT")) {
                continue;
            }
            if (tot_quant.trim().equals("")) {
                break;
            }
            rows.add(row);
        }
        return rows;
        
    }

    public static Set<String> getCategoriaBmCassa() throws IOException {
        Set<String> codici = new HashSet<>();
        DataFormatter dataFormatter = new DataFormatter();
        Workbook wbca2020 = new HSSFWorkbook(new FileInputStream(CATEGORIA_BM_CASSA));
        for (Row row: wbca2020.getSheetAt(0)) {
            String ancodice = dataFormatter.formatCellValue(row.getCell(1));
            if (ancodice.endsWith("ancodice")) {
                continue;
            }
            codici.add(ancodice);
        }
        return codici;        
    }
    
    public static List<Row> getOmaggioGesuitiMessaggio2020() throws IOException {
        return getOmaggi(new File(ELENCO_OMAGGIO_GESUITI_MESSAGGIO));
    }

    public static List<Row> getOmaggioGesuitiBlocchetti2020() throws IOException {
        return getOmaggi(new File(ELENCO_OMAGGIO_GESUITI_BLOCCHETTI));
    }

    public static List<Row> getOmaggioGesuitiManifesti2020() throws IOException {
        return getOmaggi(new File(ELENCO_OMAGGIO_GESUITI_MANIFESTI));
    }

    public static List<Row> getOmaggioMessaggio2020() throws IOException {
        return getOmaggi(new File(ELENCO_OMAGGIO_MESSAGGIO));
    }
    
    public static List<Row> getOmaggioBlocchetti2020() throws IOException {
        return getOmaggi(new File(ELENCO_OMAGGIO_BLOCCHETTI));
    }

    public static List<Row> getOmaggioLodare2020() throws IOException {
        return getOmaggi(new File(ELENCO_OMAGGIO_LODARE));
    }

    public static List<Anagrafica> importOmaggio(List<Row> campagnaRows) throws IOException {        
        DataFormatter dataFormatter = new DataFormatter();
        Set<String> errors = new HashSet<>();
        List<Anagrafica> anagraficaMap = new ArrayList<>();
        for (Row row : campagnaRows ) {
            try {
                Anagrafica a = processRowOmaggio(row,dataFormatter);
                anagraficaMap.add(a);                
            } catch (UnsupportedOperationException e) {
                errors.add(e.getMessage());
                continue;
            }
        }
        log.debug("Omaggio Messaggio 2020 -  Errori Trovati: "
                + errors.size());
        log.debug("Omaggio Messaggio 2020 -  Clienti Trovati: "
                + anagraficaMap.size());
        return anagraficaMap;
    }

    public static Integer getQuantitaFromOmaggioRow(Row row) {
        DataFormatter dataFormatter = new DataFormatter();
        return getRowInteger(dataFormatter.formatCellValue(row.getCell(0)));
    }

    public static String getNominativoFromOmaggioRow(Row row) {
        DataFormatter dataFormatter = new DataFormatter();
        return dataFormatter.formatCellValue(row.getCell(2));
    }

    public static Provincia getProvinciaFromOmaggioRow(Row row) {
        DataFormatter dataFormatter = new DataFormatter();
        return getProvincia(dataFormatter.formatCellValue(row.getCell(7)));
    }

    private static Anagrafica processRowOmaggio(Row row, DataFormatter dataFormatter) throws UnsupportedOperationException {

        String cap = dataFormatter.formatCellValue(row.getCell(1));
        String nominativo = dataFormatter.formatCellValue(row.getCell(2));
        String titolo = dataFormatter.formatCellValue(row.getCell(3));
        String sottointes = dataFormatter.formatCellValue(row.getCell(4));
        String indirizzo = dataFormatter.formatCellValue(row.getCell(5));
        String citta = dataFormatter.formatCellValue(row.getCell(6));
        String provincia = dataFormatter.formatCellValue(row.getCell(7));
        String via_aerea = dataFormatter.formatCellValue(row.getCell(8));
        if (!via_aerea.equals("N")) {
            log.debug("via_aerea mismatch: {} -> {}" ,nominativo, via_aerea);
            throw new UnsupportedOperationException("via_aerea mismatch ->"+ nominativo);
        }
        
        String paese = dataFormatter.formatCellValue(row.getCell(9));
        String abstato = dataFormatter.formatCellValue(row.getCell(10));
        if (!abstato.equals("OM")) {
            log.debug("abstato mismatch: {} -> {}" , nominativo,abstato);
            throw new UnsupportedOperationException("abstato mismatch ->"+ nominativo);
        }
        Anagrafica anagrafica = getAnagraficaByAncodcon(Anagrafica.generaCodeLineBase());
        anagrafica.setCap(cap);
        anagrafica.setTitolo(TitoloAnagrafica.getByIntestazione(titolo));
        anagrafica.setNome(sottointes);
        anagrafica.setDenominazione(nominativo);
        anagrafica.setIndirizzo(indirizzo);
        anagrafica.setCitta(citta);
        anagrafica.setProvincia(getProvincia(provincia));
        anagrafica.setPaese(Paese.getByNome(paese));
        anagrafica.setAreaSpedizione(getAreaSpedizione(anagrafica.getPaese().getSigla()));
        
                
        
        return anagrafica;

    }

    public static Map<String,Row> getCampagna2020() throws IOException {
        DataFormatter dataFormatter = new DataFormatter();
        File ca2020 = new File(CAMPAGNA_2020);
        Workbook wbca2020 = new HSSFWorkbook(new FileInputStream(ca2020));
        Map<String,Row> rows = new HashMap<>();
        for (Row row: wbca2020.getSheetAt(0)) {
            String pncodcon = dataFormatter.formatCellValue(row.getCell(0));
            if (pncodcon.endsWith("pncodcon")) {
                continue;
            }
            rows.put(pncodcon,row);
        }
        return rows;
    }
    
    public static Map<String,Anagrafica> importCampagna2020(Map<String, Row> campagnaRows) throws IOException {        
        DataFormatter dataFormatter = new DataFormatter();
        
        Set<String> errors = new HashSet<>();
        Map<String, Anagrafica> anagraficaMap = new HashMap<>();
        for (String pncodcon : campagnaRows.keySet() ) {
            try {
                anagraficaMap.put(pncodcon, processRowCampagna(campagnaRows.get(pncodcon),pncodcon,dataFormatter));                
            } catch (UnsupportedOperationException e) {
                errors.add(pncodcon);
                continue;
            }
        }
        log.debug("Campagna 2020 -  Errori Trovati: "
                + errors.size());
        log.debug("Campagna 2020 -  Clienti Trovati: "
                + anagraficaMap.size());
        return anagraficaMap;
    }
     
    private static Anagrafica processRowCampagna(Row row, String pncodcon, DataFormatter dataFormatter) throws UnsupportedOperationException {

        String destitolo = dataFormatter.formatCellValue(row.getCell(1));
        String andescri = dataFormatter.formatCellValue(row.getCell(2));
        String andescr2 = dataFormatter.formatCellValue(row.getCell(3));
        String anindiri = dataFormatter.formatCellValue(row.getCell(4));
        String anindir1 = dataFormatter.formatCellValue(row.getCell(5));
        String anlocali = dataFormatter.formatCellValue(row.getCell(6));
        Anagrafica anagrafica = getAnagraficaByAncodcon(pncodcon);
        
        populateAnagraficaCampagna(anagrafica, destitolo, andescri, andescr2, anindiri, anindir1, anlocali);
                
        String scopro =        dataFormatter.formatCellValue(row.getCell(17));
        if (!scopro.equals("")) {
            log.debug("scopro mismatch: " + pncodcon);
            throw new UnsupportedOperationException();
        }
        
        return anagrafica;

    }
    
    public static Map<String,Anagrafica> importArchivioClienti() throws IOException {
        DataFormatter dataFormatter = new DataFormatter();

        File ac = new File(ARCHIVIO_CLIENTI);
        Workbook wbac = new HSSFWorkbook(new FileInputStream(ac));

        Set<String> errors = new HashSet<>();
        Map<String, Anagrafica> anagraficaMap = new HashMap<>();
        for (Row row : wbac.getSheetAt(0)) {
            String picoddio = dataFormatter.formatCellValue(row.getCell(0));
            String ancodice = dataFormatter.formatCellValue(row.getCell(1));
            if (picoddio.equalsIgnoreCase("PICODDIO")) {
                continue;
            }
            try {
                anagraficaMap.put(ancodice, processRowAnagrafica(row, picoddio,ancodice, dataFormatter));
            } catch (UnsupportedOperationException e) {
                errors.add(ancodice);
                continue;
            }
        }

        log.debug("Archivio Clienti Errori Trovati: "
                + errors.size());
        log.debug("Archivio Clienti Trovati: "
                + anagraficaMap.size());
        
        return anagraficaMap;
    }

    public static Map<String,Anagrafica> importElencoAbbonati() throws IOException {
        DataFormatter dataFormatter = new DataFormatter();

        File ac = new File(ELENCO_ABBONATI);
        Workbook wbac = new HSSFWorkbook(new FileInputStream(ac));

        Set<String> errors = new HashSet<>();
        Map<String, Anagrafica> anagraficaMap = new HashMap<>();
        for (Row row : wbac.getSheetAt(0)) {
            String picoddio = dataFormatter.formatCellValue(row.getCell(0));
            String ancodice = dataFormatter.formatCellValue(row.getCell(1));
            if (picoddio.equalsIgnoreCase("PICODDIO")) {
                continue;
            }
            try {
                anagraficaMap.put(ancodice, processRowAnagrafica(row, picoddio,ancodice, dataFormatter));
            } catch (UnsupportedOperationException e) {
                errors.add(ancodice);
                continue;
            }
        }

        log.debug("Elenco Abbonanti Errori Trovati: "
                + errors.size());
        log.debug("Elenco Abbonati Trovati: "
                + anagraficaMap.size());
        
        return anagraficaMap;
    }
    
    public static List<Row> getBeneficiari() throws IOException {
        DataFormatter dataFormatter = new DataFormatter();

        File ac = new File(ELENCO_BENEFICIARI_2020);
        Workbook wbac = new HSSFWorkbook(new FileInputStream(ac));

        List<Row> rows = new ArrayList<Row>();
        for (Row row : wbac.getSheetAt(0)) {
            String antipcon = dataFormatter.formatCellValue(row.getCell(0));
            if (antipcon.equalsIgnoreCase("antipcon")) {
                continue;
            }
            rows.add(row);
        }            
        return rows;
    }
    
    public static String getAbrinnautFromBeneficiari(Row row) {
        DataFormatter dataFormatter = new DataFormatter();
        return dataFormatter.formatCellValue(row.getCell(9));        
    }

    public static String getAbstatoFromBeneficiari(Row row) {
        DataFormatter dataFormatter = new DataFormatter();
        return dataFormatter.formatCellValue(row.getCell(10));        
    }

    public static BigDecimal getPrezzoFromBeneficiari(Row row) {
        DataFormatter dataFormatter = new DataFormatter();
        return getRowBigDecimal(dataFormatter.formatCellValue(row.getCell(11)));
    }

    public static Integer getQuantitaFromBeneficiari(Row row) {
        DataFormatter dataFormatter = new DataFormatter();
        return getRowInteger(dataFormatter.formatCellValue(row.getCell(14)));
    }

    public static String getAncodiceFromBeneficiari(Row row) {
        DataFormatter dataFormatter = new DataFormatter();
        return dataFormatter.formatCellValue(row.getCell(1));
        
    }
    
    public static String getBancodiceFromBeneficiari(Row row) {
        DataFormatter dataFormatter = new DataFormatter();
        return dataFormatter.formatCellValue(row.getCell(15));        
    }

    public static Map<String,Anagrafica> importBeneficiari(List<Row> rows) throws IOException {
        DataFormatter dataFormatter = new DataFormatter();

        Set<String> errors = new HashSet<>();
        Map<String, Anagrafica> anagraficaMap = new HashMap<>();
        for (Row row : rows) {
            String ancodice = dataFormatter.formatCellValue(row.getCell(1));
            String andescri = dataFormatter.formatCellValue(row.getCell(2));
            String anindiri = dataFormatter.formatCellValue(row.getCell(3));
            String anindir2 = dataFormatter.formatCellValue(row.getCell(4));
            String an___cap = dataFormatter.formatCellValue(row.getCell(5));
            String anlocali = dataFormatter.formatCellValue(row.getCell(6));
            String anprovin = dataFormatter.formatCellValue(row.getCell(7));
            String annazion = dataFormatter.formatCellValue(row.getCell(8));
            String bancodice  = dataFormatter.formatCellValue(row.getCell(15));
            String bandescri = dataFormatter.formatCellValue(row.getCell(16));
            String banindiri = dataFormatter.formatCellValue(row.getCell(17));
            String banindir2 = dataFormatter.formatCellValue(row.getCell(18));
            String ban___cap = dataFormatter.formatCellValue(row.getCell(19));
            String banlocali = dataFormatter.formatCellValue(row.getCell(20));
            String banprovin = dataFormatter.formatCellValue(row.getCell(21));
            String bannazion = dataFormatter.formatCellValue(row.getCell(22));
            Anagrafica abene = getAnagraficaByAncodcon(ancodice);
            abene.setDenominazione(andescri);
            abene.setIndirizzo(anindiri);
            abene.setIndirizzoSecondaRiga(anindir2);
            abene.setCitta(anlocali);
            if (!an___cap.trim().equals("")) {
                abene.setCap(an___cap);
            }
            abene.setProvincia(getProvincia(anprovin));
            abene.setPaese(Paese.getBySigla(annazion));
            abene.setAreaSpedizione(getAreaSpedizione(annazion));
            
            anagraficaMap.put(ancodice, abene);
            
            if (bancodice.trim().equals("")) {
                continue;
            }
            Anagrafica ainte = getAnagraficaByAncodcon(bancodice);
            ainte.setDenominazione(bandescri);
            ainte.setIndirizzo(banindiri);
            ainte.setIndirizzoSecondaRiga(banindir2);
            ainte.setCitta(banlocali);
            if (!ban___cap.trim().equals("")) {
                ainte.setCap(ban___cap);
            }
            ainte.setProvincia(getProvincia(banprovin));
            ainte.setPaese(Paese.getBySigla(bannazion));
            ainte.setAreaSpedizione(getAreaSpedizione(bannazion));
            anagraficaMap.put(bancodice, ainte);
        }

        log.debug("Beneficiari Ita Errori Trovati: "
                + errors.size());
        log.debug("Beneficiari Ita Trovati: "
                + anagraficaMap.size());
        
        return anagraficaMap;
    }

    public static List<Row> getAbbonatiItaEstero() throws IOException{
        DataFormatter dataFormatter = new DataFormatter();

        File ac = new File(ELENCO_ABBONATI_ITA_ESTERO);
        Workbook wbac = new HSSFWorkbook(new FileInputStream(ac));
        List<Row> rows = new ArrayList<>();
        for (Row row : wbac.getSheetAt(0)) {
            String ancodiceI = dataFormatter.formatCellValue(row.getCell(0));
            if (ancodiceI.equals("COD.")) {
                continue;
            }
            if (ancodiceI.trim().equalsIgnoreCase("")) {
                break;
            }
            rows.add(row);
        }
       
        return rows;
    }

    public static String getTestataFromAbbonatiItaEstero(Row row) {
        DataFormatter dataFormatter = new DataFormatter();
        return dataFormatter.formatCellValue(row.getCell(8));
    }

    public static Integer getQuantFromAbbonatiItaEstero(Row row) {
        DataFormatter dataFormatter = new DataFormatter();
        return getRowInteger(dataFormatter.formatCellValue(row.getCell(9)));
    }

    public static BigDecimal getImportoRivistaFromAbbonatiItaEstero(Row row) {
        DataFormatter dataFormatter = new DataFormatter();
        return getRowBigDecimal(dataFormatter.formatCellValue(row.getCell(10)));
    }

    public static BigDecimal getSpeseFromAbbonatiItaEstero(Row row) {
        DataFormatter dataFormatter = new DataFormatter();
        return getRowBigDecimal(dataFormatter.formatCellValue(row.getCell(11)));
    }

    public static String getAnCodiceIntestatarioAbbonatiItaEstero(Row row) {
        DataFormatter dataFormatter = new DataFormatter();
        return "00000"+dataFormatter.formatCellValue(row.getCell(0));        
    }

    public static String getAnCodiceDestinatatarioAbbonatiItaEstero(Row row) {
        DataFormatter dataFormatter = new DataFormatter();
        return "00000"+dataFormatter.formatCellValue(row.getCell(2));        
    }

    public static Paese getPaeseDestinatarioAbbonatiEstero(Row row) {
        DataFormatter dataFormatter = new DataFormatter();
        return Paese.getBySigla(dataFormatter.formatCellValue(row.getCell(7)));        
    }

    public static AreaSpedizione getAreaSpedizioneDestinatarioAbbonatiEstero(Row row) {
        DataFormatter dataFormatter = new DataFormatter();
        return getAreaSpedizione(dataFormatter.formatCellValue(row.getCell(7)));        
    }

    public static Map<String,Anagrafica> importAbbonatiItaEstero(List<Row> rows) throws IOException {
 
        DataFormatter dataFormatter = new DataFormatter();
        Set<String> errors = new HashSet<>();
        Map<String, Anagrafica> anagraficaMap = new HashMap<>();
        for (Row row : rows) {
            String ancodiceI = "00000"+dataFormatter.formatCellValue(row.getCell(0));
            String destinatI = dataFormatter.formatCellValue(row.getCell(1));
            
            String ancodiceD = "00000"+dataFormatter.formatCellValue(row.getCell(2));
            String destinatD = dataFormatter.formatCellValue(row.getCell(3));
            /* not used
            String descri = dataFormatter.formatCellValue(row.getCell(4));
            String indiri = dataFormatter.formatCellValue(row.getCell(5));
            String locali = dataFormatter.formatCellValue(row.getCell(6));
            */
            try {
                Anagrafica intestatario = getAnagraficaByAncodcon(ancodiceI);
                intestatario.setDenominazione(destinatI);
                anagraficaMap.put(ancodiceI, intestatario);
                Anagrafica beneficiario = getAnagraficaByAncodcon(ancodiceD);
                beneficiario.setDenominazione(destinatD);
                anagraficaMap.put(ancodiceD, beneficiario);
            } catch (UnsupportedOperationException e) {
                errors.add(ancodiceI);
                continue;
            }
        }

        log.debug("Anagrafica ITA Estero Errori Trovati: "
                + errors.size());
        log.debug("Anagrafica ITA Estero Trovati: "
                + anagraficaMap.size());
        
        return anagraficaMap;
    }

    public static Map<String,Row> getAbbonatiEstero() throws IOException {
        DataFormatter dataFormatter = new DataFormatter();
        File ac = new File(ELENCO_ABBONATI_ESTERO);
        Workbook wbac = new HSSFWorkbook(new FileInputStream(ac));

        Map<String, Row> rowMap = new HashMap<>();
        for (Row row : wbac.getSheetAt(0)) {
            String ancodice = dataFormatter.formatCellValue(row.getCell(0));
            if (ancodice.equalsIgnoreCase("ancodice")) {
                continue;
            }
            rowMap.put(ancodice, row);
        }
        return rowMap;
    }
    
    public static Map<String,Anagrafica> importAbbonatiEstero(Map<String,Row> rowMap) throws IOException {
        DataFormatter dataFormatter = new DataFormatter();

        Set<String> errors = new HashSet<>();
        Map<String, Anagrafica> anagraficaMap = new HashMap<>();
        for (String ancodice : rowMap.keySet()) {
            Row row = rowMap.get(ancodice);
            try {
                anagraficaMap.put(ancodice, processRowAbbonatiEstero(row, ancodice, dataFormatter));
            } catch (UnsupportedOperationException e) {
                errors.add(ancodice);
                continue;
            }
        }

        log.debug("Abbonanti Estero Errori Trovati: "
                + errors.size());
        log.debug("Abbonati Estero Trovati: "
                + anagraficaMap.size());
        
        return anagraficaMap;
    }
    
    // 3 estratti/manifesti
    public static int getIdEstrattiAbbEstero(Row row) {
        DataFormatter dataFormatter = new DataFormatter();
        return getRowInteger(dataFormatter.formatCellValue(row.getCell(13)));
    }

    public static BigDecimal getCostoEstrattiAbbEstero(Row row) {
        DataFormatter dataFormatter = new DataFormatter();
        return getRowBigDecimal(dataFormatter.formatCellValue(row.getCell(14)));
    }
    
    public static int getQtaEstrattiAbbEstero(Row row) {
        DataFormatter dataFormatter = new DataFormatter();
        return getRowInteger(dataFormatter.formatCellValue(row.getCell(15)));
    }

    // id=2 blocchetti
    public static int getIdBlocchettiAbbEstero(Row row) {
        DataFormatter dataFormatter = new DataFormatter();
        return getRowInteger(dataFormatter.formatCellValue(row.getCell(16)));
    }

    public static BigDecimal getCostoBlocchettiAbbEstero(Row row) {
        DataFormatter dataFormatter = new DataFormatter();
        return getRowBigDecimal(dataFormatter.formatCellValue(row.getCell(17)));
    }
    
    public static int getQtaBlocchettiAbbEstero(Row row) {
        DataFormatter dataFormatter = new DataFormatter();
        return getRowInteger(dataFormatter.formatCellValue(row.getCell(18)));
    }
    // 1 messaggio
    public static int getIdMessaggioAbbEstero(Row row) {
        DataFormatter dataFormatter = new DataFormatter();
        return getRowInteger(dataFormatter.formatCellValue(row.getCell(19)));
    }

    public static BigDecimal getCostoMessaggioAbbEstero(Row row) {
        DataFormatter dataFormatter = new DataFormatter();
        return getRowBigDecimal(dataFormatter.formatCellValue(row.getCell(20)));
    }
    
    public static int getQtaMessaggioAbbEstero(Row row) {
        DataFormatter dataFormatter = new DataFormatter();
        return getRowInteger(dataFormatter.formatCellValue(row.getCell(21)));
    }
    // 6 lodare
    public static BigDecimal getSpeseAbbEstero(Row row) {
        DataFormatter dataFormatter = new DataFormatter();
        return getRowBigDecimal(dataFormatter.formatCellValue(row.getCell(23)));
    }

    public static Anagrafica processRowAbbonatiEstero(Row row, String ancodice,DataFormatter dataFormatter) throws UnsupportedOperationException {
        
        String andescri = dataFormatter.formatCellValue(row.getCell(1));
        String andescr2 = dataFormatter.formatCellValue(row.getCell(2));
        String anindiri = dataFormatter.formatCellValue(row.getCell(3));
        String anindir1 = dataFormatter.formatCellValue(row.getCell(4));
        String anlocali = dataFormatter.formatCellValue(row.getCell(6));
        String annazion = dataFormatter.formatCellValue(row.getCell(8));        
        String destitolo = dataFormatter.formatCellValue(row.getCell(10));
                
        Anagrafica anagrafica = getAnagraficaByAncodcon(ancodice);
        
        populateAnagraficaEstero(anagrafica, ancodice,destitolo, andescri, andescr2, anindiri, anindir1, anlocali, annazion);
        return anagrafica;        
    }

    public static Anagrafica processRowAnagrafica(Row row, String picoddio,String ancodice,DataFormatter dataFormatter) throws UnsupportedOperationException {
                    
        String andescri = dataFormatter.formatCellValue(row.getCell(2));
        String andescr2 = dataFormatter.formatCellValue(row.getCell(3));
        String anindiri = dataFormatter.formatCellValue(row.getCell(4));
        String anlocali = dataFormatter.formatCellValue(row.getCell(5));
        String anprovin = dataFormatter.formatCellValue(row.getCell(6));
        String an___cap = dataFormatter.formatCellValue(row.getCell(7));
        String annazion = dataFormatter.formatCellValue(row.getCell(8));
        String ancodfis = dataFormatter.formatCellValue(row.getCell(9));
        String anpariva = dataFormatter.formatCellValue(row.getCell(10));
        String antelefo = dataFormatter.formatCellValue(row.getCell(11));
        //String antipcon = dataFormatter.formatCellValue(row.getCell(12));
        String annumcel = dataFormatter.formatCellValue(row.getCell(13));
        String an_email = dataFormatter.formatCellValue(row.getCell(14));
        //String abcodese = dataFormatter.formatCellValue(row.getCell(15));
        
        Anagrafica anagrafica = getAnagraficaByAncodcon(ancodice);
        anagrafica.setDenominazione(andescri);
        anagrafica.setNome(andescr2);
        anagrafica.setIndirizzo(anindiri);
        anagrafica.setCitta(anlocali);
        if (!an___cap.trim().equals("")) {
            anagrafica.setCap(an___cap);
        }
        anagrafica.setProvincia(getProvincia(anprovin));
        anagrafica.setCodfis(ancodfis);
        anagrafica.setPiva(anpariva);
        anagrafica.setTelefono(antelefo);
        anagrafica.setCellulare(annumcel);
        anagrafica.setEmail(an_email);    
        anagrafica.setPaese(Paese.getBySigla(annazion));
        anagrafica.setAreaSpedizione(getAreaSpedizione(annazion));
        if (!checkDiocesi(anagrafica, 
                          annazion, 
                          ancodice, 
                          andescri, 
                          anindiri, 
                          anlocali,
                          anprovin,
                          picoddio)) {
                throw new UnsupportedOperationException();
        }

        if (anagrafica.getPaese() == Paese.ND) {
            log.debug("-----Paese non Definito------");
            log.debug(ancodice);
            log.debug(andescri);
            log.debug(anindiri);
            log.debug(anlocali);
            log.debug(anprovin);
            log.debug(annazion);
            log.debug(picoddio);
            log.debug("------------------------------");
            
            throw new UnsupportedOperationException();
        }
        
        if (annazion.equals("ITA") && anagrafica.getDiocesi() == Diocesi.DIOCESI000) {
            log.debug("-----Nazione ITA Errata ------");
            log.debug("ANCODICE: " + ancodice);
            log.debug("ANDESCRI: " + andescri);
            log.debug("ANINDIRI: " + anindiri);
            log.debug("ANLOCALI: " + anlocali);
            log.debug("ANPROVIN: " + anprovin);
            log.debug("ANNAZION: " + annazion);
            log.debug("Diocesi: "
                    + anagrafica.getDiocesi().getDetails());
            log.debug("------------------------------");
            
            throw new UnsupportedOperationException();
        }
        
        if (!checkProvincia(anagrafica, annazion, 
                          ancodice, 
                          andescri, 
                          anindiri, 
                          anlocali,
                          anprovin,
                          picoddio)) {
                throw new UnsupportedOperationException();
        }
        
        return anagrafica;
        
    }
        
    private static boolean checkDiocesi(Anagrafica anagrafica, 
            String annazion, 
            String ancodice, 
            String andescri, 
            String anindiri, 
            String anlocali,
            String anprovin,
            String picoddio) {
        if (anagrafica.getDiocesi() == Diocesi.DIOCESISTD) {
            anagrafica.setDiocesi(Diocesi.getDiocesiByCodice(picoddio));
        }
        
        if (ancodice.equals("0000062854")) {
            anagrafica.setDiocesi(Diocesi.DIOCESI193);
            log.debug("-----Diocesi 193------");
            log.debug("ANCODICE: " + ancodice);
            log.debug("Provincia: "
                    + anagrafica.getProvincia().getNome());
            log.debug("ANNAZION: " + annazion);
            log.debug("Diocesi: "
                    + anagrafica.getDiocesi().getDetails());
            log.debug("------------------------------");
            
        } else if (ancodice.equals("0000072701")) {
            anagrafica.setDiocesi(Diocesi.DIOCESI194);
            log.debug("-----Diocesi 194------");
            log.debug("ANCODICE: " + ancodice);
            log.debug("ANDESCRI: " + andescri);
            log.debug("ANINDIRI: " + anindiri);
            log.debug("ANLOCALI: " + anlocali);
            log.debug("ANPROVIN: " + anprovin);
            log.debug("ANNAZION: " + annazion);
            log.debug("Diocesi: "
                    + anagrafica.getDiocesi().getDetails());
            log.debug("------------------------------");
            
        }

        if (anagrafica.getDiocesi() == Diocesi.DIOCESISTD) {
            if (annazion.equals("RSM")) {
                anagrafica.setDiocesi(Diocesi.DIOCESI175);
                log.debug("-----Diocesi RSM------");
                log.debug("ANCODICE: " + ancodice);
                log.debug("ANDESCRI: " + andescri);
                log.debug("ANINDIRI: " + anindiri);
                log.debug("ANLOCALI: " + anlocali);
                log.debug("ANPROVIN: " + anprovin);
                log.debug("ANNAZION: " + annazion);
                log.debug("Diocesi: "
                        + anagrafica.getDiocesi().getDetails());
                log.debug("------------------------------");
                
            } else if (!annazion.equals("ITA")) {
                anagrafica.setDiocesi(Diocesi.DIOCESI000);
                log.debug("-----Diocesi ESTERO------");
                log.debug("ANCODICE: " + ancodice);
                log.debug("ANDESCRI: " + andescri);
                log.debug("ANINDIRI: " + anindiri);
                log.debug("ANLOCALI: " + anlocali);
                log.debug("ANPROVIN: " + anprovin);
                log.debug("ANNAZION: " + annazion);
                log.debug("Diocesi: "
                        + anagrafica.getDiocesi().getDetails());
                log.debug("------------------------------");
                
            } else if (anlocali.equals("ROMA")) {
                anagrafica.setDiocesi(Diocesi.DIOCESI168);
                log.debug("-----Diocesi ROMA------");
                log.debug("ANCODICE: " + ancodice);
                log.debug("ANDESCRI: " + andescri);
                log.debug("ANINDIRI: " + anindiri);
                log.debug("ANLOCALI: " + anlocali);
                log.debug("ANPROVIN: " + anprovin);
                log.debug("ANNAZION: " + annazion);
                log.debug("Diocesi: "
                        + anagrafica.getDiocesi().getDetails());
                log.debug("------------------------------");
                
            } else {
                log.debug("-----Diocesi non Definita------");
                log.debug("ANCODICE: " + ancodice);
                log.debug("ANDESCRI: " + andescri);
                log.debug("ANINDIRI: " + anindiri);
                log.debug("ANLOCALI: " + anlocali);
                log.debug("ANPROVIN: " + anprovin);
                log.debug("ANNAZION: " + annazion);
                log.debug("PICODDIO: " + picoddio);
                log.debug("------------------------------");
                
                return false;
            }
        }
        return true;
    }

    private static boolean checkProvincia( Anagrafica anagrafica, 
    String annazion, 
    String ancodice, 
    String andescri, 
    String anindiri, 
    String anlocali,
    String anprovin,
    String picoddio) {
        if (annazion.equals("ITA") && anagrafica.getProvincia() == Provincia.ND) {
            if (anlocali.equals("ROMA")) {
                anagrafica.setProvincia(Provincia.RM);
            } else if (anlocali.equals("MESSINA")) {
                anagrafica.setProvincia(Provincia.ME);
            } else if (anlocali.equals("ALESSANDRIA")) {
                anagrafica.setProvincia(Provincia.AL);
            } else if (anlocali.equals("SAVONA")) {
                anagrafica.setProvincia(Provincia.SV);
            } else if (anlocali.equals("PERUGIA")) {
                anagrafica.setProvincia(Provincia.PG);
            } else if (anlocali.equals("REGGIO CALABRIA")) {
                anagrafica.setProvincia(Provincia.RC);
            } else if (anlocali.equals("PESARO")) {
                anagrafica.setProvincia(Provincia.PU);
            } else if (anlocali.equals("PARMA")) {
                anagrafica.setProvincia(Provincia.PR);
            } else if (anlocali.equals("MANTOVA")) {
                anagrafica.setProvincia(Provincia.MN);
            } else if (anlocali.equals("TORINO")) {
                anagrafica.setProvincia(Provincia.TO);
            } else if (anlocali.equals("BOLOGNA")) {
                anagrafica.setProvincia(Provincia.BO);
            } else if (anlocali.equals("MILANO")) {
                anagrafica.setProvincia(Provincia.MI);
            } else if (anlocali.equals("NAPOLI")) {
                anagrafica.setProvincia(Provincia.NA);
            } else if (anlocali.equals("PADOVA")) {
                anagrafica.setProvincia(Provincia.PD);
            } else if (anlocali.equals("FIRENZE")) {
                anagrafica.setProvincia(Provincia.FI);
            } else if (anlocali.equals("CATANZARO")) {
                anagrafica.setProvincia(Provincia.CZ);
            } else if (anlocali.equals("GENOVA")) {
                anagrafica.setProvincia(Provincia.GE);
            } else if (anlocali.equals("LIVORNO")) {
                anagrafica.setProvincia(Provincia.LI);
            } else if (anlocali.equals("NUORO")) {
                anagrafica.setProvincia(Provincia.NU);
            } else if (anlocali.equals("MATERA")) {
                anagrafica.setProvincia(Provincia.MT);
            } else if (anlocali.equals("COSENZA")) {
                anagrafica.setProvincia(Provincia.CS);
            } else if (anlocali.equals("FERRARA")) {
                anagrafica.setProvincia(Provincia.FE);
            } else if (anlocali.equals("RAVENNA")) {
                anagrafica.setProvincia(Provincia.RA);
            } else if (anlocali.equals("TARANTO")) {
                anagrafica.setProvincia(Provincia.TA);
            } else if (anlocali.equals("TRIESTE")) {
                anagrafica.setProvincia(Provincia.TS);
            } else if (anlocali.equals("SIENA")) {
                anagrafica.setProvincia(Provincia.SI);
            } else if (anlocali.equals("BARI")) {
                anagrafica.setProvincia(Provincia.BA);
            } else if (anlocali.equals("COMO")) {
                anagrafica.setProvincia(Provincia.CO);
            } else if (anlocali.equals("PALERMO")) {
                anagrafica.setProvincia(Provincia.PA);
            } else if (anlocali.equals("VENEZIA")) {
                anagrafica.setProvincia(Provincia.VE);
            } else if (anlocali.equals("L'AQUILA")) {
                anagrafica.setProvincia(Provincia.AQ);
            } else if (anlocali.equals("CHIETI")) {
                anagrafica.setProvincia(Provincia.CH);
            } else if (anlocali.equals("PAVIA")) {
                anagrafica.setProvincia(Provincia.PV);
            } else if (anlocali.equals("SACRAMORA DI RIMINI")) {
                anagrafica.setProvincia(Provincia.RN);
                anagrafica.setCitta("RIMINI");
            } else if (anlocali.equals("GROSIO")) {
                anagrafica.setProvincia(Provincia.SO);
            } else if (anagrafica.getDiocesi() == Diocesi.DIOCESI175) {
                anagrafica.setPaese(Paese.SM);
                log.debug("-----Paese RSM------");
                log.debug("ANCODICE: " + ancodice);
                log.debug("ANDESCRI: " + andescri);
                log.debug("ANINDIRI: " + anindiri);
                log.debug("ANLOCALI: " + anlocali);
                log.debug("Paese: "
                        + anagrafica.getPaese().getNome());
                log.debug("Diocesi: "
                        + anagrafica.getDiocesi().getDetails());
                log.debug("------------------------------");
                
            } else {
                log.debug("-----Provincia non Definita------");
                log.debug("ANCODICE: " + ancodice);
                log.debug("ANDESCRI: " + andescri);
                log.debug("ANINDIRI: " + anindiri);
                log.debug("ANLOCALI: " + anlocali);
                log.debug("ANPROVIN: " + anprovin);
                log.debug("ANNAZION: " + annazion);
                log.debug("Diocesi: "
                        + anagrafica.getDiocesi().getDetails());
                log.debug("------------------------------");
                
                return false;
            }
        }
        return true;
   
    }
    public static void fixAbbonatiEstero(Map<String, Anagrafica> elencoAbbonatiMap, Map<String, Anagrafica> abbonatiEsteroMap) throws UnsupportedOperationException {
        for (String ancodice : abbonatiEsteroMap.keySet()) {
            Anagrafica ae = abbonatiEsteroMap.get(ancodice);
            if (ae  == null ) {
                throw new UnsupportedOperationException("Anagrafica Abbonati Estero is null per ancodice: "+ancodice);
            }
            if (!elencoAbbonatiMap.containsKey(ancodice)){
                throw new UnsupportedOperationException("Elenco Abbonati non contiene ancodice: "+ancodice);
            }
            Anagrafica ac = elencoAbbonatiMap.get(ancodice);
            if (ac == null) {
                throw new UnsupportedOperationException("Anagrafica Elenco Abbonati is null per ancodice: "+ancodice);
            }
            ae.setDiocesi(ac.getDiocesi());
            ae.setCodfis(ac.getCodfis());
            ae.setPiva(ac.getPiva());
            ae.setTelefono(ac.getTelefono());
            ae.setCellulare(ac.getCellulare());
            ae.setEmail(ac.getEmail());    
     
            ac.setTitolo(ae.getTitolo());
        }
        
    }

    public static void fixBeneficiari(Map<String, Anagrafica> archivioClientiMap, 
            Map<String, Anagrafica> elencoAbbonatiMap,Map<String, Anagrafica> beneficiariMap) throws UnsupportedOperationException{
        for (String ancodice: beneficiariMap.keySet()) {
            if (elencoAbbonatiMap.containsKey(ancodice)) {
                beneficiariMap.put(ancodice, elencoAbbonatiMap.get(ancodice));
            } else if (archivioClientiMap.containsKey(ancodice)) {
                beneficiariMap.put(ancodice, archivioClientiMap.get(ancodice));
            } else {
                throw new UnsupportedOperationException("Beneficiario non trovato");
            }
        }
    }

    public static Map<String,Anagrafica> fixOmaggio(
            Map<String,Anagrafica> elencoAbbonatiMap, 
            Map<String,Anagrafica> archivioClientiMap,
            List<Anagrafica> clientiOmaggio) {

        Map<String,Anagrafica> clientiOmaggioMap = new HashMap<>();
UP:        for (Anagrafica utenteOmaggio : clientiOmaggio) {
            for (String  ancodice: elencoAbbonatiMap.keySet()) {
                Anagrafica anagElenco = elencoAbbonatiMap.get(ancodice);
                if (
                        utenteOmaggio.getDenominazione().equals(anagElenco.getDenominazione())
                        && anagElenco.getProvincia() == utenteOmaggio.getProvincia()                        
                    ) {
                    anagElenco.setTitolo(utenteOmaggio.getTitolo());
                    utenteOmaggio.setCodeLineBase(anagElenco.getCodeLineBase());
                    utenteOmaggio.setDiocesi(anagElenco.getDiocesi());
                    utenteOmaggio.setCellulare(anagElenco.getCellulare());
                    utenteOmaggio.setTelefono(anagElenco.getTelefono());
                    utenteOmaggio.setEmail(anagElenco.getEmail());
                    utenteOmaggio.setCodfis(anagElenco.getCodfis());
                    utenteOmaggio.setPiva(anagElenco.getPiva());
                    clientiOmaggioMap.put(ancodice, utenteOmaggio);
                    log.debug("Elenco: {}->{}", ancodice,anagElenco.getDenominazione());
                    continue UP;
                }
            }
            for (String ancodice : archivioClientiMap.keySet()) {
                Anagrafica anagArchivio = archivioClientiMap.get(ancodice);
                if (
                        utenteOmaggio.getDenominazione().equals(anagArchivio.getDenominazione())
                        && anagArchivio.getProvincia() == utenteOmaggio.getProvincia()
                        ) {
                    utenteOmaggio.setCodeLineBase(anagArchivio.getCodeLineBase());
                    if (anagArchivio.getTitolo() == TitoloAnagrafica.Nessuno) {
                        anagArchivio.setTitolo(utenteOmaggio.getTitolo());
                    }
                    if (
                            utenteOmaggio.getCodeLineBase().equals("00000000005133") 
                         || utenteOmaggio.getCodeLineBase().equals("00000000004447")) {
                        anagArchivio.setIndirizzo(utenteOmaggio.getIndirizzo());
                        log.debug("{}---> Updated Anagrafica", ancodice);
                    } else if (
                            utenteOmaggio.getCodeLineBase().equals("00000000021809") 
                        ) {
                        utenteOmaggio.setNome("");
                        utenteOmaggio.setTitolo(TitoloAnagrafica.VescovoAusiliare);
                        anagArchivio.setTitolo(TitoloAnagrafica.VescovoAusiliare);
                        anagArchivio.setIndirizzo(utenteOmaggio.getIndirizzo());
                        log.debug("{}---> Updated Anagrafica", ancodice);
                    }
                    
                    utenteOmaggio.setDiocesi(anagArchivio.getDiocesi());
                    utenteOmaggio.setCellulare(anagArchivio.getCellulare());
                    utenteOmaggio.setTelefono(anagArchivio.getTelefono());
                    utenteOmaggio.setEmail(anagArchivio.getEmail());
                    utenteOmaggio.setCodfis(anagArchivio.getCodfis());
                    utenteOmaggio.setPiva(anagArchivio.getPiva());
                    clientiOmaggioMap.put(ancodice,utenteOmaggio);
                    log.debug("Archivio: {}->{}", ancodice,anagArchivio.getDenominazione());
                    break;
                }
            }
        }
        return clientiOmaggioMap;
        
    }

    public static void fixElencoAbbonatiCampagna(Map<String, Anagrafica> elencoAbbonatiMap, Map<String, Anagrafica> abbonatiCa2020Map) throws UnsupportedOperationException {
        int i=0;
        for (String ancodice : abbonatiCa2020Map.keySet()) {
            i++;
            Anagrafica ca = abbonatiCa2020Map.get(ancodice);
            if (ca == null) {
                throw new UnsupportedOperationException("null Anagrafica for campagna 2020 ancodice: "+ancodice);
            }
            if (!elencoAbbonatiMap.containsKey(ancodice)) {
                throw new UnsupportedOperationException("Elenco Abbonati non contiene ancodice: "+ancodice);                
            }
            Anagrafica ac = elencoAbbonatiMap.get(ancodice);
            if (ac == null) {
                throw new UnsupportedOperationException("null Anagrafica for elenco abbonati ancodice: "+ancodice);
            }
            
            ca.setDiocesi(ac.getDiocesi());
            ca.setCodfis(ac.getCodfis());
            ca.setPiva(ac.getPiva());
            ca.setTelefono(ac.getTelefono());
            ca.setCellulare(ac.getCellulare());
            ca.setEmail(ac.getEmail());    
     
            ac.setTitolo(ca.getTitolo());

                ca.setDenominazione(ac.getDenominazione());
                ca.setNome(ac.getNome());
                ca.setCap(ac.getCap());
                ca.setIndirizzo(ac.getIndirizzo());
                ca.setProvincia(ac.getProvincia());
                ca.setCitta(ac.getCitta());
              
            if (ancodice.equals("0000061880") 
            || ancodice.equals("0000066055")
            || ancodice.equals("0000005008")
            || ancodice.equals("0000012438")
            || ancodice.equals("0000006605")
            || ancodice.equals("0000020992")
               ) {
                log.debug(ancodice+"--->"+i+"--->Updated acAnagrafica");
                ac.setIndirizzo(ca.getIndirizzo());
            }
            
            if (ancodice.equals("0000069501") 
                    || ancodice.equals("0000022252")
                    || ancodice.equals("0000072596")) {
                log.debug(ancodice+"--->"+i+"--->Updated caAnagrafica");
                ac.setIndirizzoSecondaRiga(ca.getIndirizzoSecondaRiga());
            }
        }

    }
    
    @Override
    public void run() {
        log.info("Start Loading Pubblicazioni Adp");
        loadPubblicazioniAdp();
        loadSpeseSpedizione();
        log.info("End Loading Pubblicazioni Adp");

        log.info("Start Import Adp");
        try {
            loadAnagraficaAdp();
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return;
        }
        log.info("End Import Adp");
    }
        
    public void loadAnagraficaAdp() throws Exception {
        log.info("Start Saving Anagrafica");
        Map<String, Anagrafica> eaMap = importElencoAbbonati();      
        Map<String,Row> rowMap = getCampagna2020();    
        Map<String, Anagrafica> caMap = importCampagna2020(rowMap);
        fixElencoAbbonatiCampagna(eaMap, caMap);
        Map<String,Anagrafica>  anagraficaMap = new HashMap<String, Anagrafica>();
        for (String key: eaMap.keySet()) {
            anagraficaMap.put(key, eaMap.get(key));
        }

        // Abbonamenti Estero
        Map<String,Row> aeRowMap = getAbbonatiEstero();
        Map<String, Anagrafica> aeMap =importAbbonatiEstero(aeRowMap);
        fixAbbonatiEstero(eaMap, aeMap);
        
        //Beneficiari Ita Estero
        Map<String, Anagrafica> acMap = importArchivioClienti();
        List<Row> airows = getAbbonatiItaEstero();
        Map<String, Anagrafica> aiMap = importAbbonatiItaEstero(airows);
        fixBeneficiari(acMap, eaMap, aiMap);
        for (String ancodice : aiMap.keySet()) {
            if (!anagraficaMap.containsKey(ancodice)) {
                anagraficaMap.put(ancodice, aiMap.get(ancodice));
            }
        }

        //Beneficiari
        List<Row> abrows = getBeneficiari();
        Map<String, Anagrafica> abMap = importBeneficiari(abrows);
        fixBeneficiari(acMap, eaMap, abMap);
        for (String ancodice : abMap.keySet()) {
            if (!anagraficaMap.containsKey(ancodice)) {
                anagraficaMap.put(ancodice, abMap.get(ancodice));
            }
        }

        //Omaggi Messaggi
        List<Row> omrows = getOmaggioMessaggio2020();
        List<Anagrafica> omlist = importOmaggio(omrows);
        Map<String,Anagrafica> omMap = fixOmaggio(anagraficaMap, acMap, omlist);
        for (String ancodice : omMap.keySet()) {
            if (!anagraficaMap.containsKey(ancodice)) {
                anagraficaMap.put(ancodice, omMap.get(ancodice));
            }
        }
        
        //Omaggi Gesuiti Messaggi
        List<Row> ogmrows = getOmaggioGesuitiMessaggio2020();
        List<Anagrafica> ogmlist = importOmaggio(ogmrows);
        Map<String,Anagrafica> ogmMap = fixOmaggio(anagraficaMap, acMap, ogmlist);
        for (String ancodice : ogmMap.keySet()) {
            if (!anagraficaMap.containsKey(ancodice)) {
                anagraficaMap.put(ancodice, ogmMap.get(ancodice));
            }
        }
       
        //Omaggi Blocchetti
        List<Row> obrows = getOmaggioBlocchetti2020();
        List<Anagrafica> oblist = importOmaggio(obrows);
        Map<String,Anagrafica> obMap = fixOmaggio(anagraficaMap, acMap, oblist);
        for (String ancodice : obMap.keySet()) {
            if (!anagraficaMap.containsKey(ancodice)) {
                anagraficaMap.put(ancodice, obMap.get(ancodice));
            }
        }

        //Omaggi Gesuiti Blocchetti
        List<Row> ogbrows = getOmaggioGesuitiBlocchetti2020();
        List<Anagrafica> ogblist = importOmaggio(ogbrows);
        Map<String,Anagrafica> ogbMap = fixOmaggio(anagraficaMap, acMap, ogblist);
        for (String ancodice : ogbMap.keySet()) {
            if (!anagraficaMap.containsKey(ancodice)) {
                anagraficaMap.put(ancodice, ogbMap.get(ancodice));
            }
        }
        
        //Omaggi Lodare
        List<Row> olrows = getOmaggioLodare2020();
        List<Anagrafica> ollist = importOmaggio(olrows);
        Map<String,Anagrafica> olMap = fixOmaggio(anagraficaMap, acMap, ollist); 
        for (String ancodice : olMap.keySet()) {
            if (!anagraficaMap.containsKey(ancodice)) {
                anagraficaMap.put(ancodice, olMap.get(ancodice));
            }
        }

        //Omaggi Gesuiti Manifesti
        List<Row> ogerows = getOmaggioGesuitiManifesti2020();
        List<Anagrafica> ogelist = importOmaggio(ogerows);
        Map<String,Anagrafica> ogeMap = fixOmaggio(anagraficaMap, acMap, ogelist);
        for (String ancodice : ogeMap.keySet()) {
            if (!anagraficaMap.containsKey(ancodice)) {
                anagraficaMap.put(ancodice, ogeMap.get(ancodice));
            }
        }
        anagraficaMap.values().forEach(a -> anagraficaDao.save(a));
        log.info("End Saving Anagrafica");

        log.info("Start Saving Storico Beneficiari");
        Set<String> bmcassa = getCategoriaBmCassa();
        SmdImportAdp
        .getStoriciFromBeneficiari2010(abrows, anagraficaMap, messaggio, lodare, blocchetti, estratti, bmcassa)        
        .stream().forEach(s -> {
            storicoDao.save(s);
            notaDao.save(SmdHelper.getNota(s));
        });        
        log.info("End Saving Storico Beneficiari");

        log.info("Start Saving Storico Estero");
        SmdImportAdp
        .getStoriciFromEstero2010(aeRowMap,anagraficaMap,messaggio,lodare,blocchetti,estratti)        
        .stream().forEach(s -> {
            storicoDao.save(s);
            notaDao.save(SmdHelper.getNota(s));
        });        
        log.info("End Saving Storico  Estero");

        log.info("Start Saving Storico Ita Estero");
        SmdImportAdp
        .getStoriciFromItaEstero2010(airows,anagraficaMap,messaggio,lodare,blocchetti,estratti)        
        .stream().forEach(s -> {
            storicoDao.save(s);
            notaDao.save(SmdHelper.getNota(s));
        });        
        log.info("End Saving Storico Ita Estero");

        log.info("Start Saving Storico Omaggio");
        getStoriciFromOmaggio(omrows, anagraficaMap, omMap,
                                                 messaggio,
                                                 InvioSpedizione.Spedizioniere,
                                                 TipoEstrattoConto.OmaggioCuriaDiocesiana)
        .stream().forEach(s -> {
            storicoDao.save(s);
            notaDao.save(SmdHelper.getNota(s));
        });
        
        getStoriciFromOmaggio(olrows, anagraficaMap, olMap,
                                                 lodare,
                                                 InvioSpedizione.Spedizioniere,
                                                 TipoEstrattoConto.OmaggioCuriaDiocesiana)
        .stream().forEach(s -> {
            storicoDao.save(s);
            notaDao.save(SmdHelper.getNota(s));
        });
        getStoriciFromOmaggio(obrows, anagraficaMap, obMap,
                                                 blocchetti,
                                                 InvioSpedizione.Spedizioniere,
                                                 TipoEstrattoConto.OmaggioCuriaDiocesiana)
        .stream().forEach(s -> {
            storicoDao.save(s);
            notaDao.save(SmdHelper.getNota(s));
        });

        getStoriciFromOmaggio(ogmrows, anagraficaMap,
                                                 ogmMap, messaggio,
                                                 InvioSpedizione.AdpSede,
                                                 TipoEstrattoConto.OmaggioGesuiti)
        .stream().forEach(s -> {
            storicoDao.save(s);
            notaDao.save(SmdHelper.getNota(s));
        });

        getStoriciFromOmaggio(ogbrows, anagraficaMap,
                                                 ogbMap, blocchetti,
                                                 InvioSpedizione.AdpSede,
                                                 TipoEstrattoConto.OmaggioGesuiti)
        .stream().forEach(s -> {
            storicoDao.save(s);
            notaDao.save(SmdHelper.getNota(s));
        });

        getStoriciFromOmaggio(ogerows, anagraficaMap,
                                                 ogeMap, estratti,
                                                 InvioSpedizione.AdpSede,
                                                 TipoEstrattoConto.OmaggioGesuiti)
        .stream().forEach(s -> {
            storicoDao.save(s);
            notaDao.save(SmdHelper.getNota(s));
        });
        log.info("End Saving Storico Omaggio");

        log.info("Start Generating Campagna");
        Campagna campagna = new Campagna();
        campagna.setAnno(Anno.ANNO2020);
        List<Pubblicazione> attivi = pubblicazioneDao.findAll().stream().filter(p -> p.isActive()
                && p.getTipo() != TipoPubblicazione.UNICO).collect(Collectors.toList());
        
        for (Pubblicazione pubb: attivi) {
            CampagnaItem ci = new CampagnaItem();
            ci.setPubblicazione(pubb);
            ci.setCampagna(campagna);
            campagna.addCampagnaItem(ci);
        }
        smdService.generaCampagnaAbbonamenti(campagna, attivi);
        log.info("End Generating Campagna");

        log.info("Start Fix codeline");
        List<Abbonamento> abbonamenti = abbonamentoDao.findAll();
        rowMap.keySet().forEach(cod ->
        {
            final String codeline = processRowCampagnaCodeline(rowMap.get(cod), cod);
            Anagrafica a = anagraficaMap.get(cod);
            abbonamenti
            .stream()
            .filter(abb-> abb.getIntestatario().getId().longValue() == a.getId().longValue())
            .forEach(abb -> {
                log.debug("Fix codeline: {}",codeline);
                abb.setCodeLine(codeline);
                abbonamentoDao.save(abb);
            });
            
        });
        log.info("End Fix codeline");
        
        log.info("Start Fix Spese");
        abbonamentoDao.findAll().forEach(abb ->{
            abb.setSpese(BigDecimal.ZERO);
            abbonamentoDao.save(abb);
        });

        Map<String,BigDecimal> fixSpeseEsteroMap = 
                fixSpeseEstero(aeRowMap);
        for (String cod: fixSpeseEsteroMap.keySet()) {
            Anagrafica intestatario = anagraficaMap.get(cod);
            abbonamentoDao.findByIntestatario(intestatario).forEach(abb -> {
                abb.setSpese(fixSpeseEsteroMap.get(cod));
                abbonamentoDao.save(abb);
            });
        }
        
        Map<String,BigDecimal> fixSpeseItaEsteroMap = 
                fixSpeseItaEstero(airows);
        for (String cod: fixSpeseItaEsteroMap.keySet()) {
            Anagrafica intestatario = anagraficaMap.get(cod);
            abbonamentoDao.findByIntestatario(intestatario).forEach(abb -> {
                abb.setSpese(fixSpeseItaEsteroMap.get(cod));
                abbonamentoDao.save(abb);
            });
        }       
        
        Map<String,BigDecimal> fixSpeseBeneficiariMap = 
                fixSpeseBeneficiari(abrows, messaggio, lodare, blocchetti, estratti);
        for (String cod: fixSpeseBeneficiariMap.keySet()) {
            Anagrafica intestatario = anagraficaMap.get(cod);
            abbonamentoDao.findByIntestatario(intestatario).forEach(abb -> {
                abb.setSpese(fixSpeseBeneficiariMap.get(cod));
                abbonamentoDao.save(abb);
            });
        }

        log.info("End Fix Spese");
    }


}
