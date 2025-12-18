package uz.codebyz.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import uz.codebyz.common.ErrorCode;

import java.io.IOException;

@Component
public class OAuthFailureHandler implements AuthenticationFailureHandler {

    private final OAuthProperties props;

    public OAuthFailureHandler(OAuthProperties props) {
        this.props = props;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
            throws IOException, ServletException {

        String url = UriComponentsBuilder.fromUriString(props.getFrontendBaseUrl() + "/error")
                .queryParam("errorCode", ErrorCode.OAUTH2_FAILED.name())
                .build().toUriString();
        response.sendRedirect(url);
    }
}
