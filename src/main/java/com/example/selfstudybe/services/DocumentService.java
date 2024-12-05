package com.example.selfstudybe.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.selfstudybe.dtos.Document.CreateDocumentDto;
import com.example.selfstudybe.dtos.Document.DocumentDto;
import com.example.selfstudybe.exception.CustomBadRequestException;
import com.example.selfstudybe.models.Document;
import com.example.selfstudybe.models.Subject;
import com.example.selfstudybe.models.User;
import com.example.selfstudybe.repositories.DocumentRepository;
import com.example.selfstudybe.repositories.SubjectRepository;
import com.example.selfstudybe.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.hibernate.Hibernate;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@AllArgsConstructor
public class DocumentService {
    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;
    private final Cloudinary cloudinary;
    private final ModelMapper modelMapper;

    public DocumentDto createNewDocument(CreateDocumentDto request) {
        User user = userRepository.findById(request.getUserId()).orElseThrow(
                () -> new RuntimeException("Can't find user with id " + request.getUserId())
        );

        Subject subject = subjectRepository.findById(request.getSubjectId()).orElseThrow(
                () -> new RuntimeException("Can't find subject with id " + request.getSubjectId())
        );

        // Check duplicated
        if(documentRepository.existsByNameAndSubject(request.getName(), subject))
            throw new CustomBadRequestException("Document already exists in the subject");

        Document document = new Document();
        document.setName(request.getName());
        document.setSubject(subject);
        document.setCreator(user);
        document.setCreatedAt(LocalDateTime.now());

        Document savedDocument = documentRepository.save(document);

        return modelMapper.map(savedDocument, DocumentDto.class);
    }

    public String uploadDocument(UUID documentId, MultipartFile multipartFile) throws IOException {
        Document document = documentRepository.findById(documentId).orElseThrow(
                ()-> new CustomBadRequestException("Can't find document with id " + documentId)
        );

        //Remove old file
        if(document.getDocLink() != null)
        {
            String publicId = document.getId() + "." + document.getExtension();
            cloudinary.uploader().destroy(publicId, ObjectUtils.asMap( "resource_type" ,"raw"));
        }

        // Get file extension
        String originalFilename = multipartFile.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains("."))
            extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        else
            throw new CustomBadRequestException("Unknown file extension");

        Hibernate.initialize(document.getSubject());
        String folderPath = "Document/" + document.getSubject().getId().toString();

        Map params = ObjectUtils.asMap(
                "resource_type", "raw",
                "public_id", document.getId().toString() + "." + extension,
                "asset_folder", folderPath,
                "overwrite", true
        );

        Map result = cloudinary.uploader().upload(multipartFile.getBytes(),params);
        String url = result.get("secure_url").toString();

        document.setDocLink(url);
        document.setExtension(extension);
        documentRepository.save(document);

        return url;
    }

    public List<DocumentDto> getAllDocumentsForSubject(UUID subjectId) {
        Subject subject = subjectRepository.findById(subjectId).orElseThrow(
                () -> new RuntimeException("Can't find subject with id " + subjectId)
        );

        List<Document> documents = documentRepository.findBySubject(subject);
        return modelMapper.map(documents, new TypeToken<List<DocumentDto>>() {}.getType());
    }

    public DocumentDto updateDocument(UUID id, String name) {
        Document document = documentRepository.findById(id).orElseThrow(
                ()-> new CustomBadRequestException("Can't find document with id "+ id)
        );

        // Check duplicated
        Hibernate.initialize(document.getSubject());
        if(documentRepository.existsByNameAndSubject(name, document.getSubject()))
            throw new CustomBadRequestException("Document already exists in the subject");

        document.setName(name);
        documentRepository.save(document);
        return modelMapper.map(document, DocumentDto.class);
    }

    public void deleteDocument(UUID id) throws IOException {
        Document document = documentRepository.findById(id).orElseThrow(
                () -> new CustomBadRequestException("Can't find document with id " + id)
        );

        String publicId = id + "." + document.getExtension();
        cloudinary.uploader().destroy(publicId, ObjectUtils.asMap( "resource_type" ,"raw"));

        documentRepository.delete(document);
    }
}
