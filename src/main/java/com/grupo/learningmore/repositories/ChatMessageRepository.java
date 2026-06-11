package com.grupo.learningmore.repositories;

import com.grupo.learningmore.domain.chat.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, String> {

    List<ChatMessage> findByChatRoomIdOrderBySentAtAsc(String chatRoomId);
}