package com.example.scanlink.api;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.bson.Document;
import org.junit.jupiter.api.Test;

public class TestMongoDConnection {

    @Test
    void testRawConnection() {
        String connectionString = System.getenv("MONGODB_URI");
        if (connectionString == null || connectionString.isEmpty() || connectionString.contains("<db_username>")) {
            connectionString = System.getProperty("MONGODB_URI");
        }
        if (connectionString == null || connectionString.isEmpty() || connectionString.contains("<db_username>")) {
            connectionString = "mongodb+srv://scanlink115:Wm70OzzvPCTlAAH0@scanlink.pkullw8.mongodb.net/?appName=ScanLink";
        }

        ServerApi serverApi = ServerApi.builder()
                .version(ServerApiVersion.V1)
                .build();

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString))
                .serverApi(serverApi)
                .build();

        try (MongoClient mongoClient = MongoClients.create(settings)) {
            mongoClient.getDatabase("admin").runCommand(new Document("ping", 1));
            System.out.println("Pinged your deployment. You successfully connected to MongoDB!");
        } catch (Exception e) {
            e.printStackTrace();
            org.junit.jupiter.api.Assertions.fail("Kết nối thất bại rồi!");
        }
    }
}
