package com.grupo.learningmore.services;

import com.grupo.learningmore.domain.course.Resource;
import com.grupo.learningmore.repositories.ResourceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HexFormat;
import java.util.List;

import java.security.SecureRandom;
 

@Service
public class ResourceService {

    private static final Logger log = LoggerFactory.getLogger(ResourceService.class);

    private static final SecureRandom secureRandom = new SecureRandom();


    private final ResourceRepository resourceRepository;
    private final CourseService courseService;

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    public ResourceService(ResourceRepository resourceRepository, CourseService courseService) {
        this.resourceRepository = resourceRepository;
        this.courseService = courseService;
    }

    @Transactional
    public Resource uploadResource(String courseId, MultipartFile file, String uploadedBy) throws IOException {
    
    log.info("Uploading resource to course {} by user {}", courseId, uploadedBy);

    
    courseService.findById(courseId);

        if (courseId == null || courseId.isBlank()) {
            log.warn("Invalid courseId upload attempt by user {}", uploadedBy);
            throw new IllegalArgumentException("Invalid course id");
        }
        String cleanCourseId = org.springframework.util.StringUtils.cleanPath(courseId);
        if (cleanCourseId.contains("..") ||
                cleanCourseId.contains("/") ||
                cleanCourseId.contains("\\")) {
            log.warn("Path traversal attempt detected in courseId '{}' by user {}",
                    cleanCourseId, uploadedBy);
            throw new IllegalArgumentException("Invalid course id");
        }
        String safeCourseId = Paths.get(cleanCourseId).getFileName().toString();

    // Validate file
    if (file.isEmpty()) {
        log.warn("Empty file upload attempt by user {} in course {}", uploadedBy, courseId);
        throw new IllegalArgumentException("File cannot be empty");
    }

    String originalFilename = file.getOriginalFilename();

    if (originalFilename == null || originalFilename.isBlank()) {
        log.warn("Invalid filename upload attempt by user {} in course {}", uploadedBy, courseId);
        throw new IllegalArgumentException("Invalid file name");
    }

    //Against Path Traversal
    String cleanOriginalFilename = org.springframework.util.StringUtils.cleanPath(originalFilename);

    if (cleanOriginalFilename.contains("..") ||
            cleanOriginalFilename.contains("/") ||
            cleanOriginalFilename.contains("\\")) {

        log.warn("Path traversal attempt detected in filename '{}' by user {}",
                cleanOriginalFilename, uploadedBy);

        throw new IllegalArgumentException("Invalid file name");
    }

    String safeOriginalFilename = Paths.get(cleanOriginalFilename).getFileName().toString();


        Path uploadBasePath = Paths.get(uploadDir)
                .normalize()
                .toAbsolutePath();
        Path uploadPath = uploadBasePath.resolve(safeCourseId)
                .normalize()
                .toAbsolutePath();
        if (!uploadPath.startsWith(uploadBasePath)) {
            log.warn("Invalid resolved upload path detected for course {} by user {}",
                    courseId, uploadedBy);
            throw new IllegalArgumentException("Invalid course path");
        }

    Files.createDirectories(uploadPath);

     
    byte[] randomBytes = new byte[16]; 
    secureRandom.nextBytes(randomBytes);
    String randomPrefix = HexFormat.of().formatHex(randomBytes).toUpperCase();
    
    String filename = randomPrefix + "_" + safeOriginalFilename;

    Path filePath = uploadPath.resolve(filename)
            .normalize()
            .toAbsolutePath();

    if (!filePath.startsWith(uploadPath)) {
        log.warn("Invalid resolved file path detected for course {} by user {}",
                courseId, uploadedBy);

        throw new IllegalArgumentException("Invalid file path");
    }

     
    Files.write(filePath, file.getBytes());

     
    Resource resource = new Resource(
            courseId,
            filename,  
            filePath.toString(),
            file.getSize(),
            file.getContentType(),
            uploadedBy
    );

    Resource saved = resourceRepository.save(resource);

    log.info("Resource uploaded successfully: {} for course {} by user {}",
            saved.getId(), courseId, uploadedBy);

    return saved;
}

    @Transactional(readOnly = true)
    public Resource findById(String id) {

        log.info("Fetching resource {}", id);

        return resourceRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Resource not found: {}", id);
                    return new IllegalArgumentException("Resource not found");
                });
    }

    @Transactional(readOnly = true)
    public List<Resource> findByCourseId(String courseId) {

        log.info("Fetching resources for course {}", courseId);

        List<Resource> result = resourceRepository.findByCourseId(courseId);

        log.info("Found {} resources for course {}", result.size(), courseId);

        return result;
    }

    @Transactional
    public void deleteResource(String id) throws IOException {

        log.warn("Deleting resource {}", id);

        Resource resource = findById(id);

        Path baseUploadPath = Paths.get(uploadDir)
                .normalize()
                .toAbsolutePath();

        Path filePath = Paths.get(resource.getFilePath())
                .normalize()
                .toAbsolutePath();

        if (!filePath.startsWith(baseUploadPath)) {
            log.error("Invalid file path detected during delete for resource {}", id);
            throw new IllegalArgumentException("Invalid file path");
        }

        if (Files.exists(filePath)) {
            Files.delete(filePath);
            log.info("File deleted from disk for resource {}", id);
        } else {
            log.warn("File not found on disk for resource {}", id);
        }

        resourceRepository.deleteById(id);

        log.info("Resource {} deleted successfully", id);
    }
}