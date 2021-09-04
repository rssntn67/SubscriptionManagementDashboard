package it.arsinfo.smd.service.config;

import java.math.BigDecimal;

public interface CampagnaServiceConfig {
    BigDecimal getLimiteInvioEstratto();
    BigDecimal getLimiteInvioSollecito();
    BigDecimal getSpeseEstrattoConto();
    BigDecimal getSpeseSollecito();
}
