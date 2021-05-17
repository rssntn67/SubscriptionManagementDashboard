package it.arsinfo.smd.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

public interface CcpConfig {
    String getCcpApiKey();
    String getCcpApiUser();
    String getCcpFilePath();
    String getCcpApiUrl();
}
