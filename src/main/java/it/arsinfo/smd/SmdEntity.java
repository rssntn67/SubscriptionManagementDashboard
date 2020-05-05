package it.arsinfo.smd;

public interface SmdEntity {
    
    Long getId();

    static String decodeForGrid(boolean status) {
        if (status) {
            return "si";
        }
        return "no";
    }

}
