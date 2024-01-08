package ch.bbzbl.mynotes.security;

import ch.bbzbl.mynotes.data.Role;
import ch.bbzbl.mynotes.data.entity.User;
import ch.bbzbl.mynotes.data.service.UserService;
import ch.bbzbl.mynotes.security.mfa.CustomAuthenticationProvider;
import ch.bbzbl.mynotes.security.mfa.CustomWebAuthenticationDetailsSource;
import ch.bbzbl.mynotes.security.mfa.MFATokenService;
import ch.bbzbl.mynotes.views.login.LoginView;
import com.vaadin.flow.spring.security.VaadinWebSecurity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@EnableWebSecurity
@Configuration
public class SecurityConfiguration extends VaadinWebSecurity {
	@Autowired
	private CustomWebAuthenticationDetailsSource authenticationDetailsSource;
	@Autowired
	UserService userService;
	@Autowired
	UserDetailsServiceImpl userDetailsService;
	@Autowired
	private final MFATokenService mfaTokenService;

	public SecurityConfiguration(MFATokenService mfaTokenService) {
		this.mfaTokenService = mfaTokenService;
	}

	@Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
    	http.oauth2Login().loginPage("/login").permitAll().successHandler((request, response, authentication) -> {

			SecurityContextHolder.getContext().setAuthentication(authentication);

			   OAuth2AuthenticatedPrincipal principal = (OAuth2AuthenticatedPrincipal) authentication.getPrincipal();

			   // Check if the email is already in the database
			   User existingUser = userService.findByEmail(principal.getAttribute("email"));
				if (existingUser != null) {
					// Email address already exists
					if (existingUser.isOAuthUser()) {
						// E-Mail already in use by an OAuth user, login the user
						SecurityContextHolder.getContext().setAuthentication(authentication);
						response.sendRedirect("account");

					} else {
						// E-Mail already in use by a normal user, logout and go back to login page with error message
						SecurityContextHolder.getContext().setAuthentication(null);
						request.logout();
						response.sendRedirect("/login/OAuthErrorEmailAlreadyExists");
					}
				} else {
					// New email address
					// register the user
					User newUser = new User();
					String username = principal.getAttribute("sub");
					newUser.setUsername(username);
					newUser.setName(principal.getAttribute("name"));
					newUser.setEmail(principal.getAttribute("email"));
					newUser.setHashedPassword("");
					newUser.setOAuthUser(true); // Mark the user as an OAuth user.
					newUser.addRole(Role.USER);
					userService.save(newUser);

					SecurityContextHolder.getContext().setAuthentication(authentication);

					response.sendRedirect("account");
				}

		});
		http.formLogin().authenticationDetailsSource(authenticationDetailsSource);
		http.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
				.sessionFixation().migrateSession()
				.invalidSessionUrl("/login");
		http.sessionManagement().maximumSessions(1);
        http.authorizeHttpRequests().requestMatchers(new AntPathRequestMatcher("/images/*")).permitAll();

        // Icons from the line-awesome addon
        http.authorizeHttpRequests().requestMatchers(new AntPathRequestMatcher("/line-awesome/**/*.svg")).permitAll();
        super.configure(http);
        setLoginView(http, LoginView.class);
    }
	@Bean
	public DaoAuthenticationProvider authProvider() {
		CustomAuthenticationProvider authProvider = new CustomAuthenticationProvider(mfaTokenService);
		authProvider.setUserDetailsService(userDetailsService);
		authProvider.setPasswordEncoder(passwordEncoder());
		return authProvider;
	}

	@Bean
	public HttpSessionEventPublisher httpSessionEventPublisher() {
		return new HttpSessionEventPublisher();
	}
    
}
