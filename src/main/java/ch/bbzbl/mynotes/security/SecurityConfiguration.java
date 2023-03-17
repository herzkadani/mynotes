package ch.bbzbl.mynotes.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.vaadin.flow.spring.security.VaadinWebSecurity;

import ch.bbzbl.mynotes.data.entity.User;
import ch.bbzbl.mynotes.data.service.UserService;
import ch.bbzbl.mynotes.views.login.LoginView;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@EnableWebSecurity
@Configuration
public class SecurityConfiguration extends VaadinWebSecurity {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Autowired
    UserService userService;
    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
    	

    	http.oauth2Login().loginPage("/login").permitAll().successHandler(new AuthenticationSuccessHandler() {
			
			@Override
			public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
					Authentication authentication) throws IOException, ServletException {
				
				SecurityContextHolder.getContext().setAuthentication(authentication);
				
			       OAuth2AuthenticatedPrincipal principal = (OAuth2AuthenticatedPrincipal) authentication.getPrincipal();
			       
			       
			       // Check if the email is already in the database
			       User existingUser = userService.findByEmail(principal.getAttribute("email"));
			        if (existingUser != null) {
			            // Email address already exists
			            if (existingUser.isOAuthUser()) {
			                // E-Mail already in use by an OAuth user, login the user
							SecurityContextHolder.getContext().setAuthentication(authentication);
			                response.sendRedirect("mynotes");
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
			            userService.save(newUser);
			            
			            SecurityContextHolder.getContext().setAuthentication(authentication);

		                response.sendRedirect("mynotes");
			        }
			       
			}
		});
    	
        http.authorizeHttpRequests().requestMatchers(new AntPathRequestMatcher("/images/*")).permitAll();

        // Icons from the line-awesome addon
        http.authorizeHttpRequests().requestMatchers(new AntPathRequestMatcher("/line-awesome/**/*.svg")).permitAll();
        super.configure(http);
        setLoginView(http, LoginView.class);
    }

}
