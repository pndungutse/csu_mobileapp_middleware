package com.dsu.hope_bank_app_middleware.security;

import com.dsu.hope_bank_app_middleware.security.entity.User;
import com.dsu.hope_bank_app_middleware.security.repository.UserRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private JwtTokenProvider jwtTokenProvider;

    private UserDetailsService userDetailsService;

    private UserRepository userRepository;
    private TokenBlacklistService tokenBlacklistService;

    public JwtAuthenticationFilter(
            JwtTokenProvider jwtTokenProvider,
            UserDetailsService userDetailsService,
            UserRepository userRepository,
            TokenBlacklistService tokenBlacklistService
    ) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // get JWT token from http request
        String token = getTokenFromRequest(request);

        // validate token
        if(StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)){
            if (tokenBlacklistService.isBlacklisted(token)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"message\":\"Token is invalidated. Please login again.\"}");
                return;
            }

            // get username from token
            String username = jwtTokenProvider.getUsername(token);
            User user = userRepository.findByUsername(username).orElse(null);

            // Block access to non-auth resources until the user changes the initial password.
            if (user != null && user.isMustChangePassword() && !isPasswordChangeAllowedPath(request.getRequestURI())) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json");
                response.getWriter().write("{\"message\":\"Password change required before accessing this resource\"}");
                return;
            }

            // load the user associated with token
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );

            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        }

        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request){

        String bearerToken = request.getHeader("Authorization");

        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")){
            return bearerToken.substring(7, bearerToken.length());
        }

        return null;
    }

    private boolean isPasswordChangeAllowedPath(String path) {
        return "/api/auth/login".equals(path)
                || "/api/auth/register".equals(path)
                || "/api/auth/change-password".equals(path);
    }


}
