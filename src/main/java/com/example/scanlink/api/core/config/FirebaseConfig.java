package com.example.scanlink.api.core.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.io.FileInputStream;
import java.io.InputStream;

@Slf4j
@Configuration
public class FirebaseConfig {

    @Value("${FIREBASE_SERVICE_ACCOUNT_PATH:}")
    private String firebaseConfigPath;

    @PostConstruct
    public void initialize() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                InputStream serviceAccount = null;

                if (firebaseConfigPath != null && !firebaseConfigPath.trim().isEmpty()) {
                    try {
                        serviceAccount = new FileInputStream(firebaseConfigPath);
                        log.info(">> [Firebase] Đang tải cấu hình từ file bên ngoài: {}", firebaseConfigPath);
                    } catch (Exception e) {
                        log.warn(">> [Firebase] Không thể đọc file bên ngoài, thử chuyển sang classpath...");
                    }
                }

                if (serviceAccount == null) {
                    serviceAccount = getClass().getClassLoader()
                            .getResourceAsStream("scanlink-firebase-service-account.json");
                }

                if (serviceAccount == null) {
                    throw new RuntimeException("Không tìm thấy file cấu hình Firebase ở cả môi trường ngoài và tài nguyên cục bộ!");
                }

                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();

                FirebaseApp.initializeApp(options);
                log.info(">> [Firebase] Kết nối thành công bằng Service Account!");
            }
        } catch (Exception e) {
            log.error(">> [Firebase] Lỗi khởi tạo cực kỳ nghiêm trọng: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Bean
    public FirebaseAuth firebaseAuth() {
        return FirebaseAuth.getInstance();
    }
}