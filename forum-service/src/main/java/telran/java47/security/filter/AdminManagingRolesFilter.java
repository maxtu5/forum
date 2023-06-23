package telran.java47.security.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import telran.java47.security.model.Role;
import telran.java47.security.model.User;

@RequiredArgsConstructor
@Component
@Order(20)
public class AdminManagingRolesFilter implements Filter {
	
	@Value("${endpoints.useraccount.roles.regex}")
	private String accountRolesPathRegex;	

	
	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		if (checkEndPoint(request.getMethod(), request.getServletPath())) {
			User user = (User) request.getUserPrincipal();
			if (!user.getRoles().contains(Role.ADMINISTRATOR)) {
				response.sendError(403, "Administrator permission required");
				return;
			}
		}	
		chain.doFilter(request, response);
	}

	private boolean checkEndPoint(String method, String servletPath) {
		return servletPath.matches(accountRolesPathRegex);
	}
	
}
