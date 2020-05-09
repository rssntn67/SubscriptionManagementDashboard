package it.arsinfo.smd.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.arsinfo.smd.dao.repository.UserInfoDao;
import it.arsinfo.smd.entity.UserInfo;

@Service
public class UserInfoServiceDao implements SmdServiceDao<UserInfo> {

    @Autowired
    private UserInfoDao repository;

	@Override
	public UserInfo save(UserInfo entity) throws Exception {
		return repository.save(entity);
	}

	@Override
	public void delete(UserInfo entity) throws Exception {
		repository.delete(entity);
	}

	@Override
	public UserInfo findById(Long id) {
		return repository.findById(id).get();
	}

	@Override
	public List<UserInfo> findAll() {
		return repository.findAll();
	}

	public UserInfoDao getRepository() {
		return repository;
	}
	
}
