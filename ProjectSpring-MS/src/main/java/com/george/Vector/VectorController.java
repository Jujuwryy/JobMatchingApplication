package com.george.Vector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VectorController{

    @Autowired
    private CreateEmbeddings jobPostService;

    @GetMapping("/generate-embeddings")
    public String generateEmbeddings() {
        jobPostService.createEmbeddings();
        return "Embeddings generated and saved successfully!";
    }
}