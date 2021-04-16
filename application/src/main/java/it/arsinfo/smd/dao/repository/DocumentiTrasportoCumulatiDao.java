package it.arsinfo.smd.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.entity.DocumentiTrasportoCumulati;

public interface DocumentiTrasportoCumulatiDao extends JpaRepository<DocumentiTrasportoCumulati, Long> {

	DocumentiTrasportoCumulati findByAnno(Anno anno);


}
