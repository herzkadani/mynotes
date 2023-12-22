package ch.bbzbl.mynotes.security.mfa;

import dev.samstevens.totp.exceptions.QrGenerationException;

public interface MFATokenService {
    String generateSecretKey();
    String getQRCode(final String secret) throws QrGenerationException;
    boolean verifyTotp(final String code, final String secret);
}
