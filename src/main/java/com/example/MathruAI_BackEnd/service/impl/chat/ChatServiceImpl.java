package com.example.MathruAI_BackEnd.service.impl.chat;

import com.example.MathruAI_BackEnd.dto.chat.ChatConversationResponseDto;
import com.example.MathruAI_BackEnd.dto.chat.ChatMessageResponseDto;
import com.example.MathruAI_BackEnd.dto.chat.ChatParticipantDto;
import com.example.MathruAI_BackEnd.entity.Role;
import com.example.MathruAI_BackEnd.entity.User;
import com.example.MathruAI_BackEnd.entity.chat.ChatConversation;
import com.example.MathruAI_BackEnd.entity.chat.LiveChatMessage;
import com.example.MathruAI_BackEnd.repository.UserRepository;
import com.example.MathruAI_BackEnd.repository.chat.ChatConversationRepository;
import com.example.MathruAI_BackEnd.repository.chat.ChatMessageRepository;
import com.example.MathruAI_BackEnd.service.interservice.chat.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatServiceImpl implements ChatService {

    private final UserRepository userRepository;
    private final ChatConversationRepository chatConversationRepository;
    private final ChatMessageRepository chatMessageRepository;

    private static final Set<Role> MOTHER_ROLES = Set.of(
            Role.HOPE_TO_PREGNANT_MOTHER,
            Role.PREGNANT_MOTHER,
            Role.POST_PREGNANT_MOTHER
    );

    @Override
    @Transactional(readOnly = true)
    public List<ChatConversationResponseDto> getMyConversations(Long currentUserId) {
        User currentUser = getUserOrThrow(currentUserId);

        List<ChatConversation> conversations = new ArrayList<>();
        conversations.addAll(chatConversationRepository.findByMidwifeIdOrderByLastMessageAtDesc(currentUserId));
        conversations.addAll(chatConversationRepository.findByMotherIdOrderByLastMessageAtDesc(currentUserId));

        return conversations.stream()
                .sorted(Comparator.comparing(ChatConversation::getLastMessageAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .map(conversation -> mapConversation(conversation, currentUser))
                .collect(Collectors.toList());
    }

    @Override
    public ChatConversationResponseDto getOrCreateConversation(Long currentUserId, Long targetUserId) {
        User currentUser = getUserOrThrow(currentUserId);
        User targetUser = getUserOrThrow(targetUserId);

        Pair pair = resolveValidPair(currentUser, targetUser);

        ChatConversation conversation = chatConversationRepository
                .findByMidwifeIdAndMotherId(pair.midwife().getId(), pair.mother().getId())
                .orElseGet(() -> chatConversationRepository.save(
                        ChatConversation.builder()
                                .midwife(pair.midwife())
                                .mother(pair.mother())
                                .createdAt(LocalDateTime.now())
                                .lastMessageAt(LocalDateTime.now())
                                .build()
                ));

        return mapConversation(conversation, currentUser);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatMessageResponseDto> getMessages(Long currentUserId, Long conversationId) {
        User currentUser = getUserOrThrow(currentUserId);
        ChatConversation conversation = getAuthorizedConversationOrThrow(currentUser, conversationId);

        return chatMessageRepository.findByConversationIdOrderByCreatedAtAsc(conversation.getId())
                .stream()
                .map(this::mapMessage)
                .collect(Collectors.toList());
    }

    @Override
    public ChatMessageResponseDto sendMessage(Long currentUserId, Long conversationId, String content) {
        if (content == null || content.isBlank()) {
            throw new RuntimeException("Message content is required.");
        }

        User sender = getUserOrThrow(currentUserId);
        ChatConversation conversation = getAuthorizedConversationOrThrow(sender, conversationId);

        User receiver;
        if (Objects.equals(conversation.getMidwife().getId(), sender.getId())) {
            receiver = conversation.getMother();
        } else if (Objects.equals(conversation.getMother().getId(), sender.getId())) {
            receiver = conversation.getMidwife();
        } else {
            throw new RuntimeException("You are not part of this conversation.");
        }

        validateStillAssigned(conversation.getMidwife(), conversation.getMother());

        LiveChatMessage saved = chatMessageRepository.save(
                LiveChatMessage.builder()
                        .conversation(conversation)
                        .sender(sender)
                        .receiver(receiver)
                        .content(content.trim())
                        .isRead(false)
                        .createdAt(LocalDateTime.now())
                        .build()
        );

        conversation.setLastMessageAt(saved.getCreatedAt());
        chatConversationRepository.save(conversation);

        return mapMessage(saved);
    }

    @Override
    public void markConversationAsRead(Long currentUserId, Long conversationId) {
        User currentUser = getUserOrThrow(currentUserId);
        ChatConversation conversation = getAuthorizedConversationOrThrow(currentUser, conversationId);

        List<LiveChatMessage> unreadMessages = chatMessageRepository
                .findByConversationIdAndReceiverIdAndIsReadFalse(conversation.getId(), currentUserId);

        unreadMessages.forEach(message -> message.setRead(true));
        chatMessageRepository.saveAll(unreadMessages);
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount(Long currentUserId) {
        getUserOrThrow(currentUserId);
        return chatMessageRepository.countByReceiverIdAndIsReadFalse(currentUserId);
    }

    private ChatConversation getAuthorizedConversationOrThrow(User currentUser, Long conversationId) {
        ChatConversation conversation = chatConversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found with id: " + conversationId));

        boolean belongs =
                Objects.equals(conversation.getMidwife().getId(), currentUser.getId()) ||
                        Objects.equals(conversation.getMother().getId(), currentUser.getId());

        if (!belongs) {
            throw new RuntimeException("You are not allowed to access this conversation.");
        }

        validateStillAssigned(conversation.getMidwife(), conversation.getMother());

        return conversation;
    }

    private Pair resolveValidPair(User userA, User userB) {
        boolean aMidwife = hasRole(userA, Role.MIDWIFE);
        boolean bMidwife = hasRole(userB, Role.MIDWIFE);
        boolean aMother = hasAnyRole(userA, MOTHER_ROLES);
        boolean bMother = hasAnyRole(userB, MOTHER_ROLES);

        if (aMidwife && bMother) {
            validateStillAssigned(userA, userB);
            return new Pair(userA, userB);
        }

        if (bMidwife && aMother) {
            validateStillAssigned(userB, userA);
            return new Pair(userB, userA);
        }

        throw new RuntimeException("Chat is only allowed between an assigned midwife and mother-side user.");
    }

    private void validateStillAssigned(User midwife, User mother) {
        if (!hasRole(midwife, Role.MIDWIFE)) {
            throw new RuntimeException("Selected midwife user is invalid.");
        }

        if (!hasAnyRole(mother, MOTHER_ROLES)) {
            throw new RuntimeException("Selected mother-side user is invalid.");
        }

        if (mother.getAssignedMidwife() == null ||
                !Objects.equals(mother.getAssignedMidwife().getId(), midwife.getId())) {
            throw new RuntimeException("Chat is only available for currently assigned midwife-patient pairs.");
        }
    }

    private User getUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    private boolean hasRole(User user, Role role) {
        return user.getRoles() != null && user.getRoles().contains(role);
    }

    private boolean hasAnyRole(User user, Collection<Role> roles) {
        return user.getRoles() != null && user.getRoles().stream().anyMatch(roles::contains);
    }

    private ChatConversationResponseDto mapConversation(ChatConversation conversation, User currentUser) {
        User other = Objects.equals(conversation.getMidwife().getId(), currentUser.getId())
                ? conversation.getMother()
                : conversation.getMidwife();

        List<LiveChatMessage> messages = chatMessageRepository.findByConversationIdOrderByCreatedAtAsc(conversation.getId());
        String lastMessage = messages.isEmpty() ? null : messages.get(messages.size() - 1).getContent();

        long unreadCount = chatMessageRepository.countByConversationIdAndReceiverIdAndIsReadFalse(
                conversation.getId(),
                currentUser.getId()
        );

        return ChatConversationResponseDto.builder()
                .id(conversation.getId())
                .otherUser(ChatParticipantDto.builder()
                        .id(other.getId())
                        .firstName(other.getFirstName())
                        .lastName(other.getLastName())
                        .email(other.getEmail())
                        .profileImageUrl(other.getProfileImageUrl())
                        .roles(other.getRoles())
                        .build())
                .lastMessage(lastMessage)
                .lastMessageAt(conversation.getLastMessageAt())
                .unreadCount(unreadCount)
                .build();
    }

    private ChatMessageResponseDto mapMessage(LiveChatMessage message) {
        return ChatMessageResponseDto.builder()
                .id(message.getId())
                .conversationId(message.getConversation().getId())
                .senderId(message.getSender().getId())
                .senderFirstName(message.getSender().getFirstName())
                .senderLastName(message.getSender().getLastName())
                .senderEmail(message.getSender().getEmail())
                .senderRoles(message.getSender().getRoles())
                .receiverId(message.getReceiver().getId())
                .content(message.getContent())
                .read(message.isRead())
                .createdAt(message.getCreatedAt())
                .build();
    }

    private record Pair(User midwife, User mother) {}
}