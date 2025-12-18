package uz.codebyz.admin.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestParam;
import uz.codebyz.admin.dto.user.AdminChangeApprovalStatusRequest;
import uz.codebyz.common.ResponseDto;
import uz.codebyz.common.dto.user.UserResponse;
import uz.codebyz.user.entity.ApprovalStatus;

import java.util.UUID;

public interface AdminUserService {
    ResponseDto<Page<UserResponse>> findAllUsers(int page, int size);
    ResponseDto<Page<UserResponse>> search(int page, int size,ApprovalStatus approvalStatus,String query);

    ResponseDto<Page<UserResponse>> findAllUsersByApprovalStatus(int page, int size, ApprovalStatus status);

    ResponseDto<UserResponse> changeApprovalStatus(AdminChangeApprovalStatusRequest req,UUID userid);

    ResponseDto<UserResponse> blockUser(UUID userId);

    ResponseDto<UserResponse> unblockUser(UUID userId);
}
