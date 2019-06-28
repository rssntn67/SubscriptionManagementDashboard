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
import it.arsinfo.smd.data.InvioSpedizione;
import it.arsinfo.smd.data.Mese;
import it.arsinfo.smd.data.TipoEstrattoConto;
import it.arsinfo.smd.data.TipoPubblicazione;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.EstrattoConto;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.entity.Spedizione;

@RunWith(SpringRunner.class)
public class SmdUnitTests {
    
    private static EstrattoConto creaECStd(Pubblicazione p, TipoEstrattoConto tipo, int numero) {
        Anno anno = Smd.getAnnoProssimo();
        Mese mese = Smd.getMeseCorrente();
        if (mese.getPosizione()+p.getAnticipoSpedizione() > 12) {
            anno=Anno.getAnnoSuccessivo(anno);
        }
        return creaEC(Mese.GENNAIO, anno, Mese.DICEMBRE, anno, p, tipo, numero);
    }
    
    private static EstrattoConto creaEC(Mese inizio, Anno ai, Mese fine, Anno af, Pubblicazione p, TipoEstrattoConto tipo, int numero) {
        Abbonamento abb = new Abbonamento();
        EstrattoConto ec =  new EstrattoConto();
        ec.setAbbonamento(abb);
        ec.setNumero(numero);
        ec.setPubblicazione(p);
        ec.setTipoEstrattoConto(tipo);
        ec.setDestinatario(new Anagrafica());
        abb.addEstrattoConto(ec);
        return Smd.generaEC(ec,InvioSpedizione.Spedizioniere,inizio, ai, fine, ai);
    }
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
        String campo = Smd.generaVCampo(Anno.ANNO2019);
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
    
    private void verificaImportoAbbonamentoAnnuale(EstrattoConto ec, Pubblicazione p, int numero) {
        switch (ec.getTipoEstrattoConto()) {
        case OmaggioCuriaDiocesiana:
            assertEquals(0.0,ec.getImporto().doubleValue(),0);
            assertEquals(0.0,ec.getSpesePostali().doubleValue(),0);
           break;
        case OmaggioGesuiti:
            assertEquals(0.0,ec.getImporto().doubleValue(),0);
            assertEquals(0.0,ec.getSpesePostali().doubleValue(),0);
                       break;
        case OmaggioCuriaGeneralizia:
            assertEquals(0.0,ec.getImporto().doubleValue(),0);
            assertEquals(0.0,ec.getSpesePostali().doubleValue(),0);
            break;
        case OmaggioDirettoreAdp:
            assertEquals(0.0,ec.getImporto().doubleValue(),0);
            assertEquals(0.0,ec.getSpesePostali().doubleValue(),0);
            break;
        case OmaggioEditore:
            assertEquals(0.0,ec.getImporto().doubleValue(),0);
            assertEquals(0.0,ec.getSpesePostali().doubleValue(),0);
            break;
        case Ordinario:
            assertEquals(p.getAbbonamentoItalia().multiply(new BigDecimal(numero)).doubleValue()
                         ,ec.getImporto().doubleValue(),0);
            assertEquals(0.0,ec.getSpesePostali().doubleValue(),0);
            break;
        case Sostenitore:
            assertEquals(p.getAbbonamentoSostenitore().multiply(new BigDecimal(numero)).doubleValue()
                         ,ec.getImporto().doubleValue(),0);
            assertEquals(0.0,ec.getSpesePostali().doubleValue(),0);
            break;
        case Scontato:
            assertEquals(p.getAbbonamentoConSconto().multiply(new BigDecimal(numero)).doubleValue()
                         ,ec.getImporto().doubleValue(),0);
            assertEquals(0.0,ec.getSpesePostali().doubleValue(),0);
            break;
        case Web:
            assertEquals(p.getAbbonamentoWeb().multiply(new BigDecimal(numero)).doubleValue()
                         ,ec.getImporto().doubleValue(),0);
            assertEquals(0.0,ec.getSpesePostali().doubleValue(),0);
            break;
        case AmericaAfricaAsia:
            assertEquals(p.getAbbonamentoAmericaAsiaAfrica().multiply(new BigDecimal(numero)).doubleValue()
                         ,ec.getImporto().doubleValue(),0);
            assertEquals(0.0,ec.getSpesePostali().doubleValue(),0);
            break;
        case EuropaBacinoMediterraneo:
            assertEquals(p.getAbbonamentoEuropa().multiply(new BigDecimal(numero)).doubleValue()
                         ,ec.getImporto().doubleValue(),0);
            assertEquals(0.0,ec.getSpesePostali().doubleValue(),0);
            break;
        default:
            break;
        }
        
        ec.getSpedizioni().stream().forEach(s -> assertEquals(s.getInvioSpedizione(),InvioSpedizione.Spedizioniere));

    }
    
    @Test
    public void testCreaSpedizioneMessaggio() {
        Pubblicazione messaggio = SmdLoadSampleData.getMessaggio();
        assertEquals(3, messaggio.getAnticipoSpedizione());
        EstrattoConto ec = new EstrattoConto();
        ec.setPubblicazione(messaggio);
        ec.setNumero(10);
        ec.setDestinatario(new Anagrafica());
        Anno anno = Smd.getAnnoProssimo();
        anno = Anno.getAnnoSuccessivo(anno);
        Spedizione s1 = Smd.creaSpedizione(ec,Mese.GENNAIO, anno, InvioSpedizione.Spedizioniere);
        assertTrue(messaggio.getMesiPubblicazione().contains(Mese.GENNAIO));
        assertEquals(Mese.GENNAIO, s1.getMesePubblicazione());
        assertEquals(anno, s1.getAnnoPubblicazione());
        assertEquals(Mese.OTTOBRE, s1.getMeseSpedizione());
        assertEquals(Anno.getAnnoPrecedente(anno), s1.getAnnoSpedizione());
        assertTrue(!Smd.spedizionePosticipata(s1, messaggio.getAnticipoSpedizione()));

        s1 = Smd.creaSpedizione(ec,Mese.FEBBRAIO, anno, InvioSpedizione.Spedizioniere);
        assertTrue(messaggio.getMesiPubblicazione().contains(Mese.FEBBRAIO));
        assertEquals(Mese.FEBBRAIO, s1.getMesePubblicazione());
        assertEquals(anno, s1.getAnnoPubblicazione());
        assertEquals(Mese.NOVEMBRE, s1.getMeseSpedizione());
        assertEquals(Anno.getAnnoPrecedente(anno), s1.getAnnoSpedizione());
        assertTrue(!Smd.spedizionePosticipata(s1, messaggio.getAnticipoSpedizione()));
        
        s1 = Smd.creaSpedizione(ec,Mese.MARZO, anno, InvioSpedizione.Spedizioniere);
        assertTrue(messaggio.getMesiPubblicazione().contains(Mese.MARZO));
        assertEquals(Mese.MARZO, s1.getMesePubblicazione());
        assertEquals(anno, s1.getAnnoPubblicazione());
        assertEquals(Mese.DICEMBRE, s1.getMeseSpedizione());
        assertEquals(Anno.getAnnoPrecedente(anno), s1.getAnnoSpedizione());
        assertTrue(!Smd.spedizionePosticipata(s1, messaggio.getAnticipoSpedizione()));
        
        s1 = Smd.creaSpedizione(ec,Mese.APRILE, anno, InvioSpedizione.Spedizioniere);
        assertTrue(messaggio.getMesiPubblicazione().contains(Mese.APRILE));
        assertEquals(Mese.APRILE, s1.getMesePubblicazione());
        assertEquals(anno, s1.getAnnoPubblicazione());
        assertEquals(Mese.GENNAIO, s1.getMeseSpedizione());
        assertEquals(anno, s1.getAnnoSpedizione());
        assertTrue(!Smd.spedizionePosticipata(s1, messaggio.getAnticipoSpedizione()));

        s1 = Smd.creaSpedizione(ec,Mese.MAGGIO, anno, InvioSpedizione.Spedizioniere);
        assertTrue(messaggio.getMesiPubblicazione().contains(Mese.MAGGIO));
        assertEquals(Mese.MAGGIO, s1.getMesePubblicazione());
        assertEquals(anno, s1.getAnnoPubblicazione());
        assertEquals(Mese.FEBBRAIO, s1.getMeseSpedizione());
        assertEquals(anno, s1.getAnnoSpedizione());
        assertTrue(!Smd.spedizionePosticipata(s1, messaggio.getAnticipoSpedizione()));

        s1 = Smd.creaSpedizione(ec,Mese.GIUGNO, anno, InvioSpedizione.Spedizioniere);
        assertTrue(messaggio.getMesiPubblicazione().contains(Mese.GIUGNO));
        assertEquals(Mese.GIUGNO, s1.getMesePubblicazione());
        assertEquals(anno, s1.getAnnoPubblicazione());
        assertEquals(Mese.MARZO, s1.getMeseSpedizione());
        assertEquals(anno, s1.getAnnoSpedizione());
        assertTrue(!Smd.spedizionePosticipata(s1, messaggio.getAnticipoSpedizione()));

        s1 = Smd.creaSpedizione(ec,Mese.LUGLIO, anno, InvioSpedizione.Spedizioniere);
        assertTrue(messaggio.getMesiPubblicazione().contains(Mese.LUGLIO));
        assertEquals(Mese.LUGLIO, s1.getMesePubblicazione());
        assertEquals(anno, s1.getAnnoPubblicazione());
        assertEquals(Mese.APRILE, s1.getMeseSpedizione());
        assertEquals(anno, s1.getAnnoSpedizione());
        assertTrue(!Smd.spedizionePosticipata(s1, messaggio.getAnticipoSpedizione()));

        try {
            assertTrue(!messaggio.getMesiPubblicazione().contains(Mese.AGOSTO));
            s1 = Smd.creaSpedizione(ec,Mese.AGOSTO, anno, InvioSpedizione.Spedizioniere);
        } catch (UnsupportedOperationException e) {
            assertEquals("cannot create spedizione for month pubblicazione: SpedizioniereAGOSTOANNO2021", e.getMessage());
        }

        s1 = Smd.creaSpedizione(ec,Mese.SETTEMBRE, anno, InvioSpedizione.Spedizioniere);
        assertTrue(messaggio.getMesiPubblicazione().contains(Mese.SETTEMBRE));
        assertEquals(Mese.SETTEMBRE, s1.getMesePubblicazione());
        assertEquals(anno, s1.getAnnoPubblicazione());
        assertEquals(Mese.GIUGNO, s1.getMeseSpedizione());
        assertEquals(anno, s1.getAnnoSpedizione());
        assertTrue(!Smd.spedizionePosticipata(s1, messaggio.getAnticipoSpedizione()));
        
        s1 = Smd.creaSpedizione(ec,Mese.OTTOBRE, anno, InvioSpedizione.Spedizioniere);
        assertTrue(messaggio.getMesiPubblicazione().contains(Mese.OTTOBRE));
        assertEquals(Mese.OTTOBRE, s1.getMesePubblicazione());
        assertEquals(anno, s1.getAnnoPubblicazione());
        assertEquals(Mese.LUGLIO, s1.getMeseSpedizione());
        assertEquals(anno, s1.getAnnoSpedizione());
        assertTrue(!Smd.spedizionePosticipata(s1, messaggio.getAnticipoSpedizione()));
        
        s1 = Smd.creaSpedizione(ec,Mese.NOVEMBRE, anno, InvioSpedizione.Spedizioniere);
        assertTrue(messaggio.getMesiPubblicazione().contains(Mese.NOVEMBRE));
        assertEquals(Mese.NOVEMBRE, s1.getMesePubblicazione());
        assertEquals(anno, s1.getAnnoPubblicazione());
        assertEquals(Mese.AGOSTO, s1.getMeseSpedizione());
        assertEquals(anno, s1.getAnnoSpedizione());
        assertTrue(!Smd.spedizionePosticipata(s1, messaggio.getAnticipoSpedizione()));

        s1 = Smd.creaSpedizione(ec,Mese.DICEMBRE, anno, InvioSpedizione.Spedizioniere);
        assertTrue(messaggio.getMesiPubblicazione().contains(Mese.DICEMBRE));
        assertEquals(Mese.DICEMBRE, s1.getMesePubblicazione());
        assertEquals(anno, s1.getAnnoPubblicazione());
        assertEquals(Mese.SETTEMBRE, s1.getMeseSpedizione());
        assertEquals(anno, s1.getAnnoSpedizione());
        assertTrue(!Smd.spedizionePosticipata(s1, messaggio.getAnticipoSpedizione()));



    }

    @Test
    public void testCalcolaCostoAbbonamentoStd() {
        Pubblicazione messaggio = SmdLoadSampleData.getMessaggio();
        
        EnumSet.allOf(TipoEstrattoConto.class).stream().forEach(tpec -> {
            EstrattoConto ec = creaECStd(messaggio, tpec, 10); 
            assertTrue(ec.hasAllMesiPubblicazione());
            assertEquals(11, ec.getSpedizioni().size());
            System.err.println(ec);
            for (Spedizione s : ec.getSpedizioni()) {
                System.err.println(s);
            }
            assertEquals(0, ec.getNumeroSpedizioniConSpesePostali());
            verificaImportoAbbonamentoAnnuale(ec,messaggio,10);
        });

        Pubblicazione lodare = SmdLoadSampleData.getLodare();
        EnumSet.allOf(TipoEstrattoConto.class).stream().forEach(tpec -> {
            EstrattoConto ec = creaECStd(lodare, tpec, 10); 
            assertTrue(ec.hasAllMesiPubblicazione());
            assertEquals(12, ec.getSpedizioni().size());
            assertEquals(0, ec.getNumeroSpedizioniConSpesePostali());
            verificaImportoAbbonamentoAnnuale(ec,lodare,10);
        });

        Pubblicazione blocchetti = SmdLoadSampleData.getBlocchetti();
        EnumSet.allOf(TipoEstrattoConto.class).stream().forEach(tpec -> {
            EstrattoConto ec = creaECStd(blocchetti, tpec, 45); 
            assertTrue(ec.hasAllMesiPubblicazione());
            assertEquals(2, ec.getSpedizioni().size());
            assertEquals(0, ec.getNumeroSpedizioniConSpesePostali());
            verificaImportoAbbonamentoAnnuale(ec,blocchetti,45);
        });

        Pubblicazione estratti = SmdLoadSampleData.getEstratti();
        EnumSet.allOf(TipoEstrattoConto.class).stream().forEach(tpec -> {
            EstrattoConto ec = creaECStd( estratti, tpec, 11); 
            assertTrue(ec.hasAllMesiPubblicazione());
            assertEquals(1, ec.getSpedizioni().size());
            assertEquals(0, ec.getNumeroSpedizioniConSpesePostali());
            verificaImportoAbbonamentoAnnuale(ec,estratti,11);
        });

    }
    
    @Test
    public void testCalcolaImporti() {
        Pubblicazione messaggio = SmdLoadSampleData.getMessaggio();
        EstrattoConto ec = creaEC(Mese.GENNAIO, Smd.getAnnoPassato(), Mese.MARZO, Smd.getAnnoPassato(),messaggio, TipoEstrattoConto.Scontato, 1);
        assertTrue(!ec.hasAllMesiPubblicazione());
        assertEquals(3, ec.getSpedizioni().size());
        assertEquals(3, ec.getNumeroSpedizioniConSpesePostali());
        assertEquals(messaggio.getCostoUnitario().doubleValue()*3, ec.getImporto().doubleValue(),0);
        assertEquals(messaggio.getSpeseSpedizione().doubleValue()*3, ec.getSpesePostali().doubleValue(),0);
        ec.getSpedizioni().stream().forEach(s -> assertEquals(s.getInvioSpedizione(),InvioSpedizione.AdpSede));
        try {
            creaEC(Mese.MARZO, Anno.ANNO2019, Mese.GENNAIO, Anno.ANNO2019, messaggio, TipoEstrattoConto.Scontato, 1);
            assertTrue(false);
        } catch (UnsupportedOperationException e) {
            assertEquals("data inizio maggiore di data fine", e.getMessage());
        }
        
        try {
            creaEC(Mese.AGOSTO, Anno.ANNO2019, Mese.AGOSTO, Anno.ANNO2019, messaggio, TipoEstrattoConto.Scontato, 1);
            assertTrue(false);
        } catch (UnsupportedOperationException e) {
            assertEquals("Nessuna spedizione per estratto conto", e.getMessage());
        }
        
    }

}
