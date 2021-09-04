package it.arsinfo.smd.service.impl;

import it.arsinfo.smd.service.config.CampagnaServiceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
public class CampagnaServiceConfigImpl implements CampagnaServiceConfig {
    @Value("${limite.invio.estratto:7.00}")
    private String limiteInvioEstratto;

    @Value("${limite.invio.sollecito:7.00}")
    private String limiteInvioSollecito;

    @Value("${spese.estratto.conto:0.00}")
    private String speseEstrattoConto;

    @Value("${spese.sollecito:2.00}")
    private String speseSollecito;

    private static final Logger log = LoggerFactory.getLogger(CampagnaServiceConfigImpl.class);

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

    @Override
    public BigDecimal getSpeseEstrattoConto() {
        try {
            return new BigDecimal(speseEstrattoConto);
        } catch (Exception e) {
            log.error("getSpeseEstrattoconto: {}", e.getMessage());
        }
        return new BigDecimal("0.00");
    }

    @Override
    public BigDecimal getSpeseSollecito() {
        try {
            return new BigDecimal(speseSollecito);
        } catch (Exception e) {
            log.error("getSpeseSollecito: {}", e.getMessage());
        }
        return new BigDecimal("2.00");
    }


}
