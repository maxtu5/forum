package telran.java47.security;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import telran.java47.accounting.dao.UserAccountRepository;
import telran.java47.accounting.model.UserAccount;
import telran.java47.exceptions.UserNotFoundException;

@RequiredArgsConstructor
@Component
@Order(20)
public class AdminFilter implements Filter {
	
	final UserAccountRepository userAccountRepository; 

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		String accessLevel = checkEndPoint(request.getMethod(), request.getServletPath());

		if (!"NONE".equals(accessLevel)) {
			String s = request.getUserPrincipal().getName();
			Optional<UserAccount> o = userAccountRepository.findById(s);
			UserAccount userAccount = o.orElseThrow(() -> new RuntimeException("Employee not found"));
		
			if ("ADMIN".equals(accessLevel) && !userAccount.getRoles().contains("Administrator")) {
				response.sendError(401, "Administrator permission required");
				return;
			}
			if ("OWNER".equals(accessLevel) && !request.getServletPath().split("/")[3].equals(request.getUserPrincipal().getName())) {
				response.sendError(401, "Ownership required");
				return;
			}
			
			if ("ADMIN_OR_OWNER".equals(accessLevel) 
					&& !(request.getServletPath().split("/")[3].equals(request.getUserPrincipal().getName()) 
							|| userAccount.getRoles().contains("Administrator"))) {
				response.sendError(401, "Ownership or Administrator permission required");
				return;
			}
			if ("UNKNOWN".equals(accessLevel)) {
				response.sendError(401, "Bad request");
				return;
			}
		}	
		chain.doFilter(request, response);
	}

	private String checkEndPoint(String method, String servletPath) {
		if ("POST".equals(method) && servletPath.matches("/account/register/?"))
			return "NONE";
		if (("PUT".equals(method) || "DELETE".equals(method)) 
				&& servletPath.matches("/account/user/[a-zA-Z_][0-9a-zA-Z_]*/role/[a-zA-Z_][0-9a-zA-Z_]*/?"))
			return "ADMIN";
		if ("PUT".equals(method) && servletPath.matches("/account/user/[a-zA-Z_][0-9a-zA-Z_]*/?"))
			return "OWNER";
		if ("DELETE".equals(method) && servletPath.matches("/account/user/[a-zA-Z_][0-9a-zA-Z_]*/?"))
			return "ADMIN_OR_OWNER";
		return "UNKNOWN";
	}

}
