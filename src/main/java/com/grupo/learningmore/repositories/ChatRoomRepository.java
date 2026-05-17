package com.grupo.learningmore.repositories;

import com.grupo.learningmore.domain.chat.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, UUID> {
}