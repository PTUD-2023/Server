package com.example.insurance.service;

import com.example.insurance.entity.ClaimRequest;
import com.example.insurance.entity.Document;
import com.example.insurance.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DocumentService {
    private final DocumentRepository documentRepository;

    @Autowired
    public DocumentService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    public void createDocument(Document document) {
        documentRepository.save(document);
    }
}
