//package com.swp391.eyewear_management_backend.config;
//
//import com.devteria.identityservice.entity.User;
//import com.devteria.identityservice.enums.Role;
//import com.devteria.identityservice.repository.UserRepo;
//import lombok.AccessLevel;
//import lombok.RequiredArgsConstructor;
//import lombok.experimental.FieldDefaults;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//import java.util.HashSet;
//
//@Configuration
//@RequiredArgsConstructor
//@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
//@Slf4j
//public class ApplicationInitConfig {
//
//    PasswordEncoder passwordEncoder;
//
//    @Bean
//    ApplicationRunner applicationRunner(UserRepo userRepo) {
//        return args -> {
//            if(userRepo.findByUsername("admin").isEmpty()) {
//                var roles = new HashSet<String>();
//                roles.add(Role.ADMIN.name());
//                User user = User.builder()
//                        .username("admin")
//                        .password(passwordEncoder.encode("admin"))
//                        //.roles(roles)
//                        .build();
//
//                userRepo.save(user);
//                log.warn("admin user has been created with default password: admin, please change");
//            }
//        };
//    }
//}
