package com.swp391.eyewear_management_backend.hashPassword;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class HashTool {
    public static void main(String[] args) {
        System.out.println(new BCryptPasswordEncoder(10).encode("cust123"));
    }
}
