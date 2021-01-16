package es.codeurjc.books.configuration.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.codeurjc.books.configuration.TokenProvider;
import es.codeurjc.books.dtos.requests.UserRequestDto;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static es.codeurjc.books.configuration.Constants.HEADER_AUTHORIZATION_KEY;
import static es.codeurjc.books.configuration.Constants.TOKEN_BEARER_PREFIX;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private AuthenticationManager authenticationManager;

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
        super.setAuthenticationFailureHandler(new JWTAuthenticationFailureHandler());
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        try {
            UserRequestDto userCredentials = new ObjectMapper().readValue(request.getInputStream(), UserRequestDto.class);

            return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    userCredentials.getNick(), userCredentials.getPassword()));
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        String token = TokenProvider.generateToken(authResult);
        response.addHeader(HEADER_AUTHORIZATION_KEY, TOKEN_BEARER_PREFIX + " " + token);
    }
}
