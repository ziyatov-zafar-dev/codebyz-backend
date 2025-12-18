package uz.codebyz.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import uz.codebyz.common.ErrorCode;
import uz.codebyz.user.entity.User;
import uz.codebyz.user.repo.UserRepository;

import java.io.IOException;

@Component
public class OAuthSuccessHandler implements AuthenticationSuccessHandler {

    private final OAuthProperties props;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public OAuthSuccessHandler(OAuthProperties props, UserRepository userRepository, JwtService jwtService) {
        this.props = props;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {

        String email = null;
        if (authentication instanceof OAuth2AuthenticationToken token) {
            Object v = token.getPrincipal().getAttributes().get("email");
            if (v != null) email = String.valueOf(v);
        }

        if (email == null) {
            String url = UriComponentsBuilder.fromUriString(props.getFrontendBaseUrl() + "/error")
                    .queryParam("errorCode", ErrorCode.OAUTH2_FAILED.name())
                    .build().toUriString();
            response.sendRedirect(url);
            return;
        }

        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            String url = UriComponentsBuilder.fromUriString(props.getFrontendBaseUrl() + "/error")
                    .queryParam("errorCode", ErrorCode.GOOGLE_ACCOUNT_NOT_REGISTERED.name())
                    .queryParam("email", email)
                    .build().toUriString();
            response.sendRedirect(url);
            return;
        }

        String access = jwtService.generateAccessToken(user.getId(), user.getEmail());
        String refresh = jwtService.generateRefreshToken(user.getId(), user.getEmail());

        String url = UriComponentsBuilder.fromUriString(props.getFrontendBaseUrl() + "/success")
                .queryParam("email", email)
                .queryParam("accessToken", access)
                .queryParam("refreshToken", refresh)
                .build().toUriString();
        response.sendRedirect(url);
    }
}
