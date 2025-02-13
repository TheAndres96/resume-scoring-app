package com.resumescorer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;

public class TikaTest {
    
    public static void main(String[] args) {
        try {
            File file = new File("src/test/resources/AndresMerlosCSResume.pdf");

            // Force Apache Tika to use PDFParser
            PDFParser parser = new PDFParser();
            BodyContentHandler handler = new BodyContentHandler();
            Metadata metadata = new Metadata();
            FileInputStream inputstream = new FileInputStream(file);
            ParseContext context = new ParseContext();

            parser.parse(inputstream, handler, metadata, context);

            // Print extracted content
            System.out.println("Extracted Text: \n" + handler.toString());

            // Print metadata (helps identify file type issues)
            System.out.println("\nMetadata:");
            for (String name : metadata.names()) {
                System.out.println(name + ": " + metadata.get(name));
            }

        } catch (IOException | TikaException | SAXException e) {
            e.printStackTrace();
        }
    }
}