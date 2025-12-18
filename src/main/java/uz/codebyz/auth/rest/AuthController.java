package uz.codebyz.auth.rest;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import uz.codebyz.auth.dto.*;
import uz.codebyz.auth.service.AuthService;
import uz.codebyz.common.ResponseDto;
import uz.codebyz.user.entity.UserRole;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // ================= SIGN IN =================

    @PostMapping("/sign-in")
    public ResponseDto<SignInInitResponse> signIn(
            @Valid @RequestBody SignInRequest req
    ) {
        return authService.signIn(req);
    }

    @PostMapping("/sign-in/verify")
    public ResponseDto<AuthTokensResponse> signInVerify(
            @Valid @RequestBody VerifyCodeRequest req
    ) {
        return authService.signInVerify(req);
    }

    // ================= SIGN UP =================

    @PostMapping("/sign-up")
    public ResponseDto<Void> signUp(
            @Valid @RequestBody SignUpRequest req
    ) {
        return authService.signUp(req);
    }

    @PostMapping("/sign-up/verify")
    public ResponseDto<Void> signUpVerify(
            @Valid @RequestBody SignUpVerifyBody body
    ) {
        return authService.signUpVerify(
                body.toSignUpRequest(),
                body.toVerifyRequest()
        );
    }

    // ================= BODY =================

    /**
     * Sign-up verify uchun body:
     * - original sign-up ma'lumotlari
     * - verification code
     */
    public static class SignUpVerifyBody {

        private String fullName;
        private String username;
        private String email;
        private String password;
        private UserRole role; // ⭐ MUHIM
        private String code;

        public SignUpVerifyBody() {}

        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }

        public UserRole getRole() { return role; }
        public void setRole(UserRole role) { this.role = role; }

        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }

        // ---------- MAPPER ----------

        public SignUpRequest toSignUpRequest() {
            SignUpRequest r = new SignUpRequest();
            r.setFullName(fullName);
            r.setUsername(username);
            r.setEmail(email);
            r.setPassword(password);
            r.setRole(role);
            return r;
        }

        public VerifyCodeRequest toVerifyRequest() {
            VerifyCodeRequest v = new VerifyCodeRequest();
            v.setIdentifier(email); // 🔥 verification HAR DOIM email orqali
            v.setCode(code);
            return v;
        }
    }
}
