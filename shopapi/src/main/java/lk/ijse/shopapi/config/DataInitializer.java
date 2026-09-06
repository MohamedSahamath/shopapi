package lk.ijse.shopapi.config;

import lk.ijse.shopapi.entity.*;
import lk.ijse.shopapi.repository.*;
import lk.ijse.shopapi.util.enums.RoleName;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final ExpenseCategoryRepository expenseCategoryRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedRoles();
        seedAdmin();
        seedExpenseCategories();
    }

    private void seedRoles() {
        for (RoleName name : RoleName.values()) {
            roleRepository.findByName(name).orElseGet(() -> {
                log.info("Creating role: {}", name);
                return roleRepository.save(Role.builder().name(name).build());
            });
        }
    }

    private void seedAdmin() {
        String adminEmail = "admin@shop.lk";
        if (userRepository.existsByEmail(adminEmail)) return;

        Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN)
                .orElseThrow(() -> new IllegalStateException("ROLE_ADMIN missing"));

        User admin = User.builder()
                .email(adminEmail)
                .password(passwordEncoder.encode("Admin@123"))
                .enabled(true)
                .roles(Set.of(adminRole))
                .build();

        userRepository.save(admin);
        log.info("Default admin created: {} / Admin@123", adminEmail);
    }

    private void seedExpenseCategories() {
        String[] names = {"Rent", "Salaries", "Utilities", "Transport", "Marketing", "Other"};
        for (String name : names) {
            if (!expenseCategoryRepository.existsByName(name)) {
                expenseCategoryRepository.save(
                        ExpenseCategory.builder().name(name).build());
            }
        }
    }
}