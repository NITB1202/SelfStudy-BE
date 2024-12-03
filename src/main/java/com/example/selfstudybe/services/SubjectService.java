package com.example.selfstudybe.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.selfstudybe.dtos.Subject.CreateUserSubjectDto;
import com.example.selfstudybe.dtos.Subject.SubjectDto;
import com.example.selfstudybe.dtos.Subject.UpdateSubjectDto;
import com.example.selfstudybe.exception.CustomBadRequestException;
import com.example.selfstudybe.exception.CustomNotFoundException;
import com.example.selfstudybe.models.Document;
import com.example.selfstudybe.models.Subject;
import com.example.selfstudybe.models.User;
import com.example.selfstudybe.repositories.DocumentRepository;
import com.example.selfstudybe.repositories.SubjectRepository;
import com.example.selfstudybe.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@AllArgsConstructor
public class SubjectService {
    private final SubjectRepository subjectRepository;
    private final UserRepository userRepository;
    private final DocumentRepository documentRepository;
    private final Cloudinary cloudinary;

    public SubjectDto createUserSubject(CreateUserSubjectDto request) {
        User user = userRepository.findById(request.getUserId()).orElseThrow(
                ()-> new CustomNotFoundException("Can't find user with id " + request.getUserId()));

        // Check duplicated
        if(subjectRepository.existsByNameAndCreatorAndIsPersonal(request.getName(), user,true))
            throw new CustomBadRequestException("Subject already exists");

        Subject subject = new ModelMapper().map(request, Subject.class);
        subject.setCreator(user);
        subject.setIsPersonal(true);

        Subject savedSubject = subjectRepository.save(subject);

        return new ModelMapper().map(savedSubject, SubjectDto.class);
    }

    public List<SubjectDto> getAllUserSubjects(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(
                ()-> new CustomNotFoundException("Can't find user with id " + userId));

         List<Subject> subjects = subjectRepository.findByCreatorAndIsPersonal(user, true);

         return new ModelMapper().map(subjects, new TypeToken<List<SubjectDto>>() {}.getType());
    }

    public SubjectDto updateSubject(UpdateSubjectDto request) {
        Subject subject = subjectRepository.findById(request.getId()).orElseThrow(
                ()-> new CustomNotFoundException("Can't find subject with id " + request.getId())
        );

        // Check duplicated
        if(request.getName() != null && subjectRepository.existsByNameAndCreatorAndIsPersonal(request.getName(), subject.getCreator(), subject.getIsPersonal()))
            throw new CustomBadRequestException("Subject already exists");

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setSkipNullEnabled(true);

        modelMapper.map(request, subject);

        subjectRepository.save(subject);

        return modelMapper.map(subject, SubjectDto.class);
    }

    public String uploadSubjectImage(UUID subjectId, MultipartFile file) throws IOException {
        Subject subject = subjectRepository.findById(subjectId).orElseThrow(
                ()-> new CustomNotFoundException("Can't find subject with id " + subjectId));

        Map params = ObjectUtils.asMap(
                "resource_type", "auto",
                "public_id", subjectId.toString(),
                "asset_folder", "Subject",
                "overwrite", true
        );

        Map result = cloudinary.uploader().upload(file.getBytes(),params);
        String url = result.get("secure_url").toString();

        subject.setImageLink(url);
        subjectRepository.save(subject);

        return url;
    }

    @Transactional
    public void deleteSubject(UUID id) throws Exception {
        Subject subject = subjectRepository.findById(id).orElseThrow(
                ()-> new CustomNotFoundException("Can't find subject with id " + id)
        );

        // Delete image
        if(subject.getImageLink() != null) {
            cloudinary.uploader().destroy(id.toString(), ObjectUtils.emptyMap());
        }

        // Delete all documents in subject
        if(documentRepository.existsBySubjectId(id))
        {
            List<Document> documents = documentRepository.findBySubject(subject);
            for(Document document : documents)
            {
                String publicId = document.getId() + "." + document.getExtension();
                cloudinary.uploader().destroy(publicId, ObjectUtils.asMap("resource_type", "raw"));
            }
            String folderPath = "Document/" + id;
            cloudinary.api().deleteFolder(folderPath, ObjectUtils.emptyMap());
        }

        subjectRepository.delete(subject);
    }
}
