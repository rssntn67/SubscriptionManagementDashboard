package it.arsinfo.smd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.Mese;
import it.arsinfo.smd.data.Omaggio;
import it.arsinfo.smd.data.TipoPubblicazione;
import it.arsinfo.smd.entity.Pubblicazione;

@RunWith(SpringRunner.class)
public class SmdUnitTests {
    
    @Test
    public void testMesiPubblicazione() {
 
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
    public void testGenerateCampo() {
        Set<String> campi = new HashSet<>();
        for (int i=0; i< 200000;i++) {
        String campo = Smd.generaVCampo(Anno.ANNO2019, Mese.GENNAIO,Mese.DICEMBRE);
        assertEquals("19", campo.substring(0, 2));
        assertTrue(Smd.checkCampo(campo));
        assertTrue(campi.add(campo));
        }
        assertEquals(200000, campi.size());        
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
        
        assertEquals(1, Smd.calcolaNumeroPubblicazioni(Mese.MARZO,
                                                   Mese.DICEMBRE,
                                                   Mese.MARZO,
                                                   TipoPubblicazione.ANNUALE));
        assertEquals(0, Smd.calcolaNumeroPubblicazioni(Mese.APRILE,
                                                             Mese.DICEMBRE,
                                                             Mese.MARZO,
                                                             TipoPubblicazione.ANNUALE));
        assertEquals(2, Smd.calcolaNumeroPubblicazioni(Mese.MARZO,
                                                             Mese.DICEMBRE,
                                                             Mese.MARZO,
                                                             TipoPubblicazione.SEMESTRALE));
        assertEquals(1, Smd.calcolaNumeroPubblicazioni(Mese.SETTEMBRE,
                                                             Mese.DICEMBRE,
                                                             Mese.MARZO,
                                                             TipoPubblicazione.SEMESTRALE));
        assertEquals(0, Smd.calcolaNumeroPubblicazioni(Mese.OTTOBRE,
                                                             Mese.DICEMBRE,
                                                             Mese.MARZO,
                                                             TipoPubblicazione.SEMESTRALE));
        
        assertEquals(3, Smd.calcolaNumeroPubblicazioni(Mese.OTTOBRE,
                                                   Mese.DICEMBRE,
                                                   Mese.GENNAIO,
                                                   TipoPubblicazione.MENSILE));
        assertEquals(12, Smd.calcolaNumeroPubblicazioni(Mese.GENNAIO,
                                                   Mese.DICEMBRE,
                                                   Mese.GENNAIO,
                                                   TipoPubblicazione.MENSILE));

 
    }
    
    @Test
    public void testCalcolaCosto() {
        Pubblicazione messaggio = SmdLoadSampleData.getMessaggio();
        assertEquals(TipoPubblicazione.MENSILE, messaggio.getTipo());
        assertEquals(0 , messaggio.getCostoUnitario().subtract(new BigDecimal(1.25)).signum());
        assertEquals(0 , messaggio.getCostoScontato().subtract(new BigDecimal(1.25)).signum());
        
        assertEquals(0.0,Smd.calcolaCosto(Mese.GENNAIO, Mese.DICEMBRE, messaggio, Omaggio.CuriaDiocesiana, 1),0);
        assertEquals(0.0,Smd.calcolaCosto(Mese.GENNAIO, Mese.DICEMBRE, messaggio, Omaggio.CuriaGeneralizia, 1),0);
        assertEquals(0.0,Smd.calcolaCosto(Mese.GENNAIO, Mese.DICEMBRE, messaggio, Omaggio.Gesuiti, 1),0);
        assertEquals(15.0,Smd.calcolaCosto(Mese.GENNAIO, Mese.DICEMBRE, messaggio, Omaggio.ConSconto, 1),0);
        assertEquals(15.0,Smd.calcolaCosto(Mese.GENNAIO, Mese.DICEMBRE, messaggio, Omaggio.No, 1),0);
        assertEquals(2.5,Smd.calcolaCosto(Mese.GENNAIO, Mese.FEBBRAIO, messaggio, Omaggio.No, 1),0);
        assertEquals(3.75,Smd.calcolaCosto(Mese.GENNAIO, Mese.MARZO, messaggio, Omaggio.No, 1),0);
        assertEquals(3.75,Smd.calcolaCosto(Mese.GENNAIO, Mese.MARZO, messaggio, Omaggio.ConSconto, 1),0);
        assertEquals(6.25,Smd.calcolaCosto(Mese.GIUGNO, Mese.OTTOBRE, messaggio, Omaggio.ConSconto, 1),0);
        try {
            Smd.calcolaCosto(Mese.MARZO, Mese.GENNAIO, messaggio, Omaggio.ConSconto, 1);
        } catch (UnsupportedOperationException e) {
            assertEquals("Valori non ammissibili", e.getMessage());
        }
        
        Pubblicazione lodare = SmdLoadSampleData.getLodare();
        assertEquals(TipoPubblicazione.MENSILE, lodare.getTipo());
        assertEquals(0 , lodare.getCostoUnitario().subtract(new BigDecimal(1.5)).signum());
        assertEquals(0 , lodare.getCostoScontato().subtract(new BigDecimal(1.5)).signum());
        
        assertEquals(0.0,Smd.calcolaCosto(Mese.GENNAIO, Mese.DICEMBRE, lodare, Omaggio.CuriaDiocesiana, 1),0);
        assertEquals(0.0,Smd.calcolaCosto(Mese.GENNAIO, Mese.DICEMBRE, lodare, Omaggio.CuriaGeneralizia, 1),0);
        assertEquals(0.0,Smd.calcolaCosto(Mese.GENNAIO, Mese.DICEMBRE, lodare, Omaggio.Gesuiti, 1),0);
        assertEquals(18.0,Smd.calcolaCosto(Mese.GENNAIO, Mese.DICEMBRE, lodare, Omaggio.ConSconto, 1),0);
        assertEquals(18.0,Smd.calcolaCosto(Mese.GENNAIO, Mese.DICEMBRE, lodare, Omaggio.No, 1),0);
        assertEquals(3.0,Smd.calcolaCosto(Mese.GENNAIO, Mese.FEBBRAIO, lodare, Omaggio.No, 1),0);
        assertEquals(4.5,Smd.calcolaCosto(Mese.GENNAIO, Mese.MARZO, lodare, Omaggio.No, 1),0);
        assertEquals(4.5,Smd.calcolaCosto(Mese.GENNAIO, Mese.MARZO, lodare, Omaggio.ConSconto, 1),0);
        assertEquals(7.5,Smd.calcolaCosto(Mese.GIUGNO, Mese.OTTOBRE, lodare, Omaggio.ConSconto, 1),0);
        
        Pubblicazione estratti = SmdLoadSampleData.getEstratti();
        assertEquals(TipoPubblicazione.ANNUALE, estratti.getTipo());
        assertEquals(0 , estratti.getCostoUnitario().subtract(new BigDecimal(10.00)).signum());
        assertEquals(0 , estratti.getCostoScontato().subtract(new BigDecimal(10.00)).signum());
        assertEquals(Mese.LUGLIO, estratti.getMese());
        assertEquals(0.0,Smd.calcolaCosto(Mese.GENNAIO, Mese.DICEMBRE, estratti, Omaggio.CuriaDiocesiana, 1),0);
        assertEquals(0.0,Smd.calcolaCosto(Mese.GENNAIO, Mese.DICEMBRE, estratti, Omaggio.CuriaGeneralizia, 1),0);
        assertEquals(0.0,Smd.calcolaCosto(Mese.GENNAIO, Mese.DICEMBRE, estratti, Omaggio.Gesuiti, 1),0);
        assertEquals(10.0,Smd.calcolaCosto(Mese.GENNAIO, Mese.DICEMBRE, estratti, Omaggio.ConSconto, 1),0);
        assertEquals(10.0,Smd.calcolaCosto(Mese.GENNAIO, Mese.DICEMBRE, estratti, Omaggio.No, 1),0);
        assertEquals(0.0,Smd.calcolaCosto(Mese.GENNAIO, Mese.FEBBRAIO, estratti, Omaggio.No, 1),0);
        assertEquals(0.0,Smd.calcolaCosto(Mese.GENNAIO, Mese.MARZO, estratti, Omaggio.No, 1),0);
        assertEquals(0.0,Smd.calcolaCosto(Mese.GENNAIO, Mese.MARZO, estratti, Omaggio.ConSconto, 1),0);
        assertEquals(10.0,Smd.calcolaCosto(Mese.GIUGNO, Mese.OTTOBRE, estratti, Omaggio.ConSconto, 1),0);
        
        Pubblicazione blocchetti = SmdLoadSampleData.getBlocchetti();
        assertEquals(TipoPubblicazione.SEMESTRALE, blocchetti.getTipo());
        assertEquals(0 , blocchetti.getCostoUnitario().subtract(new BigDecimal(3.00)).signum());
        assertEquals(0 , blocchetti.getCostoScontato().subtract(new BigDecimal(2.40)).signum());
        assertEquals(Mese.MARZO, blocchetti.getMese());
        assertEquals(0.0,Smd.calcolaCosto(Mese.GENNAIO, Mese.DICEMBRE, blocchetti, Omaggio.CuriaDiocesiana, 1),0);
        assertEquals(0.0,Smd.calcolaCosto(Mese.GENNAIO, Mese.DICEMBRE, blocchetti, Omaggio.CuriaGeneralizia, 1),0);
        assertEquals(0.0,Smd.calcolaCosto(Mese.GENNAIO, Mese.DICEMBRE, blocchetti, Omaggio.Gesuiti, 1),0);
        assertEquals(4.80,Smd.calcolaCosto(Mese.GENNAIO, Mese.DICEMBRE, blocchetti, Omaggio.ConSconto, 1),0);
        assertEquals(6.00,Smd.calcolaCosto(Mese.GENNAIO, Mese.DICEMBRE, blocchetti, Omaggio.No, 1),0);
        assertEquals(0.0,Smd.calcolaCosto(Mese.GENNAIO, Mese.FEBBRAIO, blocchetti, Omaggio.No, 1),0);
        assertEquals(3.00,Smd.calcolaCosto(Mese.GENNAIO, Mese.MARZO, blocchetti, Omaggio.No, 1),0);
        assertEquals(2.40,Smd.calcolaCosto(Mese.GENNAIO, Mese.MARZO, blocchetti, Omaggio.ConSconto, 1),0);
        assertEquals(2.40,Smd.calcolaCosto(Mese.GIUGNO, Mese.OTTOBRE, blocchetti, Omaggio.ConSconto, 1),0);


    }

}
