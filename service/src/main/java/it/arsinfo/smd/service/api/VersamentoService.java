package it.arsinfo.smd.service.api;

import java.time.LocalDate;
import java.util.List;

import it.arsinfo.smd.entity.Anno;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.DocumentoTrasporto;
import it.arsinfo.smd.entity.Offerta;
import it.arsinfo.smd.entity.OperazioneIncasso;
import it.arsinfo.smd.entity.UserInfo;
import it.arsinfo.smd.entity.Versamento;

public interface VersamentoService extends SmdServiceBase<Versamento> {

	List<Versamento> searchBy(String importo, LocalDate dataContabile, LocalDate dataPagamento,
			String codeLine);	
    void associaCommittente(Anagrafica committente, Versamento versamento);
    void rimuoviCommittente(Versamento versamento);
    List<OperazioneIncasso> getAssociati(Versamento versamento);
    List<Abbonamento> getAssociabili(Versamento versamento);
	void storna(OperazioneIncasso operazioneIncasso, UserInfo loggedInUser, String description) throws Exception;
	void incassa(Abbonamento abbonamento, Versamento selected, UserInfo loggedInUser, String description) throws Exception;

	void storna(Offerta offerta, UserInfo loggedInUser) throws Exception;
	void incassaOfferta(String importo,Anno anno, Versamento versamento, UserInfo loggedInUser, Anagrafica committente) throws Exception;
	
	void storna(DocumentoTrasporto offerta, UserInfo loggedInUser) throws Exception;
	void incassaDdt(String ddt, String importo,Anno anno, Versamento versamento, UserInfo loggedInUser, Anagrafica committente) throws Exception;

	Anagrafica findCommittente(Versamento selected);
	List<Offerta> getOfferte(Versamento selected);
	List<DocumentoTrasporto> getDocumentiTrasporto(Versamento selected);
	List<Versamento> searchBy(Anagrafica tValue, Anno sValue) throws Exception;
	List<Versamento> findBy(Anagrafica tValue) throws Exception;

}
