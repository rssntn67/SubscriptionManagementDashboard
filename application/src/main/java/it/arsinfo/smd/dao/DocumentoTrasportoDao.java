package it.arsinfo.smd.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import it.arsinfo.smd.entity.DocumentiTrasportoCumulati;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.DocumentoTrasporto;
import it.arsinfo.smd.entity.Versamento;

public interface DocumentoTrasportoDao extends JpaRepository<DocumentoTrasporto, Long> {

	List<DocumentoTrasporto> findByDdt(String ddt);
	List<DocumentoTrasporto> findByDocumentiTrasportoCumulati(DocumentiTrasportoCumulati abbonamento);
	List<DocumentoTrasporto> findByVersamento(Versamento versamento);
	List<DocumentoTrasporto> findByCommittente(Anagrafica committente);
	List<DocumentoTrasporto> findByDocumentiTrasportoCumulatiAndVersamento(DocumentiTrasportoCumulati abbonamento, Versamento versamento);

}
