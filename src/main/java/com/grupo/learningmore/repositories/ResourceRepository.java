package com.grupo.learningmore.repositories;

import com.grupo.learningmore.domain.course.Resource;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
 

public interface ResourceRepository extends JpaRepository<Resource, String> {
    List<Resource> findByCourseId(String courseId);
}
