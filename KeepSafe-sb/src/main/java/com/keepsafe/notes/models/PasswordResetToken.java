package com.keepsafe.notes.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Data
@NoArgsConstructor
public class PasswordResetToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private Instant expiry; //represents single point in a timeline with a greater precision than Date (can be substituted)

    private boolean used; //if the token has been used to reset the password or not

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", nullable=false)
    private User user; //user linked with the token

    public PasswordResetToken(String token, Instant expiry, User user) {
        this.token = token;
        this.expiry = expiry;
        this.user = user;
    }
}
