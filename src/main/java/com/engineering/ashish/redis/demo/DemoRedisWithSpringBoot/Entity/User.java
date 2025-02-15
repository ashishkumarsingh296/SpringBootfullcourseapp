package com.engineering.ashish.redis.demo.DemoRedisWithSpringBoot.Entity;

import com.engineering.ashish.redis.demo.DemoRedisWithSpringBoot.enums.Role;
import com.engineering.ashish.redis.demo.DemoRedisWithSpringBoot.enums.UserType;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ApiKey> apiKeys = new ArrayList<>();


    @Column(unique = true)  // Store latest API key directly in user table
    private String apiKey;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserType userType;
}
