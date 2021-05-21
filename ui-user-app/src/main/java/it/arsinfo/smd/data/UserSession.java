package it.arsinfo.smd.data;

import it.arsinfo.smd.dao.AbbonamentoDao;
import it.arsinfo.smd.dao.AnagraficaDao;
import it.arsinfo.smd.dao.RivistaAbbonamentoDao;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.RivistaAbbonamento;
import it.arsinfo.smd.entity.UserInfo;
import it.arsinfo.smd.service.api.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@SessionScope
public class UserSession implements Serializable {

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private AnagraficaDao anagraficaDao;

    @Autowired
    private AbbonamentoDao abbonamentoDao;

    @Autowired
    private RivistaAbbonamentoDao rivistaAbbonamentoDao;

    public UserInfo getLoggedIn() throws UnsupportedOperationException {
        UserInfo userInfo = userInfoService.findByUsername(getUser().getEmail());
        if (userInfo == null  || userInfo.getRole() != UserInfo.Role.SUBSCRIBED )
            throw new UnsupportedOperationException("Lo user si deve registrare");
        return userInfo;
    }

    public Anagrafica findIntestatario(String code) {
        return anagraficaDao.findByCodeLineBase(code);
    }

    public Anagrafica getLoggedInIntestatario() {
        return anagraficaDao.findByCodeLineBase(getLoggedIn().getPasswordHash());
    }

    public List<Abbonamento> getAbbonamenti() {
        return abbonamentoDao.findByIntestatario(getLoggedInIntestatario());
    }

    public List<Anagrafica> getDestinatari() {
        final Set<Anagrafica> destinatari = new HashSet<>();
        for (Abbonamento abb: getAbbonamenti()) {
            for (RivistaAbbonamento rivista: rivistaAbbonamentoDao.findByAbbonamento(abb)) {
                destinatari.add(rivista.getDestinatario());
            }
        }
        return new ArrayList<>(destinatari);
    }

    public List<Anagrafica> getSubscriptions() {
        return userInfoService.findByUserInfo(getLoggedIn());
    }

    public void save(String code) throws Exception {
        UserInfo remote = userInfoService.findByUsername(getUser().getEmail());
        if (remote == null) {
            remote = new UserInfo();
            remote.setUsername(getUser().getEmail());
            remote.setPasswordHash(code);
        }
        remote.setRole(UserInfo.Role.SUBSCRIBED);
        userInfoService.save(remote);
    }

    public void unsubscribe() throws Exception {
        UserInfo remote = userInfoService.findByUsername(getUser().getEmail());
        userInfoService.delete(remote);
    }

    public User getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        OAuth2AuthenticatedPrincipal principal = (OAuth2AuthenticatedPrincipal) authentication.getPrincipal();

        return new User(principal.getAttribute("given_name"), principal.getAttribute("family_name"), principal.getAttribute("email"),
                principal.getAttribute("picture"));
    }

    public boolean isLoggedIn() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null;
    }
}