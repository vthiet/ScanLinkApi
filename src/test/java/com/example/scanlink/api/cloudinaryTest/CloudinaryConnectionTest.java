package com.example.scanlink.api.cloudinaryTest;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class CloudinaryConnectionTest {
    @Autowired
    private Cloudinary cloudinary;

    @Test
    void testCloudinaryConnection() throws Exception {
        Map result = cloudinary.api().ping(ObjectUtils.emptyMap());
        System.out.println("✅ Cloudinary kết nối thành công: " + result);
        assertEquals("ok", result.get("status").toString());
    }
}
