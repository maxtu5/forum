package telran.java47.accounting.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;

@Getter
@Document(collection = "users")
public class UserAccount {
	@Id
	String login;
	@Setter
	String password;
	@Setter
	LocalDate passDate;
	@Setter
	String firstName;
	@Setter
	String lastName;
	Set<String> roles;
	
	public UserAccount() {
		roles = new HashSet<>();
	}

	public UserAccount(String login, String password, String firstName, String lastName) {
		this();
		this.login = login;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public boolean addRole(String role) {
		return roles.add(role);
	}

	public boolean removeRole(String role) {
		return roles.remove(role);
	}
	
	public boolean passExpired() {
		return passDate == null ? true : LocalDate.now().isAfter(passDate.plus(30, ChronoUnit.DAYS));
 	}

}
