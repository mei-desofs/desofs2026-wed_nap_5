package com.grupo.learningmore.services;

import com.grupo.learningmore.domain.course.Course;
import com.grupo.learningmore.repositories.CourseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CourseService {

    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @Transactional
    public Course createCourse(String code, String name, String description, UUID createdBy) {
        if (courseRepository.existsByCode(code)) {
            throw new IllegalArgumentException("Course with code " + code + " already exists");
        }

        Course course = new Course(code, name, description, createdBy);
        return courseRepository.save(course);
    }

    @Transactional(readOnly = true)
    public Course findById(UUID id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));
    }

    @Transactional(readOnly = true)
    public Course findByCode(String code) {
        return courseRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));
    }

    @Transactional(readOnly = true)
    public List<Course> findAll() {
        return courseRepository.findAll();
    }

    @Transactional
    public Course updateCourse(UUID id, String name, String description) {
        Course course = findById(id);
        course.setName(name);
        course.setDescription(description);
        course.setUpdatedAt(LocalDateTime.now());
        return courseRepository.save(course);
    }

    @Transactional
    public void deleteCourse(UUID id) {
        courseRepository.deleteById(id);
    }

    @Transactional
    public void deleteCourseByCode(String code) {
        Course course = findByCode(code);
        courseRepository.deleteById(course.getId());
    }
}
