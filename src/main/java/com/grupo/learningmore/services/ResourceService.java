package com.grupo.learningmore.services;

import com.grupo.learningmore.domain.course.Resource;
import com.grupo.learningmore.repositories.ResourceRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class ResourceService {

    private final ResourceRepository resourceRepository;
    private final CourseService courseService;

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    public ResourceService(ResourceRepository resourceRepository, CourseService courseService) {
        this.resourceRepository = resourceRepository;
        this.courseService = courseService;
    }

    @Transactional
    public Resource uploadResource(UUID courseId, MultipartFile file, UUID uploadedBy) throws IOException {
        // Verify course exists
        courseService.findById(courseId);

        // Validate file
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }

        // Create upload directory if it doesn't exist
        Path uploadPath = Paths.get(uploadDir, courseId.toString());
        Files.createDirectories(uploadPath);

        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String filename = UUID.randomUUID() + "_" + originalFilename;
        Path filePath = uploadPath.resolve(filename);

        // Save file to disk
        Files.write(filePath, file.getBytes());

        // Create and save resource entity
        Resource resource = new Resource(
                courseId,
                originalFilename,
                filePath.toString(),
                file.getSize(),
                file.getContentType(),
                uploadedBy
        );

        return resourceRepository.save(resource);
    }

    @Transactional(readOnly = true)
    public Resource findById(UUID id) {
        return resourceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Resource not found"));
    }

    @Transactional(readOnly = true)
    public List<Resource> findByCourseId(UUID courseId) {
        return resourceRepository.findByCourseId(courseId);
    }

    @Transactional
    public void deleteResource(UUID id) throws IOException {
        Resource resource = findById(id);
        
        // Delete file from disk
        Path filePath = Paths.get(resource.getFilePath());
        if (Files.exists(filePath)) {
            Files.delete(filePath);
        }

        // Delete entity
        resourceRepository.deleteById(id);
    }
}
