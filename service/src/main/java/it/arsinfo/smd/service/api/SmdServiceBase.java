package it.arsinfo.smd.service.api;

import java.util.List;

import it.arsinfo.smd.entity.SmdEntity;


public interface SmdServiceBase<S extends SmdEntity> {
	
	S save(S entity) throws Exception;
	void delete(S entity) throws Exception ;
	S findById(Long id);
	List<S> findAll();
	List<S> searchByDefault();
	S add();

}
