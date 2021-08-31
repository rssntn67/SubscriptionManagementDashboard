package it.arsinfo.smd.dao;

import it.arsinfo.smd.data.StatoWooCommerceOrder;
import it.arsinfo.smd.entity.WooCommerceOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WooCommerceOrderDao extends JpaRepository<WooCommerceOrder, Long> {

	List<WooCommerceOrder> findByStatus(StatoWooCommerceOrder status);

}
