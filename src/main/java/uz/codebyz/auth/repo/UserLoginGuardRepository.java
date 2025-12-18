package uz.codebyz.auth.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.codebyz.auth.entity.UserLoginGuard;

import java.util.UUID;

public interface UserLoginGuardRepository extends JpaRepository<UserLoginGuard, UUID> {
}
