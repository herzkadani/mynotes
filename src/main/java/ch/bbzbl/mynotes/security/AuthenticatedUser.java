package ch.bbzbl.mynotes.security;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vaadin.flow.spring.security.AuthenticationContext;

import ch.bbzbl.mynotes.data.entity.User;
import ch.bbzbl.mynotes.data.service.UserService;

@Component
public class AuthenticatedUser {

	@Autowired
    private UserService userRepository;
    private final AuthenticationContext authenticationContext;

    public AuthenticatedUser(AuthenticationContext authenticationContext) {
        this.authenticationContext = authenticationContext;
    }

    public Optional<User> get() {
    	Optional<String> principalName = authenticationContext.getPrincipalName();
    	if (principalName.isPresent()) {
    	return userRepository.getByUsername(principalName.get());
    	}else {
    		return Optional.empty();
    	}
    }

    public void logout() {
        authenticationContext.logout();
    }

}
