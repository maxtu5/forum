package telran.java47.security.model;

import lombok.Getter;

public enum Role {
	ADMINISTRATOR("administrator"), MODERATOR("moderator"), USER("user");
	
	@Getter
	String roleName;

	private Role(String roleName) {
		this.roleName = roleName;
	}
	
    public static Role fromString(String text) {
        for (Role r : Role.values()) {
            if (r.roleName.equalsIgnoreCase(text)) {
                return r;
            }
        }
        return null;
    }
}
