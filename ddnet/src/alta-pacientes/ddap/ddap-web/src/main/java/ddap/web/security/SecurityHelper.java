package ddap.web.security;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.servlet.http.HttpSession;

import ddap.ejb.UsuariosManagerLocal;
import ddap.ejb.entities.Usuario;

@Stateless
@LocalBean
public class SecurityHelper {
	private static final String LOGIN_ATTRIBUTE_NAME = "dd_login";
	private static final String AUTHENTICATED_FLAG_ATTRIBUTE_NAME = "dd_authenticated";
	
	@EJB
	private UsuariosManagerLocal uManager;
			
	public boolean isAuthenticated(HttpSession session) {
		if (session == null)
			return false;
		
		final Object authenticatedAttribute = session.getAttribute(AUTHENTICATED_FLAG_ATTRIBUTE_NAME);
		return authenticatedAttribute != null ? Boolean.parseBoolean(authenticatedAttribute.toString()) : false;		
	}
	
	public boolean login(HttpSession session, String login, String password) {
		session.removeAttribute(LOGIN_ATTRIBUTE_NAME);
		session.removeAttribute(AUTHENTICATED_FLAG_ATTRIBUTE_NAME);
		
		Usuario user = uManager.getByLogin(login);		
		if (user != null) {
			if (!user.checkPassword(password))
				return false;
						
			session.setAttribute(LOGIN_ATTRIBUTE_NAME, login);
			session.setAttribute(AUTHENTICATED_FLAG_ATTRIBUTE_NAME, Boolean.TRUE);
			
			return true;
		} else {
			return false;
		}
	}
	
	public void logout(HttpSession session) {
		session.removeAttribute(LOGIN_ATTRIBUTE_NAME);
		session.removeAttribute(AUTHENTICATED_FLAG_ATTRIBUTE_NAME);
		session.invalidate();
	}
	
	public Usuario getUser(HttpSession session) {
		return isAuthenticated(session) ?
			uManager.getByLogin((String)session.getAttribute(LOGIN_ATTRIBUTE_NAME)): null;
	}
}
