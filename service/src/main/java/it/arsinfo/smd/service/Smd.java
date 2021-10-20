package it.arsinfo.smd.service;

import it.arsinfo.smd.dto.RivistaAbbonamentoAggiorna;
import it.arsinfo.smd.dto.SpedizioneWithItems;
import it.arsinfo.smd.entity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Smd {

    private static final Logger log = LoggerFactory.getLogger(Smd.class);

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
            log.error("aggiorna: failed {} : Aggiorna non consentita per Numero <= 0",abbonamento);
            throw new UnsupportedOperationException("Aggiorna non consentito per Numero minore di zero");
        }

        RivistaAbbonamentoAggiorna output = new RivistaAbbonamentoAggiorna();
        

    	abbonamento.setImporto(abbonamento.getImporto().subtract(original.getImporto()));
        log.info("aggiorna: sottratto importo rivista {} da abbonamento {}", original.getImporto(),abbonamento);

        if (numero == original.getNumero()) {
            log.info("aggiorna: only type are different: originario: {}", original);        	
           	original.setTipoAbbonamentoRivista(tipo);
           	original.calcolaImporto();
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
				if ( original.getId().equals(item.getRivistaAbbonamento().getId())) {
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
					if ( original.getId().equals(item.getRivistaAbbonamento().getId())) {
						item.setNumero(numero);
                		numeroTotaleRiviste+=numero;
					}
        		}
        	}
        	original.setTipoAbbonamentoRivista(tipo);
        	original.setNumero(numero);
        	original.setNumeroTotaleRiviste(numeroTotaleRiviste);
        	
        	original.calcolaImporto();
        	abbonamento.setImporto(abbonamento.getImporto().add(original.getImporto()));
        	Abbonamento.calcolaPesoESpesePostali(abbonamento, spedizioni, spese);

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
        	for (SpedizioneWithItems nuovaspedwithitem: Abbonamento.genera(abbonamento,original, new ArrayList<>(), spese,meseSped,annoSped)) {
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
        	Abbonamento.calcolaPesoESpesePostali(abbonamento, spedizioni, spese);
        	
            output.setAbbonamentoToSave(abbonamento);
            output.setSpedizioniToSave(spedizioni);
            output.getRivisteToSave().add(original);
            log.info("aggiorna: spedizioni inviata ed incremento {} {} ",abbonamento,original);
            return output;
    	}
    	log.info("aggiorna: {}, spedizioni inviate e decremento: prima {} {}, ultima {} {}", original,meseInizioInv, annoInizioInv,meseFineInv,annoFineInv);
		original.setTipoAbbonamentoRivista(tipo);

		RivistaAbbonamento r = original.clone();
		r.setNumero(numero);
		r.setTipoAbbonamentoRivista(tipo);
		log.info("aggiorna: new {}", r);

    	original.setMeseInizio(meseInizioInv);
    	original.setMeseFine(meseFineInv);
    	original.setAnnoInizio(annoInizioInv);
    	original.setAnnoFine(annoFineInv);
    	original.setNumero(original.getNumero()-numero);
		log.info("aggiorna: updated {}", original);


    	int itemsoriginal=0;
    	int itemsupdated=0;
        for (SpedizioneWithItems spedwith: spedinviate) {
        	List<SpedizioneItem> listitem = new ArrayList<>(spedwith.getSpedizioneItems());
        	for (SpedizioneItem originitem: listitem) {
				if ( original.getId().equals(originitem.getRivistaAbbonamento().getId())) {
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
    	original.calcolaImporto();
    	abbonamento.setImporto(abbonamento.getImporto().add(original.getImporto()));

        final List<SpedizioneItem> rimItems = new ArrayList<>();
        for (SpedizioneWithItems sw:spedizioni) {
        	for (SpedizioneItem originitem: new ArrayList<>(sw.getSpedizioneItems())) {
            	if (originitem.getStatoSpedizione() != StatoSpedizione.INVIATA) {
					if ( original.getId().equals(originitem.getRivistaAbbonamento().getId())) {
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
    	r.calcolaImporto();
    	abbonamento.setImporto(abbonamento.getImporto().add(r.getImporto()));
        Abbonamento.calcolaPesoESpesePostali(abbonamento, spedizioni, spese);
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
					if ( original.getId().equals(item.getRivistaAbbonamento().getId())) {
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
					if ( original.getId().equals(item.getRivistaAbbonamento().getId())) {
        				rimItems.add(item);
        				s.deleteSpedizioneItem(item);        			
    				}
        		}
        	}
        	Abbonamento.calcolaPesoESpesePostali(abb, spedizioni, spese);
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
					if ( original.getId().equals(originitem.getRivistaAbbonamento().getId())) {
            			rimItems.add(originitem);
            			sw.deleteSpedizioneItem(originitem);
            		}
            	}        		
        	}        	
        }
        Abbonamento.calcolaPesoESpesePostali(abb, spedizioni, spese);
       	original.calcolaImporto();
    	abb.setImporto(abb.getImporto().add(original.getImporto()));
        aggiorna.setAbbonamentoToSave(abb);
        aggiorna.setSpedizioniToSave(spedizioni);
        aggiorna.getRivisteToSave().add(original);
        aggiorna.setItemsToDelete(rimItems);
        log.info("rimuovi:  from {} ec -> {}",abb,original);

    	return aggiorna;

    }
}
