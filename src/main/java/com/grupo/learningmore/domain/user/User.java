package com.grupo.learningmore.domain.user;

import jakarta.persistence.*;
import java.util.UUID;
import java.util.HexFormat;
import java.security.SecureRandom;

@Entity
@Table(name = "users")
public class User {

    private static final SecureRandom secureRandom = new SecureRandom();

    @Id
    @Column(unique = true, nullable = false)
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Column(nullable = false)
    private boolean active;

    @Column(nullable = false)
    private long tokenVersion;

    protected User() {
    }

    public User(String name, String email, String passwordHash, UserRole role) {
        this.id = generateSecureId();
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.active = true;
        this.tokenVersion = 0;
    }

    private String generateSecureId() {
        byte[] bytes = new byte[16]; // 16 bytes = 128 bits de entropia pura
        secureRandom.nextBytes(bytes); // CSPRNG preenche o array com bytes seguros
        return "USR-" + HexFormat.of().formatHex(bytes).toUpperCase(); // Transforma em String Hexadecimal
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public UserRole getRole() {
        return role;
    }

    public boolean isActive() {
        return active;
    }

    public long getTokenVersion() {
        return tokenVersion;
    }

    public void changePassword(String newPasswordHash) {
        this.passwordHash = newPasswordHash;
        this.tokenVersion++;
    }

    public void deactivate() {
        this.active = false;
        this.tokenVersion++;
    }

    public void activate() {
        this.active = true;
        this.tokenVersion++;
    }
}
