package com.example.insurance.component;

import com.example.insurance.service.JwtService;
import com.example.insurance.service.UserAccountService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Date;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserAccountService userAccountService;
//    private final RequestMatcher uriMatcher = new AntPathRequestMatcher("/v1/insurance-plan/**");
    private final RequestMatcher uriMatcher = new OrRequestMatcher(
            new AntPathRequestMatcher("/v1/authenticate/**"),
            new AntPathRequestMatcher("/v1/insurance-plan/**"),
            new AntPathRequestMatcher("/v1/insurance-price/**"),
            new AntPathRequestMatcher("/v1/user/set-new-password")
    );

    @Autowired
    public JwtAuthFilter(JwtService jwtService, UserAccountService userAccountService) {
        this.jwtService = jwtService;
        this.userAccountService = userAccountService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String token;
        String email;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            try{
                email = jwtService.extractUsername(token);
            }
            catch (ExpiredJwtException e)
            {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write("{\"statusCode\": 401,\"errorKey\":\"TokenIsExpired\",\"message\": \"Your token has expired\", \"date\": \"" + new Date() +  "\" }");
                return;
            }
            catch (Exception e)
            {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write("{\"statusCode\": 401,\"errorKey\":\"TokenIsInvalid\", \"message\": \"Token code is invalid\" , \"date\": \"" + new Date() +  "\" }");
                return;
            }
        }
        else{
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"statusCode\": 401,\"errorKey\":\"YouDoNotHavePermission\",\"message\": \"You do not have access\", \"date\": \"" + new Date() +  "\" }" );
            return;
        }
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userAccountService.loadUserByUsername(email);
            if (jwtService.validateToken(token, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        RequestMatcher matcher = new NegatedRequestMatcher(uriMatcher);
        return !matcher.matches(request);
    }
}
