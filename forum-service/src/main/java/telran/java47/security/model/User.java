package telran.java47.security.model;
import java.security.Principal;
import java.util.Set;

import lombok.Getter;

public class User implements Principal{
	
	public User(String login, Set<Role> roles) {
		super();
		this.login = login;
		this.roles = roles;
	}
		
	String login;
	@Getter
	Set<Role> roles;
	
	@Override
	public String getName() {
		return login;
	}

}
