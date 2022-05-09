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
    //Init bean firebase application
    public FirebaseApp getFirebaseApp() throws IOException {
        if (FirebaseApp.getApps().isEmpty()) {
            //Load credentials from file
            GoogleCredentials googleCredentials = GoogleCredentials.fromStream(new ClassPathResource("Firebase-adminsdk-9-privatekey.json").getInputStream());
            //Set option include firebase authentication
            FirebaseOptions options = FirebaseOptions.builder().setCredentials(googleCredentials).build();
            FirebaseApp.initializeApp(options);
        }
        return FirebaseApp.getInstance();
    }

    @Bean
    //Init bean firebase authentication
    public FirebaseAuth getAuth() throws IOException {
        return FirebaseAuth.getInstance(getFirebaseApp());
    }
}
