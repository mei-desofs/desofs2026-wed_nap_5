package com.grupo.learningmore.repositories;

import com.grupo.learningmore.domain.chat.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, UUID> {
    @Query("""
            select distinct c
            from ChatRoom c
            join Enrollment e on e.courseId = c.course.id
            where e.userId = :userId
            """)
    List<ChatRoom> findChatsByUserId(@Param("userId") UUID userId);

    List<ChatRoom> findByCourseId(UUID courseId);
}