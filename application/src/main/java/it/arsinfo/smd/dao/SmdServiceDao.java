package it.arsinfo.smd.dao;

import java.util.List;

import it.arsinfo.smd.entity.SmdEntity;


public interface SmdServiceDao<S extends SmdEntity> {
	
	S save(S entity) throws Exception;
	void delete(S entity) throws Exception ;
	S findById(Long id);
	List<S> findAll();
}
