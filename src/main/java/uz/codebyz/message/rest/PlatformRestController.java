package uz.codebyz.message.rest;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import uz.codebyz.common.ResponseDto;
import uz.codebyz.message.dto.message.MessageResponse;
import uz.codebyz.message.dto.platform.PlatformAnnouncementRequest;
import uz.codebyz.message.service.PlatformService;
import uz.codebyz.security.JwtUser;
import uz.codebyz.user.entity.UserRole;

@RestController
@RequestMapping("/api/platform")
public class PlatformRestController {

    private final PlatformService platformService;

    public PlatformRestController(PlatformService platformService) {
        this.platformService = platformService;
    }

    @PostMapping("/announce")
    public ResponseEntity<ResponseDto<MessageResponse>> announce(
            @AuthenticationPrincipal JwtUser jwtUser,
            @Valid @RequestBody PlatformAnnouncementRequest req
    ) {
        UserRole userRole;
        if (UserRole.ADMIN.getName().equals(jwtUser.getRole()))
            userRole = UserRole.ADMIN;
        else if (UserRole.TEACHER.getName().equals(jwtUser.getRole()))
            userRole = UserRole.TEACHER;
        else if (UserRole.STUDENT.getName().equals(jwtUser.getRole()))
            userRole = UserRole.STUDENT;
        else userRole = null;
        return ResponseEntity.ok(platformService.announce(jwtUser.getUserId(), userRole, req));
    }
}
