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

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new IllegalArgumentException("Invalid file name");
        }
        if (originalFilename.contains("..") || originalFilename.contains("/") || originalFilename.contains("\\")) {
            throw new IllegalArgumentException("Invalid file name");
        }
        String safeOriginalFilename = Paths.get(originalFilename).getFileName().toString();


        // Create upload directory if it doesn't exist
        Path uploadPath = Paths.get(uploadDir, courseId.toString()).normalize().toAbsolutePath();
        Files.createDirectories(uploadPath);

        // Generate unique filename
        String filename = UUID.randomUUID() + "_" + safeOriginalFilename;
        Path filePath = uploadPath.resolve(filename).normalize().toAbsolutePath();
        if (!filePath.startsWith(uploadPath)) {
            throw new IllegalArgumentException("Invalid file path");
        }

        // Save file to disk
        Files.write(filePath, file.getBytes());

        // Create and save resource entity
        Resource resource = new Resource(
                courseId,
                safeOriginalFilename,
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
        Path baseUploadPath = Paths.get(uploadDir).normalize().toAbsolutePath();
        Path filePath = Paths.get(resource.getFilePath()).normalize().toAbsolutePath();
        if (!filePath.startsWith(baseUploadPath)) {
            throw new IllegalArgumentException("Invalid file path");
        }

        if (Files.exists(filePath)) {
            Files.delete(filePath);
        }

        // Delete entity
        resourceRepository.deleteById(id);
    }
}
