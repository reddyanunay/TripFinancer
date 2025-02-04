package com.example.backend.Filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;


public class JwtFilter extends GenericFilter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Bypass CORS preflight requests
        if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
            chain.doFilter(request, response);
            return;
        }

        String token = httpRequest.getHeader("Authorization");

        if (token != null && token.startsWith("Bearer ")) {
            String jwtToken = token.substring(7);
            try {
                Claims claims = Jwts.parser()
                        .setSigningKey("secretkey") // Replace with your actual secret key
                        .parseClaimsJws(jwtToken)
                        .getBody();
                String email = claims.getSubject();
                // Set the user details in the security context or request attributes if needed
                // SecurityContextHolder.getContext().setAuthentication(new JwtAuthentication(email));
            } catch (Exception e) {
                // Handle token parsing exceptions
                httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        } else {
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        chain.doFilter(request, response);
    }


}
