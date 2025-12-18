package uz.codebyz.admin;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import uz.codebyz.user.entity.ApprovalStatus;
import uz.codebyz.user.entity.User;
import uz.codebyz.user.entity.UserRole;
import uz.codebyz.user.repo.UserRepository;

import java.time.Instant;

@Component
public class AdminSeeder implements CommandLineRunner {

    private final AdminSeedProperties props;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminSeeder(AdminSeedProperties props, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.props = props;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (!props.isEnabled()) return;
        String email = props.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmail(email)) return;

        User admin = new User();
        admin.setEmail(email);
        admin.setUsername(props.getUsername().trim().toLowerCase());
        admin.setFullName(props.getFullName());
        admin.setPasswordHash(passwordEncoder.encode(props.getPassword()));
        admin.setRole(UserRole.ADMIN);

        admin.setEmailVerified(true);
        admin.setApprovalStatus(ApprovalStatus.CONFIRMED);
        admin.setApprovalUpdatedAt(Instant.now());
        userRepository.save(admin);
    }
}
