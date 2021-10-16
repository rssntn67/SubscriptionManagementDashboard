package it.arsinfo.smd.entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Transient;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;

public interface SmdEntity {

    Logger log = LoggerFactory.getLogger(SmdEntity.class);

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

    public static NumberFormat getEuroCurrency() {
        return NumberFormat.getCurrencyInstance(getLocalFromISO("EUR"));
    }

    public static Locale getLocalFromISO(String iso4217code){
        Locale toReturn = null;
        for (Locale locale : NumberFormat.getAvailableLocales()) {
            String code = NumberFormat.getCurrencyInstance(locale).
                    getCurrency().getCurrencyCode();
            if (iso4217code.equals(code)) {
                toReturn = locale;
                break;
            }
        }
        return toReturn;
    }

}
