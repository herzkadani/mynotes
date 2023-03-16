package ch.bbzbl.mynotes.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.security.SecureRandom;

public class PasswordEncoder {
	
    private static BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10, new SecureRandom());

    private PasswordEncoder() {}
    
    public static String encodePassoword(String plainPw) {
        return passwordEncoder.encode(plainPw);
    }

    public static boolean passwordCorrect(String input, String hashedPw) {
        return passwordEncoder.matches(input, hashedPw);
    }
}
