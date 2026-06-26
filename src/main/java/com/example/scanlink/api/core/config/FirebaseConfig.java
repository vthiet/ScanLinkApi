package com.example.scanlink.api.core.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.InputStream;

@Slf4j
@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void initialize() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {

                InputStream serviceAccount = getClass().getClassLoader()
                        .getResourceAsStream("scanlink-firebase-service-account.json");

                if (serviceAccount == null) {
                    throw new RuntimeException("Không tìm thấy file scanlink-firebase-service-account.json trong resources!");
                }

                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();

                FirebaseApp.initializeApp(options);

                System.out.println(">> [Firebase] Kết nối thành công bằng Service Account!");
            }
        } catch (Exception e) {
            System.err.println(">> [Firebase] Lỗi khởi tạo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Bean
    public FirebaseAuth firebaseAuth() {
        return FirebaseAuth.getInstance();
    }
}
