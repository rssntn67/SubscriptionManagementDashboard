package it.arsinfo.smd.bancoposta.impl;

import it.arsinfo.smd.bancoposta.config.BancoPostaServiceConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BancoPostaServiceConfigImpl implements BancoPostaServiceConfig {

    @Value("${upload.file.path}")
    private String uploadFilePath;

    public String getUploadFilePath() {
        return uploadFilePath;
    }

}
