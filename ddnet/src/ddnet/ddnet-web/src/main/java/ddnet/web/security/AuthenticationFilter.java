package ddnet.web.security;

import java.io.IOException;

import javax.ejb.EJB;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AuthenticationFilter implements Filter {
	private static final String DEFAULT_LOGIN_FORM_PATH = "/ddnet-web/login-form.html";
	
	private String loginFormPath;
	
	@EJB
	private SecurityHelper securityHelper;
	
	@Override
	public void init(FilterConfig config) throws ServletException {
		loginFormPath = config.getInitParameter("login-form");
		if (loginFormPath == null)
			loginFormPath = DEFAULT_LOGIN_FORM_PATH;
	}

	@Override
	public void destroy() {
		// Do nothing.
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain filterChain) throws IOException, ServletException {
		
		HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;			
		
        if (securityHelper.isAuthenticated(httpRequest.getSession())) {
        	filterChain.doFilter(request, response);
        } else {
        	httpResponse.sendRedirect(loginFormPath);
        }
	}	
}
