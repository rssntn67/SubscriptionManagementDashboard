package it.arsinfo.smd.dao;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.boot.actuate.audit.listener.AuditApplicationEvent;

import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.InvioSpedizione;
import it.arsinfo.smd.data.Mese;
import it.arsinfo.smd.data.SpedizioneWithItems;
import it.arsinfo.smd.data.StatoSpedizione;
import it.arsinfo.smd.data.TipoAbbonamentoRivista;
import it.arsinfo.smd.dto.AbbonamentoConRiviste;
import it.arsinfo.smd.dto.SpedizioneDto;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.DocumentiTrasportoCumulati;
import it.arsinfo.smd.entity.DocumentoTrasporto;
import it.arsinfo.smd.entity.Offerta;
import it.arsinfo.smd.entity.OfferteCumulate;
import it.arsinfo.smd.entity.OperazioneIncasso;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.entity.RivistaAbbonamento;
import it.arsinfo.smd.entity.Spedizione;
import it.arsinfo.smd.entity.UserInfo;
import it.arsinfo.smd.entity.Versamento;

public interface SmdService {

	void auditlog(AuditApplicationEvent auditApplicationEvent);

	void logout(String user);
	UserInfo login(String user);

    List<AbbonamentoConRiviste> get(List<Abbonamento> abbonamenti);

    void genera(Abbonamento abbonamento) throws Exception;
    void rimuovi(Abbonamento abbonamento) throws Exception;
    void programmaSpedizioni(Abbonamento abbonamento, RivistaAbbonamento rivista);
    void sospendiSpedizioni(Abbonamento abbonamento, RivistaAbbonamento rivista);
    
    void rimuovi(Abbonamento abbonamento, RivistaAbbonamento rivistaAbbonamento) throws Exception;
    void aggiorna(RivistaAbbonamento rivistaAbbonamento, int numero, TipoAbbonamentoRivista tipo) throws Exception;    
    
    void generaStatisticheTipografia(Anno anno, Mese mese, Pubblicazione p); 
    void generaStatisticheTipografia(Anno anno, Pubblicazione p); 
    void inviaSpedizionere(Mese meseSpedizione, Anno annoSpedizione, Pubblicazione p) throws Exception;
    void spedisciAdpSede(Mese meseSpedizione, Anno annoSpedizione, Pubblicazione p, InvioSpedizione invio) throws Exception;

    void inviaDuplicato(Spedizione spedizione, InvioSpedizione invio) throws Exception;

    List<SpedizioneDto> listBy(Pubblicazione pubblicazione,Mese meseSpedizione, Anno annoSpedizione, StatoSpedizione statoSpedizione, InvioSpedizione invio);
    List<SpedizioneWithItems> findByAbbonamento(Abbonamento abb);
        
    void incassa(Abbonamento abbonamento, Versamento versamento, UserInfo user, String description) throws Exception;    
    void storna(OperazioneIncasso operazioneIncasso,UserInfo user, String description) throws Exception;    

    void incassa(BigDecimal importo, OfferteCumulate offerte, Versamento selected, UserInfo loggedInUser, Anagrafica committente) throws Exception;
	void storna(Offerta offerta, UserInfo loggedInUser) throws Exception;
    
	void storna(DocumentoTrasporto ddt, UserInfo loggedInUser);
	void incassa(String ddt, BigDecimal bigDecimal, DocumentiTrasportoCumulati ddtAnno, Versamento selected,
			UserInfo loggedInUser, Anagrafica committente);


}
