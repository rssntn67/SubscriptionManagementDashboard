package it.arsinfo.smd.bollettino.impl;

import it.arsinfo.smd.bollettino.config.BollettinoApiServiceConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BollettinoApiServiceConfigImpl implements BollettinoApiServiceConfig {

    @Value("${ccp.api.url}")
    private String ccpApiUrl;

    @Value("${ccp.api.key}")
    private String ccpApiKey;

    @Value("${ccp.api.user}")
    private String ccpApiUser;

    @Value("${ccp.file.path}")
    private String ccpFilePath;

    public String getCcpApiKey() {
        return ccpApiKey;
    }

    public String getCcpApiUser() {
        return ccpApiUser;
    }

    public String getCcpFilePath() {
        return ccpFilePath;
    }

    public String getCcpApiUrl() {
        return ccpApiUrl;
    }

}
