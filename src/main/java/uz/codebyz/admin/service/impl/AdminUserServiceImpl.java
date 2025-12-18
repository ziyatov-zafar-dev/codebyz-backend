package uz.codebyz.admin.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uz.codebyz.admin.dto.user.AdminChangeApprovalStatusRequest;
import uz.codebyz.admin.service.AdminUserService;
import uz.codebyz.common.ErrorCode;
import uz.codebyz.common.Helper;
import uz.codebyz.common.ResponseDto;
import uz.codebyz.common.dto.user.UserResponse;
import uz.codebyz.common.mapper.UserMapper;
import uz.codebyz.user.entity.ApprovalStatus;
import uz.codebyz.user.entity.User;
import uz.codebyz.user.repo.UserRepository;

import java.util.Optional;
import java.util.UUID;

@Service
public class AdminUserServiceImpl implements AdminUserService {

    private final Logger log = LoggerFactory.getLogger(AdminUserServiceImpl.class);

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public AdminUserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public ResponseDto<Page<UserResponse>> findAllUsers(int page, int size) {
        log.info("Admin requested all users | page={}, size={}", page, size);

        Page<User> allUsers =
                userRepository.getAllUsers(PageRequest.of(page, size));

        log.info("Fetched {} users", allUsers.getTotalElements());

        Page<UserResponse> userResponsePage =
                allUsers.map(userMapper::toDto);

        return ResponseDto.ok(
                "Kullanıcılar başarıyla getirildi",
                userResponsePage
        );
    }

    @Override
    public ResponseDto<Page<UserResponse>> findAllUsersByApprovalStatus(
            int page,
            int size,
            ApprovalStatus status
    ) {
        log.info(
                "Admin requested users by approval status | status={}, page={}, size={}",
                status, page, size
        );

        Page<User> allUsers =
                userRepository.getAllUsersByApprovalStatus(
                        PageRequest.of(page, size),
                        status
                );

        log.info(
                "Fetched {} users with approvalStatus={}",
                allUsers.getTotalElements(),
                status
        );

        Page<UserResponse> userResponsePage =
                allUsers.map(userMapper::toDto);

        return ResponseDto.ok(
                "Onay durumuna göre kullanıcılar başarıyla getirildi",
                userResponsePage
        );
    }

    @Override
    public ResponseDto<UserResponse> changeApprovalStatus(
            AdminChangeApprovalStatusRequest req,
            UUID userId
    ) {
        log.info(
                "Admin attempting to change approval status | userId={}, newStatus={}",
                userId, req.getStatus()
        );

        Optional<User> optionalUser = userRepository.findById(userId);

        if (optionalUser.isEmpty()) {
            log.warn("Approval status change failed | user not found | userId={}", userId);
            return ResponseDto.fail(
                    404,
                    ErrorCode.USER_NOT_FOUND,
                    "Kullanıcı bulunamadı"
            );
        }

        User user = optionalUser.get();

        user.setApprovalReason(req.getApprovalReason());
        user.setApprovalStatus(req.getStatus());
        user.setApprovalUpdatedAt(Helper.currentTimeInstant());

        User savedUser = userRepository.save(user);

        log.info(
                "Approval status updated successfully | userId={}, status={}",
                userId, savedUser.getApprovalStatus()
        );

        return ResponseDto.ok(
                "Kullanıcının onay durumu başarıyla güncellendi",
                userMapper.toDto(savedUser)
        );
    }

    @Override
    public ResponseDto<UserResponse> blockUser(UUID userId) {
        log.info("Admin attempting to block user | userId={}", userId);

        Optional<User> optionalUser = userRepository.findById(userId);

        if (optionalUser.isEmpty()) {
            log.warn("Block user failed | user not found | userId={}", userId);
            return ResponseDto.fail(
                    404,
                    ErrorCode.USER_NOT_FOUND,
                    "Kullanıcı bulunamadı"
            );
        }

        User user = optionalUser.get();

        if (!user.isActive()) {
            log.warn("Block user failed | user already blocked | userId={}", userId);
            return ResponseDto.fail(
                    400,
                    ErrorCode.USER_ALREADY_BLOCKED,
                    "Kullanıcı zaten engellenmiş"
            );
        }

        user.setActive(false);

        User savedUser = userRepository.save(user);

        log.info("User blocked successfully | userId={}", userId);

        return ResponseDto.ok(
                "Kullanıcı başarıyla engellendi",
                userMapper.toDto(savedUser)
        );
    }

    @Override
    public ResponseDto<UserResponse> unblockUser(UUID userId) {
        log.info("Admin attempting to unblock user | userId={}", userId);

        Optional<User> optionalUser = userRepository.findById(userId);

        if (optionalUser.isEmpty()) {
            log.warn("Unblock user failed | user not found | userId={}", userId);
            return ResponseDto.fail(
                    404,
                    ErrorCode.USER_NOT_FOUND,
                    "Kullanıcı bulunamadı"
            );
        }

        User user = optionalUser.get();

        if (user.isActive()) {
            log.warn("Unblock user failed | user already active | userId={}", userId);
            return ResponseDto.fail(
                    400,
                    ErrorCode.USER_NOT_BLOCKED,
                    "Kullanıcı zaten aktif"
            );
        }

        user.setActive(true);

        User savedUser = userRepository.save(user);

        log.info("User unblocked successfully | userId={}", userId);

        return ResponseDto.ok(
                "Kullanıcının engeli başarıyla kaldırıldı",
                userMapper.toDto(savedUser)
        );
    }
}

