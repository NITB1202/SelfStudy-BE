package com.example.selfstudybe.controllers;

import com.example.selfstudybe.dtos.Document.CreateDocumentDto;
import com.example.selfstudybe.dtos.Document.DocumentDto;
import com.example.selfstudybe.services.DocumentService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("v1/document")
public class DocumentController {
    private final DocumentService documentService;

    @PostMapping
    @Operation(summary = "Create a new document")
    public ResponseEntity<DocumentDto> createNewDocument(@Valid @RequestBody CreateDocumentDto document) {
        return ResponseEntity.ok(documentService.createNewDocument(document));
    }

    @PostMapping(value ="upload/{id}", consumes = "multipart/form-data")
    @Operation(summary = "Upload document file")
    public ResponseEntity<String> uploadDocument(@PathVariable UUID id, @RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(documentService.uploadDocument(id,file));
    }

    @GetMapping
    @Operation(summary = "Get all documents for the subject")
    public ResponseEntity<List<DocumentDto>> getAllDocumentsByPlan(@RequestParam UUID subjectId) {
        return ResponseEntity.ok(documentService.getAllDocumentsForSubject(subjectId));
    }

    @PatchMapping
    @Operation(summary = "Update the document's name")
    public ResponseEntity<DocumentDto> updateDocument(@RequestParam UUID id, @RequestParam String name) {
        return ResponseEntity.ok(documentService.updateDocument(id, name));
    }

    @DeleteMapping
    @Operation(summary = "Delete the document")
    public ResponseEntity<String> deleteDocument(@RequestParam UUID id) throws IOException {
        documentService.deleteDocument(id);
        return ResponseEntity.ok("Delete successfully");
    }
}
