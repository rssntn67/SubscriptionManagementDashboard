package it.arsinfo.smd.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import it.arsinfo.smd.data.Accettazione;
import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.AreaSpedizione;
import it.arsinfo.smd.data.Bollettino;
import it.arsinfo.smd.data.Cassa;
import it.arsinfo.smd.data.Ccp;
import it.arsinfo.smd.data.Cuas;
import it.arsinfo.smd.data.Incassato;
import it.arsinfo.smd.data.InvioSpedizione;
import it.arsinfo.smd.data.Mese;
import it.arsinfo.smd.data.RangeSpeseSpedizione;
import it.arsinfo.smd.data.Sostitutivo;
import it.arsinfo.smd.data.SpedizioneWithItems;
import it.arsinfo.smd.data.StatoAbbonamento;
import it.arsinfo.smd.data.StatoCampagna;
import it.arsinfo.smd.data.StatoSpedizione;
import it.arsinfo.smd.data.TipoAbbonamentoRivista;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Campagna;
import it.arsinfo.smd.entity.DistintaVersamento;
import it.arsinfo.smd.entity.OfferteCumulate;
import it.arsinfo.smd.entity.Operazione;
import it.arsinfo.smd.entity.OperazioneIncasso;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.entity.RivistaAbbonamento;
import it.arsinfo.smd.entity.Spedizione;
import it.arsinfo.smd.entity.SpedizioneItem;
import it.arsinfo.smd.entity.SpesaSpedizione;
import it.arsinfo.smd.entity.Storico;
import it.arsinfo.smd.entity.Versamento;

@Configuration
public class Smd {

    private static final Logger log = LoggerFactory.getLogger(Smd.class);
    private static final DateFormat formatter = new SimpleDateFormat("yyMMddH");
    private static final DateFormat unformatter = new SimpleDateFormat("yyMMdd");    

    
    public static final BigDecimal contrassegno=new BigDecimal(4.50);
    @Bean
    public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
    }

    public static Map<Integer,SpedizioneWithItems> getSpedizioneMap(List<SpedizioneWithItems> spedizioni) {
	    final Map<Integer,SpedizioneWithItems> spedMap = new HashMap<>();
	    for (SpedizioneWithItems spedizione:spedizioni) {
	        spedMap.put(getHashCode(spedizione.getSpedizione(), spedizione.getSpedizioneItems().iterator().next().getPubblicazione()), spedizione);
	    }
	    return spedMap;        
	}

	public static OutputStream getFileOutputStream(File file) throws Exception {
        try {
            log.info("Loading file: {}" , file.getName());
            return new FileOutputStream(file);
        } catch (final java.io.FileNotFoundException e) {
        	log.error("Cannot open file {}", e.getMessage());
        	throw e;
        }
    }
    public static File getIncassoFile(String filename) {
    	return new File("/tmp/" + filename);
    }
    
    public static List<DistintaVersamento> uploadIncasso(File file) throws Exception {
    	List<DistintaVersamento> incassi = new ArrayList<>();
        FileInputStream fstream;
        try {
            fstream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            log.error("Incasso Cancellato: " + e.getMessage());
            throw e;
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

        String strLine;

        //Read File Line By Line
        try {
            Set<String> versamentiLine = new HashSet<>();
            while ((strLine = br.readLine()) != null)   {
                if (strLine.trim().equals("")) {
                    log.warn("uploadIncasso: Riga vuota!");
                } else if (isVersamento(strLine)) {
                    versamentiLine.add(strLine);
                } else if (isRiepilogo(strLine)) {
                    incassi.add(Smd.generaIncasso(versamentiLine, strLine));
                    versamentiLine.clear();
                } else {
                    throw new UnsupportedOperationException("Valore non riconosciuto->" +strLine);
                }
            }
        } catch (Exception e) {
            log.error("uploadIncasso:: Incasso da File Cancellato: " + e.getMessage());
            throw e;
        } finally {
            br.close();
    	}
    	return incassi;
    }
    
    public static Map<Versamento, BigDecimal> getVersamentoMap(List<OperazioneIncasso> operazioni) {
    	Map<Versamento, BigDecimal> versamentoMap = new HashMap<>();
    	for (OperazioneIncasso oi: operazioni ) {
        	BigDecimal saldo = BigDecimal.ZERO;
    		if (versamentoMap.containsKey(oi.getVersamento())) {
    			saldo = versamentoMap.get(oi.getVersamento());
    		}
    		switch (oi.getStatoOperazioneIncasso()) {
			case Incasso:
				saldo=saldo.add(oi.getImporto());
				break;
			case Storno:
				saldo=saldo.subtract(oi.getImporto());
				break;
			default:
				break;
    		}
    		versamentoMap.put(oi.getVersamento(), saldo);
    	}
    	return versamentoMap;
    }

    public static Map<Abbonamento, BigDecimal> getAbbonamentoMap(List<OperazioneIncasso> operazioni) {
    	Map<Abbonamento, BigDecimal> abbonamentoMap = new HashMap<>();
    	for (OperazioneIncasso oi: operazioni ) {
        	BigDecimal saldo = BigDecimal.ZERO;
    		if (abbonamentoMap.containsKey(oi.getAbbonamento())) {
    			saldo = abbonamentoMap.get(oi.getAbbonamento());
    		}
    		switch (oi.getStatoOperazioneIncasso()) {
			case Incasso:
				saldo=saldo.add(oi.getImporto());
				break;
			case Storno:
				saldo=saldo.subtract(oi.getImporto());
				break;
			default:
				break;
    		}
    		abbonamentoMap.put(oi.getAbbonamento(), saldo);
    	}
    	return abbonamentoMap;
    }

    public static Incassato getStatoIncasso(Abbonamento abbonamento) {
    	if (abbonamento.getStatoAbbonamento() != StatoAbbonamento.Valido && abbonamento.getTotale().signum() == 0) {
    		return Incassato.Zero;
    	}
        if (abbonamento.getImporto().signum() == 0 && abbonamento.getTotale().signum() == 0) {
            return Incassato.Omaggio;
        }
        if (abbonamento.getIncassato().signum() == 0) {
            return Incassato.No;
        }
        if (abbonamento.getResiduo().signum() == 0) {
            return Incassato.Si;
        } 
        if (abbonamento.getImporto().add(abbonamento.getSpeseEstero()).compareTo(abbonamento.getIncassato()) <= 0) {
            return Incassato.SiConDebito;
        }
        return Incassato.Parzialmente;
    }

    public static Map<Anno, EnumSet<Mese>> getAnnoMeseMap(Mese meseInizio, Anno annoInizio, Mese meseFine, Anno annoFine, Pubblicazione p) throws UnsupportedOperationException {
        if (annoInizio.getAnno() > annoFine.getAnno()) {
            throw new UnsupportedOperationException("data inizio maggiore di data fine");
        }
        if (annoInizio == annoFine 
                && meseInizio.getPosizione() > meseFine.getPosizione()) {
            throw new UnsupportedOperationException("data inizio maggiore di data fine");
        }
        Map<Anno,EnumSet<Mese>> map = new HashMap<>();
        Anno anno = annoInizio;
        Mese mese = meseInizio;
        while (anno.getAnno() < annoFine.getAnno()) {
            if (p.getMesiPubblicazione().contains(mese)) {
                if (!map.containsKey(anno)) {
                    map.put(anno, EnumSet.noneOf(Mese.class));
                }                
                map.get(anno).add(mese);
            }
            mese = Mese.getMeseSuccessivo(mese);
            if (mese == Mese.GENNAIO) {
                anno=Anno.getAnnoSuccessivo(anno);
            }
        }
        
        while (mese.getPosizione() <= meseFine.getPosizione()) {
            if (p.getMesiPubblicazione().contains(mese)) {
                if (!map.containsKey(anno)) {
                    map.put(anno, EnumSet.noneOf(Mese.class));
                }                
                map.get(anno).add(mese);
            }
            mese = Mese.getMeseSuccessivo(mese);
            if (mese == Mese.GENNAIO) {
                break;
            }
        }
        return map;
    }

    public static RivistaAbbonamento genera(Abbonamento abb,Storico storico) {
        final RivistaAbbonamento ec = new RivistaAbbonamento();
        ec.setStorico(storico);
        ec.setAbbonamento(abb);
        ec.setPubblicazione(storico.getPubblicazione());
        ec.setNumero(storico.getNumero());
        ec.setTipoAbbonamentoRivista(storico.getTipoAbbonamentoRivista());
        ec.setMeseInizio(Mese.GENNAIO);
        ec.setAnnoInizio(abb.getAnno());
        ec.setMeseFine(Mese.DICEMBRE);
        ec.setAnnoFine(abb.getAnno());
        ec.setInvioSpedizione(storico.getInvioSpedizione());
        ec.setDestinatario(storico.getDestinatario());
        abb.addItem(ec);
        return ec;
    }

    public static boolean aggiorna(
    		RivistaAbbonamento updated, 
            List<SpedizioneWithItems> spedizioni,
            List<SpesaSpedizione> spese,    		
            RivistaAbbonamento original
		)    throws UnsupportedOperationException {
    	Abbonamento abb = original.getAbbonamento();
        if (abb.getStatoAbbonamento() == StatoAbbonamento.Annullato) {
            log.warn("aggiorna: failed {} {} : Aggiorna EC non consentita per Abbonamenti Annullati",abb,original);
            throw new UnsupportedOperationException("Aggiorna non consentito per Abbonamenti Annullati");
        }
        if (!original.equals(updated)) {
            log.warn("aggiorna: failed {} {} : Aggiorna consentito per numero e tipo abbonamento",original,updated);
            throw new UnsupportedOperationException("Aggiorna non consentito: Riviste non congruenti");
        }
        log.info("aggiorna: {}", abb);
        log.info("aggiorna: persisted: {}", original);
        log.info("aggiorna: update: {}",updated);
        
        //First of all I found all spedizioni for abbonamento,
        //Second I changed One Rivista:
        // If I changed the TipoAbbonamento, this has no impact on expedition..
        // Second if I changed 
        // Second I update
        if (updated.getNumero() == original.getNumero() && updated.getTipoAbbonamentoRivista() == original.getTipoAbbonamentoRivista()) {
            log.info("aggiorna: updated equals to persisted: {}", abb);        	
        	return true;
        }

        if (updated.getNumero() == original.getNumero()) {
        	calcoloImporto(updated);
            log.info("aggiorna: only type are different: importo updated: {}", updated.getImporto());        	
            log.info("aggiorna: only type are different: importo persisted: {}", original.getImporto());        	
        	abb.setImporto(abb.getImporto().subtract(original.getImporto()));
        	abb.setImporto(abb.getImporto().add(updated.getImporto()));
        	original=null;
            log.info("aggiorna: {}",abb);
            log.info("aggiorna: {}",updated);
           	return true;
        }

    	List<SpedizioneWithItems> inviate = new ArrayList<>();
    	for (SpedizioneWithItems spedwith: spedizioni) {
    		if (spedwith.getSpedizione().getStatoSpedizione() == StatoSpedizione.INVIATA) {
    			inviate.add(spedwith);
    		}
    	}
    	
        if (inviate.size() == 0) {
        	calcoloImporto(updated);
            log.info("aggiorna: nessuna spedizione inviata: importo updated: {}", updated.getImporto());        	
            log.info("aggiorna: nessuna spedizione inviata: importo persisted: {}", original.getImporto());        	
        	abb.setImporto(abb.getImporto().subtract(original.getImporto()));
        	abb.setImporto(abb.getImporto().add(updated.getImporto()));
        	
        	int numeroTotaleRiviste = 0;
        	for (SpedizioneWithItems s: spedizioni) {
        		for (SpedizioneItem item: s.getSpedizioneItems()) {
        			if (item.getRivistaAbbonamento().equals(updated)) {
        				item.setNumero(updated.getNumero());
                		numeroTotaleRiviste+=updated.getNumero();
        			}
        		}
        	}
        	updated.setNumeroTotaleRiviste(numeroTotaleRiviste);
        	calcolaPesoESpesePostali(abb, spedizioni, spese);
        	original=null;
            log.info("aggiorna: {}",abb);
            log.info("aggiorna: {}",updated);
    		return true;
        }
        
    	if (updated.getNumero() > original.getNumero()) {
        	original.setTipoAbbonamentoRivista(updated.getTipoAbbonamentoRivista());
        	abb.setImporto(abb.getImporto().subtract(original.getImporto()));
        	calcoloImporto(original);
        	abb.setImporto(abb.getImporto().add(original.getImporto()));
        	RivistaAbbonamento r = updated.clone();
        	r.setNumero(updated.getNumero()-original.getNumero());
        	updated = r;
        	calcoloImporto(updated);
            log.info("aggiorna: spedizione inviata: updated.numero>original.numero: importo updated: {}", updated.getImporto());        	
            log.info("aggiorna: spedizione inviata: updated.numero>original.numero:importo persisted: {}", original.getImporto());        	
        	genera(abb,updated, spedizioni, spese);
        	abb.setImporto(abb.getImporto().add(original.getImporto()));
        	calcolaPesoESpesePostali(abb, spedizioni, spese);
            log.info("aggiorna: {}",abb);
            log.info("aggiorna: {}",updated);
            log.info("aggiorna: {}",original);
        	return false;
    	}

    	// original = 
        log.info("aggiorna: importo abbonamento: {}", abb.getImporto());
        log.info("aggiorna: spese abbonamento: {}", abb.getSpese());
        log.info("aggiorna: pregresso abbonamento: {}", abb.getPregresso());
        
        final List<SpedizioneItem> invItems = new ArrayList<>(); 
        final List<SpedizioneItem> rimItems = new ArrayList<>();
        
        calcoloImporto(original);
        log.info("aggiorna: update: ec: meseInizio: {}", original.getMeseInizio().getNomeBreve());
        log.info("aggiorna: update: ec: annoInizio: {}", original.getAnnoInizio().getAnnoAsString());
        log.info("aggiornaEC: update: ec: meseFine: {}", original.getMeseFine().getNomeBreve());
        log.info("aggiornaEC: update: ec: annoFine: {}", original.getAnnoFine().getAnnoAsString());
        log.info("aggiornaEC: update: ec: quantità: {}", original.getNumero());
        log.info("aggiornaEC: update: ec: importo: {}", original.getImporto());
        abb.setImporto(abb.getImporto().add(original.getImporto()));
        log.info("aggiornaEC: update: abb: importo: {}", abb.getImporto());
        log.info("aggiornaEC: update: abb: spese: {}", abb.getSpese());
        log.info("aggiornaEC: update: abb:pregresso: {}", abb.getPregresso());
                
        calcolaPesoESpesePostali(abb, spedizioni, spese);
        log.info("aggiornaEC: update: abb: importo: {}", abb.getImporto());
        log.info("aggiornaEC: update: abb: spese: {}", abb.getSpese());
        log.info("aggiornaEC: update: abb:pregresso: {}", abb.getPregresso());

        //Updated status
        spedizioni.stream()
        .filter(sped -> sped.getSpedizione().getStatoSpedizione() != StatoSpedizione.INVIATA)
        .forEach(sped -> {
            switch (abb.getStatoAbbonamento()) {
            case Nuovo:
                sped.getSpedizione().setStatoSpedizione(StatoSpedizione.PROGRAMMATA);
                break;
            case Proposto:
                sped.getSpedizione().setStatoSpedizione(StatoSpedizione.PROGRAMMATA);
                break;
            case Valido:
                sped.getSpedizione().setStatoSpedizione(StatoSpedizione.PROGRAMMATA);
                break;
            case Sospeso:
                sped.getSpedizione().setStatoSpedizione(StatoSpedizione.SOSPESA);
                break;
            case Annullato:
                break;

            default:
                break;
            }
        });
        log.info("aggiornaEC: abb: intestatario: {}", abb.getIntestatario().getCaption());
        log.info("aggiornaEC: abb: area: {}", abb.getIntestatario().getAreaSpedizione());
        log.info("aggiornaEC: abb: importo: {}", abb.getImporto());
        log.info("aggiornaEC: abb: spese: {}", abb.getSpese());
        log.info("aggiornaEC: abb:pregresso: {}", abb.getPregresso());
        log.info("aggiornaEC: ec: pubbli.: {}", original.getPubblicazione().getNome());
        log.info("aggiornaEC: ec: meseInizio: {}", original.getMeseInizio().getNomeBreve());
        log.info("aggiornaEC: ec: annoInizio: {}", original.getAnnoInizio().getAnnoAsString());
        log.info("aggiornaEC: ec: meseFine: {}", original.getMeseFine().getNomeBreve());
        log.info("aggiornaEC: ec: annoFine: {}", original.getAnnoFine().getAnnoAsString());
        log.info("aggiornaEC: ec: quantità: {}", original.getNumero());
        log.info("aggiornaEC: ec: importo: {}", original.getImporto());
        return false;
    }

    public static List<SpedizioneItem> rimuoviEC(
            Abbonamento abb, 
            RivistaAbbonamento ec, 
            List<SpedizioneWithItems> spedizioni,
            List<SpesaSpedizione> spese) 
    {
        abb.setImporto(abb.getImporto().subtract(ec.getImporto()));
        abb.setSpese(BigDecimal.ZERO);
        final Map<Integer,SpedizioneWithItems> spedMap = spedizioni.stream().collect(Collectors.toMap(s->s.hashCode(), s->s));        
        final Map<Integer,List<SpedizioneItem>> inviati = new HashMap<>(); 
        final Map<Integer,List<SpedizioneItem>> rimossiMap = new HashMap<>(); 
        for (SpedizioneWithItems sped:spedizioni) {
            sped.getSpedizioneItems()
            .stream()
            .filter(item -> 
                item.getRivistaAbbonamento() == ec 
                || 
                (ec.getId() != null && item.getRivistaAbbonamento().getId() != null &&
                    item.getRivistaAbbonamento().getId().longValue() == ec.getId().longValue()
                    )
                )
            .forEach(item -> {
                switch (sped.getSpedizione().getStatoSpedizione()) {
                case INVIATA:
                    if (!inviati.containsKey(sped.hashCode()))
                    {
                        inviati.put(sped.hashCode(), new ArrayList<>());
                    }
                    inviati.get(sped.hashCode()).add(item);
                    break;
                case PROGRAMMATA:
                    if (!rimossiMap.containsKey(sped.hashCode()))
                    {
                        rimossiMap.put(sped.hashCode(), new ArrayList<>());
                    }
                    rimossiMap.get(sped.hashCode()).add(item);
                    break;
                case SOSPESA:
                    if (!rimossiMap.containsKey(sped.hashCode()))
                    {
                        rimossiMap.put(sped.hashCode(), new ArrayList<>());
                    }
                    rimossiMap.get(sped.hashCode()).add(item);
                    break;    
                default:
                    break;
                }
            });
        }
        List<SpedizioneItem> rimossi = new ArrayList<>();
        for (int hash:rimossiMap.keySet()) {
            for (SpedizioneItem rimosso:rimossiMap.get(hash)) {
                log.info("rimuoviEC: rimosso" + rimosso);
                spedMap.get(hash).deleteSpedizioneItem(rimosso);
                rimossi.add(rimosso);
            }
        }
        
        int numeroinviato=0;
        for (int hash:inviati.keySet()) {
                   for (SpedizioneItem inviata:inviati.get(hash)) {
                       numeroinviato+=inviata.getNumero();
                   }
        }
        ec.setNumero(0);
        ec.setNumeroTotaleRiviste(numeroinviato);
        calcolaPesoESpesePostali(abb,spedizioni, spese);
        calcoloImporto(ec);
        abb.setImporto(abb.getImporto().add(ec.getImporto())); 
        return rimossi;
    }

    public static List<SpedizioneItem> generaSpedizioneItems(RivistaAbbonamento ec) throws UnsupportedOperationException {
        log.debug("generaECItems: tipo: "+ ec.getTipoAbbonamentoRivista());
        log.debug("generaECItems: pubbli.: "+ ec.getPubblicazione().getNome());
        log.debug("generaECItems: meseInizio: "+ ec.getMeseInizio().getNomeBreve());
        log.debug("generaECItems: annoInizio: "+ ec.getAnnoInizio().getAnnoAsString());
        log.debug("generaECItems: meseFine: "+ ec.getMeseFine().getNomeBreve());
        log.debug("generaECItems: annoFine: "+ ec.getAnnoFine().getAnnoAsString());
        log.debug("generaECItems: quantità: "+ ec.getNumero());
        List<SpedizioneItem> items = new ArrayList<>();
        Map<Anno, EnumSet<Mese>> mappaPubblicazioni = RivistaAbbonamento.getAnnoMeseMap(ec);
        for (Anno anno: mappaPubblicazioni.keySet()) {
            mappaPubblicazioni.get(anno).stream().forEach(mese -> {
                SpedizioneItem item = new SpedizioneItem();
                item.setRivistaAbbonamento(ec);
                item.setAnnoPubblicazione(anno);
                item.setMesePubblicazione(mese);
                item.setNumero(ec.getNumero());
                item.setPubblicazione(ec.getPubblicazione());
                items.add(item);
            });
        }
      if (items.isEmpty()) {
          throw new UnsupportedOperationException("Nessuna spedizione per rivista in Abbonamento");
      }
      return items; 
    }

    private static int getHashCode(Spedizione sped, Pubblicazione p) {
    	if (sped.getInvioSpedizione() == InvioSpedizione.Spedizioniere) {
    		return sped.hashCode()+p.hashCode();
    	}
    	return sped.hashCode();
    }

    public static void aggiungiItemSpedizione(Abbonamento abb, RivistaAbbonamento ec,Map<Integer,SpedizioneWithItems> spedMap, SpedizioneItem item) {
        Anagrafica destinatario = ec.getDestinatario();
        InvioSpedizione invioSpedizione =ec.getInvioSpedizione();
        log.info("aggiungiItemSpedizione: {} ",item);
        InvioSpedizione isped = invioSpedizione;
        Mese mesePubblicazione = item.getMesePubblicazione();
        Anno annoPubblicazione = item.getAnnoPubblicazione();
        boolean posticipata=false;
        int anticipoSpedizione = ec.getPubblicazione().getAnticipoSpedizione();
        Mese spedMese;
        Anno spedAnno;

        if (mesePubblicazione.getPosizione() - anticipoSpedizione <= 0) {
            spedMese = Mese.getByPosizione(12
                    + mesePubblicazione.getPosizione()
                    - anticipoSpedizione);
            spedAnno = Anno.getAnnoPrecedente(annoPubblicazione);
        } else {
            spedMese = Mese.getByPosizione(mesePubblicazione.getPosizione()
                    - anticipoSpedizione);
            spedAnno = annoPubblicazione;
        }
        log.info("aggiungiItemSpedizione: teorico: {}, {}, {}",spedMese.getNomeBreve(),spedAnno.getAnnoAsString(),isped);

        if (spedAnno.getAnno() < Anno.getAnnoCorrente().getAnno()
                || (spedAnno == Anno.getAnnoCorrente()
                        && spedMese.getPosizione() < Mese.getMeseCorrente().getPosizione())) {
            spedMese = Mese.getMeseCorrente();
            spedAnno = Anno.getAnnoCorrente();
            isped = InvioSpedizione.AdpSede;
            posticipata=true;
            log.info("aggiungiItemSpedizione: posticipata: {}, {}, {}",spedMese.getNomeBreve(),spedAnno.getAnnoAsString(),isped);

        }
        if (destinatario.getAreaSpedizione() != AreaSpedizione.Italia) {
            isped = InvioSpedizione.AdpSede;
            log.info("aggiungiItemSpedizione: estero: {}, {}, {}",spedMese.getNomeBreve(),spedAnno.getAnnoAsString(),isped);
        }
        Spedizione spedizione = new Spedizione();
        spedizione.setMeseSpedizione(spedMese);
        spedizione.setAnnoSpedizione(spedAnno);
        spedizione.setInvioSpedizione(isped);
        spedizione.setAbbonamento(abb);
        spedizione.setDestinatario(destinatario);
        int hash = getHashCode(spedizione, item.getPubblicazione());
        if (!spedMap.containsKey(hash)) {
            spedMap.put(hash, new SpedizioneWithItems(spedizione));
        }
        SpedizioneWithItems sped = spedMap.get(hash);
        item.setPosticipata(posticipata);
        item.setSpedizione(sped.getSpedizione());
        sped.addSpedizioneItem(item);        
    }
    
    public static List<SpedizioneWithItems> genera(Abbonamento abb,
            RivistaAbbonamento ec, 
            List<SpedizioneWithItems> spedizioni, 
            List<SpesaSpedizione> spese) throws UnsupportedOperationException {

        ec.setAbbonamento(abb);
        List<SpedizioneItem> items = generaSpedizioneItems(ec);
        ec.setNumeroTotaleRiviste(ec.getNumero()*items.size());
        calcoloImporto(ec);
        abb.setImporto(abb.getImporto().add(ec.getImporto()));
        final Map<Integer, SpedizioneWithItems> spedMap = getSpedizioneMap(spedizioni);

        for (SpedizioneItem item : items) {
            aggiungiItemSpedizione(abb, ec, spedMap, item);
        }
        calcolaPesoESpesePostali(abb, spedMap.values(), spese);
        return spedMap.values().stream().collect(Collectors.toList());
    }

    private static void calcolaPesoESpesePostali(Abbonamento abb, Collection<SpedizioneWithItems> spedizioni, List<SpesaSpedizione> spese) {
        abb.setSpese(BigDecimal.ZERO);
        abb.setSpeseEstero(BigDecimal.ZERO);
        for (SpedizioneWithItems sped: spedizioni) {
            int pesoStimato=0;
            for (SpedizioneItem item: sped.getSpedizioneItems()) {
                pesoStimato+=item.getNumero()*item.getPubblicazione().getGrammi();
            }
            SpesaSpedizione spesa = 
                    getSpesaSpedizione(
                               spese, 
                               sped.getSpedizione().getDestinatario().getAreaSpedizione(), 
                               RangeSpeseSpedizione.getByPeso(pesoStimato)
                    		);
            
            sped.getSpedizione().setPesoStimato(pesoStimato);
            switch (sped.getSpedizione().getDestinatario().getAreaSpedizione()) {
            case Italia:
                calcolaSpesePostali(sped.getSpedizione(), spesa);
                abb.setSpese(abb.getSpese().add(sped.getSpedizione().getSpesePostali()));
                break;
            case EuropaBacinoMediterraneo:
                calcolaSpesePostali(sped.getSpedizione(), spesa);
                abb.setSpeseEstero(abb.getSpeseEstero().add(sped.getSpedizione().getSpesePostali()));
                break;
            case AmericaAfricaAsia:
                calcolaSpesePostali(sped.getSpedizione(), spesa);
                abb.setSpeseEstero(abb.getSpeseEstero().add(sped.getSpedizione().getSpesePostali()));
                break;
            default:
                break;
            }
        }
        if (abb.isContrassegno()) {
            abb.setSpese(abb.getSpese().add(contrassegno));                
        }

    }
    
    public static SpesaSpedizione getSpesaSpedizione(List<SpesaSpedizione> ss,AreaSpedizione area, RangeSpeseSpedizione range) throws UnsupportedOperationException {
        for (SpesaSpedizione s: ss) {
            if (s.getAreaSpedizione() == area && s.getRangeSpeseSpedizione() == range) {
                return s;
            }
        }
        throw new UnsupportedOperationException("cannot get spese di spedizione per Area: " + area.name() + ", range: " + range.name());
    }
    
    public static void calcolaSpesePostali(Spedizione sped, SpesaSpedizione spesa) throws UnsupportedOperationException {
        switch (sped.getInvioSpedizione()) {
        case Spedizioniere:
        	break;
        case AdpSedeNoSpese:
        	break;
        case AdpSede:
            sped.setSpesePostali(spesa.getSpese());
        	break;
        case AdpSedeCorriere24hh:
            sped.setSpesePostali(spesa.getCor24h());
        	break;
        case AdpSedeCorriere3gg:
            sped.setSpesePostali(spesa.getCor3gg());
        	break;
        default:
        	break;        	
        }
    }
    
    public static List<RivistaAbbonamento> 
        generaRivisteAbbonamentiCampagna(final Campagna campagna,final Abbonamento abbonamento, List<Storico> storici) 
        throws UnsupportedOperationException {
        if (abbonamento.getCampagna() != campagna) {
            throw new UnsupportedOperationException("Campagna ed abbonamento non matchano");
        }
        if (abbonamento.getStatoAbbonamento() != StatoAbbonamento.Nuovo || campagna.getStatoCampagna() != StatoCampagna.Generata) {
            throw new UnsupportedOperationException("Campagna ed abbonamento non nuovi");
        }
        final List<RivistaAbbonamento> ecs = new ArrayList<>();
        storici
        .stream()
        .filter(storico -> 
            storico.getIntestatario().getId().longValue() == abbonamento.getIntestatario().getId().longValue()
            && 
            campagna.hasPubblicazione(storico.getPubblicazione())
            &&
            abbonamento.isContrassegno() == storico.isContrassegno()
                ).forEach(storico ->
            genera(abbonamento, storico));
        return ecs;
    }
    
    public static List<Abbonamento> genera(final Campagna campagna, List<Anagrafica> anagrafiche, List<Storico> storici) {
        final List<Abbonamento> abbonamenti = new ArrayList<>();
        anagrafiche.stream().forEach(a -> {
            abbonamenti.addAll(genera(campagna, a, storici));
        });        
        return abbonamenti;

    }
    
    public static List<Abbonamento> genera(Campagna campagna,Anagrafica a, List<Storico> storici) {
        final List<Abbonamento> abbonamenti = new ArrayList<>();
        for (Storico storico: storici) {
        	if (
            	campagna.hasPubblicazione(storico.getPubblicazione()) 
            	&& storico.getIntestatario().equals(a) 
                && storico.isContrassegno()
            ) {
    		    try {
    	            abbonamenti.add(genera(campagna, a,true));
    	            break;
    	        } catch (Exception e) {
    	            e.printStackTrace();
    	        }        	
        	}
        }
        for (Storico storico: storici) {
        	if (
            	campagna.hasPubblicazione(storico.getPubblicazione()) 
            	&& storico.getIntestatario().equals(a) 
                && !storico.isContrassegno()
            ) {
    		    try {
    	            abbonamenti.add(genera(campagna, a,false));
    	            break;
    	        } catch (Exception e) {
    	            e.printStackTrace();
    	        }        	
        	}
        }
        return abbonamenti;
    }

    public static Abbonamento genera(Campagna campagna, Anagrafica a,boolean contrassegno) throws Exception {
        if (campagna == null) {
            throw new Exception("genera: Null Campagna");
        }
        if (a == null) {
            throw new Exception("genera: Null Intestatario");
        }
        Abbonamento abbonamento = new Abbonamento();
        abbonamento.setIntestatario(a);
        abbonamento.setCampagna(campagna);
        abbonamento.setAnno(campagna.getAnno());
        abbonamento.setContrassegno(contrassegno);
        abbonamento.setCodeLine(Abbonamento.generaCodeLine(abbonamento.getAnno(),a));
        abbonamento.setStatoAbbonamento(StatoAbbonamento.Nuovo);
        return abbonamento;
    }

    public static void calcoloImporto(RivistaAbbonamento ec) throws UnsupportedOperationException {
        BigDecimal importo=BigDecimal.ZERO;
        switch (ec.getTipoAbbonamentoRivista()) {
        case Ordinario:
            importo = ec.getPubblicazione().getAbbonamento().multiply(new BigDecimal(ec.getNumero()));
            if (!ec.isAbbonamentoAnnuale() || ec.getNumero() == 0) {
                importo = ec.getPubblicazione().getCostoUnitario().multiply(new BigDecimal(ec.getNumeroTotaleRiviste()));
            }
            break;

        case Web:
            if (!ec.isAbbonamentoAnnuale()) {
                    throw new UnsupportedOperationException("Valori mesi inizio e fine non ammissibili per " + TipoAbbonamentoRivista.Web);
            }
            importo = ec.getPubblicazione().getAbbonamentoWeb().multiply(new BigDecimal(ec.getNumero()));
            break;

        case Scontato:
            if (!ec.isAbbonamentoAnnuale()) {
                throw new UnsupportedOperationException("Valori mesi inizio e fine non ammissibili per " + TipoAbbonamentoRivista.Web);
            }
            importo = ec.getPubblicazione().getAbbonamentoConSconto().multiply(new BigDecimal(ec.getNumero()));
            break;

        case Sostenitore:
            if (!ec.isAbbonamentoAnnuale()) {
                throw new UnsupportedOperationException("Valori mesi inizio e fine non ammissibili per " + TipoAbbonamentoRivista.Web);
            }
            importo = ec.getPubblicazione().getAbbonamentoSostenitore().multiply(new BigDecimal(ec.getNumero()));
            break;
                
        case OmaggioCuriaDiocesiana:
            break;
        case OmaggioCuriaGeneralizia:
            break;
        case OmaggioDirettoreAdp:
            break;
        case OmaggioEditore:
            break;
        case OmaggioGesuiti:
            break;
        case Duplicato:
            break;
        default:
            break;

        }          
        ec.setImporto(importo);
    }
           
    public static Date getStandardDate(LocalDate localDate) {
        return getStandardDate(Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));       
    }

    public static Date getStandardDate(Date date) {
        return getStandardDate(unformatter.format(date));
    }

    public static Date getStandardDate(String yyMMdd) {
        try {
            return formatter.parse(yyMMdd+"8");
        } catch (ParseException e) {
            log.error(e.getMessage());
        }
        return null;
    };

    public static List<Operazione> generaOperazioni(
            List<Pubblicazione> pubblicazioni, 
            List<SpedizioneWithItems> spedizioni
        ) {
        Anno anno = Anno.getAnnoCorrente();
        Mese mese = Mese.getMeseCorrente();
        List<Operazione> operazioni = new ArrayList<>();
        pubblicazioni.stream().forEach(p -> {
            Operazione operazione = generaOperazione(p, spedizioni,mese,anno);
            if (operazione.getStimatoSede() != 0 || operazione.getStimatoSped() != 0) {
                    operazioni.add(operazione);
            }
        }
        );
        return operazioni;
    }

    public static Operazione generaOperazione(
            Pubblicazione pubblicazione, 
            List<SpedizioneWithItems> spedizioni,Mese mese, Anno anno) {
    	log.info("generaOperazione {}, {}, {}", pubblicazione,mese,anno);
        final Operazione op = new Operazione(pubblicazione, anno, mese);
        int posizioneMese=mese.getPosizione()+pubblicazione.getAnticipoSpedizione();
        Mese mesePubblicazione;
        Anno annoPubblicazione;
        if (posizioneMese > 12) {
            mesePubblicazione = Mese.getByPosizione(posizioneMese-12);
            annoPubblicazione = Anno.getAnnoSuccessivo(anno);
        } else {
            annoPubblicazione=anno;
            mesePubblicazione=Mese.getByPosizione(posizioneMese);
        }

        if (!pubblicazione.getMesiPubblicazione().contains(mesePubblicazione)) {
            return op;
        }
        op.setMesePubblicazione(mesePubblicazione);
        op.setAnnoPubblicazione(annoPubblicazione);
        spedizioni
            .stream()
            .filter( sped -> 
                   sped.getSpedizione().getStatoSpedizione() == StatoSpedizione.PROGRAMMATA 
                && sped.getSpedizione().getAnnoSpedizione() == anno 
                && sped.getSpedizione().getMeseSpedizione() == mese) 
            .forEach( sped -> 
                  sped
                  .getSpedizioneItems()
                  .stream()
                  .filter(item -> !item.isPosticipata() && item.getPubblicazione().hashCode() == pubblicazione.hashCode())
                  .forEach(item -> 
                  {
                      switch (sped.getSpedizione().getInvioSpedizione()) {
                      case  Spedizioniere: 
                          op.setStimatoSped(op.getStimatoSped()+item.getNumero());
                          break;
                      default:
                          op.setStimatoSede(op.getStimatoSede()+item.getNumero());
                        break;
                      }
                  })
              );                        
        return op;        
    }
 
    public static BigDecimal incassa(DistintaVersamento incasso, Versamento versamento, OfferteCumulate offerte, BigDecimal importo) throws UnsupportedOperationException {
        if (incasso == null ) {
            log.error("incassa: Incasso null");
            throw new UnsupportedOperationException("incassa: Incasso null");
        }
        if (versamento == null ) {
            log.error("incassa: Versamento null");
            throw new UnsupportedOperationException("incassa: Versamento null");
        }
        if (offerte == null ) {
            log.error("incassa: Offerte null");
            throw new UnsupportedOperationException("incassa: Abbonamento null");
        }
 
        BigDecimal incassato = importo;
        if (importo.compareTo(versamento.getResiduo()) > 0) {
        	incassato = new BigDecimal(versamento.getResiduo().doubleValue());
        }        
        versamento.setIncassato(versamento.getIncassato().add(incassato));
        offerte.setImporto(offerte.getImporto().add(incassato));
        incasso.setIncassato(incasso.getIncassato().add(incassato));
        return incassato;
    }
    
    public static void storna(DistintaVersamento incasso, Versamento versamento, OfferteCumulate offerte, BigDecimal importo) throws UnsupportedOperationException {
        if (incasso == null ) {
            log.error("storna: Incasso null");
            throw new UnsupportedOperationException("storna: Incasso null");
        }
        if (versamento == null ) {
            log.error("storna: Versamento null");
            throw new UnsupportedOperationException("storna: Versamento null");
        }
        if (offerte == null ) {
            log.error("storna: Offerte null");
            throw new UnsupportedOperationException("storna: Abbonamento null");
        }
        if (versamento.getIncassato().compareTo(importo) < 0) {
            log.error("storna: incassato Versamento minore importo da stornare");
            throw new UnsupportedOperationException("storna: importo Versamento minore importo da stornare");
        }
        if (offerte.getImporto().compareTo(importo) < 0) {
            log.error("storna: incassato Offerte minore importo da stornare");
            throw new UnsupportedOperationException("storna: totale Offerte minore importo da stornare");
        }
        versamento.setIncassato(versamento.getIncassato().subtract(importo));
        offerte.setImporto(offerte.getImporto().subtract(importo));
        incasso.setIncassato(incasso.getIncassato().subtract(importo));
    }


    public static BigDecimal incassa(DistintaVersamento incasso, Versamento versamento, Abbonamento abbonamento) throws UnsupportedOperationException {
        if (incasso == null ) {
            log.error("incassa: Incasso null");
            throw new UnsupportedOperationException("incassa: Incasso null");
        }
        if (versamento == null ) {
            log.error("incassa: Versamento null");
            throw new UnsupportedOperationException("incassa: Versamento null");
        }
        if (abbonamento == null ) {
            log.error("incassa: Abbonamento null");
            throw new UnsupportedOperationException("incassa: Abbonamento null");
        }
 
        BigDecimal incassato = BigDecimal.ZERO;
        if ((versamento.getResiduo()).compareTo(abbonamento.getResiduo()) < 0) {
        	incassato = new BigDecimal(versamento.getResiduo().doubleValue());
        } else {
        	incassato = new BigDecimal(abbonamento.getResiduo().doubleValue());
        }
        
        versamento.setIncassato(versamento.getIncassato().add(incassato));
        abbonamento.setIncassato(abbonamento.getIncassato().add(incassato));
        incasso.setIncassato(incasso.getIncassato().add(incassato));
        return incassato;
    }

    public static void storna(DistintaVersamento incasso, Versamento versamento, Abbonamento abbonamento, BigDecimal importo) throws UnsupportedOperationException {
        if (incasso == null ) {
            log.error("storna: Incasso null");
            throw new UnsupportedOperationException("storna: Incasso null");
        }
        if (versamento == null ) {
            log.error("storna: Versamento null");
            throw new UnsupportedOperationException("storna: Versamento null");
        }
        if (abbonamento == null ) {
            log.error("storna: Abbonamento null");
            throw new UnsupportedOperationException("storna: Abbonamento null");
        }
        if (versamento.getIncassato().compareTo(importo) < 0) {
            log.error("storna: incassato Versamento minore importo da stornare");
            throw new UnsupportedOperationException("storna: importo Versamento minore importo da stornare");
        }
        if (abbonamento.getIncassato().compareTo(importo) < 0) {
            log.error("storna: incassato Abbonamento minore importo da stornare");
            throw new UnsupportedOperationException("storna: totale Abbonamento minore importo da stornare");
        }
        versamento.setIncassato(versamento.getIncassato().subtract(importo));
        abbonamento.setIncassato(abbonamento.getIncassato().subtract(importo));
        incasso.setIncassato(incasso.getIncassato().subtract(importo));
    }
    
    public static boolean isVersamento(String versamento) {   
               return (
                versamento != null && versamento.length() == 200 
                && (versamento.trim().length() == 82 || versamento.trim().length() == 89));
    }
    
    public static boolean isRiepilogo(String riepilogo) {
        return ( riepilogo != null &&
                 riepilogo.length() == 200 &&
                 riepilogo.trim().length() == 96 &&
                 riepilogo.substring(19,33).trim().length() == 0 &&
                 riepilogo.substring(33,36).equals("999")
                );
    }
    
    public static DistintaVersamento generaIncasso(Set<String> versamenti,
            String riepilogo) throws UnsupportedOperationException {
        final DistintaVersamento incasso = new DistintaVersamento();
        incasso.setCassa(Cassa.Ccp);
        incasso.setCuas(Cuas.getCuas(Integer.parseInt(riepilogo.substring(0,1))));
        incasso.setCcp(Ccp.getByCc(riepilogo.substring(1,13)));
        incasso.setDataContabile(Smd.getStandardDate(riepilogo.substring(13,19)));
//          String filler = riepilogo.substring(19,33);
//          String idriepilogo = riepilogo.substring(33,36);
        incasso.setDocumenti(Integer.parseInt(riepilogo.substring(36,44)));
        incasso.setImporto(new BigDecimal(riepilogo.substring(44,54)
                + "." + riepilogo.substring(54,56)));

        incasso.setEsatti(Integer.parseInt(riepilogo.substring(56,64)));
        incasso.setImportoEsatti(new BigDecimal(riepilogo.substring(64,74)
                + "." + riepilogo.substring(74, 76)));

        incasso.setErrati(Integer.parseInt(riepilogo.substring(76,84)));
        incasso.setImportoErrati(new BigDecimal(riepilogo.substring(84,94)
                + "." + riepilogo.substring(94, 96)));
        log.info("generaIncasso: {}", incasso);

        versamenti.
            forEach(s -> incasso.addItem(generateVersamento(incasso,s)));
        checkIncasso(incasso);
        return incasso;
    }

    private static void checkIncasso(DistintaVersamento incasso) throws UnsupportedOperationException {
    	BigDecimal importoVersamenti = BigDecimal.ZERO;
    	for (Versamento v: incasso.getItems()) {
    		importoVersamenti = importoVersamenti.add(v.getImporto());
    	}
    	if (incasso.getImporto().subtract(importoVersamenti).signum() != 0 ) {
    		log.error("checkincasso: importo incasso {} non corrisponde a importoVersamenti {}",incasso.getImporto(),importoVersamenti);
    		throw new UnsupportedOperationException("Importo Incasso e Versamento non corrispondono ");
    	}
    }
    
    private static Versamento generateVersamento(DistintaVersamento incasso,String value)
            {
        Versamento versamento = new Versamento(incasso,new BigDecimal(value.substring(36, 44) + "." + value.substring(44, 46)));
        versamento.setBobina(value.substring(0, 3));
        versamento.setProgressivoBobina(value.substring(3, 8));
        versamento.setProgressivo(value.substring(8,15));
        versamento.setDataPagamento(Smd.getStandardDate(value.substring(27,33)));
        versamento.setBollettino(Bollettino.getTipoBollettino(Integer.parseInt(value.substring(33,36))));
        versamento.setProvincia(value.substring(46, 49));
        versamento.setUfficio(value.substring(49, 52));
        versamento.setSportello(value.substring(52, 54));
//          value.substring(54,55);
        versamento.setDataContabile(Smd.getStandardDate(value.substring(55,61)));
        versamento.setCodeLine(value.substring(61,79));
        versamento.setAccettazione(Accettazione.getTipoAccettazione(value.substring(79,81)));
        versamento.setSostitutivo(Sostitutivo.getTipoAccettazione(value.substring(81,82)));
        log.info("generateVersamento: {}", versamento);
        return versamento;
    }
        
    public static void calcoloImportoIncasso(DistintaVersamento incasso) {
        BigDecimal importo = BigDecimal.ZERO;
        for (Versamento versamento: incasso.getItems()) {
            importo=importo.add(versamento.getImporto());
        }
        incasso.setImporto(importo);
        incasso.setDocumenti(incasso.getItems().size());
        incasso.setErrati(0);
        incasso.setEsatti(incasso.getDocumenti());
        incasso.setImportoErrati(BigDecimal.ZERO);
        incasso.setImportoEsatti(incasso.getImporto());
    }

    public static void calcoloImportoIncasso(DistintaVersamento incasso, List<Versamento> versamenti) {
        BigDecimal importo = BigDecimal.ZERO;
        for (Versamento versamento: versamenti) {
            importo=importo.add(versamento.getImporto());
        }
        incasso.setImporto(importo);
        incasso.setImportoEsatti(incasso.getImporto().subtract(incasso.getImportoErrati()));
    }
    
    public static Versamento getWithAnagrafica(Versamento v,Anagrafica a) {
    	if (v != null && a != null && v.getCommittente() != null && v.getCommittente().equals(a)) {
        	v.setCommittente(a);    		
    	}
    	return v;
    }
    
    public static List<Versamento> getWithAnagrafiche(List<Versamento> versamenti, List<Anagrafica> anagrafica) {
        Map<Long,Anagrafica> anagraficaMap=anagrafica
        		.stream()
        		.collect(Collectors.toMap(Anagrafica::getId, Function.identity()));
      	for (Versamento versamento: versamenti) {
    		if (versamento.getCommittente() != null) {
    			versamento.setCommittente(anagraficaMap.get(versamento.getCommittente().getId()));
    		}
    	}
      	return versamenti;

    }
    
}
