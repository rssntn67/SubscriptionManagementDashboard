package it.arsinfo.smd.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CcpConfig {

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

    public void setCcpApiUrl(String ccpApiUrl) {
        this.ccpApiUrl = ccpApiUrl;
    }

    public void setCcpApiKey(String ccpApiKey) {
        this.ccpApiKey = ccpApiKey;
    }

    public void setCcpApiUser(String ccpApiUser) {
        this.ccpApiUser = ccpApiUser;
    }

    public void setCcpFilePath(String ccpFilePath) {
        this.ccpFilePath = ccpFilePath;
    }
}