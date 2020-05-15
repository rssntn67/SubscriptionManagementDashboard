package it.arsinfo.smd.dao;

import java.time.LocalDate;
import java.util.List;

import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.OperazioneIncasso;
import it.arsinfo.smd.entity.UserInfo;
import it.arsinfo.smd.entity.Versamento;

public interface VersamentoServiceDao extends SmdServiceDao<Versamento> {

	List<Versamento> searchBy(String importo, LocalDate dataContabile, LocalDate dataPagamento,
			String codeLine);	
    void associaCommittente(Anagrafica committente, Versamento versamento);
    void rimuoviCommittente(Versamento versamento);
    List<OperazioneIncasso> getAssociati(Versamento versamento);
    List<Abbonamento> getAssociabili(Versamento versamento);
	void storna(OperazioneIncasso operazioneIncasso, UserInfo loggedInUser, String string) throws Exception;
	void incassa(Abbonamento abbonamento, Versamento selected, UserInfo loggedInUser, String string) throws Exception;
	Anagrafica findCommittente(Versamento selected);

}
