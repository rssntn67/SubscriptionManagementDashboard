package it.arsinfo.smd.dao;

import java.util.List;

import it.arsinfo.smd.entity.SmdEntity;
import it.arsinfo.smd.entity.SmdEntityItems;


public interface SmdServiceItemDao<T extends SmdEntityItems<I>,I extends SmdEntity> extends SmdServiceDao<T>{
	List<I>	getItems(T t);
	T deleteItem(T t,I item) throws Exception;
	T saveItem(T t,I item) throws Exception;
}
