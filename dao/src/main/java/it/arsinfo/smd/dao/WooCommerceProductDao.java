package it.arsinfo.smd.dao;

import it.arsinfo.smd.entity.WooCommerceProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WooCommerceProductDao extends JpaRepository<WooCommerceProduct, Long> {

	List<WooCommerceProduct> findByPagato(boolean pagato);

}
