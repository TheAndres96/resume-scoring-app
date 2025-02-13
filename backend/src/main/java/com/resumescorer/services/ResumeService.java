package com.resumescorer.services;

import java.io.IOException;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ResumeService {

    private final Tika tika= new Tika(); //Apache Tika Instance

    public String extractTextFromResume(MultipartFile file) {
        try {
            String extractedText = tika.parseToString(file.getInputStream());
            System.out.println("Extracted Text: " + extractedText);
            return extractedText; 
        }
        catch (IOException | TikaException e) { //Catch both exceptions
            throw new RuntimeException("Error processing resume file", e);
        }
    }
}

