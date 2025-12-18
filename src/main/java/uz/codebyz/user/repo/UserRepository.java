package uz.codebyz.user.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.codebyz.user.entity.ApprovalStatus;
import uz.codebyz.user.entity.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);

    boolean existsByEmail(String email);
    boolean existsByUsername(String username);

    @Query("select u from User u where lower(u.email) = lower(:identifier) or lower(u.username) = lower(:identifier)")
    Optional<User> findByEmailOrUsername(@Param("identifier") String identifier);

    @Query("select u from User u order by u.createdAt")
    Page<User>getAllUsers(Pageable pageable);

    @Query("select u from User u where (u.approvalStatus=:status) order by u.createdAt")
    Page<User>getAllUsersByApprovalStatus(Pageable pageable,@Param("status") ApprovalStatus status);
}
