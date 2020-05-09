package it.arsinfo.smd.entity;

import java.util.List;

import javax.persistence.Transient;

public interface SmdEntityItems<S extends SmdEntity> extends SmdEntity {
    
	@Transient
	boolean addItem(S item);
	@Transient
	boolean removeItem(S item);
	@Transient
	List<S> getItems();

	@Transient
	void setItems(List<S> items);

}
