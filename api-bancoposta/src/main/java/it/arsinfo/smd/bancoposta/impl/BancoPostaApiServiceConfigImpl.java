package it.arsinfo.smd.bancoposta.impl;

import it.arsinfo.smd.bancoposta.config.BancoPostaApiServiceConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BancoPostaApiServiceConfigImpl implements BancoPostaApiServiceConfig {

    @Value("${upload.file.path}")
    private String uploadFilePath;

    public String getUploadFilePath() {
        return uploadFilePath;
    }

}
