package it.arsinfo.smd.bollettino.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

public interface BollettinoServiceConfig {
    String getCcpApiKey();
    String getCcpApiUser();
    String getCcpFilePath();
    String getCcpApiUrl();
}
