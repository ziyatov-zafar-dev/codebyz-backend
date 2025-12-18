package uz.codebyz.user.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
}
