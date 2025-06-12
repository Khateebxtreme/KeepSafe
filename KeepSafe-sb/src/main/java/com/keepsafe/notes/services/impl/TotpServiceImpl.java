package com.keepsafe.notes.services.impl;

import com.keepsafe.notes.services.TotpService;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import org.springframework.stereotype.Service;

@Service
public class TotpServiceImpl implements TotpService {

    private final GoogleAuthenticator googleAuthenticator;

    public TotpServiceImpl(GoogleAuthenticator googleAuthenticator) {
        this.googleAuthenticator = googleAuthenticator;
    }

    public TotpServiceImpl() {
        this.googleAuthenticator = new GoogleAuthenticator();
    }

    @Override
    public GoogleAuthenticatorKey generateSecret(){
        //returns the secret that is required for MFA using Google Authenticator
        return googleAuthenticator.createCredentials();
    }

    @Override
    public String getQrCodeUrl(GoogleAuthenticatorKey secret, String username){
        //method that generates the QR code required for MFA into the system and returns the URL for the QR code to further the authentication process(basically to scan the same on front end).
        return GoogleAuthenticatorQRGenerator.getOtpAuthURL("KeepSafe", username, secret);
    }

    @Override
    public boolean verifyCode(String secret, int code){
        //function to verify the code entered by the user, if it matches the code that is generated on the authenticator app.
        return googleAuthenticator.authorize(secret,code);
    }
}
