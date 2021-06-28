package it.arsinfo.smd.ui.security;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import it.arsinfo.smd.dao.UserInfoDao;
import it.arsinfo.smd.entity.UserInfo;

/**
 * SecurityUtils takes care of all such static operations that have to do with
 * security and querying rights from different beans of the UI.
 *
 */
public class SecurityUtils {

    private static String pattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&+=])(?=\\S+$).{8,}$";

	private SecurityUtils() {
		// Util methods only
	}

	/**
	 * Gets the user name of the currently signed in user.
	 *
	 * @return the user name of the current user or <code>null</code> if the
	 *         user has not signed in
	 */
	public static String getUsername() {
		SecurityContext context = SecurityContextHolder.getContext();
		UserDetails userDetails = (UserDetails) context.getAuthentication().getPrincipal();
		return userDetails.getUsername();
	}

	/**
	 * Gets the user object for the current user.
	 *
	 * @return the user object
	 */
	public static UserInfo getCurrentUser(UserInfoDao userInfoDao) {
		return userInfoDao.findByUsernameAndProvider(SecurityUtils.getUsername(), UserInfo.Provider.LOCAL);
	}

	public static boolean verify(String password) {
	    return password.matches(pattern);
	}
}
