package com.grupo.learningmore.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

@Service
public class BreachedPasswordService {

    private static final Logger log = LoggerFactory.getLogger(BreachedPasswordService.class);
    private static final String HIBP_API_URL = "https://api.pwnedpasswords.com/range/";
    private final RestTemplate restTemplate;

    public BreachedPasswordService() {
        this.restTemplate = new RestTemplate();
    }

    public boolean isPasswordBreached(String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }

        try {
            String sha1Hex = sha1(password).toUpperCase();
            String prefix = sha1Hex.substring(0, 5);
            String suffix = sha1Hex.substring(5);

            String response = restTemplate.getForObject(HIBP_API_URL + prefix, String.class);

            if (response != null && response.contains(suffix)) {
                log.warn("Password breach detected for prefix {}", prefix);
                return true;
            }

        } catch (Exception e) {
            log.error("Error checking breached password API", e);
            // In case of API failure, we might choose to allow or deny. 
            return false;
        }

        return false;
    }

    private String sha1(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] hash = md.digest(input.getBytes());
        return HexFormat.of().formatHex(hash);
    }
}
