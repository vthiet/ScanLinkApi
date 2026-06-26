package com.example.scanlink.api.features.sharefile.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.security.Permission;

@Getter
@Setter
@Data
@Document(collection  = "DOCUMENT_PERMiSSION")
public class DocumentPermission {
    private String user_uid;
    private Permission role;
}
