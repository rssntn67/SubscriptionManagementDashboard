package it.arsinfo.smd.service.api;

import it.arsinfo.smd.data.*;
import it.arsinfo.smd.dto.AbbonamentoConRiviste;
import it.arsinfo.smd.dto.SpedizioneDto;
import it.arsinfo.smd.entity.*;

import java.math.BigDecimal;
import java.util.List;

public interface SmdService {

    List<AbbonamentoConRiviste> get(List<Abbonamento> abbonamenti);

    void genera(Abbonamento abbonamento) throws Exception;
    void aggiornaStato(Abbonamento abbonamento) throws Exception;
    void rimuovi(Abbonamento abbonamento) throws Exception;
    
    void rimuovi(Abbonamento abbonamento, RivistaAbbonamento rivista) throws Exception;
    
    void programmaSpedizioniSospese(Abbonamento abbonamento, RivistaAbbonamento rivista);
    void sospendiSpedizioniProgrammate(Abbonamento abbonamento, RivistaAbbonamento rivista);
    
    void aggiornaRivistaAbbonamento(RivistaAbbonamento rivista, int numero, TipoAbbonamentoRivista tipo) throws Exception;    
    
    void generaStatisticheTipografia(Anno anno, Mese mese, Pubblicazione p); 
    void inviaSpedizionere(Operazione operazione) throws Exception;
    void inviaAdpSede(Mese meseSpedizione, Anno annoSpedizione, InvioSpedizione invio) throws Exception;
    void inviaSpedizioni(Mese meseSpedizione, Anno annoSpedizione, Pubblicazione p, InvioSpedizione invio) throws Exception;
	void annullaAdpSede(Mese mese, Anno anno, InvioSpedizione invio) throws Exception;

    void inviaDuplicato(Spedizione spedizione, InvioSpedizione invio) throws Exception;

    List<SpedizioneDto> listBy(Pubblicazione pubblicazione,Mese meseSpedizione, Anno annoSpedizione, StatoSpedizione statoSpedizione, InvioSpedizione invio);
    List<SpedizioneDto> listBy(Mese meseSpedizione, Anno annoSpedizione, StatoSpedizione statoSpedizione, InvioSpedizione invio);
    List<SpedizioneWithItems> findByAbbonamento(Abbonamento abb);
        
    void incassa(Abbonamento abbonamento, Versamento versamento, UserInfo user, String description) throws Exception;    
    void storna(OperazioneIncasso operazioneIncasso,UserInfo user, String description) throws Exception;    

    void incassa(BigDecimal importo, OfferteCumulate offerte, Versamento selected, UserInfo loggedInUser, Anagrafica committente) throws Exception;
	void storna(Offerta offerta, UserInfo loggedInUser) throws Exception;
    
	void storna(DocumentoTrasporto ddt, UserInfo loggedInUser);
	void incassa(String ddt, BigDecimal bigDecimal, DocumentiTrasportoCumulati ddtAnno, Versamento selected,
			UserInfo loggedInUser, Anagrafica committente);

	List<RivistaAbbonamento> getNotValid(Abbonamento abbonamento, Campagna campagna);



}
