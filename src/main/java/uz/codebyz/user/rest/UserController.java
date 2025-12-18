package uz.codebyz.user.rest;

import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.codebyz.common.ResponseDto;
import uz.codebyz.security.JwtUser;
import uz.codebyz.user.dto.*;
import uz.codebyz.user.service.BaseUserService;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final BaseUserService baseUserService;

    public UserController(BaseUserService baseUserService) {
        this.baseUserService = baseUserService;
    }

    @GetMapping("/me")
    public ResponseDto<UserMeResponse> me(@AuthenticationPrincipal JwtUser principal) {
        return baseUserService.me(principal);
    }

    @PostMapping("/change-username")
    public ResponseDto<Void> changeUsername(@AuthenticationPrincipal JwtUser principal,
                                           @Valid @RequestBody ChangeUsernameRequest req) {
        return baseUserService.changeUsername(principal, req);
    }

    @PostMapping("/change-email")
    public ResponseDto<Void> changeEmail(@AuthenticationPrincipal JwtUser principal,
                                         @Valid @RequestBody ChangeEmailRequest req) {
        return baseUserService.changeEmail(principal, req);
    }

    @PostMapping("/update-profile")
    public ResponseDto<Void> updateProfile(@AuthenticationPrincipal JwtUser principal,
                                          @Valid @RequestBody UpdateProfileRequest req) {
        return baseUserService.updateProfile(principal, req);
    }

    @PostMapping(value = "/add-profile-image", consumes = "multipart/form-data")
    public ResponseDto<ProfileImageDto> addProfileImage(@AuthenticationPrincipal JwtUser principal,
                                                        @RequestParam("file") MultipartFile file) {
        return baseUserService.addProfileImage(principal, file);
    }

    @DeleteMapping("/remove-profile-image/{imageId}")
    public ResponseDto<Void> removeProfileImage(@AuthenticationPrincipal JwtUser principal,
                                                @PathVariable("imageId") UUID imageId) {
        return baseUserService.removeProfileImage(principal, imageId);
    }
}
