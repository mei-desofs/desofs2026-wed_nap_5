package com.grupo.learningmore.repositories;

import com.grupo.learningmore.domain.course.Resource;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ResourceRepository extends JpaRepository<Resource, UUID> {
    List<Resource> findByCourseId(UUID courseId);
}
