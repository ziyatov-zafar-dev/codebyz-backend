package uz.codebyz.user.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.codebyz.user.entity.ProfileImage;

import java.util.UUID;

public interface ProfileImageRepository extends JpaRepository<ProfileImage, UUID> {
}
