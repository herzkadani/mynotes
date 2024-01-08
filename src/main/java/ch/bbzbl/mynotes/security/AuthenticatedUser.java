package ch.bbzbl.mynotes.security;


import ch.bbzbl.mynotes.data.entity.User;
import ch.bbzbl.mynotes.data.service.UserService;
import com.vaadin.flow.spring.security.AuthenticationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;


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
        //TODO LOGGING VIEW visit and logout and login, db changes etc and validation register and notes
        authenticationContext.logout();
    }

}
