package com.grupo.learningmore.repositories;

import com.grupo.learningmore.domain.chat.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, UUID> {

    Page<ChatMessage> findByChatRoomIdOrderBySentAtAsc(
            UUID chatRoomId,
            Pageable pageable
    );
}