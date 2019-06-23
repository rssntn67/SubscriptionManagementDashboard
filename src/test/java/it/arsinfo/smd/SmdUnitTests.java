package it.arsinfo.smd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
import it.arsinfo.smd.data.TipoEstrattoConto;
import it.arsinfo.smd.data.TipoPubblicazione;
import it.arsinfo.smd.entity.Pubblicazione;

@RunWith(SpringRunner.class)
public class SmdUnitTests {
    
    @Test
    public void testMesiPubblicazione() {
 
        Pubblicazione pe = new Pubblicazione("pe", TipoPubblicazione.MENSILE);
        pe.setGen(true);
        pe.setFeb(true);
        pe.setMar(true);
        pe.setApr(true);
        pe.setMag(true);
        pe.setGiu(true);
        pe.setLug(true);
        pe.setAgo(true);
        pe.setSet(true);
        pe.setOtt(true);
        pe.setNov(true);
        pe.setDic(true);
        assertEquals(EnumSet.allOf(Mese.class), pe.getMesiPubblicazione());

        
        Pubblicazione pd = new Pubblicazione("pd", TipoPubblicazione.MENSILE);
        pd.setGen(true);
        pd.setFeb(true);
        pd.setMar(true);
        pd.setApr(true);
        pd.setMag(true);
        pd.setGiu(true);
        pd.setLug(true);
        pd.setSet(true);
        pd.setOtt(true);
        pd.setNov(true);
        pd.setDic(true);
        assertEquals(11, pd.getMesiPubblicazione().size());
        assertFalse(pd.getMesiPubblicazione().contains(Mese.AGOSTO));

        Pubblicazione pa = new Pubblicazione("pa", TipoPubblicazione.SEMESTRALE);
        pa.setFeb(true);
        pa.setAgo(true);
        assertEquals(2, pa.getMesiPubblicazione().size());
        assertTrue(pa.getMesiPubblicazione().contains(Mese.AGOSTO));
        assertTrue(pa.getMesiPubblicazione().contains(Mese.FEBBRAIO));
        
        Pubblicazione pb = new Pubblicazione("pb", TipoPubblicazione.SEMESTRALE);
        pb.setMar(true);
        pb.setSet(true);
        assertEquals(2, pb.getMesiPubblicazione().size());
        assertTrue(pb.getMesiPubblicazione().contains(Mese.MARZO));
        assertTrue(pb.getMesiPubblicazione().contains(Mese.SETTEMBRE));

        Pubblicazione pc = new Pubblicazione("pc", TipoPubblicazione.ANNUALE);
        pc.setApr(true);
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
                                                   EnumSet.of(Mese.MARZO),
                                                   TipoPubblicazione.ANNUALE));
        assertEquals(0, Smd.calcolaNumeroPubblicazioni(Mese.APRILE,
                                                         Mese.DICEMBRE,
                                                         EnumSet.of(Mese.MARZO),
                                                         TipoPubblicazione.ANNUALE));
        assertEquals(2, Smd.calcolaNumeroPubblicazioni(Mese.MARZO,
                                                             Mese.DICEMBRE,
                                                             EnumSet.of(Mese.MARZO,Mese.SETTEMBRE),
                                                             TipoPubblicazione.SEMESTRALE));
        assertEquals(1, Smd.calcolaNumeroPubblicazioni(Mese.SETTEMBRE,
                                                             Mese.DICEMBRE,
                                                             EnumSet.of(Mese.MARZO,Mese.SETTEMBRE),
                                                             TipoPubblicazione.SEMESTRALE));
        assertEquals(0, Smd.calcolaNumeroPubblicazioni(Mese.OTTOBRE,
                                                             Mese.DICEMBRE,
                                                             EnumSet.of(Mese.MARZO,Mese.SETTEMBRE),
                                                           TipoPubblicazione.SEMESTRALE));
        
        assertEquals(3, Smd.calcolaNumeroPubblicazioni(Mese.OTTOBRE,
                                                   Mese.DICEMBRE,
                                                   EnumSet.allOf(Mese.class),
                                                   TipoPubblicazione.MENSILE));
        assertEquals(12, Smd.calcolaNumeroPubblicazioni(Mese.GENNAIO,
                                                   Mese.DICEMBRE,
                                                   EnumSet.allOf(Mese.class),
                                                   TipoPubblicazione.MENSILE));

 
    }
    
    @Test
    public void testCalcolaCosto() {
        Pubblicazione messaggio = SmdLoadSampleData.getMessaggio();
        assertEquals(TipoPubblicazione.MENSILE, messaggio.getTipo());
        assertEquals(0 , messaggio.getCostoUnitario().subtract(new BigDecimal(1.50)).signum());
        assertEquals(0 , messaggio.getAbbonamentoItalia().subtract(new BigDecimal(15.00)).signum());
        
        assertEquals(0.0,Smd.calcolaCosto(Mese.GENNAIO, Mese.DICEMBRE, messaggio, TipoEstrattoConto.OmaggioCuriaDiocesiana, 1),0);
        assertEquals(0.0,Smd.calcolaCosto(Mese.GENNAIO, Mese.DICEMBRE, messaggio, TipoEstrattoConto.OmaggioCuriaGeneralizia, 1),0);
        assertEquals(0.0,Smd.calcolaCosto(Mese.GENNAIO, Mese.DICEMBRE, messaggio, TipoEstrattoConto.OmaggioGesuiti, 1),0);
        assertEquals(15.0,Smd.calcolaCosto(Mese.GENNAIO, Mese.DICEMBRE, messaggio, TipoEstrattoConto.Scontato, 1),0);
        assertEquals(15.0,Smd.calcolaCosto(Mese.GENNAIO, Mese.DICEMBRE, messaggio, TipoEstrattoConto.Ordinario, 1),0);
        assertEquals(3.0,Smd.calcolaCosto(Mese.GENNAIO, Mese.FEBBRAIO, messaggio, TipoEstrattoConto.Ordinario, 1),0);
        assertEquals(4.50,Smd.calcolaCosto(Mese.GENNAIO, Mese.MARZO, messaggio, TipoEstrattoConto.Ordinario, 1),0);
        
        try {
            Smd.calcolaCosto(Mese.GENNAIO, Mese.MARZO, messaggio, TipoEstrattoConto.Scontato, 1);
            } catch (UnsupportedOperationException e) {
            assertEquals("Valori mesi inizio e fine non ammissibili per " + TipoEstrattoConto.Scontato, e.getMessage());
        }
        try {
            Smd.calcolaCosto(Mese.MARZO, Mese.GENNAIO, messaggio, TipoEstrattoConto.Scontato, 1);
        } catch (UnsupportedOperationException e) {
            assertEquals("Valori non ammissibili", e.getMessage());
        }
        
        Pubblicazione lodare = SmdLoadSampleData.getLodare();
        assertEquals(TipoPubblicazione.MENSILE, lodare.getTipo());
        assertEquals(0 , lodare.getCostoUnitario().subtract(new BigDecimal(2.0)).signum());
        
        assertEquals(0.0,Smd.calcolaCosto(Mese.GENNAIO, Mese.DICEMBRE, lodare, TipoEstrattoConto.OmaggioCuriaDiocesiana, 1),0);
        assertEquals(0.0,Smd.calcolaCosto(Mese.GENNAIO, Mese.DICEMBRE, lodare, TipoEstrattoConto.OmaggioCuriaGeneralizia, 1),0);
        assertEquals(0.0,Smd.calcolaCosto(Mese.GENNAIO, Mese.DICEMBRE, lodare, TipoEstrattoConto.OmaggioGesuiti, 1),0);
        assertEquals(18.0,Smd.calcolaCosto(Mese.GENNAIO, Mese.DICEMBRE, lodare, TipoEstrattoConto.Scontato, 1),0);
        assertEquals(20.0,Smd.calcolaCosto(Mese.GENNAIO, Mese.DICEMBRE, lodare, TipoEstrattoConto.Ordinario, 1),0);
        assertEquals(4.0,Smd.calcolaCosto(Mese.GENNAIO, Mese.FEBBRAIO, lodare, TipoEstrattoConto.Ordinario, 1),0);
        assertEquals(6.0,Smd.calcolaCosto(Mese.GENNAIO, Mese.MARZO, lodare, TipoEstrattoConto.Ordinario, 1),0);
        assertEquals(10.0,Smd.calcolaCosto(Mese.GIUGNO, Mese.OTTOBRE, lodare, TipoEstrattoConto.Ordinario, 1),0);
        
        Pubblicazione estratti = SmdLoadSampleData.getEstratti();
        assertEquals(TipoPubblicazione.ANNUALE, estratti.getTipo());
        assertEquals(0 , estratti.getCostoUnitario().subtract(new BigDecimal(10.00)).signum());
        assertEquals(Mese.LUGLIO, estratti.getMesiPubblicazione().iterator().next());
        assertEquals(0.0,Smd.calcolaCosto(Mese.GENNAIO, Mese.DICEMBRE, estratti, TipoEstrattoConto.OmaggioCuriaDiocesiana, 1),0);
        assertEquals(0.0,Smd.calcolaCosto(Mese.GENNAIO, Mese.DICEMBRE, estratti, TipoEstrattoConto.OmaggioCuriaGeneralizia, 1),0);
        assertEquals(0.0,Smd.calcolaCosto(Mese.GENNAIO, Mese.DICEMBRE, estratti, TipoEstrattoConto.OmaggioGesuiti, 1),0);
        assertEquals(10.0,Smd.calcolaCosto(Mese.GENNAIO, Mese.DICEMBRE, estratti, TipoEstrattoConto.Scontato, 1),0);
        assertEquals(10.0,Smd.calcolaCosto(Mese.GENNAIO, Mese.DICEMBRE, estratti, TipoEstrattoConto.Ordinario, 1),0);
        assertEquals(0.0,Smd.calcolaCosto(Mese.GENNAIO, Mese.FEBBRAIO, estratti, TipoEstrattoConto.Ordinario, 1),0);
        assertEquals(0.0,Smd.calcolaCosto(Mese.GENNAIO, Mese.MARZO, estratti, TipoEstrattoConto.Ordinario, 1),0);
        assertEquals(0.0,Smd.calcolaCosto(Mese.GENNAIO, Mese.MARZO, estratti, TipoEstrattoConto.Ordinario, 1),0);
        assertEquals(10.0,Smd.calcolaCosto(Mese.GIUGNO, Mese.OTTOBRE, estratti, TipoEstrattoConto.Ordinario, 1),0);
        
        Pubblicazione blocchetti = SmdLoadSampleData.getBlocchetti();
        assertEquals(TipoPubblicazione.SEMESTRALE, blocchetti.getTipo());
        assertEquals(0 , blocchetti.getCostoUnitario().subtract(new BigDecimal(3.00)).signum());
        assertEquals(0.0,Smd.calcolaCosto(Mese.GENNAIO, Mese.DICEMBRE, blocchetti, TipoEstrattoConto.OmaggioCuriaDiocesiana, 1),0);
        assertEquals(0.0,Smd.calcolaCosto(Mese.GENNAIO, Mese.DICEMBRE, blocchetti, TipoEstrattoConto.OmaggioCuriaGeneralizia, 1),0);
        assertEquals(0.0,Smd.calcolaCosto(Mese.GENNAIO, Mese.DICEMBRE, blocchetti, TipoEstrattoConto.OmaggioGesuiti, 1),0);
        assertEquals(0.0,Smd.calcolaCosto(Mese.GENNAIO, Mese.DICEMBRE, blocchetti, TipoEstrattoConto.OmaggioDirettoreAdp, 1),0);
        assertEquals(6.00,Smd.calcolaCosto(Mese.GENNAIO, Mese.DICEMBRE, blocchetti, TipoEstrattoConto.Scontato, 1),0);
        assertEquals(6.00,Smd.calcolaCosto(Mese.GENNAIO, Mese.DICEMBRE, blocchetti, TipoEstrattoConto.Ordinario, 1),0);
        assertEquals(0.0,Smd.calcolaCosto(Mese.GENNAIO, Mese.FEBBRAIO, blocchetti, TipoEstrattoConto.Ordinario, 1),0);
        assertEquals(3.00,Smd.calcolaCosto(Mese.GENNAIO, Mese.MARZO, blocchetti, TipoEstrattoConto.Ordinario, 1),0);
        assertEquals(3.00,Smd.calcolaCosto(Mese.GIUGNO, Mese.OTTOBRE, blocchetti, TipoEstrattoConto.Ordinario, 1),0);


    }

}
