package it.arsinfo.smd.entity;

import javax.persistence.Transient;

public interface SmdEntity {
    
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
