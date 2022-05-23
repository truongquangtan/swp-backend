package com.swp.backend.service;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class FirebaseStoreService {
    public Bucket bucket;

    public FirebaseStoreService(Bucket bucket) {
        this.bucket = bucket;
    }

    public String uploadFile(MultipartFile multipartFile) throws IOException {
        String fileName = UUID.randomUUID().toString();
        BlobId blobId = BlobId.of(bucket.getName(), fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(multipartFile.getContentType()).build();
        Blob blob = bucket.getStorage().create(blobInfo, multipartFile.getInputStream().readAllBytes());
        return  "https://firebasestorage.googleapis.com/v0/b/" + bucket.getName() + "/o/" + fileName + "?alt=media";
    }
}
