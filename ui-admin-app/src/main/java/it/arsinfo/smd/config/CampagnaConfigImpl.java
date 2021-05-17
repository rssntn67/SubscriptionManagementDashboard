package it.arsinfo.smd.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
public class CampagnaConfigImpl implements CampagnaConfig {
    @Value("${limite.invio.estratto:7.00}")
    private String limiteInvioEstratto;

    @Value("${limite.invio.sollecito:7.00}")
    private String limiteInvioSollecito;

    private static final Logger log = LoggerFactory.getLogger(CampagnaConfigImpl.class);

    public BigDecimal getLimiteInvioEstratto() {
        try {
            return new BigDecimal(limiteInvioEstratto);
        } catch (Exception e) {
            log.error("getLimiteInvioEstratto: {}", e.getMessage());
        }
        return new BigDecimal("7.00");
    }

    public BigDecimal getLimiteInvioSollecito() {
        try {
            return new BigDecimal(limiteInvioSollecito);
        } catch (Exception e) {
            log.error("getLimiteInvioSollecito: {}", e.getMessage());
        }
        return new BigDecimal("7.00");
    }


}
