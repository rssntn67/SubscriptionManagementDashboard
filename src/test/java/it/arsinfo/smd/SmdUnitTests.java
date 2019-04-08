package it.arsinfo.smd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;
import java.util.EnumSet;

import org.junit.Test;

import it.arsinfo.smd.data.Mese;
import it.arsinfo.smd.data.TipoPubblicazione;
import it.arsinfo.smd.entity.Pubblicazione;
public class SmdUnitTests {
    
    @Test
    public void testPubblicazione() {
 
        Pubblicazione pe = new Pubblicazione("pe", TipoPubblicazione.MENSILE);
        pe.setMese(Mese.FEBBRAIO);
        assertEquals(EnumSet.allOf(Mese.class), pe.getMesiPubblicazione());

        
        Pubblicazione pd = new Pubblicazione("pd", TipoPubblicazione.MENSILE);
        pd.setMese(Mese.NOVEMBRE);
        assertEquals(EnumSet.allOf(Mese.class), pe.getMesiPubblicazione());

        Pubblicazione pa = new Pubblicazione("pa", TipoPubblicazione.SEMESTRALE);
        pa.setMese(Mese.AGOSTO);
        assertEquals(2, pa.getMesiPubblicazione().size());
        assertTrue(pa.getMesiPubblicazione().contains(Mese.AGOSTO));
        assertTrue(pa.getMesiPubblicazione().contains(Mese.FEBBRAIO));
        
        Pubblicazione pb = new Pubblicazione("pb", TipoPubblicazione.SEMESTRALE);
        pb.setMese(Mese.MARZO);
        assertEquals(2, pb.getMesiPubblicazione().size());
        assertTrue(pb.getMesiPubblicazione().contains(Mese.MARZO));
        assertTrue(pb.getMesiPubblicazione().contains(Mese.SETTEMBRE));

        Pubblicazione pc = new Pubblicazione("pc", TipoPubblicazione.ANNUALE);
        pc.setMese(Mese.APRILE);
        assertEquals(1, pc.getMesiPubblicazione().size());
        assertTrue(pc.getMesiPubblicazione().contains(Mese.APRILE));
        
    }

    @Test
    public void testAbbonamentoPubblicazioneMensile() {
        
    }

    @Test
    public void testAnnoMese() {
        Calendar now = Calendar.getInstance();
        now.setTime(new Date());
        int anno = now.get(Calendar.YEAR);
        assertEquals(anno-1, Smd.getAnnoPassato().getAnno());
        assertEquals(anno, Smd.getAnnoCorrente().getAnno());
        assertEquals(anno+1, Smd.getAnnoProssimo().getAnno());
        int mese = now.get(Calendar.MONTH);
        // Mese comincia da 0 
        assertEquals(mese+1, Smd.getMeseCorrente().getPosizione());
        
    }

    @Test
    public void testGetNumeroPubblicazioni() {
        
        assertEquals(1, Smd.getNumeroPubblicazioni(Mese.MARZO,
                                                   Mese.DICEMBRE,
                                                   Mese.MARZO,
                                                   TipoPubblicazione.ANNUALE));
        assertEquals(0, Smd.getNumeroPubblicazioni(Mese.APRILE,
                                                             Mese.DICEMBRE,
                                                             Mese.MARZO,
                                                             TipoPubblicazione.ANNUALE));
        assertEquals(2, Smd.getNumeroPubblicazioni(Mese.MARZO,
                                                             Mese.DICEMBRE,
                                                             Mese.MARZO,
                                                             TipoPubblicazione.SEMESTRALE));
        assertEquals(1, Smd.getNumeroPubblicazioni(Mese.SETTEMBRE,
                                                             Mese.DICEMBRE,
                                                             Mese.MARZO,
                                                             TipoPubblicazione.SEMESTRALE));
        assertEquals(0, Smd.getNumeroPubblicazioni(Mese.OTTOBRE,
                                                             Mese.DICEMBRE,
                                                             Mese.MARZO,
                                                             TipoPubblicazione.SEMESTRALE));
        
        assertEquals(3, Smd.getNumeroPubblicazioni(Mese.OTTOBRE,
                                                   Mese.DICEMBRE,
                                                   Mese.GENNAIO,
                                                   TipoPubblicazione.MENSILE));
        assertEquals(12, Smd.getNumeroPubblicazioni(Mese.GENNAIO,
                                                   Mese.DICEMBRE,
                                                   Mese.GENNAIO,
                                                   TipoPubblicazione.MENSILE));

 
    }

}
