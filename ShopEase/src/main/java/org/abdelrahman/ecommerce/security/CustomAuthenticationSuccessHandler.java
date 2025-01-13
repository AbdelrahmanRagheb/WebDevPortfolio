package org.abdelrahman.ecommerce.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.util.Collection;

public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication ) throws IOException {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        for (GrantedAuthority grantedAuthority : authorities) {
            if (grantedAuthority.getAuthority().equals("ROLE_VENDOR")) {
                response.sendRedirect("/");

                return;
            } else if (grantedAuthority.getAuthority().equals("ROLE_CONSUMER")) {
                response.sendRedirect("/");
                return;
            } else if (grantedAuthority.getAuthority().equals("ROLE_ADMIN")) {
                response.sendRedirect("/");
                return;
            }
        }

        throw new IllegalStateException("User has no role assigned");
    }
}
