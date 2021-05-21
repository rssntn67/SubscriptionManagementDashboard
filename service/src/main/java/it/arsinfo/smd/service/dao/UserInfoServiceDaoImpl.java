package it.arsinfo.smd.service.dao;

import java.util.List;
import java.util.stream.Collectors;

import it.arsinfo.smd.dao.AnagraficaDao;
import it.arsinfo.smd.dao.RemoteUserInfoDao;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.RemoteUserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import it.arsinfo.smd.service.api.UserInfoService;
import it.arsinfo.smd.dao.UserInfoDao;
import it.arsinfo.smd.entity.UserInfo;
import it.arsinfo.smd.entity.UserInfo.Role;

@Service
public class UserInfoServiceDaoImpl implements UserInfoService {

    @Autowired
    private UserInfoDao repository;

    @Autowired
	private RemoteUserInfoDao remoteUserInfoDao;

    @Autowired
	private AnagraficaDao anagraficaDao;



	@Override
	public UserInfo save(UserInfo entity) throws Exception {
		UserInfo saved = repository.save(entity);
		if (entity.getRole() == Role.SUBSCRIBED) {
			add(saved,saved.getPasswordHash());
		}
		return saved;
	}

	@Override
	public void delete(UserInfo entity) throws Exception {
		if (entity.getRole() == Role.SUBSCRIBED) {
			entity.setRole(Role.UNSUBSCRIBED);
			repository.save(entity);
		} else
			repository.delete(entity);
	}

	@Override
	public UserInfo findById(Long id) {
		return repository.findById(id).orElse(null);
	}

	@Override
	public List<UserInfo> findAll() {
		return repository.findAll();
	}

	@Override
	public List<UserInfo> searchByDefault() {
		return repository.findAll();
	}

	@Override
	public UserInfo add() {
		return new UserInfo();
	}

	public UserInfoDao getRepository() {
		return repository;
	}

	public List<UserInfo> searchBy(String searchText, Role role) {
        if (!StringUtils.hasLength(searchText) && role == null) {
            return findAll();
        }
        if (!StringUtils.hasLength(searchText)) {
            return repository.findByRole(role);
        }
        if (role == null ) {
            return repository.findByUsernameContainingIgnoreCase(searchText);
        }
        return repository.findByUsernameContainingIgnoreCaseAndRole(searchText, role);
	}

	@Override
	public UserInfo findByUsername(String name) {
		return repository.findByUsername(name);
	}

	@Override
	public List<Anagrafica> findByUserInfo(UserInfo userInfo) {
		return remoteUserInfoDao.findByUserInfo(userInfo).stream().map(ru -> anagraficaDao.findByCodeLineBase(ru.getCode())).collect(Collectors.toList());
	}

	@Override
	public void add(UserInfo userInfo, String code) {
		RemoteUserInfo ru = remoteUserInfoDao.findByUserInfoAndCode(userInfo,code);
		if (ru == null)
			remoteUserInfoDao.save(new RemoteUserInfo(userInfo,code));
	}

	@Override
	public void delete(UserInfo userInfo, String code) {
		RemoteUserInfo ru = remoteUserInfoDao.findByUserInfoAndCode(userInfo,code);
		if (ru != null)
			remoteUserInfoDao.delete(ru);

	}

}
