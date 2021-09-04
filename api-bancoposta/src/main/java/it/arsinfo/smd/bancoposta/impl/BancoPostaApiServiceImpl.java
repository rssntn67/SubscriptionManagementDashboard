package it.arsinfo.smd.bancoposta.impl;

import it.arsinfo.smd.bancoposta.api.BancoPostaApiService;
import it.arsinfo.smd.bancoposta.config.BancoPostaApiServiceConfig;
import it.arsinfo.smd.entity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class BancoPostaApiServiceImpl implements BancoPostaApiService {

    private static final Logger log = LoggerFactory.getLogger(BancoPostaApiService.class);
    @Autowired
    private BancoPostaApiServiceConfig config;

    @Override
    public File getFile(String filename) {
        return new File(config.getUploadFilePath() +"/"+ filename);
    }

    @Override
    public List<DistintaVersamento> uploadIncasso(File file) throws FileNotFoundException, UnsupportedOperationException {
        List<DistintaVersamento> incassi = new ArrayList<>();
        FileInputStream fstream = new FileInputStream(file);

        //Read File Line By Line
        try (BufferedReader br = new BufferedReader(new InputStreamReader(fstream))) {
            String strLine;
            Set<String> versamentiLine = new HashSet<>();
            while ((strLine = br.readLine()) != null) {
                if (strLine.trim().equals("")) {
                    log.warn("uploadIncasso: Riga vuota!");
                } else if (isVersamento(strLine)) {
                    versamentiLine.add(strLine);
                } else if (isRiepilogo(strLine)) {
                    incassi.add(generaIncasso(versamentiLine, strLine));
                    versamentiLine.clear();
                } else {
                    throw new UnsupportedOperationException("Valore non riconosciuto->" + strLine);
                }
            }
        } catch (Exception e) {
            log.error("uploadIncasso:: Incasso da File Cancellato: " + e.getMessage());
            throw new UnsupportedOperationException(e);
        }
        return incassi;
    }

    public static boolean isVersamento(String versamento) {
        return (
                versamento != null && versamento.length() == 200
                        && (versamento.trim().length() == 82 || versamento.trim().length() == 89));
    }

    public static boolean isRiepilogo(String riepilogo) {
        return ( riepilogo != null &&
                riepilogo.length() == 200 &&
                riepilogo.trim().length() == 96 &&
                riepilogo.substring(19,33).trim().length() == 0 &&
                riepilogo.startsWith("999", 33)
        );
    }

    public static DistintaVersamento generaIncasso(Set<String> versamenti,
                                                   String riepilogo) throws UnsupportedOperationException {
        final DistintaVersamento incasso = new DistintaVersamento();
        incasso.setCassa(Cassa.Ccp);
        incasso.setCuas(Cuas.getCuas(Integer.parseInt(riepilogo.substring(0,1))));
        incasso.setCcp(Ccp.getByCc(riepilogo.substring(1,13)));
        incasso.setDataContabile(SmdEntity.getStandardDate(riepilogo.substring(13,19)));
        incasso.setDocumenti(Integer.parseInt(riepilogo.substring(36,44)));
        incasso.setImporto(new BigDecimal(riepilogo.substring(44,54)
                + "." + riepilogo.substring(54,56)));

        incasso.setEsatti(Integer.parseInt(riepilogo.substring(56,64)));
        incasso.setImportoEsatti(new BigDecimal(riepilogo.substring(64,74)
                + "." + riepilogo.substring(74, 76)));

        incasso.setErrati(Integer.parseInt(riepilogo.substring(76,84)));
        incasso.setImportoErrati(new BigDecimal(riepilogo.substring(84,94)
                + "." + riepilogo.substring(94, 96)));
        log.info("generaIncasso: {}", incasso);

        versamenti.
                forEach(s -> incasso.addItem(generateVersamento(incasso,s)));
        checkIncasso(incasso);
        return incasso;
    }

    private static void checkIncasso(DistintaVersamento incasso) throws UnsupportedOperationException {
        BigDecimal importoVersamenti = BigDecimal.ZERO;
        for (Versamento v: incasso.getItems()) {
            importoVersamenti = importoVersamenti.add(v.getImporto());
        }
        if (incasso.getImporto().subtract(importoVersamenti).signum() != 0 ) {
            log.error("checkincasso: importo incasso {} non corrisponde a importoVersamenti {}",incasso.getImporto(),importoVersamenti);
            throw new UnsupportedOperationException("Importo Incasso e Versamento non corrispondono ");
        }
    }

    private static Versamento generateVersamento(DistintaVersamento incasso,String value)
    {
        Versamento versamento = new Versamento(incasso,new BigDecimal(value.substring(36, 44) + "." + value.substring(44, 46)));
        versamento.setBobina(value.substring(0, 3));
        versamento.setProgressivoBobina(value.substring(3, 8));
        versamento.setProgressivo(value.substring(8,15));
        versamento.setDataPagamento(SmdEntity.getStandardDate(value.substring(27,33)));
        versamento.setBollettino(Bollettino.getTipoBollettino(Integer.parseInt(value.substring(33,36))));
        versamento.setProvincia(value.substring(46, 49));
        versamento.setUfficio(value.substring(49, 52));
        versamento.setSportello(value.substring(52, 54));
//          value.substring(54,55);
        versamento.setDataContabile(SmdEntity.getStandardDate(value.substring(55,61)));
        versamento.setCodeLine(value.substring(61,79));
        versamento.setAccettazione(Accettazione.getTipoAccettazione(value.substring(79,81)));
        versamento.setSostitutivo(Sostitutivo.getTipoAccettazione(value.substring(81,82)));
        log.info("generateVersamento: {}", versamento);
        return versamento;
    }

}
