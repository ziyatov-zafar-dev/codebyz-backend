package uz.codebyz.message.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uz.codebyz.user.entity.User;
import uz.codebyz.user.entity.UserRole;

import java.util.List;
import java.util.UUID;

public interface UserLiteRepository extends JpaRepository<User, UUID> {

    @Query("select u from User u where u.active = true and u.emailVerified = true")
    List<User> findAllActiveVerified();

    @Query("select u from User u where u.active = true and u.emailVerified = true and u.role = :role")
    List<User> findAllActiveVerifiedByRole(UserRole role);
}
