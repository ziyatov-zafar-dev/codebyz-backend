package uz.codebyz.common.mapper;

import org.springframework.stereotype.Component;
import uz.codebyz.common.dto.user.UserResponse;
import uz.codebyz.user.dto.ProfileImageDto;
import uz.codebyz.user.entity.ProfileImage;
import uz.codebyz.user.entity.User;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserMapper {
    public UserResponse toDto(User user) {
        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setFullName(user.getFullName());
        userResponse.setUsername(user.getUsername());
        userResponse.setEmail(user.getEmail());
        userResponse.setBirthDate(user.getBirthDate());
        userResponse.setSocialLinks(user.getSocialLinks());
        List<ProfileImageDto> images = new ArrayList<>();
        user.getProfileImages().stream().filter(ProfileImage::getActive).forEach(profileImage -> {
            images.add(new ProfileImageDto(
                    profileImage.getId(),
                    profileImage.getFileUrl(),
                    profileImage.getOriginalFileName(),
                    profileImage.getFileSize(),
                    profileImage.getUploadedAt()
            ));
        });
        userResponse.setProfileImages(images);
        userResponse.setRole(user.getRole());
        userResponse.setApprovalStatus(user.getApprovalStatus());
        userResponse.setApprovalUpdatedAt(user.getApprovalUpdatedAt());
        user.setApprovalReason(user.getApprovalReason());
        return userResponse;
    }
}
