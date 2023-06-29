package telran.java47.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.RequestMatcher;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class AuthorisationConfiguration {
	
	@Bean
	public SecurityFilterChain configure(HttpSecurity http) throws Exception {
		 http.httpBasic(withDefaults());
	        http.csrf().disable();
	        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
	        http.authorizeRequests(authorize -> authorize
	        		.mvcMatchers("/account/register", "/forum/posts/**")
	        			.permitAll()
				    .mvcMatchers(HttpMethod.PUT, "/account/password")
	        			.permitAll()
	        		.mvcMatchers("/account/user/{login}/role/{role}")
	        			.hasRole("ADMINISTRATOR")
	        		.mvcMatchers(HttpMethod.PUT, "/account/user/{login}")
	        			.access("#login == authentication.name")
	        		.mvcMatchers(HttpMethod.DELETE, "/account/user/{login}")
	        			.access("#login == authentication.name or hasRole('ADMINISTRATOR')")
	        		.mvcMatchers(HttpMethod.POST, "/forum/post/{author}")
	        			.access("#author == authentication.name")
	        		.mvcMatchers(HttpMethod.PUT, "/forum/post/{id}/comment/{author}")
	        			.access("#author == authentication.name")
	        		.mvcMatchers(HttpMethod.PUT, "/forum/post/{id}")
	        			.access("@customSecurity.checkPostAuthor(#id, authentication.name)")
	        		.mvcMatchers(HttpMethod.DELETE, "/forum/post/{id}")
	        			.access("@customSecurity.checkPostAuthor(#id, authentication.name) or hasRole('MODERATOR')")
	        		.anyRequest()
        				.access("@customSecurity.userNotExpired(authentication.name)")
	        );
			
		return http.build();
	}
}
