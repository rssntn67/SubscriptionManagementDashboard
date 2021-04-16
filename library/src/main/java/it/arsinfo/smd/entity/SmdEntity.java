package it.arsinfo.smd.entity;

import it.arsinfo.smd.data.Incassato;
import it.arsinfo.smd.service.Smd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Transient;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public interface SmdEntity {

    static final Logger log = LoggerFactory.getLogger(SmdEntity.class);

    DateFormat formatter = new SimpleDateFormat("yyMMddH");
    DateFormat unformatter = new SimpleDateFormat("yyMMdd");

    static Date getStandardDate(LocalDate localDate) {
        return getStandardDate(Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
    }

    static Date getStandardDate(Date date) {
        return getStandardDate(unformatter.format(date));
    }

    static Date getStandardDate(String yyMMdd) {
        try {
            return formatter.parse(yyMMdd+"8");
        } catch (ParseException e) {
            log.error("getStandardDate: {}", e.getMessage());
        }
        return null;
    }

    Long getId();
    
    @Transient
    String getHeader();

    static String decodeForGrid(boolean status) {
        if (status) {
            return "si";
        }
        return "no";
    }
}
