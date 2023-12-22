package ch.bbzbl.mynotes.security.mfa.data;

import java.io.Serializable;

public class MFATokenData implements Serializable {

    private String qrCode;
    private String mfaCode;

    public MFATokenData(String qrCode, String mfaCode) {
        this.qrCode = qrCode;
        this.mfaCode = mfaCode;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getMfaCode() {
        return mfaCode;
    }

    public void setMfaCode(String mfaCode) {
        this.mfaCode = mfaCode;
    }
}
