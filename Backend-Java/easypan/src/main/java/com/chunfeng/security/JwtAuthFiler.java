package com.chunfeng.security;

import com.chunfeng.componet.RedisComponent;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * @ClassName JwtAuthFiler
 * @Author chunfeng
 * @Description
 * @date 2025/12/18 16:56
 * @Version 1.0
 */
public class JwtAuthFiler extends OncePerRequestFilter {

    private final RedisComponent redisComponent;
    private final JwtUtil jwtUtil;

    public JwtAuthFiler (RedisComponent redisComponent, JwtUtil jwtUtil){
        this.jwtUtil = jwtUtil;
        this.redisComponent =  redisComponent;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String auth = request.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = auth.substring(7);

        if (redisComponent.isTokenBlacklisted(accessToken)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        if (SecurityContextHolder.getContext().getAuthentication() == null
             && jwtUtil.validateAccessToken(accessToken)) {
             Long userId = jwtUtil.getUserIdFromAccessToken(accessToken);
            UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userId, null, null);

            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
        filterChain.doFilter(request, response);
    }
}
