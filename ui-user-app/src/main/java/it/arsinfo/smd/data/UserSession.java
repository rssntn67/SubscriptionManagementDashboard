package it.arsinfo.smd.data;

import it.arsinfo.smd.dao.AbbonamentoDao;
import it.arsinfo.smd.dao.AnagraficaDao;
import it.arsinfo.smd.dao.RivistaAbbonamentoDao;
import it.arsinfo.smd.dao.StoricoDao;
import it.arsinfo.smd.entity.*;
import it.arsinfo.smd.service.api.UserInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.stream.Collectors;

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
    private StoricoDao storicoDao;

    @Autowired
    private RivistaAbbonamentoDao rivistaAbbonamentoDao;

    private static final Logger log = LoggerFactory.getLogger(UserSession.class);

    public UserInfo getLoggedIn() {
        User user = getUser();
        UserInfo userInfo = userInfoService.findByUsernameAndProvider(user.getEmail(),user.getProvider());
        if (userInfo == null  || userInfo.getRole() != UserInfo.Role.SUBSCRIBED ) {
            log.info("getLoggedIn: not Subscribed");
            return null;
        }
        log.info("getLoggedIn: {} Role {}",userInfo.getUsername(),userInfo.getRole());
        return userInfo;
    }

    public Anagrafica findIntestatario(String code) {
        return anagraficaDao.findByCodeLineBase(code);
    }

    public Anagrafica getLoggedInIntestatario() {
        if (getLoggedIn() == null) {
            return null;
        }
        return anagraficaDao.findByCodeLineBase(getLoggedIn().getPasswordHash());
    }

    public List<Anagrafica> getAnagraficaStorico() {
        final Set<Anagrafica> anagrafiche = new HashSet<>();
        anagrafiche.addAll(getSubscriptions());
        anagrafiche.addAll(
            storicoDao.findByIntestatario(getLoggedInIntestatario()).stream()
                .map(Storico::getDestinatario).collect(Collectors.toList())
        );
        anagrafiche.addAll(
            storicoDao.findByDestinatario(getLoggedInIntestatario()).stream()
                .map(Storico::getIntestatario).collect(Collectors.toList())
        );
        return new ArrayList<>(anagrafiche);
    }

    public List<Abbonamento> getAbbonamentiIntestatario() {
        return abbonamentoDao.findByIntestatario(getLoggedInIntestatario());
    }

    public List<Anagrafica> getDestinatariAbbonamento() {
        final Set<Anagrafica> destinatari = new HashSet<>();
        for (Abbonamento abb: getAbbonamentiIntestatario()) {
            for (RivistaAbbonamento rivista: rivistaAbbonamentoDao.findByAbbonamento(abb)) {
                destinatari.add(rivista.getDestinatario());
            }
        }
        return new ArrayList<>(destinatari);
    }

    public List<Anagrafica> getSubscriptions() {
        return userInfoService.findByUserInfo(getLoggedIn());
    }

    public void add(String code) {
        User user = getUser();
        UserInfo remote = userInfoService.findByUsernameAndProvider(user.getEmail(),user.getProvider());
        userInfoService.add(remote,code);
    }

    public void save(String code) throws Exception {
        User user = getUser();
        UserInfo remote = userInfoService.findByUsernameAndProvider(user.getEmail(),user.getProvider());
        if (remote == null) {
            remote = new UserInfo();
            remote.setUsername(getUser().getEmail());
        }
        remote.setPasswordHash(code);
        remote.setRole(UserInfo.Role.SUBSCRIBED);
        remote.setProvider(user.getProvider());
        userInfoService.save(remote);
    }

    public void unsubscribe() throws Exception {
        User user = getUser();
        UserInfo remote = userInfoService.findByUsernameAndProvider(user.getEmail(),user.getProvider());
        userInfoService.delete(remote);
    }

    public User getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assert(authentication != null);
        log.info("getUser: provider: {}",authentication.getAuthorities());
        OAuth2AuthenticatedPrincipal principal = (OAuth2AuthenticatedPrincipal) authentication.getPrincipal();
        assert(principal != null);

        log.info("getUser: attributes: {}",principal.getAttributes());

        if (principal.getAttribute("iss") != null) {
            return new User(principal.getAttribute("given_name"), principal.getAttribute("family_name"), principal.getAttribute("email"),
                    principal.getAttribute("picture"), UserInfo.Provider.GOOGLE);
        }
        return new User(principal.getAttribute("name"), "",principal.getAttribute("email"),"", UserInfo.Provider.FACEBOOK);
    }

    public boolean isLoggedIn() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null;
    }
}