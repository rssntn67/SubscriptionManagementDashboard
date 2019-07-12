package it.arsinfo.smd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.junit4.SpringRunner;

import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.AreaSpedizione;
import it.arsinfo.smd.data.Invio;
import it.arsinfo.smd.data.InvioSpedizione;
import it.arsinfo.smd.data.Mese;
import it.arsinfo.smd.data.TipoEstrattoConto;
import it.arsinfo.smd.data.TipoPubblicazione;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.EstrattoConto;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.entity.Spedizione;
import it.arsinfo.smd.entity.SpedizioneItem;
import it.arsinfo.smd.entity.SpesaSpedizione;

@RunWith(SpringRunner.class)
public class SmdUnitTests {
    
    private static final Logger log = LoggerFactory.getLogger(Smd.class);

    private static EstrattoConto creaECItalia(Abbonamento abb,Pubblicazione p, TipoEstrattoConto tipo, int numero) {
        Anno anno = Smd.getAnnoProssimo();
        Mese mese = Smd.getMeseCorrente();
        if (mese.getPosizione()+p.getAnticipoSpedizione() > 12) {
            anno=Anno.getAnnoSuccessivo(anno);
        }
        return creaEC(abb,Mese.GENNAIO, anno, Mese.DICEMBRE, anno, p, tipo, numero);
    }
    
    private static EstrattoConto creaEC(Abbonamento abb, Mese inizio, Anno ai, Mese fine, Anno af, Pubblicazione p, TipoEstrattoConto tipo, int numero) {
        EstrattoConto ec =  new EstrattoConto();
        ec.setAbbonamento(abb);
        ec.setNumero(numero);
        ec.setPubblicazione(p);
        ec.setTipoEstrattoConto(tipo);
        ec.setMeseInizio(inizio);
        ec.setAnnoInizio(ai);
        ec.setMeseFine(fine);
        ec.setAnnoFine(af);
        Smd.generaECItems(abb,ec);

        return ec;
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
    
    private void verificaImportoAbbonamentoAnnuale(Abbonamento abb, EstrattoConto ec) {
        assertEquals(0.0,abb.getSpese().doubleValue(),0);
        assertEquals(true, ec.isAbbonamentoAnnuale());
        assertEquals(abb, ec.getAbbonamento());
        switch (ec.getTipoEstrattoConto()) {
        case OmaggioCuriaDiocesiana:
            assertEquals(0.0,abb.getImporto().doubleValue(),0);
           break;
        case OmaggioGesuiti:
            assertEquals(0.0,ec.getImporto().doubleValue(),0);
                       break;
        case OmaggioCuriaGeneralizia:
            assertEquals(0.0,ec.getImporto().doubleValue(),0);
            break;
        case OmaggioDirettoreAdp:
            assertEquals(0.0,ec.getImporto().doubleValue(),0);
            break;
        case OmaggioEditore:
            assertEquals(0.0,ec.getImporto().doubleValue(),0);
            break;
        case Ordinario:
            assertEquals(ec.getPubblicazione().getAbbonamento().multiply(new BigDecimal(ec.getNumero())).doubleValue()
                         ,ec.getImporto().doubleValue(),0);
            break;
        case Sostenitore:
            assertEquals(ec.getPubblicazione().getAbbonamentoSostenitore().multiply(new BigDecimal(ec.getNumero())).doubleValue()
                         ,ec.getImporto().doubleValue(),0);
            break;
        case Scontato:
            assertEquals(ec.getPubblicazione().getAbbonamentoConSconto().multiply(new BigDecimal(ec.getNumero())).doubleValue()
                         ,ec.getImporto().doubleValue(),0);
            break;
        case Web:
            assertEquals(ec.getPubblicazione().getAbbonamentoWeb().multiply(new BigDecimal(ec.getNumero())).doubleValue()
                         ,ec.getImporto().doubleValue(),0);
            break;
        default:
            break;
        }
                

    }
    
    @Test
    public void testCreaSpedizioneMessaggio() {
        List<SpesaSpedizione> spese = SmdLoadSampleData.getSpeseSpedizione();
        Abbonamento abb = new Abbonamento();
        abb.setIntestatario(new Anagrafica());
        Pubblicazione messaggio = SmdLoadSampleData.getMessaggio();
        assertEquals(2, messaggio.getAnticipoSpedizione());
        EstrattoConto ec = new EstrattoConto();
        ec.setPubblicazione(messaggio);
        ec.setNumero(10);
        Anno anno = Smd.getAnnoProssimo();
        anno = Anno.getAnnoSuccessivo(anno);
        ec.setMeseFine(Mese.GENNAIO);
        ec.setMeseFine(Mese.MARZO);
        List<SpedizioneItem> items = Smd.generaECItems(abb, ec);
        
        assertEquals(TipoEstrattoConto.Ordinario, ec.getTipoEstrattoConto());
        assertEquals(messaggio.getAbbonamento().multiply(new BigDecimal(10)).doubleValue(), ec.getImporto().doubleValue(),0);
        assertEquals(abb.getImporto().doubleValue(), ec.getImporto().doubleValue(),0);
        assertEquals(BigDecimal.ZERO, abb.getSpese());
        assertEquals(abb, ec.getAbbonamento());
        assertEquals(messaggio.getMesiPubblicazione().size()*10, ec.getNumeroTotaleRiviste().intValue());
        assertEquals(messaggio.getMesiPubblicazione().size(), items.size());
        EnumSet<Mese> mesiPubb = EnumSet.noneOf(Mese.class);
        for (SpedizioneItem item: items) {
            assertEquals(anno, item.getAnnoPubblicazione());
            assertEquals(ec, item.getEstrattoConto());
            assertEquals(10, item.getNumero().intValue());
            mesiPubb.add(item.getMesePubblicazione());
           
            log.info(item.toString());
        }
        List<Spedizione> spedizioni = Smd.generaSpedizioni(abb, items, Invio.Destinatario, InvioSpedizione.Spedizioniere, new Anagrafica(), spese);
        
        for (Spedizione spedizione: spedizioni) {
            assertEquals(abb, spedizione.getAbbonamento());
        }
        
    }

    @Test
    public void testImportoAbbonamentoStd() {
        Pubblicazione messaggio = SmdLoadSampleData.getMessaggio();
        Anagrafica ar = SmdLoadSampleData.getAR();
        Abbonamento abb = new Abbonamento();
        abb.setIntestatario(ar);
        EnumSet.allOf(TipoEstrattoConto.class).stream().forEach(tpec -> {
            EstrattoConto ec = creaECItalia(abb,messaggio, tpec, 10); 
            verificaImportoAbbonamentoAnnuale(abb,ec);
        });
    }

    @Test
    public void testCostiAbbonamentoEsteroStd() {
        Pubblicazione p = SmdLoadSampleData.getMessaggio();
        Anno anno = Smd.getAnnoProssimo();
        Mese mese = Smd.getMeseCorrente();
        if (mese.getPosizione()+p.getAnticipoSpedizione() > 12) {
            anno=Anno.getAnnoSuccessivo(anno);
        }
        Abbonamento abb = new Abbonamento();
        Anagrafica intestatario = new Anagrafica();
        intestatario.setNome("Tizio");
        intestatario.setCognome("Sempronis");
        intestatario.setAreaSpedizione(AreaSpedizione.AmericaAfricaAsia);
        abb.setIntestatario(intestatario);
        EstrattoConto ec = new EstrattoConto();
        
        assertTrue(ec.isAbbonamentoAnnuale());
    }
    
}
