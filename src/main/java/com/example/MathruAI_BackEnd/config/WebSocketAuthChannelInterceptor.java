package com.example.MathruAI_BackEnd.config;

import com.example.MathruAI_BackEnd.security.JwtUtil;
import com.example.MathruAI_BackEnd.security.UserPrincipal;
import com.example.MathruAI_BackEnd.service.impl.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class WebSocketAuthChannelInterceptor implements ChannelInterceptor {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor == null) {
            return message;
        }

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            List<String> authHeaders = accessor.getNativeHeader("Authorization");

            if (authHeaders == null || authHeaders.isEmpty()) {
                throw new RuntimeException("Missing Authorization header for websocket connection.");
            }

            String raw = authHeaders.get(0);

            if (raw == null || !raw.startsWith("Bearer ")) {
                throw new RuntimeException("Invalid websocket Authorization header.");
            }

            String token = raw.substring(7);

            if (!jwtUtil.validateJwtToken(token)) {
                throw new RuntimeException("Invalid websocket token.");
            }

            String email = jwtUtil.getUserNameFromJwtToken(token);

            UserPrincipal userPrincipal =
                    (UserPrincipal) userDetailsService.loadUserByUsername(email);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userPrincipal,
                            null,
                            userPrincipal.getAuthorities()
                    );

            accessor.setUser(authentication);
        }

        return message;
    }
}