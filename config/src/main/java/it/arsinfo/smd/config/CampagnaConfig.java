package it.arsinfo.smd.config;

import java.math.BigDecimal;

public interface CampagnaConfig {
    BigDecimal getLimiteInvioEstratto();
    BigDecimal getLimiteInvioSollecito();
    BigDecimal getSpeseEstrattoConto();
    BigDecimal getSpeseSollecito();
}
