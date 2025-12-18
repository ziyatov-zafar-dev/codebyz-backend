package uz.codebyz.user.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import uz.codebyz.common.ErrorCode;
import uz.codebyz.common.Helper;
import uz.codebyz.common.ResponseDto;
import uz.codebyz.helper.FileHelper;
import uz.codebyz.helper.UploadFileResponseDto;
import uz.codebyz.security.JwtUser;
import uz.codebyz.storage.FileStorageService;
import uz.codebyz.user.dto.*;
import uz.codebyz.user.entity.ProfileImage;
import uz.codebyz.user.entity.SocialLinks;
import uz.codebyz.user.entity.User;
import uz.codebyz.user.repo.ProfileImageRepository;
import uz.codebyz.user.repo.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BaseUserService {

    private final UserRepository userRepository;
    private final ProfileImageRepository profileImageRepository;
    private final FileStorageService storage;
    private final FileHelper fileHelper;

    @Value("${storage.profile.base.url}")
    private String profileBaseUrl;

    public BaseUserService(UserRepository userRepository, ProfileImageRepository profileImageRepository, FileStorageService storage, FileHelper fileHelper) {
        this.userRepository = userRepository;
        this.profileImageRepository = profileImageRepository;
        this.storage = storage;
        this.fileHelper = fileHelper;
    }

    public ResponseDto<UserMeResponse> me(JwtUser principal) {
        User user = userRepository.findById(principal.getUserId()).orElse(null);
        if (user == null) return ResponseDto.fail(404, ErrorCode.USER_NOT_FOUND, "User topilmadi");
        return ResponseDto.ok("OK", mapMe(user));
    }

    @Transactional
    public ResponseDto<Void> changeUsername(JwtUser principal, ChangeUsernameRequest req) {
        String newUsername = req.getUsername().trim().toLowerCase();
        if (userRepository.existsByUsername(newUsername)) {
            return ResponseDto.fail(409, ErrorCode.USERNAME_TAKEN, "Username band");
        }
        User user = userRepository.findById(principal.getUserId()).orElse(null);
        if (user == null) return ResponseDto.fail(404, ErrorCode.USER_NOT_FOUND, "User topilmadi");
        user.setUsername(newUsername);
        userRepository.save(user);
        return ResponseDto.ok("Username yangilandi");
    }

    @Transactional
    public ResponseDto<Void> changeEmail(JwtUser principal, ChangeEmailRequest req) {
        String email = req.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            return ResponseDto.fail(409, ErrorCode.EMAIL_TAKEN, "Email band");
        }
        User user = userRepository.findById(principal.getUserId()).orElse(null);
        if (user == null) return ResponseDto.fail(404, ErrorCode.USER_NOT_FOUND, "User topilmadi");
        user.setEmail(email);
        user.setEmailVerified(false);
        userRepository.save(user);
        return ResponseDto.ok("Email yangilandi (verifikatsiya kerak)");
    }

    @Transactional
    public ResponseDto<Void> updateProfile(JwtUser principal, UpdateProfileRequest req) {
        User user = userRepository.findById(principal.getUserId()).orElse(null);
        if (user == null) return ResponseDto.fail(404, ErrorCode.USER_NOT_FOUND, "User topilmadi");

        user.setFullName(req.getFullName());
        user.setBirthDate(req.getBirthDate());

        SocialLinks links = req.getSocialLinks();
        if (links != null) user.setSocialLinks(links);

        userRepository.save(user);
        return ResponseDto.ok("Profil yangilandi");
    }

    @Transactional
    public ResponseDto<ProfileImageDto> addProfileImage(JwtUser principal, MultipartFile file) {
        User user = userRepository.findById(principal.getUserId()).orElse(null);
        if (user == null) return ResponseDto.fail(404, ErrorCode.USER_NOT_FOUND, "User topilmadi");

        try {
            ResponseDto<UploadFileResponseDto> savedFile = fileHelper.uploadFile(file, profileBaseUrl);
            if (!savedFile.isSuccess()) {
                return new ResponseDto<>(
                        false, savedFile.getMessage(),
                        savedFile.getCode(), savedFile.getErrorCode(),
                        null
                );
            }
            UploadFileResponseDto data = savedFile.getData();
            ProfileImage img = new ProfileImage();
            img.setUser(user);
            img.setFileUrl(data.getFileUrl());
            img.setOriginalFileName(data.getFileName());
            img.setFileSize(data.getFileSize());
            img.setUploadedAt(Helper.currentTimeInstant());
            img.setActive(true);
            profileImageRepository.save(img);
            user.getProfileImages().add(img);
            userRepository.save(user);
            return ResponseDto.ok("Rasm qo'shildi", mapImage(img));
        } catch (Exception e) {
            return ResponseDto.fail(500, ErrorCode.INTERNAL_ERROR, "File saqlashda xatolik");
        }
    }

    @Transactional
    public ResponseDto<Void> removeProfileImage(JwtUser principal, UUID imageId) {
        User user = userRepository.findById(principal.getUserId()).orElse(null);
        if (user == null) return ResponseDto.fail(404, ErrorCode.USER_NOT_FOUND, "User topilmadi");

        ProfileImage img = profileImageRepository.findById(imageId).orElse(null);
        if (img == null || img.getUser() == null || !img.getUser().getId().equals(user.getId())) {
            return ResponseDto.fail(404, ErrorCode.VALIDATION_ERROR, "Rasm topilmadi");
        }

        profileImageRepository.delete(img);
        return ResponseDto.ok("Rasm o'chirildi");
    }

    private UserMeResponse mapMe(User u) {
        UserMeResponse r = new UserMeResponse();
        r.setId(u.getId());
        r.setFullName(u.getFullName());
        r.setUsername(u.getUsername());
        r.setEmail(u.getEmail());
        r.setBirthDate(u.getBirthDate());
        r.setSocialLinks(u.getSocialLinks());
//        List<ProfileImageDto> imgs = u.getProfileImages().stream().map(this::mapImage).collect(Collectors.toList());
        List<ProfileImageDto> imgs = new ArrayList<>();
        u.getProfileImages().stream().filter(ProfileImage::getActive).forEach(profileImage -> {
            imgs.add(new ProfileImageDto(
                    profileImage.getId(),
                    profileImage.getFileUrl(),
                    profileImage.getOriginalFileName(),
                    profileImage.getFileSize(),
                    profileImage.getUploadedAt()
            ));
        });

        r.setProfileImages(imgs);
        r.setRole(u.getRole());
        return r;
    }

    private ProfileImageDto mapImage(ProfileImage img) {
        ProfileImageDto d = new ProfileImageDto();
        d.setId(img.getId());
        d.setFileUrl(img.getFileUrl());
        d.setOriginalFileName(img.getOriginalFileName());
        d.setFileSize(img.getFileSize());
        d.setUploadedAt(img.getUploadedAt());
        return d;
    }
}
