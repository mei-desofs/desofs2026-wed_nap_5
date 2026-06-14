package com.grupo.learningmore.services;

import com.grupo.learningmore.domain.course.Course;
import com.grupo.learningmore.repositories.CourseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
 

@Service
public class CourseService {

    private static final Logger log = LoggerFactory.getLogger(CourseService.class);

    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @Transactional
    public Course createCourse(String code, String name, String description, String createdBy) {

        log.info("Creating course with code {} by user {}", code, createdBy);

        if (courseRepository.existsByCode(code)) {
            log.warn("Attempt to create duplicate course with code {}", code);
            throw new IllegalArgumentException("Course with code " + code + " already exists");
        }

        Course course = new Course(code, name, description, createdBy);
        Course saved = courseRepository.save(course);

        log.info("Course created successfully with id {} and code {}", saved.getId(), code);

        return saved;
    }

    @Transactional(readOnly = true)
    public Course findById(String id) {

        log.info("Fetching course by id {}", id);

        return courseRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Course not found by id {}", id);
                    return new IllegalArgumentException("Course not found");
                });
    }

    @Transactional(readOnly = true)
    public Course findByCode(String code) {

        log.info("Fetching course by code {}", code);

        return courseRepository.findByCode(code)
                .orElseThrow(() -> {
                    log.warn("Course not found by code {}", code);
                    return new IllegalArgumentException("Course not found");
                });
    }

    @Transactional(readOnly = true)
    public List<Course> findAll() {

        log.info("Fetching all courses");

        List<Course> courses = courseRepository.findAll();

        log.info("Found {} courses", courses.size());

        return courses;
    }

    @Transactional
    public Course updateCourse(String id, String name, String description) {

        log.info("Updating course {}", id);

        Course course = findById(id);

        course.setName(name);
        course.setDescription(description);
        course.setUpdatedAt(LocalDateTime.now());

        Course saved = courseRepository.save(course);

        log.info("Course {} updated successfully", id);

        return saved;
    }

    @Transactional
    public void deleteCourse(String id) {

        log.warn("Deleting course by id {}", id);

        courseRepository.deleteById(id);

        log.info("Course {} deleted successfully", id);
    }

    @Transactional
    public void deleteCourseByCode(String code) {

        log.warn("Deleting course by code {}", code);

        Course course = findByCode(code);

        courseRepository.deleteById(course.getId());

        log.info("Course with code {} deleted successfully", code);
    }
}