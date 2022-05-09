package com.swp.backend.configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.FileInputStream;
import java.io.IOException;


@Configuration
public class FirebaseConfig {
    @Primary
    @Bean
    public FirebaseApp getFirebaseApp() throws IOException {

        if (FirebaseApp.getApps().isEmpty()) {
            Resource resource = new ClassPathResource("Firebase-adminsdk-9-privatekey.json");
            FileInputStream refreshToken = new FileInputStream(resource.getFile());

            FirebaseOptions options = FirebaseOptions.builder().setCredentials(GoogleCredentials.fromStream(refreshToken)).build();
            FirebaseApp.initializeApp(options);
        }
        return FirebaseApp.getInstance();
    }

    @Bean
    public FirebaseAuth getAuth() throws IOException {
        return FirebaseAuth.getInstance(getFirebaseApp());
    }
}
