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


}
