package com.example.scanlink.api.mongodbTest;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class MongoConnectionTest {

    @Autowired
    private MongoClient mongoClient;

    @Test
    void testMongoConnection() {
        // Ping tới MongoDB
        MongoDatabase database = mongoClient.getDatabase("scanlink");
        Document result = database.runCommand(new Document("ping", 1));

        System.out.println("✅ MongoDB kết nối thành công: " + result.toJson());
        assertNotNull(result);
    }
}
