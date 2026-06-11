package com.grupo.learningmore.repositories;

import com.grupo.learningmore.domain.chat.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, String> {

    Page<ChatMessage> findByChatRoomIdOrderBySentAtAsc(
            String chatRoomId,
            Pageable pageable
    );
}