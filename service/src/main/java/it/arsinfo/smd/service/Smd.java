package it.arsinfo.smd.service;

import it.arsinfo.smd.data.*;
import it.arsinfo.smd.service.dto.RivistaAbbonamentoAggiorna;
import it.arsinfo.smd.service.dto.SpedizioneWithItems;
import it.arsinfo.smd.entity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
public class Smd {

    private static final Logger log = LoggerFactory.getLogger(Smd.class);

	public static StatoAbbonamento getStatoAbbonamento(boolean almenounarivistaattiva, boolean almenounarivistasospesa, Incassato incassato, StatoCampagna statoCampagna) {
    	if (statoCampagna == StatoCampagna.Generata ) {
    		return StatoAbbonamento.Nuovo;
    	}
    	
    	if (incassato == Incassato.Si || incassato == Incassato.SiConDebito) {
    		return StatoAbbonamento.Valido;
    	}
    	
    	if (statoCampagna == StatoCampagna.Inviata || statoCampagna == StatoCampagna.InviatoSollecito) {
    		return StatoAbbonamento.Proposto;
    	}

    	if (almenounarivistaattiva && !almenounarivistasospesa) {
    		return StatoAbbonamento.Proposto;
    	} 
    	if (almenounarivistaattiva){
    		return StatoAbbonamento.ParzialmenteSospeso;        		
    	}
    	
    	return StatoAbbonamento.Sospeso;

    }
    public static StatoAbbonamento getStatoAbbonamento(Abbonamento abbonamento, RivistaAbbonamento rivista, Campagna campagna, boolean rivistasospesa) {
    	
    	if (Smd.isOmaggio(rivista)) {
			return StatoAbbonamento.Valido;
		}

        StatoAbbonamento stato = StatoAbbonamento.Nuovo;
        
        if (campagna != null) {
        	switch (campagna.getStatoCampagna()) {

			case Inviata:
			case InviatoSollecito:
				stato=StatoAbbonamento.Proposto;
				break;

			case InviatoSospeso:
				if (rivistasospesa) {
					stato = StatoAbbonamento.Sospeso;
				} else {
					stato = StatoAbbonamento.Proposto;
				}
				break;

			case Chiusa:
			case InviatoEC:
				stato=StatoAbbonamento.Sospeso;
				break;

			default:
				break;
			}
        }

    	switch (abbonamento.getStatoIncasso()) {
			case Si:
			case SiConDebito:
				stato = StatoAbbonamento.Valido;
				break;
			default:
				break;
        }
    	
    	return stato;

    }

    public static StatoRivista getStatoRivista(Abbonamento abbonamento, RivistaAbbonamento rivista) {

    	if (Smd.isOmaggio(rivista)) {
			return StatoRivista.Attiva;
		}

        StatoRivista stato = StatoRivista.Sospesa;
    	switch (abbonamento.getStatoIncasso()) {
			case Si:
			case SiConDebito:
				stato = StatoRivista.Attiva;
				break;
			default:
				break;
        }

    	return stato;

    }

    public static boolean isOmaggio(RivistaAbbonamento rivistaAbbonamento) {
    	boolean isOmaggio=false;
    	switch (rivistaAbbonamento.getTipoAbbonamentoRivista()) {
	    	case Duplicato:
			case OmaggioCuriaDiocesiana:
			case OmaggioCuriaGeneralizia:
			case OmaggioDirettoreAdp:
			case OmaggioEditore:
			case OmaggioGesuiti:
    			isOmaggio=true;
				break;
			default:
				break;
    	}
    	return isOmaggio;
    }
    public static boolean isAbbonamentoAnnuale(RivistaAbbonamento rivistaAbbonamento) {
        if (rivistaAbbonamento.getAnnoInizio() != rivistaAbbonamento.getAnnoFine()) {
            return false;
        }
        if (rivistaAbbonamento.getMeseInizio() != Mese.GENNAIO) {
            return false;
        }
		return rivistaAbbonamento.getMeseFine() == Mese.DICEMBRE;
	}

    
    public static final BigDecimal contrassegno=new BigDecimal("4.50");

    public static Map<Integer, SpedizioneWithItems> getSpedizioneMap(List<SpedizioneWithItems> spedizioni) {
	    final Map<Integer,SpedizioneWithItems> spedMap = new HashMap<>();
	    for (SpedizioneWithItems spedizione:spedizioni) {
	        spedMap.put(getHashCode(spedizione.getSpedizione(), spedizione.getSpedizioneItems().iterator().next().getPubblicazione()), spedizione);
	    }
	    return spedMap;        
	}

	public static RivistaAbbonamento genera(Abbonamento abb, Storico storico) {
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

    public static boolean checkEquals(RivistaAbbonamento cloned, RivistaAbbonamento persisted) throws UnsupportedOperationException {
    	if (cloned == null || persisted == null ) {
    		throw new UnsupportedOperationException("non possono essere null");
    	}
    	if (cloned.getId() == null && persisted.getId() != null ) {
    		throw new UnsupportedOperationException("non possono essere incongruenti");
    	}
    	if (cloned.getId() != null && persisted.getId() == null ) {
    		throw new UnsupportedOperationException("non possono essere incongruenti");
    	}
    	if (cloned.getId() != null && persisted.getId() != null ) {
    		return (cloned.getId().longValue() == persisted.getId().longValue());
    	}
    	return cloned.equals(persisted);
    }
    
    public static RivistaAbbonamentoAggiorna aggiorna (
    		Abbonamento abbonamento,
            List<SpedizioneWithItems> spedizioni,
            List<SpesaSpedizione> spese,    		
            RivistaAbbonamento original,
            int numero,
            TipoAbbonamentoRivista tipo
		)    throws UnsupportedOperationException {
        if (original == null ) {
            log.error("aggiorna: failed {} : Aggiorna non consentita per Riviste null",abbonamento);
            throw new UnsupportedOperationException("Aggiorna non consentito per Riviste null");
        }
        if (tipo == null ) {
            log.error("aggiorna: failed {} : Aggiorna non consentita per Tipo null",abbonamento);
            throw new UnsupportedOperationException("Aggiorna non consentito per Tipo null");
        }
        if (numero <= 0 ) {
            log.error("aggiorna: failed {} : Aggiorna non consentita per Numero minore di zero",abbonamento);
            throw new UnsupportedOperationException("Aggiorna non consentito per Numero minore di zero");
        }

        RivistaAbbonamentoAggiorna output = new RivistaAbbonamentoAggiorna();
        

    	abbonamento.setImporto(abbonamento.getImporto().subtract(original.getImporto()));
        log.info("aggiorna: sottratto importo rivista {} da abbonamento {}", original.getImporto(),abbonamento);

        if (numero == original.getNumero()) {
            log.info("aggiorna: only type are different: originario: {}", original);        	
           	original.setTipoAbbonamentoRivista(tipo);
           	calcoloImporto(original);
        	abbonamento.setImporto(abbonamento.getImporto().add(original.getImporto()));
            output.setAbbonamentoToSave(abbonamento);
            output.getRivisteToSave().add(original);
            log.info("aggiorna: only type are different: aggiornato: {} {}",abbonamento, original);        	
        	return output;
        }

    	List<SpedizioneWithItems> spedinviate = new ArrayList<>();
    	List<SpedizioneItem> annullate = new ArrayList<>();
    	List<SpedizioneItem> usabili = new ArrayList<>();
    	List<SpedizioneItem> inviate = new ArrayList<>();
    	Mese meseInizioInv=null;
       	Anno annoInizioInv=null;
        Mese meseFineInv=null;
       	Anno annoFineInv=null;
        Mese meseUltimaSped=null;
       	Anno annoUltimaSped=null;
    	for (SpedizioneWithItems spedwith: spedizioni) {
			for (SpedizioneItem item : spedwith.getSpedizioneItems()) {
				if (checkEquals(item.getRivistaAbbonamento(),original)) {
					switch (item.getStatoSpedizione()) {
					case INVIATA:
    	    			spedinviate.add(spedwith);
    	    			inviate.add(item);
    	    			
    					if (meseInizioInv==null) {
    						meseInizioInv=item.getMesePubblicazione();
    						annoInizioInv=item.getAnnoPubblicazione();
    					} else if (annoInizioInv.getAnno() > item.getAnnoPubblicazione().getAnno()) {
    						meseInizioInv=item.getMesePubblicazione();
    						annoInizioInv=item.getAnnoPubblicazione();    						
    					} else if (annoInizioInv.getAnno() == item.getAnnoPubblicazione().getAnno() &&
    						meseInizioInv.getPosizione() > item.getMesePubblicazione().getPosizione()) {
    						meseInizioInv=item.getMesePubblicazione();
    					}
    					if (meseFineInv==null) {
    						meseFineInv=item.getMesePubblicazione();
    						annoFineInv=item.getAnnoPubblicazione();
    					} else if (annoFineInv.getAnno() < item.getAnnoPubblicazione().getAnno()) {
    						meseFineInv=item.getMesePubblicazione();
    						annoFineInv=item.getAnnoPubblicazione();    						
    					} else if (annoFineInv.getAnno() == item.getAnnoPubblicazione().getAnno() &&
    							meseFineInv.getPosizione() < item.getMesePubblicazione().getPosizione()) {
    						meseFineInv=item.getMesePubblicazione();
    					}
       					if (meseUltimaSped==null) {
    						meseUltimaSped=spedwith.getSpedizione().getMeseSpedizione();
    						annoUltimaSped=spedwith.getSpedizione().getAnnoSpedizione();
    					} else if (annoUltimaSped.getAnno() < spedwith.getSpedizione().getAnnoSpedizione().getAnno()) {
    						meseUltimaSped=spedwith.getSpedizione().getMeseSpedizione();
    						annoUltimaSped=spedwith.getSpedizione().getAnnoSpedizione();
    					} else if (annoUltimaSped.getAnno() == spedwith.getSpedizione().getAnnoSpedizione().getAnno() &&
    						meseUltimaSped.getPosizione() < spedwith.getSpedizione().getMeseSpedizione().getPosizione()) {
    						meseUltimaSped=spedwith.getSpedizione().getMeseSpedizione();
    					}
						break;
						
						case PROGRAMMATA:
						case SOSPESA:
							usabili.add(item);
							break;

						case ANNULLATA:
							annullate.add(item);
							break;
						
					default:
						break;
					}
					
    			}
    		}
    	}
    	
    	log.info("aggiorna: ultima rivista {} {}",meseFineInv,annoFineInv);
    	log.info("aggiorna: spedizione ultima {} {} inviate->{}",meseUltimaSped,annoUltimaSped, spedinviate.size());
        
    	if (spedinviate.size() == 0 && annullate.size() == 0) {
        	int numeroTotaleRiviste = 0;
        	for (SpedizioneWithItems s: spedizioni) {
        		for (SpedizioneItem item: s.getSpedizioneItems()) {
    				if (checkEquals(item.getRivistaAbbonamento(),original)) {
						item.setNumero(numero);
                		numeroTotaleRiviste+=numero;
        			}
        		}
        	}
        	original.setTipoAbbonamentoRivista(tipo);
        	original.setNumero(numero);
        	original.setNumeroTotaleRiviste(numeroTotaleRiviste);
        	
        	calcoloImporto(original);
        	abbonamento.setImporto(abbonamento.getImporto().add(original.getImporto()));
        	calcolaPesoESpesePostali(abbonamento, spedizioni, spese);

        	output.setAbbonamentoToSave(abbonamento);
            output.setSpedizioniToSave(spedizioni);
            output.getRivisteToSave().add(original);
            log.info("aggiorna: nessuna spedizione inviata o annullata: {}, {}",abbonamento,original);
            return output;
        }

    	if (numero > original.getNumero()) {
        	Mese meseSped = Mese.getMeseSuccessivo(meseUltimaSped);
        	Anno annoSped=annoUltimaSped;
        	if (meseSped==Mese.GENNAIO) {
        		annoSped=Anno.getAnnoSuccessivo(annoUltimaSped);
        	}
        	int numeroTotaleRiviste = 0;
        	original.setTipoAbbonamentoRivista(tipo);
        	original.setNumero(numero);
			for (SpedizioneItem item: usabili) {
				item.setNumero(numero);
				log.info("aggiorna: usabile {}", item);
			}
        	for (SpedizioneWithItems nuovaspedwithitem: genera(abbonamento,original, new ArrayList<>(), spese,meseSped,annoSped)) {
        		final List<SpedizioneItem> itemstoDelete = new ArrayList<>();
        		for (SpedizioneItem nuovoItem: nuovaspedwithitem.getSpedizioneItems()) {
    				log.info("aggiorna: nuovo item {}", nuovoItem);
        			numeroTotaleRiviste+=numero;
        			for (SpedizioneItem item: usabili) {
        				if (item.getMesePubblicazione() == nuovoItem.getMesePubblicazione() && item.getAnnoPubblicazione() == nuovoItem.getAnnoPubblicazione()) {
        					nuovoItem.setNumero(nuovoItem.getNumero()-item.getNumero());
            				log.info("aggiorna: match usabile ,nuovo item {}", nuovoItem);
        				}
        			}
        			for (SpedizioneItem item: inviate) {
        				if (item.getMesePubblicazione() == nuovoItem.getMesePubblicazione() && item.getAnnoPubblicazione() == nuovoItem.getAnnoPubblicazione()) {
        					nuovoItem.setNumero(nuovoItem.getNumero()-item.getNumero());
            				log.info("aggiorna: match inviate, nuovo item {}", nuovoItem);
        				}
        			}
        			if (nuovoItem.getNumero() == 0) {
        				itemstoDelete.add(nuovoItem);
        			}
        		}
        		for (SpedizioneItem itemtodelete: itemstoDelete) {
    				log.info("aggiorna: delete, nuovo item {}", itemtodelete);
        			nuovaspedwithitem.deleteSpedizioneItem(itemtodelete);
    				log.info("aggiorna: deleted, nuovo item {}", itemtodelete);
        		}
        		if (!nuovaspedwithitem.getSpedizioneItems().isEmpty()) {
        			spedizioni.add(nuovaspedwithitem);
        		}
        		
        	}

        	original.setNumeroTotaleRiviste(numeroTotaleRiviste);
        	calcolaPesoESpesePostali(abbonamento, spedizioni, spese);
        	
            output.setAbbonamentoToSave(abbonamento);
            output.setSpedizioniToSave(spedizioni);
            output.getRivisteToSave().add(original);
            log.info("aggiorna: spedizioni inviata ed incremento {} {} ",abbonamento,original);
            return output;
    	}
    	log.info("aggiorna: {}, spedizioni inviate e decremento: prima {} {}, ultima {} {}", original,meseInizioInv, annoInizioInv,meseFineInv,annoFineInv);
    	original.setTipoAbbonamentoRivista(tipo);
    	original.setMeseInizio(meseInizioInv);
    	original.setMeseFine(meseFineInv);
    	original.setAnnoInizio(annoInizioInv);
    	original.setAnnoFine(annoFineInv);
    	original.setNumero(original.getNumero()-numero);

    	RivistaAbbonamento r = original.clone();
    	r.setNumero(numero);
    	r.setTipoAbbonamentoRivista(tipo);

    	int itemsoriginal=0;
    	int itemsupdated=0;
        for (SpedizioneWithItems spedwith: spedinviate) {
        	for (SpedizioneItem originitem: spedwith.getSpedizioneItems()) {
				if (checkEquals(originitem.getRivistaAbbonamento(),original)) {
        			originitem.setNumero(original.getNumero());
        			itemsoriginal++;
        			SpedizioneItem item = new SpedizioneItem();
                    item.setRivistaAbbonamento(r);
                    item.setAnnoPubblicazione(originitem.getAnnoPubblicazione());
                    item.setMesePubblicazione(originitem.getMesePubblicazione());
                    item.setNumero(r.getNumero());
                    item.setPubblicazione(r.getPubblicazione());
                    item.setPosticipata(originitem.isPosticipata());
                    item.setSpedizione(spedwith.getSpedizione());
                    spedwith.addSpedizioneItem(item);
                    itemsupdated++;
                }
        	}
        }
        original.setNumeroTotaleRiviste(original.getNumero()*itemsoriginal);
    	calcoloImporto(original);
    	abbonamento.setImporto(abbonamento.getImporto().add(original.getImporto()));

        final List<SpedizioneItem> rimItems = new ArrayList<>();
        for (SpedizioneWithItems sw:spedizioni) {
        	for (SpedizioneItem originitem: new ArrayList<>(sw.getSpedizioneItems())) {
            	if (originitem.getStatoSpedizione() != StatoSpedizione.INVIATA) {
    				if (checkEquals(originitem.getRivistaAbbonamento(),original)) {
	    				rimItems.add(originitem);
	    				sw.deleteSpedizioneItem(originitem);
	    				SpedizioneItem item = new SpedizioneItem();
	                    item.setRivistaAbbonamento(r);
	                    item.setAnnoPubblicazione(originitem.getAnnoPubblicazione());
	                    item.setMesePubblicazione(originitem.getMesePubblicazione());
	                    item.setNumero(r.getNumero());
	                    item.setPubblicazione(r.getPubblicazione());
	                    item.setPosticipata(originitem.isPosticipata());
	                    item.setSpedizione(sw.getSpedizione());
	                    sw.addSpedizioneItem(item);
	                    itemsupdated++;
	            		}
            	}        		
        	}        	
        }
    	r.setNumeroTotaleRiviste(itemsupdated*numero);
    	calcoloImporto(r);
    	abbonamento.setImporto(abbonamento.getImporto().add(r.getImporto()));
        calcolaPesoESpesePostali(abbonamento, spedizioni, spese);
        output.setAbbonamentoToSave(abbonamento);
        output.setSpedizioniToSave(spedizioni);
        output.getRivisteToSave().add(original);
        output.getRivisteToSave().add(r);
        output.setItemsToDelete(rimItems);
        log.info("aggiorna: spedizioni inviata e decremento {} {} {} ",abbonamento,r,original);
        return output;
    }

    public static RivistaAbbonamentoAggiorna rimuovi(
            Abbonamento abb, 
            RivistaAbbonamento original, 
            List<SpedizioneWithItems> spedizioni,
            List<SpesaSpedizione> spese) {
        abb.setImporto(abb.getImporto().subtract(original.getImporto()));
        log.info("rimuovi: rimosso importo rivista: {}", abb);
        RivistaAbbonamentoAggiorna aggiorna = new RivistaAbbonamentoAggiorna();
    	Mese meseInizioInv=null;
       	Anno annoInizioInv=null;
        Mese meseFineInv=null;
       	Anno annoFineInv=null;
    	int rivisteinviate=0;
        final List<SpedizioneItem> rimItems = new ArrayList<>();
    	for (SpedizioneWithItems spedwith: spedizioni) {
			for (SpedizioneItem item : spedwith.getSpedizioneItems()) {
	    		if (item.getStatoSpedizione() == StatoSpedizione.INVIATA) {
    				if (checkEquals(item.getRivistaAbbonamento(),original)) {
    					rivisteinviate+=item.getNumero();
    					if (meseInizioInv==null) {
    						meseInizioInv=item.getMesePubblicazione();
    						annoInizioInv=item.getAnnoPubblicazione();
    					} else if (annoInizioInv.getAnno() > item.getAnnoPubblicazione().getAnno()) {
    						meseInizioInv=item.getMesePubblicazione();
    						annoInizioInv=item.getAnnoPubblicazione();    						
    					} else if (annoInizioInv.getAnno() == item.getAnnoPubblicazione().getAnno() &&
    							meseInizioInv.getPosizione() > item.getMesePubblicazione().getPosizione()) {
    						meseInizioInv=item.getMesePubblicazione();
    					}
    					if (meseFineInv==null) {
    						meseFineInv=item.getMesePubblicazione();
    						annoFineInv=item.getAnnoPubblicazione();
    					} else if (annoFineInv.getAnno() < item.getAnnoPubblicazione().getAnno()) {
    						meseFineInv=item.getMesePubblicazione();
    						annoFineInv=item.getAnnoPubblicazione();    						
    					} else if (annoFineInv.getAnno() == item.getAnnoPubblicazione().getAnno() &&
    							meseFineInv.getPosizione() < item.getMesePubblicazione().getPosizione()) {
    						meseFineInv=item.getMesePubblicazione();
    					}
    				}
    			}
    		}
    	}
    	log.info("rimuovi: {} riviste inviate", rivisteinviate);
        if (rivisteinviate == 0) {
        	for (SpedizioneWithItems s: spedizioni) {
        		for (SpedizioneItem item: new ArrayList<>(s.getSpedizioneItems())) {
    				if (checkEquals(item.getRivistaAbbonamento(),original)) {
        				rimItems.add(item);
        				s.deleteSpedizioneItem(item);        			
    				}
        		}
        	}
        	calcolaPesoESpesePostali(abb, spedizioni, spese);
            original.setNumero(0);
            original.setNumeroTotaleRiviste(0);
            original.setImporto(BigDecimal.ZERO);
            aggiorna.setItemsToDelete(rimItems);
            aggiorna.setAbbonamentoToSave(abb);
            aggiorna.setSpedizioniToSave(spedizioni);
            aggiorna.getRivisteToDelete().add(original);
            log.info("rimuovi: {}",abb);
            log.info("rimuovi: {}",original);
            return aggiorna;
        }
        
    	log.info("rimuovi: riviste inviate:{} - inizio {} {}, fine {} {}",original.getPubblicazione().getNome(), meseInizioInv, annoInizioInv,meseFineInv,annoFineInv);
    	original.setMeseInizio(meseInizioInv);
    	original.setMeseFine(meseFineInv);
    	original.setAnnoInizio(annoInizioInv);
    	original.setAnnoFine(annoFineInv);
        original.setNumeroTotaleRiviste(rivisteinviate);

        for (SpedizioneWithItems sw:spedizioni) {
        	for (SpedizioneItem originitem: new ArrayList<>(sw.getSpedizioneItems())) {
            	if (originitem.getStatoSpedizione() != StatoSpedizione.INVIATA) {
    				if (checkEquals(originitem.getRivistaAbbonamento(),original)) {
            			rimItems.add(originitem);
            			sw.deleteSpedizioneItem(originitem);
            		}
            	}        		
        	}        	
        }
        calcolaPesoESpesePostali(abb, spedizioni, spese);
       	calcoloImporto(original);
    	abb.setImporto(abb.getImporto().add(original.getImporto()));
        aggiorna.setAbbonamentoToSave(abb);
        aggiorna.setSpedizioniToSave(spedizioni);
        aggiorna.getRivisteToSave().add(original);
        aggiorna.setItemsToDelete(rimItems);
        log.info("rimuovi:  from {} ec -> {}",abb,original);

    	return aggiorna;

    }

    public static List<SpedizioneItem> generaSpedizioneItems(RivistaAbbonamento ec) throws UnsupportedOperationException {
        log.info("generaSpedizioneItems: {}", ec);
        List<SpedizioneItem> items = new ArrayList<>();
        Map<Anno, EnumSet<Mese>> mappaPubblicazioni = RivistaAbbonamento.getAnnoMeseMap(ec);
        for (Anno anno: mappaPubblicazioni.keySet()) {
            mappaPubblicazioni.get(anno).forEach(mese -> {
                SpedizioneItem item = new SpedizioneItem();
                item.setRivistaAbbonamento(ec);
                item.setAnnoPubblicazione(anno);
                item.setMesePubblicazione(mese);
                item.setNumero(ec.getNumero());
                item.setPubblicazione(ec.getPubblicazione());
                items.add(item);
                log.info("generaSpedizioneItems: {} ", item);
            });
        }
      if (items.isEmpty()) {
          throw new UnsupportedOperationException("Nessuna spedizione per rivista in Abbonamento");
      }
      log.info("generaSpedizioneItems: generati {} items", items.size());
      return items; 
    }

    private static int getHashCode(Spedizione sped, Pubblicazione p) {
    	if (sped.getInvioSpedizione() == InvioSpedizione.Spedizioniere) {
    		return sped.hashCode()+p.hashCode();
    	}
    	return sped.hashCode();
    }

    public static void aggiungiItemSpedizione(Abbonamento abb, RivistaAbbonamento ec,Map<Integer,SpedizioneWithItems> spedMap, SpedizioneItem item, Mese mesePost, Anno annoPost) {
        Anagrafica destinatario = ec.getDestinatario();
        InvioSpedizione isped = ec.getInvioSpedizione();
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
		assert spedMese != null;
		log.info("aggiungiItemSpedizione: teorico: {}, {}, {}",spedMese.getNomeBreve(),spedAnno.getAnnoAsString(),isped);

        if (spedAnno.getAnno() < annoPost.getAnno()
                || (spedAnno == annoPost
                        && spedMese.getPosizione() < mesePost.getPosizione())) {
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
        log.info("aggiungiItemSpedizione: aggiunto {}, a spedizione {}, size {}",item,spedizione,spedMap.size());
    }
    
    public static List<SpedizioneWithItems> genera(Abbonamento abb,
            RivistaAbbonamento ec, 
            List<SpedizioneWithItems> spedizioni, 
            List<SpesaSpedizione> spese) throws UnsupportedOperationException {
    	return genera(abb,
                 ec, 
                 spedizioni, 
                 spese, Mese.getMeseCorrente(), Anno.getAnnoCorrente());
    }
    
    
    public static List<SpedizioneWithItems> genera(Abbonamento abb,
            RivistaAbbonamento ec, 
            List<SpedizioneWithItems> spedizioni, 
            List<SpesaSpedizione> spese, Mese mesePost, Anno annoPost) throws UnsupportedOperationException {

    	
        ec.setAbbonamento(abb);
        List<SpedizioneItem> items = generaSpedizioneItems(ec);
        ec.setNumeroTotaleRiviste(ec.getNumero()*items.size());
        calcoloImporto(ec);
        abb.setImporto(abb.getImporto().add(ec.getImporto()));
        Map<Integer, SpedizioneWithItems> spedMap = getSpedizioneMap(spedizioni);

        if (ec.getTipoAbbonamentoRivista() != TipoAbbonamentoRivista.Web) {
	        for (SpedizioneItem item : items) {
	            aggiungiItemSpedizione(abb, ec, spedMap, item,mesePost,annoPost);
	        }
    	}
        calcolaPesoESpesePostali(abb, spedMap.values(), spese);
        return new ArrayList<>(spedMap.values());
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
            sped.getSpedizione().setSpesePostali(calcolaSpesePostali(sped.getSpedizione().getInvioSpedizione(),spesa));
            switch (sped.getSpedizione().getDestinatario().getAreaSpedizione()) {
            	case Italia:
                	abb.setSpese(abb.getSpese().add(sped.getSpedizione().getSpesePostali()));
                	break;

				case EuropaBacinoMediterraneo:
				case AmericaAfricaAsia:
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
    
    public static BigDecimal calcolaSpesePostali(InvioSpedizione sped, SpesaSpedizione spesa) throws UnsupportedOperationException {
    	BigDecimal spesePostali = BigDecimal.ZERO;
        switch (sped) {
        case AdpSede:
            spesePostali = spesa.getSpese();
        	break;
        case AdpSedeCorriere24hh:
            spesePostali = spesa.getCor24h();
        	break;
        case AdpSedeCorriere3gg:
            spesePostali = spesa.getCor3gg();
        	break;
        default:
        	break;        	
        }
        return spesePostali;
    }

    public static List<Abbonamento> genera(final Campagna campagna, List<Anagrafica> anagrafiche, List<Storico> storici) {
        final List<Abbonamento> abbonamenti = new ArrayList<>();
        anagrafiche.forEach(a -> abbonamenti.addAll(genera(campagna, a, storici)));
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
        return abbonamento;
    }

    public static void calcoloImporto(RivistaAbbonamento ec) throws UnsupportedOperationException {
        BigDecimal importo=BigDecimal.ZERO;
        switch (ec.getTipoAbbonamentoRivista()) {
        case Ordinario:
            importo = ec.getPubblicazione().getAbbonamento().multiply(new BigDecimal(ec.getNumero()));
            if (!isAbbonamentoAnnuale(ec) || ec.getNumero() == 0) {
                importo = ec.getPubblicazione().getCostoUnitario().multiply(new BigDecimal(ec.getNumeroTotaleRiviste()));
            }
            break;

        case Web:
            if (!isAbbonamentoAnnuale(ec)) {
                    throw new UnsupportedOperationException("Valori mesi inizio e fine non ammissibili per " + TipoAbbonamentoRivista.Web);
            }
            importo = ec.getPubblicazione().getAbbonamentoWeb().multiply(new BigDecimal(ec.getNumero()));
            break;

        case Scontato:
            if (!isAbbonamentoAnnuale(ec)) {
                throw new UnsupportedOperationException("Valori mesi inizio e fine non ammissibili per " + TipoAbbonamentoRivista.Web);
            }
            importo = ec.getPubblicazione().getAbbonamentoConSconto().multiply(new BigDecimal(ec.getNumero()));
            break;

        case Sostenitore:
            if (!isAbbonamentoAnnuale(ec)) {
                throw new UnsupportedOperationException("Valori mesi inizio e fine non ammissibili per " + TipoAbbonamentoRivista.Web);
            }
            importo = ec.getPubblicazione().getAbbonamentoSostenitore().multiply(new BigDecimal(ec.getNumero()));
            break;
                
        default:
            break;

        }          
        ec.setImporto(importo);
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
                   sped.getSpedizione().getAnnoSpedizione() == anno 
                && sped.getSpedizione().getMeseSpedizione() == mese) 
            .forEach( sped -> 
                  sped
                  .getSpedizioneItems()
                  .stream()
                  .filter(item -> 
                  	item.getStatoSpedizione() == StatoSpedizione.PROGRAMMATA 
                  	&& !item.isPosticipata() 
                  	&& item.getPubblicazione().hashCode() == pubblicazione.hashCode())
                  .forEach(item -> 
                  {
					  if (sped.getSpedizione().getInvioSpedizione() == InvioSpedizione.Spedizioniere) {
						  op.setStimatoSped(op.getStimatoSped() + item.getNumero());
					  } else {
						  op.setStimatoSede(op.getStimatoSede() + item.getNumero());
					  }
                  })
              );                        
        return op;        
    }
 
    public static BigDecimal incassa(DistintaVersamento incasso, Versamento versamento, DocumentiTrasportoCumulati ddtAnno, BigDecimal importo) throws UnsupportedOperationException {
        if (incasso == null ) {
            log.error("incassa: Incasso null");
            throw new UnsupportedOperationException("incassa: Incasso null");
        }
        if (versamento == null ) {
            log.error("incassa: Versamento null");
            throw new UnsupportedOperationException("incassa: Versamento null");
        }
        if (ddtAnno == null ) {
            log.error("incassa: Ddt Anno null");
            throw new UnsupportedOperationException("incassa: Ddt Anno null");
        }
 
        BigDecimal incassato = importo;
        if (importo.compareTo(versamento.getResiduo()) > 0) {
        	incassato = BigDecimal.valueOf(versamento.getResiduo().doubleValue());
        }        
        versamento.setIncassato(versamento.getIncassato().add(incassato));
        ddtAnno.setImporto(ddtAnno.getImporto().add(incassato));
        incasso.setIncassato(incasso.getIncassato().add(incassato));
        return incassato;
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
        	incassato = BigDecimal.valueOf(versamento.getResiduo().doubleValue());
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

    public static void storna(DistintaVersamento incasso, Versamento versamento,DocumentiTrasportoCumulati ddts, BigDecimal importo) throws UnsupportedOperationException {
        if (incasso == null ) {
            log.error("storna: Incasso null");
            throw new UnsupportedOperationException("storna: Incasso null");
        }
        if (versamento == null ) {
            log.error("storna: Versamento null");
            throw new UnsupportedOperationException("storna: Versamento null");
        }
        if (ddts == null ) {
            log.error("storna: DDT null");
            throw new UnsupportedOperationException("storna: Abbonamento null");
        }
        if (versamento.getIncassato().compareTo(importo) < 0) {
            log.error("storna: incassato Versamento minore importo da stornare");
            throw new UnsupportedOperationException("storna: importo Versamento minore importo da stornare");
        }
        if (ddts.getImporto().compareTo(importo) < 0) {
            log.error("storna: incassato DDT minore importo da stornare");
            throw new UnsupportedOperationException("storna: totale DDT minore importo da stornare");
        }
        versamento.setIncassato(versamento.getIncassato().subtract(importo));
        ddts.setImporto(ddts.getImporto().subtract(importo));
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
 
        BigDecimal incassato;
        if ((versamento.getResiduo()).compareTo(abbonamento.getResiduo()) < 0) {
        	incassato = BigDecimal.valueOf(versamento.getResiduo().doubleValue());
        } else {
        	incassato = BigDecimal.valueOf(abbonamento.getResiduo().doubleValue());
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

}
