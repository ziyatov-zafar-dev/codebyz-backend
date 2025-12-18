package uz.codebyz.user.rest;

import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.codebyz.common.ResponseDto;
import uz.codebyz.security.JwtUser;
import uz.codebyz.user.dto.*;
import uz.codebyz.user.service.UserService;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseDto<UserMeResponse> me(@AuthenticationPrincipal JwtUser principal) {
        return userService.me(principal);
    }

    @PostMapping("/change-username")
    public ResponseDto<Void> changeUsername(@AuthenticationPrincipal JwtUser principal,
                                           @Valid @RequestBody ChangeUsernameRequest req) {
        return userService.changeUsername(principal, req);
    }

    @PostMapping("/change-email")
    public ResponseDto<Void> changeEmail(@AuthenticationPrincipal JwtUser principal,
                                         @Valid @RequestBody ChangeEmailRequest req) {
        return userService.changeEmail(principal, req);
    }

    @PostMapping("/update-profile")
    public ResponseDto<Void> updateProfile(@AuthenticationPrincipal JwtUser principal,
                                          @Valid @RequestBody UpdateProfileRequest req) {
        return userService.updateProfile(principal, req);
    }

    @PostMapping(value = "/add-profile-image", consumes = "multipart/form-data")
    public ResponseDto<ProfileImageDto> addProfileImage(@AuthenticationPrincipal JwtUser principal,
                                                        @RequestParam("file") MultipartFile file) {
        return userService.addProfileImage(principal, file);
    }

    @DeleteMapping("/remove-profile-image/{imageId}")
    public ResponseDto<Void> removeProfileImage(@AuthenticationPrincipal JwtUser principal,
                                                @PathVariable("imageId") UUID imageId) {
        return userService.removeProfileImage(principal, imageId);
    }
}
