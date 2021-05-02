package it.arsinfo.smd.bollettino.impl;

import com.google.common.io.CharStreams;
import it.arsinfo.smd.bollettino.api.BollettinoService;
import it.arsinfo.smd.config.CcpConfig;
import it.arsinfo.smd.data.Ccp;
import it.arsinfo.smd.entity.Anagrafica;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Base64;

@Service
public class BollettinoServiceImpl implements BollettinoService {

	public static String getCcpJsonString(CcpConfig ccpConfig, String code, Anagrafica anagrafica, Ccp ccp, String reason) {
		return "{" +
				"\"apiKey\":\""+ccpConfig.getCcpApiKey()+"\"," +
				"\"apiUser\":\""+ccpConfig.getCcpApiUser()+"\","+
				"\"checkingAccount\": \""+ ccp.getCc()+"\","+
				"\"iban\": \""+ccp.getIban()+"\","+
				"\"accountHolder1\": \""+Ccp.intestazioneCcp+"\","+
				"\"accountHolder2\": \"\","+
				"\"accountAuthorizationCode\": \""+Ccp.accountAuthorizationCode+"\","+
				"\"code\": \""+code+"\","+
				"\"name\": \""+anagrafica.getIntestazione().substring(0,52)+"\","+
				"\"address\": \""+anagrafica.getIndirizzo()+"\","+
				"\"zip\": \""+anagrafica.getCap()+"\","+
				"\"city\": \""+anagrafica.getCitta()+"\","+
				"\"province\": \""+anagrafica.getProvincia().name()+"\","+
				"\"reason\": \"cod. "+anagrafica.getCodeLineBase()+"\\n"+reason+ "\","+
				"\"dueDate\": \"\"}";
	}

	private static final Logger log = LoggerFactory.getLogger(BollettinoServiceImpl.class);

	@Override
	public void getBollettino(CcpConfig ccpConfig, String code, Anagrafica anagrafica, Ccp ccp, String reason) {
		log.info("getBollettino: {} {} reason: {}", ccp,code,reason);
		File file = getFile(ccpConfig,code,anagrafica,ccp,reason);
		if (!file.exists()) {
			try {
				downloadBollettino(ccpConfig, code, anagrafica, ccp, reason, file);
			} catch (IOException e) {
				log.error("getBollettino: {}", e.getMessage(),e);
			}
		}
	}

	@Override
	public File getFile(CcpConfig ccpConfig, String code, Anagrafica anagrafica, Ccp ccp, String reason) {
		File file = new File(ccpConfig.getCcpFilePath()+"/"+code+"_"+reason.trim()+".pdf");
		log.info("getFile: {}", file.getAbsolutePath());
		return file;
	}


	private void downloadBollettino(CcpConfig ccpConfig, String code, Anagrafica anagrafica, Ccp ccp, String reason, File file) throws IOException {
		CloseableHttpClient client = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(ccpConfig.getCcpApiUrl());
		log.info("downloadBollettino: {} {} reason: {}", ccp,code,reason);
		String jsonString = getCcpJsonString(ccpConfig,code,anagrafica,ccp, reason);
		log.info(jsonString);
		StringEntity entity = new StringEntity(jsonString);
		httpPost.setEntity(entity);
		httpPost.setHeader("Content-type", "application/json");

		CloseableHttpResponse response = client.execute(httpPost);
		InputStream inputStream =response.getEntity().getContent();
		String text;
		try (Reader reader = new InputStreamReader(inputStream)) {
			text = CharStreams.toString(reader);
		}
		FileOutputStream fos = new FileOutputStream(file);
		byte[] decoder = Base64.getDecoder().decode(text);
		fos.write(decoder);
		client.close();
	}

}
