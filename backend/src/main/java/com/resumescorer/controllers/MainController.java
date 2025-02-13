package com.resumescorer.controllers;

import java.io.IOException;
import java.io.InputStream;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;


@Controller
public class MainController {

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("message", "Upload your resume for scoring!");
        return "index";
    }

    @PostMapping("/upload")
    public String uploadResume(@RequestParam("file") MultipartFile file, Model model) {
        if (file.isEmpty()) {
            model.addAttribute("message", "Please upload a valid file.");
            return "index"; //Return to form if no file was uploaded
        }

        try {
            //Use Apache Tika to extract text
            Tika tika = new Tika();
            InputStream inputStream = file.getInputStream();
            String extractedText = tika.parseToString(inputStream);
            inputStream.close();

            //Send extracted text to UI
            model.addAttribute("message","Resume uploaded successfully!");
            model.addAttribute("extractedText", extractedText);

        } catch (IOException | TikaException e) {
            model.addAttribute("message", "Error uploading resume: " + e.getMessage());
        }
        return "index"; //Reload page with extracted text
    }
    
}
