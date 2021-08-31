package it.arsinfo.smd.service.dao;

import it.arsinfo.smd.dao.WooCommerceOrderDao;
import it.arsinfo.smd.data.StatoWooCommerceOrder;
import it.arsinfo.smd.entity.WooCommerceOrder;
import it.arsinfo.smd.service.api.WooCommerceOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WooCommerceOrderServiceDaoImpl implements WooCommerceOrderService {

    @Autowired
    private WooCommerceOrderDao repository;

	@Override
	public WooCommerceOrder save(WooCommerceOrder entity) throws Exception {
		return repository.save(entity);
	}

	@Override
	public void delete(WooCommerceOrder entity) throws Exception {
		throw new UnsupportedOperationException("Cannot delete WooCommerceProduct");
	}

	@Override
	public WooCommerceOrder findById(Long id) {
		return repository.findById(id).orElse(null);
	}

	@Override
	public List<WooCommerceOrder> findAll() {
		return repository.findAll();
	}

	@Override
	public List<WooCommerceOrder> searchByDefault() {
		return repository.findByStatus(StatoWooCommerceOrder.Generated);
	}

	@Override
	public WooCommerceOrder add() {
		return new WooCommerceOrder();
	}

}
