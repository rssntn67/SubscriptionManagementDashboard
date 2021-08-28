package it.arsinfo.smd.service.dao;

import it.arsinfo.smd.dao.WooCommerceProductDao;
import it.arsinfo.smd.entity.WooCommerceProduct;
import it.arsinfo.smd.service.api.WooCommerceProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WooCommerceProductServiceDaoImpl implements WooCommerceProductService {

    @Autowired
    private WooCommerceProductDao repository;

	@Override
	public WooCommerceProduct save(WooCommerceProduct entity) throws Exception {
		return repository.save(entity);
	}

	@Override
	public void delete(WooCommerceProduct entity) throws Exception {
		throw new UnsupportedOperationException("Cannot delete WooCommerceProduct");
	}

	@Override
	public WooCommerceProduct findById(Long id) {
		return repository.findById(id).orElse(null);
	}

	@Override
	public List<WooCommerceProduct> findAll() {
		return repository.findAll();
	}

	@Override
	public List<WooCommerceProduct> searchByDefault() {
		return repository.findByPagato(false);
	}

	@Override
	public WooCommerceProduct add() {
		return new WooCommerceProduct();
	}

}
