package com.example.scanlink.api.cloudinaryTest;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

import com.example.scanlink.api.features.sharefile.service.interfaces.CloudinaryService;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {
        "scanlink.storage.URL_CLOUDINARY=scanlink/uploads"})
public class CloudinaryTest {
    @Autowired
    private Cloudinary cloudinary;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Test
    void testCloudinaryConnection() throws Exception {
        Map result = cloudinary.api().ping(ObjectUtils.emptyMap());
        System.out.println("✅ Cloudinary kết nối thành công: " + result);
        assertEquals("ok", result.get("status").toString());
    }
    @Test
    void testUploadCloudinary() throws Exception {
        byte[] fileContent = "Hello World from ScanLink API Test".getBytes();
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "test-document.txt",
                "text/plain",
                fileContent
        );

        // Upload
        Map uploadResult = cloudinaryService.uploadFile(mockFile);
        assertNotNull(uploadResult, "Upload result should not be null");
        String publicId = (String) uploadResult.get("public_id");
        assertNotNull(publicId, "public_id should not be null");
        System.out.println("Uploaded public_id: " + publicId);

        // Clean up
        cloudinaryService.delete(publicId);
    }

}
