package uz.codebyz.auth.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.codebyz.auth.entity.EmailVerification;
import uz.codebyz.auth.entity.VerificationPurpose;

import java.util.Optional;
import java.util.UUID;

public interface EmailVerificationRepository extends JpaRepository<EmailVerification, UUID> {

    @Query("select ev from EmailVerification ev where lower(ev.email)=lower(:email) and ev.purpose=:purpose and ev.used=false order by ev.createdAt desc")
    Optional<EmailVerification> findLatestNotUsed(@Param("email") String email, @Param("purpose") VerificationPurpose purpose);
}
