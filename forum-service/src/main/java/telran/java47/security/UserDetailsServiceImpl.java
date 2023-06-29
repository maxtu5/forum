package telran.java47.security;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import telran.java47.accounting.dao.UserAccountRepository;
import telran.java47.accounting.model.UserAccount;
import telran.java47.exceptions.UserNotFoundException;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
	
	final UserAccountRepository userAccountRepository;  

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		System.out.println("*********");
		System.out.println(username);
		UserAccount userAccount = userAccountRepository.findById(username)
				.orElseThrow(() -> new UserNotFoundException());
		String[] roles = userAccount.getRoles().stream().map(r -> "ROLE_" + r).toArray(String[]::new);
		User user = new User(username,
				userAccount.getPassword(),
	            true, // enabled
	            true, // accountNonExpired,
	            true, //!userAccount.passExpired(), // credentialsNonExpired,
	            true, // accountNonLocked,
	            AuthorityUtils.createAuthorityList(roles));
		return user;
	}



}
