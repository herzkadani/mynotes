package ch.bbzbl.mynotes.security.mfa;

import ch.bbzbl.mynotes.data.entity.User;
import ch.bbzbl.mynotes.data.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import java.util.Optional;

public class CustomAuthenticationProvider extends DaoAuthenticationProvider {

    @Autowired
    private UserService userService;
    @Autowired
    private final MFATokenService mfaTokenService;

    private Logger LOGGER = LoggerFactory.getLogger(CustomAuthenticationProvider.class);

    public CustomAuthenticationProvider(MFATokenService mfaTokenService) {
        this.mfaTokenService = mfaTokenService;
    }

    @Override
    public Authentication authenticate(Authentication auth)
            throws AuthenticationException {
        String verificationCode
                = ((CustomWebAuthenticationDetails) auth.getDetails())
                .getVerificationCode();
        Optional<User> user = userService.findByUsername(auth.getName()).stream().findFirst();
        if (user.isEmpty()) {
            LOGGER.error("Bad Credentials Username or Password");
            throw new BadCredentialsException("Invalid username or password");
        }

        if (!isValidLong(verificationCode) || !mfaTokenService.verifyTotp(verificationCode, user.get().getSecret())) {
           LOGGER.error("Bad Credentials Verification Code");
            throw new BadCredentialsException("Invalid verfication code");
        }

        Authentication result = super.authenticate(auth);
        return new UsernamePasswordAuthenticationToken(
                user.get(), result.getCredentials(), result.getAuthorities());
    }

    private boolean isValidLong(String code) {
        try {
            Long.parseLong(code);
        } catch (NumberFormatException e) {
            LOGGER.error("Invalid Long", e);
            return false;
        }
        return true;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
