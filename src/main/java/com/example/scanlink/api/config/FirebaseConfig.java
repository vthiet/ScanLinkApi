//package com.example.scanlink.api.config;
//
//import com.google.auth.oauth2.GoogleCredentials;
//import com.google.firebase.FirebaseApp;
//import com.google.firebase.FirebaseOptions;
//import com.google.firebase.auth.FirebaseAuth;
//import jakarta.annotation.PostConstruct;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.io.FileInputStream;
//import java.io.IOException;
//
//@Configuration
//public class FirebaseConfig {
//
//    @Value("${app.firebase.service-account-path:}")
//    private String serviceAccountPath;
//
//    @PostConstruct
//    public void init() throws IOException {
//        if (!FirebaseApp.getApps().isEmpty()) {
//            return;
//        }
//
//        if (serviceAccountPath == null || serviceAccountPath.isBlank()) {
//            FirebaseOptions options = FirebaseOptions.builder()
//                    .setCredentials(GoogleCredentials.getApplicationDefault())
//                    .build();
//            FirebaseApp.initializeApp(options);
//            return;
//        }
//
//        try (FileInputStream serviceAccount = new FileInputStream(serviceAccountPath)) {
//            FirebaseOptions options = FirebaseOptions.builder()
//                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
//                    .build();
//            FirebaseApp.initializeApp(options);
//        }
//    }
//
//    @Bean
//    public FirebaseAuth firebaseAuth() {
//        return FirebaseAuth.getInstance();
//    }
//}
