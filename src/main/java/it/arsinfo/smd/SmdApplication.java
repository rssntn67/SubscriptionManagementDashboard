package it.arsinfo.smd;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.ContoCorrentePostale;
import it.arsinfo.smd.data.Cuas;
import it.arsinfo.smd.data.Diocesi;
import it.arsinfo.smd.data.Mese;
import it.arsinfo.smd.data.TipoAccettazioneBollettino;
import it.arsinfo.smd.data.TipoDocumentoBollettino;
import it.arsinfo.smd.data.TipoPubblicazione;
import it.arsinfo.smd.data.TipoSostitutivoBollettino;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.AnagraficaPubblicazione;
import it.arsinfo.smd.entity.Incasso;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.entity.Versamento;
import it.arsinfo.smd.repository.AbbonamentoDao;
import it.arsinfo.smd.repository.AnagraficaDao;
import it.arsinfo.smd.repository.AnagraficaPubblicazioneDao;
import it.arsinfo.smd.repository.CampagnaDao;
import it.arsinfo.smd.repository.IncassoDao;
import it.arsinfo.smd.repository.PubblicazioneDao;
import it.arsinfo.smd.repository.VersamentoDao;

@SpringBootApplication
public class SmdApplication {

    private static final Logger log = LoggerFactory.getLogger(SmdApplication.class);

    public static void addAbbonamentoPubblicazione(Abbonamento abbonamento, Pubblicazione pubblicazione, int numero) {
        if (abbonamento == null || pubblicazione == null || numero <= 0) {
            return;
        }
        abbonamento.addPubblicazione(pubblicazione, numero); 
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
        log.info("lunghezza:"+versamento.length());
        log.info("lunghezza:"+versamento.trim().length());
        
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
        incasso.setCcp(ContoCorrentePostale.getByCcp(riepilogo.substring(1,
                                                                         13)));
        incasso.setDataContabile(formatter.parse(riepilogo.substring(13,
                                                                     19)));
//	    String filler = riepilogo.substring(19,33);
//	    String idriepilogo = riepilogo.substring(33,36);
        incasso.setTotaleDocumenti(Integer.parseInt(riepilogo.substring(36,
                                                                        44)));
        incasso.setTotaleImporto(new BigDecimal(riepilogo.substring(44, 54)
                + "." + riepilogo.substring(54, 56)));

        incasso.setDocumentiEsatti(Integer.parseInt(riepilogo.substring(56,
                                                                        64)));
        incasso.setImportoDocumentiEsatti(new BigDecimal(riepilogo.substring(64,
                                                                             74)
                + "." + riepilogo.substring(74, 76)));

        incasso.setDocumentiErrati(Integer.parseInt(riepilogo.substring(76,
                                                                        84)));
        incasso.setImportoDocumentiErrati(new BigDecimal(riepilogo.substring(84,
                                                                             94)
                + "." + riepilogo.substring(94, 96)));

        versamenti.stream().forEach(s -> {
            try {
                incasso.getVersamenti().add(generateVersamento(incasso,s));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });
        return incasso;
    }

    private static Versamento generateVersamento(Incasso incasso,String value)
            throws ParseException {
        DateFormat formatter = new SimpleDateFormat("yyMMdd");
        Versamento versamento = new Versamento(incasso);
        versamento.setBobina(value.substring(0, 3));
        versamento.setProgressivoBobina(value.substring(3, 8));
	String progressivo = value.substring(8,15);
	versamento.setProgressivo(progressivo);
	versamento.setErrore(progressivo.equals("9999999"));
        versamento.setCcp(ContoCorrentePostale.getByCcp(value.substring(15,27)));
        versamento.setDataPagamento(formatter.parse(value.substring(27, 33)));
        versamento.setTipoDocumento(TipoDocumentoBollettino.getTipoBollettino(Integer.parseInt(value.substring(33,36))));
        versamento.setImporto(new BigDecimal(value.substring(36, 44) + "." + value.substring(44, 46)));
        versamento.setProvincia(value.substring(46, 49));
        versamento.setUfficio(value.substring(49, 52));
        versamento.setSportello(value.substring(52, 54));
//          value.substring(54,55);
        versamento.setDataContabile(formatter.parse(value.substring(55,61)));
        String campo =value.substring(61,79);
        versamento.setCampo(campo);
        versamento.setCampovalido(checkCampo(campo));
        versamento.setTipoAccettazione(TipoAccettazioneBollettino.getTipoAccettazione(value.substring(79,81)));
        versamento.setTipoSostitutivo(TipoSostitutivoBollettino.getTipoAccettazione(value.substring(81,82)));
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

    public static int getNumeroPubblicazioni(Abbonamento abb,
            Pubblicazione pub) {
        int numero = 0;
        switch (pub.getTipo()) {
        case ANNUALE:
            if (abb.getInizio().getPosizione() < pub.getPrimaPubblicazione().getPosizione()
                    && abb.getFine().getPosizione() > pub.getPrimaPubblicazione().getPosizione()) {
                numero = 1;
            }
            break;
        case SEMESTRALE:
            if (abb.getInizio().getPosizione() < pub.getPrimaPubblicazione().getPosizione()
                    && abb.getFine().getPosizione() > pub.getPrimaPubblicazione().getPosizione()) {
                numero += 1;
            }
            if (abb.getFine().getPosizione() > pub.getPrimaPubblicazione().getPosizione()
                    + 6) {
                numero += 1;
            }
            break;
        case MENSILE:
            numero = abb.getFine().getPosizione()
                    - abb.getInizio().getPosizione() + 1;
            break;
        case UNICO:
            numero = 1;
            break;
        default:
            break;
        }
        return numero;
    }

    //FIXME
    public static BigDecimal generaCosto(Abbonamento abb) {
        double costo = 0.0;
            costo = abb.
                    getListaAbbonamentoPubblicazione().
                    stream()
                    .mapToDouble(
                                 abbpubbl -> 
                                 abbpubbl.getPubblicazione().getCostoUnitario().doubleValue()
                                 * abbpubbl.getNumero().doubleValue()
                                 * getNumeroPubblicazioni(abb, abbpubbl.getPubblicazione())).sum();

        return BigDecimal.valueOf(costo).add(abb.getSpese());
    }

    public static void main(String[] args) {
        SpringApplication.run(SmdApplication.class, args);
    }

    @Bean
    @Transactional
    public CommandLineRunner loadData(AnagraficaDao anagraficaDao, AnagraficaPubblicazioneDao anagraficaPubblicazioneDao,
            PubblicazioneDao pubblicazioneDao, AbbonamentoDao abbonamentoDao,
            CampagnaDao campagnaDao, IncassoDao incassoDao, VersamentoDao versamentoDao) {
        return (args) -> {
            // save a couple of customers

            Anagrafica jb = new Anagrafica("Jack", "Bauer");
            jb.setDiocesi(Diocesi.DIOCESI116);
            jb.setIndirizzo("Piazza Duomo 1");
            jb.setCitta("Milano");
            jb.setCap("20100");
            jb.setEmail("jb@adp.it");
            jb.setTelefono("+3902000009");
            anagraficaDao.save(jb);

            Anagrafica co = new Anagrafica("Chloe", "O'Brian");
            co.setDiocesi(Diocesi.DIOCESI116);
            co.setIndirizzo("Piazza Sant'Ambrogio 1");
            co.setCitta("Milano");
            co.setCap("20110");
            co.setEmail("co@adp.it");
            co.setTelefono("+3902000010");
            anagraficaDao.save(co);

            Anagrafica kb = new Anagrafica("Kim", "Bauer");
            kb.setDiocesi(Diocesi.DIOCESI168);
            kb.setIndirizzo("Piazza del Gesu' 1");
            kb.setCitta("Roma");
            kb.setCap("00192");
            kb.setEmail("kb@adp.it");
            kb.setTelefono("+3906000020");
            anagraficaDao.save(kb);

            Anagrafica dp = new Anagrafica("David", "Palmer");
            dp.setDiocesi(Diocesi.DIOCESI168);
            dp.setIndirizzo("Piazza Navona 3, 00100 Roma");
            dp.setCitta("Roma");
            dp.setCap("00195");
            dp.setEmail("dp@adp.it");
            dp.setTelefono("+3906000020");
            anagraficaDao.save(dp);

            Anagrafica md = new Anagrafica("Michelle", "Dessler");
            md.setDiocesi(Diocesi.DIOCESI126);
            md.setIndirizzo("Via Duomo 10");
            md.setCitta("Napoli");
            md.setCap("80135");
            md.setEmail("md@adp.it");
            md.setTelefono("+39081400022");
            anagraficaDao.save(md);

            Pubblicazione blocchetti = new Pubblicazione("Blocchetti",
                                                         TipoPubblicazione.SEMESTRALE);
            blocchetti.setActive(true);
            blocchetti.setAbbonamento(true);
            blocchetti.setCosto(new BigDecimal(3.00));
            blocchetti.setCostoScontato(new BigDecimal(2.40));
            blocchetti.setEditore("ADP");
            blocchetti.setPrimaPubblicazione(Mese.MARZO);
            pubblicazioneDao.save(blocchetti);

            Pubblicazione estratti = new Pubblicazione("Estratti",
                                                       TipoPubblicazione.ANNUALE);
            estratti.setActive(true);
            estratti.setAbbonamento(true);
            estratti.setCosto(new BigDecimal(10.00));
            estratti.setCostoScontato(new BigDecimal(10.00));
            estratti.setEditore("ADP");
            estratti.setPrimaPubblicazione(Mese.LUGLIO);
            pubblicazioneDao.save(estratti);

            Pubblicazione messaggio = new Pubblicazione("Messaggio",
                                                        TipoPubblicazione.MENSILE);
            messaggio.setActive(true);
            messaggio.setAbbonamento(true);
            messaggio.setCosto(new BigDecimal(1.25));
            messaggio.setCostoScontato(new BigDecimal(1.25));
            messaggio.setEditore("ADP");
            messaggio.setPrimaPubblicazione(Mese.GENNAIO);
            pubblicazioneDao.save(messaggio);

            Pubblicazione lodare = new Pubblicazione("Lodare e Servire",
                                                     TipoPubblicazione.MENSILE);
            lodare.setActive(true);
            lodare.setAbbonamento(true);
            lodare.setCosto(new BigDecimal(1.50));
            lodare.setCostoScontato(new BigDecimal(1.50));
            lodare.setEditore("ADP");
            lodare.setPrimaPubblicazione(Mese.GENNAIO);
            pubblicazioneDao.save(lodare);
            
            anagraficaPubblicazioneDao.save(new AnagraficaPubblicazione(md, blocchetti, 10));
            anagraficaPubblicazioneDao.save(new AnagraficaPubblicazione(md, dp, blocchetti, 5));

            Abbonamento abbonamentoMd = new Abbonamento(md);
            abbonamentoMd.addPubblicazione(blocchetti,1);
            abbonamentoMd.addPubblicazione(lodare,1);
            abbonamentoMd.setInizio(Mese.GIUGNO);
            abbonamentoMd.setAnno(Anno.ANNO2018);
            abbonamentoMd.setCampo(generateCampo(abbonamentoMd.getAnno(),
                                                 abbonamentoMd.getInizio(),
                                                 abbonamentoMd.getFine()));
            abbonamentoMd.setCost(generaCosto(abbonamentoMd));
            abbonamentoDao.save(abbonamentoMd);

            Abbonamento abbonamentoCo = new Abbonamento(co);
            abbonamentoCo.addPubblicazione(blocchetti,10);
            abbonamentoCo.addPubblicazione(lodare,10);
            abbonamentoCo.addPubblicazione(estratti,5);
            abbonamentoCo.addPubblicazione(messaggio,5);
            abbonamentoCo.setAnno(Anno.ANNO2018);
            abbonamentoCo.setCampo(generateCampo(abbonamentoCo.getAnno(),
                                                 abbonamentoCo.getInizio(),
                                                 abbonamentoCo.getFine()));
            abbonamentoCo.setCost(generaCosto(abbonamentoCo));
            abbonamentoDao.save(abbonamentoCo);

            Abbonamento abbonamentoDp = new Abbonamento(dp);
            abbonamentoDp.addPubblicazione(blocchetti,10);
            abbonamentoDp.setSpese(new BigDecimal("3.75"));
            abbonamentoDp.setInizio(Mese.MAGGIO);
            abbonamentoDp.setAnno(Anno.ANNO2018);
            abbonamentoDp.setCampo(generateCampo(abbonamentoDp.getAnno(),
                                                 abbonamentoDp.getInizio(),
                                                 abbonamentoDp.getFine()));
            abbonamentoDp.setCost(generaCosto(abbonamentoDp));
            abbonamentoDao.save(abbonamentoDp);

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
            // fetch all customers
            
            Incasso incasso4=generateIncasso(versamenti4, riepilogo4);
            incassoDao.save(incasso4);
            log.info("Customers found with findAll():");
            log.info("-------------------------------");
            for (Anagrafica customer : anagraficaDao.findAll()) {
                log.info(customer.toString());
            }
            log.info("");

            // fetch an individual customer by ID
            Anagrafica firstcustomer = anagraficaDao.findById(1L).get();
            log.info("Customer found with findOne(1L):");
            log.info("--------------------------------");
            log.info(firstcustomer.toString());
            log.info("");

            // fetch customers by last name
            log.info("Customer found with findByLastNameStartsWithIgnoreCase('Bauer'):");
            log.info("--------------------------------------------");
            for (Anagrafica bauer : anagraficaDao.findByCognomeStartsWithIgnoreCase("Bauer")) {
                log.info(bauer.toString());
            }
            log.info("");

            log.info("Customer found with findByDiocesi('ROMA'):");
            log.info("--------------------------------------------");
            for (Anagrafica roma : anagraficaDao.findByDiocesi(Diocesi.DIOCESI168)) {
                log.info(roma.toString());
            }
            log.info("");

            // fetch all pubblicazioni
            log.info("Pubblicazioni found with findAll():");
            log.info("-------------------------------");
            for (Pubblicazione pubblicazione : pubblicazioneDao.findAll()) {
                log.info(pubblicazione.toString());
            }
            log.info("");

            // fetch an individual pubblicazione by ID
            Pubblicazione pubblicazione1 = pubblicazioneDao.findById(6L).get();
            log.info("Pubblicazione found with findOne(6L):");
            log.info("--------------------------------");
            log.info(pubblicazione1.toString());
            log.info("");

            // fetch customers by last name
            log.info("Pubblicazione found with findByNameStartsWithIgnoreCase('ADP'):");
            log.info("--------------------------------------------");
            for (Pubblicazione adp : pubblicazioneDao.findByNomeStartsWithIgnoreCase("ADP")) {
                log.info(adp.toString());
            }
            log.info("");

            log.info("Pubblicazione found with findByTipo('MENSILE'):");
            log.info("--------------------------------------------");
            for (Pubblicazione mensile : pubblicazioneDao.findByTipo(TipoPubblicazione.MENSILE)) {
                log.info(mensile.toString());
            }
            log.info("");

            log.info("AnagraficaPubblicazione found with findByIntestatario('md'):");
            log.info("--------------------------------------------");
            for (AnagraficaPubblicazione anp : anagraficaPubblicazioneDao.findByIntestatario(md)) {
                log.info(anp.toString());
            }
            log.info("");

            log.info("AnagraficaPubblicazione found with findByDestinatario('dp'):");
            log.info("--------------------------------------------");
            for (AnagraficaPubblicazione anp : anagraficaPubblicazioneDao.findByDestinatario(dp)) {
                log.info(anp.toString());
            }
            log.info("");

            log.info("AnagraficaPubblicazione found with findByPubblicazione('blocchetti'):");
            log.info("--------------------------------------------");
            for (AnagraficaPubblicazione anp : anagraficaPubblicazioneDao.findByPubblicazione(blocchetti)) {
                log.info(anp.toString());
            }
            log.info("");

            // fetch all customers
            log.info("Abbonamenti found with findAll():");
            log.info("-------------------------------");
            for (Abbonamento abbonamento : abbonamentoDao.findAll()) {
                log.info(abbonamento.toString());
            }

            log.info("Abbonamenti found with findByIntestatario(Md):");
            log.info("-------------------------------");
            for (Abbonamento abbonamentomd : abbonamentoDao.findByIntestatario(md)) {
                log.info(abbonamentomd.toString());
            }

            log.info("");

            log.info("Versamenti:");
            log.info("-------------------------------");
            for (Versamento versamento : versamentoDao.findAll()) {
                log.info(versamento.toString());
            }

            log.info("");

            log.info("Incassi:");
            log.info("-------------------------------");
            for (Incasso incasso : incassoDao.findAll()) {
                log.info(incasso.toString());
            }

            log.info("");
            
            log.info("Versamenti found by incasso1:");
            log.info("-------------------------------");
            for (Versamento versamento : versamentoDao.findByIncasso(incasso1)) {
                log.info(versamento.toString());
            }

            log.info("");
            
            log.info("Versamenti found by importo 40.00:");
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
            


        };
    }
}
