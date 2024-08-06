package com.ssh.dartserver.domain.chat.presentation;

import com.ssh.dartserver.domain.chat.application.ActiveUserStore;
import com.ssh.dartserver.global.security.jwt.JwtToken;
import com.ssh.dartserver.global.security.jwt.JwtTokenProvider;
import com.ssh.dartserver.global.config.properties.JwtProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatPreHandler implements ChannelInterceptor {
    private final JwtTokenProvider jwtTokenProvider;
    private final ActiveUserStore activeUserStore;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);
        String authorizationHeader = String.valueOf(headerAccessor.getNativeHeader(JwtProperty.HEADER_STRING));

        if (StompCommand.CONNECT.equals(headerAccessor.getCommand())) {
            if (authorizationHeader == null || authorizationHeader.equals("null")) {
                throw new MessageDeliveryException("인증 토큰이 없습니다.");
            }
            JwtToken token = getJwtToken(authorizationHeader);

            token.validateToken();
            String username = token.getUsername();
            activeUserStore.storeSession(headerAccessor.getSessionId(), username);
        }
        if (StompCommand.DISCONNECT.equals(headerAccessor.getCommand())) {
            activeUserStore.removeSession(headerAccessor.getSessionId());
        }
        return message;
    }

    private JwtToken getJwtToken(final String authorizationHeader) {
        return jwtTokenProvider.decode(authorizationHeader.replaceFirst(JwtProperty.TOKEN_PREFIX, "")
                .replaceAll("[\\[\\]]", ""));
    }
}
