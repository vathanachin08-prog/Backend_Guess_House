package com.dinsaren.springbootjwtapi.startup;

import com.dinsaren.springbootjwtapi.constants.Constants;
import com.dinsaren.springbootjwtapi.models.Role;
import com.dinsaren.springbootjwtapi.models.User;
import com.dinsaren.springbootjwtapi.models.UserRole;
import com.dinsaren.springbootjwtapi.repository.EmployeeRepository;
import com.dinsaren.springbootjwtapi.repository.ProductCategoryRepository;
import com.dinsaren.springbootjwtapi.repository.ProductRepository;
import com.dinsaren.springbootjwtapi.repository.RoleRepository;
import com.dinsaren.springbootjwtapi.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Component
public class DataSeeder implements ApplicationRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmployeeRepository employeeRepository;
    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;

    public DataSeeder(UserRepository userRepository,
                      RoleRepository roleRepository,
                      PasswordEncoder passwordEncoder,
                      EmployeeRepository employeeRepository,
                      ProductRepository productRepository,
                      ProductCategoryRepository productCategoryRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.employeeRepository = employeeRepository;
        this.productRepository = productRepository;
        this.productCategoryRepository = productCategoryRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        seedAdminUser();
        cleanDummyData();
    }

    private void seedAdminUser() {
        Optional<User> existing = userRepository.findByUsername("admin");
        if (existing.isPresent()) {
            log.info("Admin user already exists, skipping seed.");
            return;
        }

        Role adminRole = roleRepository.findByName(UserRole.ROLE_ADMIN)
                .orElseThrow(() -> new RuntimeException("ROLE_ADMIN not found — ensure migration ran."));

        User admin = new User("admin", "admin@admin.com", passwordEncoder.encode("Admin@1234"), "00000000000");
        admin.setFirstName("Admin");
        admin.setLastName("Admin");
        admin.setStatus(Constants.STATUS_ACTIVE);
        admin.setChangePassword(Constants.No);

        Set<Role> roles = new HashSet<>();
        roles.add(adminRole);
        admin.setRoles(roles);

        userRepository.save(admin);
        log.info("Default admin user seeded successfully.");
    }

    /**
     * Purges any initial dummy/sample employees and products,
     * leaving only real user-created records in the database.
     */
    private void cleanDummyData() {
        // Remove dummy employees
        List<String> dummyEmails = List.of(
                "sokha.chan@company.com",
                "bopha.keo@company.com",
                "vicheka.heng@company.com",
                "dara.sam@company.com",
                "leakhena.meas@company.com",
                "rathana.kim@company.com"
        );
        for (String email : dummyEmails) {
            employeeRepository.findByEmail(email).ifPresent(emp -> {
                employeeRepository.delete(emp);
                log.info("Removed dummy employee: {}", email);
            });
        }

        // Remove dummy products
        List<String> dummyProductCodes = List.of(
                "PROD-MBP16-01",
                "PROD-XPS15-02",
                "PROD-IP16P-03",
                "PROD-MXM3S-04",
                "PROD-WHXM5-05",
                "PROD-KQ1P-06"
        );
        productRepository.findAll().stream()
                .filter(p -> p.getProductCode() != null && dummyProductCodes.contains(p.getProductCode()))
                .forEach(p -> {
                    productRepository.delete(p);
                    log.info("Removed dummy product: {}", p.getProductCode());
                });
    }
}
