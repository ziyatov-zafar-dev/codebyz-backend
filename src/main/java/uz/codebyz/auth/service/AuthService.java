package uz.codebyz.auth.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.codebyz.auth.dto.*;
import uz.codebyz.auth.entity.VerificationPurpose;
import uz.codebyz.common.ErrorCode;
import uz.codebyz.common.ResponseDto;
import uz.codebyz.notify.EmailService;
import uz.codebyz.security.JwtService;
import uz.codebyz.user.entity.ApprovalStatus;
import uz.codebyz.user.entity.User;
import uz.codebyz.user.entity.UserRole;
import uz.codebyz.user.repo.UserRepository;

import java.time.Instant;
import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final LoginGuardService loginGuardService;
    private final VerificationService verificationService;
    private final EmailService emailService;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            LoginGuardService loginGuardService,
            VerificationService verificationService,
            EmailService emailService,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.loginGuardService = loginGuardService;
        this.verificationService = verificationService;
        this.emailService = emailService;
        this.jwtService = jwtService;
    }

    /* ============================================================
       SIGN IN INIT
       ============================================================ */
    public ResponseDto<SignInInitResponse> signIn(SignInRequest req) {

        String identifier = req.getIdentifier().trim().toLowerCase();

        Optional<User> optionalUser =
                userRepository.findByEmailOrUsername(identifier);

        if (optionalUser.isEmpty()) {
            return ResponseDto.fail(
                    404,
                    ErrorCode.USER_NOT_FOUND,
                    "Kullanıcı bulunamadı"
            );
        }

        User user = optionalUser.get();

        // 🔒 brute-force guard
        LoginGuardService.GuardStatus status =
                loginGuardService.check(user.getId());

        if (status.isLocked()) {
            return ResponseDto.fail(
                    423,
                    ErrorCode.USER_LOCKED,
                    "Hesap geçici olarak kilitlendi. Açılma zamanı: "
                            + status.getLockUntil()
            );
        }

        // 🔑 password check
        if (!passwordEncoder.matches(
                req.getPassword(),
                user.getPasswordHash()
        )) {
            loginGuardService.onFailed(user.getId());
            return ResponseDto.fail(
                    401,
                    ErrorCode.BAD_CREDENTIALS,
                    "Kullanıcı adı veya şifre hatalı"
            );
        }

        if (!user.isEmailVerified()) {
            return ResponseDto.fail(
                    403,
                    ErrorCode.EMAIL_NOT_VERIFIED,
                    "E-posta doğrulanmamış"
            );
        }

        loginGuardService.onSuccess(user.getId());

        // ✉️ verification code (EMAIL üzerinden!)
        var ev = verificationService.create(
                user.getEmail(),
                VerificationPurpose.SIGN_IN
        );

        emailService.sendVerificationCode(
                user.getEmail(),
                "CodeByZ Giriş Doğrulama",
                ev.getCode(),
                "SIGN_IN"
        );

        return ResponseDto.ok(
                "Doğrulama kodu e-postanıza gönderildi",
                new SignInInitResponse(user.getEmail(), true)
        );
    }

    /* ============================================================
       SIGN IN VERIFY
       ============================================================ */
    public ResponseDto<AuthTokensResponse> signInVerify(
            VerifyCodeRequest req
    ) {

        String identifier = req.getIdentifier().trim().toLowerCase();

        Optional<User> optionalUser =
                userRepository.findByEmailOrUsername(identifier);

        if (optionalUser.isEmpty()) {
            return ResponseDto.fail(
                    404,
                    ErrorCode.USER_NOT_FOUND,
                    "Kullanıcı bulunamadı"
            );
        }

        User user = optionalUser.get();

        // 🔐 verify FAQAT email orqali
        try {
            verificationService.verify(
                    user.getEmail(),
                    VerificationPurpose.SIGN_IN,
                    req.getCode()
            );
        } catch (VerificationService.VerifyException ex) {
            return mapVerifyError(ex);
        }

        // 🧑‍💼 admin approval
        if (user.getApprovalStatus() == ApprovalStatus.CHECKING) {
            return ResponseDto.fail(
                    403,
                    ErrorCode.ACCOUNT_CHECKING,
                    "Hesabınız incelemede. Yönetici onayı bekleniyor."
            );
        }

        if (user.getApprovalStatus() == ApprovalStatus.CANCEL) {
            return ResponseDto.fail(
                    403,
                    ErrorCode.ACCOUNT_CANCELLED,
                    "Hesabınız reddedildi. Sebep: "
                            + user.getApprovalReason()
            );
        }

        // 🎟 tokens
        String accessToken =
                jwtService.generateAccessToken(
                        user.getId(),
                        user.getEmail(),
                        user.getRole()

                );

        String refreshToken =
                jwtService.generateRefreshToken(
                        user.getId(),
                        user.getEmail()
                );

        return ResponseDto.ok(
                "Giriş başarılı",
                new AuthTokensResponse(
                        user.getEmail(),
                        accessToken,
                        refreshToken
                )
        );
    }

    /* ============================================================
       SIGN UP INIT
       ============================================================ */
    public ResponseDto<Void> signUp(SignUpRequest req) {

        String email = req.getEmail().trim().toLowerCase();
        String username = req.getUsername().trim().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            return ResponseDto.fail(
                    409,
                    ErrorCode.EMAIL_TAKEN,
                    "E-posta zaten kullanılıyor"
            );
        }

        if (userRepository.existsByUsername(username)) {
            return ResponseDto.fail(
                    409,
                    ErrorCode.USERNAME_TAKEN,
                    "Kullanıcı adı zaten kullanılıyor"
            );
        }

        var ev = verificationService.create(
                email,
                VerificationPurpose.SIGN_UP
        );

        emailService.sendVerificationCode(
                email,
                "CodeByZ Kayıt Doğrulama",
                ev.getCode(),
                "SIGN_UP"
        );

        return ResponseDto.ok(
                "Doğrulama kodu e-postanıza gönderildi"
        );
    }

    /* ============================================================
       SIGN UP VERIFY
       ============================================================ */
    @Transactional
    public ResponseDto<Void> signUpVerify(
            SignUpRequest original,
            VerifyCodeRequest verify
    ) {
        String email = original.getEmail().trim().toLowerCase();
        String username = original.getUsername().trim().toLowerCase();

        // 🔐 identifier faqat email bo‘lishi kerak
        if (!email.equalsIgnoreCase(verify.getIdentifier())) {
            return ResponseDto.fail(
                    400,
                    ErrorCode.VALIDATION_ERROR,
                    "Doğrulama e-postası uyuşmuyor"
            );
        }

        // 🔥 ROLE VALIDATSIYA
        if (original.getRole() == null ||
                (original.getRole() != UserRole.STUDENT &&
                        original.getRole() != UserRole.TEACHER)) {

            return ResponseDto.fail(
                    400,
                    ErrorCode.VALIDATION_ERROR,
                    "Rol zorunludur (STUDENT veya TEACHER)"
            );
        }

        try {
            verificationService.verify(
                    email,
                    VerificationPurpose.SIGN_UP,
                    verify.getCode()
            );
        } catch (VerificationService.VerifyException ex) {
            return mapVerifyErrorVoid(ex);
        }

        if (userRepository.existsByEmail(email)) {
            return ResponseDto.fail(
                    409,
                    ErrorCode.EMAIL_TAKEN,
                    "E-posta zaten kullanılıyor"
            );
        }

        if (userRepository.existsByUsername(username)) {
            return ResponseDto.fail(
                    409,
                    ErrorCode.USERNAME_TAKEN,
                    "Kullanıcı adı zaten kullanılıyor"
            );
        }

        User user = new User();
        user.setFullName(original.getFullName());
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(original.getPassword()));
        user.setEmailVerified(true);
        user.setActive(true);

        // 🔥 ROLE
        user.setRole(original.getRole());

        // 🔥 ADMIN ONAYI
        user.setApprovalStatus(ApprovalStatus.CHECKING);
        user.setApprovalUpdatedAt(Instant.now());
        user.setApprovalReason("Yönetici incelemesi bekleniyor");
        userRepository.save(user);

        return ResponseDto.ok(
                "Kayıt başarılı. Hesabınız yönetici onayına gönderildi."
        );
    }


    /* ============================================================
       VERIFY ERROR MAPPERS
       ============================================================ */
    private ResponseDto<AuthTokensResponse> mapVerifyError(
            VerificationService.VerifyException ex
    ) {
        return switch (ex.getError()) {
            case EXPIRED -> ResponseDto.fail(
                    410,
                    ErrorCode.CODE_EXPIRED,
                    "Doğrulama kodunun süresi dolmuş"
            );
            case INVALID -> ResponseDto.fail(
                    400,
                    ErrorCode.CODE_INVALID,
                    "Doğrulama kodu hatalı"
            );
            case USED -> ResponseDto.fail(
                    409,
                    ErrorCode.CODE_ALREADY_USED,
                    "Doğrulama kodu zaten kullanılmış"
            );
            case NOT_FOUND -> ResponseDto.fail(
                    404,
                    ErrorCode.CODE_INVALID,
                    "Doğrulama kodu bulunamadı"
            );
        };
    }

    private ResponseDto<Void> mapVerifyErrorVoid(
            VerificationService.VerifyException ex
    ) {
        return switch (ex.getError()) {
            case EXPIRED -> ResponseDto.fail(
                    410,
                    ErrorCode.CODE_EXPIRED,
                    "Doğrulama kodunun süresi dolmuş"
            );
            case INVALID -> ResponseDto.fail(
                    400,
                    ErrorCode.CODE_INVALID,
                    "Doğrulama kodu hatalı"
            );
            case USED -> ResponseDto.fail(
                    409,
                    ErrorCode.CODE_ALREADY_USED,
                    "Doğrulama kodu zaten kullanılmış"
            );
            case NOT_FOUND -> ResponseDto.fail(
                    404,
                    ErrorCode.CODE_INVALID,
                    "Doğrulama kodu bulunamadı"
            );
        };
    }
}
