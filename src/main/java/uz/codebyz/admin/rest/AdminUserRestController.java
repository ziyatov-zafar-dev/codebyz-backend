package uz.codebyz.admin.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.codebyz.admin.dto.user.AdminChangeApprovalStatusRequest;
import uz.codebyz.admin.service.AdminUserService;
import uz.codebyz.common.ResponseDto;
import uz.codebyz.common.dto.user.UserResponse;
import uz.codebyz.user.entity.ApprovalStatus;

import java.util.UUID;

@Tag(name = "Admin Users", description = "Admin paneli uchun foydalanuvchilarni boshqarish API’lari")
@RestController
@RequestMapping("/api/admin/user")
public class AdminUserRestController {

    private final AdminUserService adminUserService;

    public AdminUserRestController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    // ================= GET ALL USERS =================

    @Operation(summary = "Barcha foydalanuvchilarni olish", description = """
            Admin barcha foydalanuvchilarni pagination bilan oladi.
            
            Natija sahifalangan (page, size).
            """)
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Foydalanuvchilar muvaffaqiyatli olindi", content = @Content(schema = @Schema(implementation = UserResponse.class)))})
    @GetMapping("/list")
    public ResponseEntity<ResponseDto<Page<UserResponse>>> getAllUsers(@Parameter(description = "Sahifa raqami (0 dan boshlanadi)", example = "0") @RequestParam int page,

                                                                       @Parameter(description = "Sahifadagi elementlar soni", example = "10") @RequestParam int size) {
        return ResponseEntity.ok(adminUserService.findAllUsers(page, size));
    }

    // ================= GET USERS BY APPROVAL STATUS =================

    @Operation(summary = "Onay holatiga ko‘ra foydalanuvchilarni olish", description = """
            Admin foydalanuvchilarni ularning onay holatiga qarab oladi.
            
            Mavjud statuslar:
            - PENDING
            - APPROVED
            - REJECTED
            """)
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Foydalanuvchilar muvaffaqiyatli olindi")})
    @GetMapping("/get-users-by-confirmed-status")
    public ResponseEntity<ResponseDto<Page<UserResponse>>> getAllUsersByStatusConfirmed(@Parameter(description = "Sahifa raqami", example = "0") @RequestParam("page") int page,

                                                                                        @Parameter(description = "Sahifa hajmi", example = "10") @RequestParam("size") int size) {
        return ResponseEntity.ok(adminUserService.findAllUsersByApprovalStatus(page, size, ApprovalStatus.CONFIRMED));
    }

    @GetMapping("/get-users-by-checking-status")
    public ResponseEntity<ResponseDto<Page<UserResponse>>> getAllUsersByStatusChecking(@Parameter(description = "Sahifa raqami", example = "0") @RequestParam("page") int page, @Parameter(description = "Sahifa hajmi", example = "10") @RequestParam("size") int size) {
        return ResponseEntity.ok(adminUserService.findAllUsersByApprovalStatus(page, size, ApprovalStatus.CHECKING));
    }

    @GetMapping("/get-users-by-cancel-status")
    public ResponseEntity<ResponseDto<Page<UserResponse>>> getAllUsersByStatusCancel(@Parameter(description = "Sahifa raqami", example = "0") @RequestParam("page") int page,

                                                                                     @Parameter(description = "Sahifa hajmi", example = "10") @RequestParam("size") int size) {
        return ResponseEntity.ok(adminUserService.findAllUsersByApprovalStatus(page, size, ApprovalStatus.CANCEL));
    }

    // ================= CHANGE APPROVAL STATUS =================

    @Operation(summary = "Foydalanuvchi onay holatini o‘zgartirish", description = """
            Admin foydalanuvchining onay holatini o‘zgartiradi.
            
            Masalan:
            - PENDING → APPROVED
            - PENDING → REJECTED
            """)
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Onay holati muvaffaqiyatli yangilandi"), @ApiResponse(responseCode = "404", description = "Foydalanuvchi topilmadi")})
    @PutMapping("/{userId}/approval-status")
    public ResponseDto<UserResponse> changeApprovalStatus(@Parameter(description = "Foydalanuvchi ID", example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable("userId") UUID userId,

                                                          @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Onay holatini o‘zgartirish uchun ma’lumot", required = true) @Valid @RequestBody AdminChangeApprovalStatusRequest req) {
        return adminUserService.changeApprovalStatus(req, userId);
    }

    // ================= BLOCK USER =================

    @Operation(summary = "Foydalanuvchini bloklash", description = "Admin foydalanuvchini tizimdan foydalanishini bloklaydi")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Foydalanuvchi bloklandi"), @ApiResponse(responseCode = "400", description = "Foydalanuvchi allaqachon bloklangan"), @ApiResponse(responseCode = "404", description = "Foydalanuvchi topilmadi")})
    @PutMapping("/{userId}/block")
    public ResponseDto<UserResponse> blockUser(@Parameter(description = "Foydalanuvchi ID") @PathVariable UUID userId) {
        return adminUserService.blockUser(userId);
    }

    // ================= UNBLOCK USER =================

    @Operation(summary = "Foydalanuvchini blokdan chiqarish", description = "Admin bloklangan foydalanuvchini qayta aktiv holatga o‘tkazadi")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Foydalanuvchi blokdan chiqarildi"), @ApiResponse(responseCode = "400", description = "Foydalanuvchi allaqachon aktiv"), @ApiResponse(responseCode = "404", description = "Foydalanuvchi topilmadi")})
    @PutMapping("/{userId}/unblock")
    public ResponseDto<UserResponse> unblockUser(@Parameter(description = "Foydalanuvchi ID") @PathVariable UUID userId) {
        return adminUserService.unblockUser(userId);
    }


    @GetMapping("search")
    public ResponseEntity<ResponseDto<Page<UserResponse>>> search(
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            @RequestParam("query") String query
    ) {
        return ResponseEntity.ok(adminUserService.search(page, size, null, query));
    }

    @GetMapping("search-with-approval-status")
    public ResponseEntity<ResponseDto<Page<UserResponse>>> searchWithStatus(
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            @RequestParam("status") ApprovalStatus status,
            @RequestParam("query") String query
    ) {
        return ResponseEntity.ok(adminUserService.search(page, size, status, query));
    }
}
