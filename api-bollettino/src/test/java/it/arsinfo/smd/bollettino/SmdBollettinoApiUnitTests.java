package it.arsinfo.smd.bollettino;

import com.google.common.io.CharStreams;
import it.arsinfo.smd.bollettino.impl.BollettinoServiceImpl;
import it.arsinfo.smd.entity.*;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Base64;
import java.util.Locale;

public class SmdBollettinoApiUnitTests {

    @Test
    public void TestTd674() throws IOException {
        String code = "2018099999110078";
        String saldo = NumberFormat.getNumberInstance(Locale.ITALY).format(new BigDecimal("17.89"));

        String ccpFilePath="/Users/antonio/Downloads/";
        String ccpApiUrl="https://api.stampabollettini.com/api/td674";
        String ccpApiKey="druslcruwaw2up5swexospl6awruphut";
        String ccpApiUser="adp-289020";

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(ccpApiUrl);

        Anagrafica gp = new Anagrafica();
        gp.setTitolo(TitoloAnagrafica.Parrocchia);
        gp.setDenominazione("IMMACOLATA CONCEZIONE E S.GIOVANNI B");
        gp.setNome("C.A.sigg.Giuseppina Bongini e Liboria Pintu Piberi");
        gp.setCodeLineBase(Anagrafica.generaCodeLineBase());
        gp.setDiocesi(Diocesi.DIOCESI116);
        gp.setIndirizzo("Piazza Sant'Ambrogio 1");
        gp.setCitta("Milano");
        gp.setProvincia(Provincia.MI);
        gp.setCap("20110");
        gp.setPaese(Paese.IT);
        gp.setEmail("gp@arsinfo.it");
        gp.setTelefono("+3902000010");

        StringEntity entity = new StringEntity(BollettinoServiceImpl.getCcpJsonString(ccpApiKey,ccpApiUser,code,gp, Ccp.UNO, "Abbonamenti 2020 - Importo da versare a Saldo: EUR " + saldo));
        httpPost.setEntity(entity);
        httpPost.setHeader("Content-type", "application/json");

        CloseableHttpResponse response = client.execute(httpPost);
        Assertions.assertEquals(response.getStatusLine().getStatusCode(), 200);
        InputStream inputStream =response.getEntity().getContent();
        String text;
        try (Reader reader = new InputStreamReader(inputStream)) {
            text = CharStreams.toString(reader);
        }
        File file = new File(ccpFilePath+code+"R.pdf");
        FileOutputStream fos = new FileOutputStream(file);
        byte[] decoder = Base64.getDecoder().decode(text);
        fos.write(decoder);
        client.close();

        Assertions.assertTrue(file.isFile());
    }


}
