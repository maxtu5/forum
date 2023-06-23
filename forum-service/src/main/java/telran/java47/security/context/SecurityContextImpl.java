package telran.java47.security.context;

import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import telran.java47.security.model.User;

@Component
public class SecurityContextImpl implements SecurityContext {

	Map<String, User> context = new ConcurrentHashMap<>();

	@Override
	public User addUserSession(String sessionId, User user) {
		return context.put(sessionId, user);
	}

	@Override
	public User removeUserSession(String sessionId) {
		return context.remove(sessionId);
	}

	@Override
	public User getUserBySessionId(String sessionId) {
		return context.get(sessionId);
	}

}
