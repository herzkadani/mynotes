package ch.bbzbl.mynotes.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.security.SecureRandom;

public class PasswordEncoder {
    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10, new SecureRandom());

    public String encodePassoword(String plainPw) {
        return passwordEncoder.encode(plainPw);
    }

    public boolean passwordCorrect(String input, String hashedPw) {
        return passwordEncoder.matches(input, hashedPw);
    }
}
