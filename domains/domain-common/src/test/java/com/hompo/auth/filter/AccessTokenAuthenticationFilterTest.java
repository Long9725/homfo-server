package com.hompo.auth.filter;

import com.hompo.auth.dto.JwtSecretDto;
import com.hompo.auth.infra.util.JwtUtil;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccessTokenAuthenticationFilterTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private JwtSecretDto jwtSecretDto;
    private List<String> accessTokenWhiteList;
    private List<String> refreshTokenBlackList;

    private AccessTokenAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        filterChain = mock(FilterChain.class);

        jwtSecretDto = new JwtSecretDto("secret", 3600000L);
        accessTokenWhiteList = List.of("/public", "/login");
        refreshTokenBlackList = List.of("/refresh");

        // 필터 인스턴스를 직접 생성하고 초기화합니다.
        filter = new AccessTokenAuthenticationFilter(jwtSecretDto, accessTokenWhiteList, refreshTokenBlackList);

        SecurityContextHolder.clearContext(); // 보안 컨텍스트 초기화
    }

    @Test
    void whenRequestPathIsInRefreshTokenBlackListAndTokenIsValid_thenShouldAuthenticate() throws ServletException, IOException {
        try (MockedStatic<JwtUtil> jwtUtil = Mockito.mockStatic(JwtUtil.class)) {
            // given
            when(request.getRequestURI()).thenReturn("/refresh");
            when(request.getHeader("Authorization")).thenReturn("Bearer validToken");

            jwtUtil.when(() -> JwtUtil.verifyToken(anyString(), any())).thenReturn(true);

            // when
            filter.doFilterInternal(request, response, filterChain);

            // then
            assertNotNull(SecurityContextHolder.getContext().getAuthentication(), "요청 경로가 refreshToken 미허용 경로에 포함되면, 인증 정보가 SecurityContext에 설정되어야 합니다.");
        }
    }

    @Test
    void whenRequestPathIsNotInAccessTokenWhiteListAndTokenIsNotExpired_thenShouldAuthenticate() throws ServletException, IOException {
        try (MockedStatic<JwtUtil> jwtUtil = Mockito.mockStatic(JwtUtil.class)) {
            // given
            when(request.getRequestURI()).thenReturn("/api/user");
            when(request.getHeader("Authorization")).thenReturn("Bearer validToken");

            jwtUtil.when(() -> JwtUtil.verifyTokenNotExpired(anyString(), any())).thenReturn(true);

            // when
            filter.doFilterInternal(request, response, filterChain);

            // then
            assertNotNull(SecurityContextHolder.getContext().getAuthentication(), "요청 경로가 accessToken 허용 경로에 포함되지 않는다면, 인증 정보가 SecurityContext에 설정되어야 합니다.");
        }
    }

    @Test
    void whenRequestPathIsInAccessTokenWhiteListAndTokenIsNotExpired_thenShouldAuthenticate() throws ServletException, IOException {
        try (MockedStatic<JwtUtil> jwtUtil = Mockito.mockStatic(JwtUtil.class)) {
            // given
            when(request.getRequestURI()).thenReturn("/public");
            when(request.getHeader("Authorization")).thenReturn("Bearer validToken");

            jwtUtil.when(() -> JwtUtil.verifyTokenNotExpired(anyString(), any())).thenReturn(true);

            // when
            filter.doFilterInternal(request, response, filterChain);

            // then
            assertNull(SecurityContextHolder.getContext().getAuthentication(), "요청 경로가 accessToken 허용 경로에 포함되면, 인증 정보가 SecurityContext에 설정되지 않아야 합니다.");
        }
    }

    @Test
    void whenTokenIsMissing_thenShouldNotAuthenticate() throws ServletException, IOException {
        // given
        when(request.getRequestURI()).thenReturn("/api/user");

        // when
        filter.doFilterInternal(request, response, filterChain);

        // then
        assertNull(SecurityContextHolder.getContext().getAuthentication(), "Token이 없으면 인증 정보가 SecurityContext에 설정되어서는 안 됩니다.");
    }

    @Test
    void whenTokenIsInvalid_thenShouldNotAuthenticate() throws ServletException, IOException {
        try (MockedStatic<JwtUtil> jwtUtil = Mockito.mockStatic(JwtUtil.class)) {
            // given
            when(request.getRequestURI()).thenReturn("/api/user");
            when(request.getHeader("Authorization")).thenReturn("Bearer invalidToken");

            jwtUtil.when(() -> JwtUtil.verifyToken(anyString(), any())).thenReturn(false);

            // when
            filter.doFilterInternal(request, response, filterChain);

            // then
            assertNull(SecurityContextHolder.getContext().getAuthentication(), "잘못된 Token이면 인증 정보가 SecurityContext에 설정되어서는 안 됩니다.");
        }
    }
}
