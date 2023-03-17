package ch.bbzbl.mynotes.security;

import ch.bbzbl.mynotes.data.entity.User;
import ch.bbzbl.mynotes.data.service.UserRepository;
import com.vaadin.flow.spring.security.AuthenticationContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class AuthenticatedUser {

	private final UserRepository userRepository;
	private final AuthenticationContext authenticationContext;

	public AuthenticatedUser(AuthenticationContext authenticationContext, UserRepository userRepository) {
		this.userRepository = userRepository;
		this.authenticationContext = authenticationContext;
	}

	public Optional<User> get() {
		return authenticationContext.getAuthenticatedUser(UserDetails.class)
				.map(userDetails -> {
					List<User> userResultList = userRepository.findByUsername(userDetails.getUsername());
					if (userResultList.isEmpty()) this.logout();
					return userResultList.get(0);
				});
	}

	public void logout() {
		authenticationContext.logout();
	}

}
