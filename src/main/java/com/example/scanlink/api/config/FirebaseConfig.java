package com.example.scanlink.api.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void init() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {

                InputStream serviceAccount = getClass().getClassLoader()
                        .getResourceAsStream("scanlink-firebase-adminsdk.json");

                if (serviceAccount == null) {
                    throw new RuntimeException("Không tìm thấy file scanlink-firebase-adminsdk.json trong resources!");
                }

                // Cấu hình thông tin xác thực
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();

                // Khởi tạo Firebase
                FirebaseApp.initializeApp(options);

                System.out.println("Firebase Admin SDK đã được khởi tạo thành công!");
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi khởi tạo Firebase: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Bean
    public FirebaseAuth firebaseAuth() {
        return FirebaseAuth.getInstance();
    }
}
