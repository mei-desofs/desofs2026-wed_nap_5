package com.grupo.learningmore.repositories;

import com.grupo.learningmore.domain.chat.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
 

public interface ChatRoomRepository extends JpaRepository<ChatRoom, String> {
    @Query("""
            select distinct c
            from ChatRoom c
            join Enrollment e on e.courseId = c.course.id
            where e.userId = :userId
            """)
    List<ChatRoom> findChatsByUserId(@Param("userId") String userId);

    List<ChatRoom> findByCourseId(String courseId);
}