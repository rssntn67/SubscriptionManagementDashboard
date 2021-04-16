package it.arsinfo.smd.ui.service.api;

import java.util.List;

import it.arsinfo.smd.entity.SmdEntity;
import it.arsinfo.smd.entity.SmdEntityItems;


public interface SmdServiceItem<T extends SmdEntityItems<I>,I extends SmdEntity> extends SmdServiceBase<T> {
	List<I>	getItems(T t);
	T deleteItem(T t,I item) throws Exception;
	T saveItem(T t,I item) throws Exception;
	List<I> findAllItems();
}
