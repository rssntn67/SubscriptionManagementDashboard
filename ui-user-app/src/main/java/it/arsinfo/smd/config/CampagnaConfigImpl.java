package it.arsinfo.smd.config;

import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
public class CampagnaConfigImpl implements CampagnaConfig {

    public BigDecimal getLimiteInvioEstratto() {
        return new BigDecimal("7.00");
    }

    public BigDecimal getLimiteInvioSollecito() {
        return new BigDecimal("7.00");
    }

    @Override
    public BigDecimal getSpeseEstrattoConto() {
        return new BigDecimal("0.00");
    }

    @Override
    public BigDecimal getSpeseSollecito() {
        return new BigDecimal("2.00");
    }


}
