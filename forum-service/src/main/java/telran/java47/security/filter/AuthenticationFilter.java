package telran.java47.security.filter;

import java.io.IOException;
import java.security.Principal;
import java.util.Base64;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import telran.java47.accounting.dao.UserAccountRepository;
import telran.java47.accounting.model.UserAccount;
import telran.java47.security.context.SecurityContext;
import telran.java47.security.model.Role;
import telran.java47.security.model.User;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

@RequiredArgsConstructor
@Component
@Order(10)
public class AuthenticationFilter implements Filter {
	
	final UserAccountRepository userAccountRepository; 
	final SecurityContext securityContext;
	
	@Value("${endpoints.useraccount.register.regex}")
	private String accountRegisterPathRegex;	
	@Value("${endpoints.posts.allactions.regex}")
	private String postsAllActionsPathRegex;
	

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		if (checkEndPoint(request.getMethod(), request.getServletPath())) {
			String sessionId = request.getSession().getId();
			User user = securityContext.getUserBySessionId(sessionId);
			if(user == null) {
				String[] credentials;
				try {
					credentials = getCredentials(request.getHeader("Authorization"));
				} catch (Exception e) {
					response.sendError(401, "Bad token");
					return;
				}
				UserAccount userAccount = userAccountRepository.findById(credentials[0]).orElse(null);
				if (userAccount == null || !BCrypt.checkpw(credentials[1], userAccount.getPassword())) {
					response.sendError(401, "Bad login or password");
					return;
				}
				user = new User(userAccount.getLogin(), 
						userAccount.getRoles().stream()
						.map(s -> Role.fromString(s))
						.collect(Collectors.toSet()));
				securityContext.addUserSession(sessionId, user);
			}
			request = new WrappedRequest(request, user.getName(), user.getRoles());
		}
		chain.doFilter(request, response);
	}

	private boolean checkEndPoint(String method, String servletPath) {
		return !(
				("POST".equalsIgnoreCase(method) && servletPath.matches(accountRegisterPathRegex))
				|| servletPath.matches(postsAllActionsPathRegex)
				);
	}

	private String[] getCredentials(String token) {
		token = token.substring(6);
		String decoded = new String(Base64.getDecoder().decode(token));
		return decoded.split(":");
	}
	
	private static class WrappedRequest extends HttpServletRequestWrapper {
		
		private String login;
		private Set<Role> roles;

		public WrappedRequest(HttpServletRequest request, String login, Set<Role> roles) {
			super(request);
			this.login = login;
			this.roles = roles;
		}
		
		@Override
		public Principal getUserPrincipal() {
			return new User(login, roles);
		}
	}

}
