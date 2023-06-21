package telran.java47.security;

import java.io.IOException;
import java.security.Principal;
import java.util.Base64;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import telran.java47.accounting.dao.UserAccountRepository;
import telran.java47.accounting.model.UserAccount;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

@RequiredArgsConstructor
@Component
@Order(10)
public class AuthenticationFilter implements Filter {
	
	final UserAccountRepository userAccountRepository; 

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		if (checkEndPoint(request.getMethod(), request.getServletPath())) {
			//		System.out.println(request.getHeader("Authorization"));
			String[] credentials;
			try {
				credentials = getCredentials(request.getHeader("Authorization"));
			} catch (Exception e) {
				response.sendError(401, "Bad token");
				return;
			}
			//		System.out.println(credentials[0] + credentials[1]);
			UserAccount userAccount = userAccountRepository.findById(credentials[0]).orElse(null);
			if (userAccount == null || !BCrypt.checkpw(credentials[1], userAccount.getPassword())) {
				response.sendError(401, "Bad login or password");
				return;
			}
			request = new WrappedRequest(request, credentials[0]);
		}
		chain.doFilter(request, response);
	}

	private boolean checkEndPoint(String method, String servletPath) {
			System.out.println(method + servletPath);
		return !("POST".equals(method) && servletPath.matches("/account/register/?"));
	}

	private String[] getCredentials(String token) {
		token = token.substring(6);
		String decoded = new String(Base64.getDecoder().decode(token));
		return decoded.split(":");
	}
	
	private static class WrappedRequest extends HttpServletRequestWrapper {
		
		private String login;

		public WrappedRequest(HttpServletRequest request, String login) {
			super(request);
			this.login = login;
		}
		
		@Override
		public Principal getUserPrincipal() {
			return () -> login;
		}
	}

}
