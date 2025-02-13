package com.engineering.ashish.redis.demo.DemoRedisWithSpringBoot.cache;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor  // Required for Jackson deserialization
@AllArgsConstructor
public class UserData {
    private String email;
    private String role;
}
