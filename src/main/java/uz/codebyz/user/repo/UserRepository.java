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

    @Query("select u from User u order by u.createdAt desc")
    Page<User> getAllUsers(Pageable pageable);

    @Query("select u from User u where (u.approvalStatus=:status) order by u.createdAt desc")
    Page<User> getAllUsersByApprovalStatus(Pageable pageable, @Param("status") ApprovalStatus status);


    @Query("""
                select u
                from User u
                where
                    (:status is null or u.approvalStatus = :status)
                    and (
                        :q is null
                        or lower(u.fullName) like lower(concat('%', :q, '%'))
                        or lower(u.username) like lower(concat('%', :q, '%'))
                        or lower(u.email) like lower(concat('%', :q, '%'))
                    )
                order by u.createdAt desc
            """)
    Page<User> search(
            Pageable pageable,
            @Param("q") String q,
            @Param("status") ApprovalStatus status
    );

}
