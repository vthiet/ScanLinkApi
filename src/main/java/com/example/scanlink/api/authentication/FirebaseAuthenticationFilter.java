//package com.example.scanlink.api.authentication;
//
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseToken;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.http.HttpHeaders;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//
//@Component
//public class FirebaseAuthenticationFilter extends OncePerRequestFilter {
//
//    private static final String BEARER_PREFIX = "Bearer ";
//
//    private final FirebaseAuth firebaseAuth;
//
//    public FirebaseAuthenticationFilter(FirebaseAuth firebaseAuth) {
//        this.firebaseAuth = firebaseAuth;
//    }
//
//    @Override
//    protected void doFilterInternal(
//            HttpServletRequest request,
//            HttpServletResponse response,
//            FilterChain filterChain
//    ) throws ServletException, IOException {
//        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
//
//        if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
//            String idToken = authHeader.substring(BEARER_PREFIX.length());
//            try {
//                FirebaseToken decodedToken = firebaseAuth.verifyIdToken(idToken);
//                FirebaseUserPrincipal principal = new FirebaseUserPrincipal(
//                        decodedToken.getUid(),
//                        decodedToken.getEmail(),
//                        decodedToken.getName(),
//                        decodedToken.isEmailVerified()
//                );
//                FirebaseAuthenticationToken authentication = new FirebaseAuthenticationToken(principal);
//                SecurityContextHolder.getContext().setAuthentication(authentication);
//            } catch (Exception ignored) {
//                SecurityContextHolder.clearContext();
//            }
//        }
//
//        filterChain.doFilter(request, response);
//    }
//}
