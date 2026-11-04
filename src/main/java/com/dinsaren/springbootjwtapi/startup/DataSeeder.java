package com.dinsaren.springbootjwtapi.startup;

import com.dinsaren.springbootjwtapi.constants.Constants;
import com.dinsaren.springbootjwtapi.models.Employee;
import com.dinsaren.springbootjwtapi.models.Role;
import com.dinsaren.springbootjwtapi.models.User;
import com.dinsaren.springbootjwtapi.models.UserRole;
import com.dinsaren.springbootjwtapi.models.product.Product;
import com.dinsaren.springbootjwtapi.models.product.ProductCategory;
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

import java.util.Date;
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
        seedEmployees();
        seedProducts();
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

    private void seedEmployees() {
        if (employeeRepository.count() > 0) {
            return;
        }

        List<Employee> dummyEmployees = List.of(
                Employee.builder().name("Sokha Chan").email("sokha.chan@company.com").phone("012345678").position("Senior Fullstack Engineer").department("Engineering").salary(2500.0).status(Constants.STATUS_ACTIVE).build(),
                Employee.builder().name("Bopha Keo").email("bopha.keo@company.com").phone("098765432").position("Lead UI/UX Designer").department("Design").salary(1800.0).status(Constants.STATUS_ACTIVE).build(),
                Employee.builder().name("Vicheka Heng").email("vicheka.heng@company.com").phone("089112233").position("Backend Developer").department("Engineering").salary(1600.0).status(Constants.STATUS_ACTIVE).build(),
                Employee.builder().name("Dara Sam").email("dara.sam@company.com").phone("077889900").position("Product Manager").department("Product").salary(2200.0).status(Constants.STATUS_ACTIVE).build(),
                Employee.builder().name("Leakhena Meas").email("leakhena.meas@company.com").phone("011223344").position("QA Automation Engineer").department("Quality Assurance").salary(1400.0).status(Constants.STATUS_ACTIVE).build(),
                Employee.builder().name("Rathana Kim").email("rathana.kim@company.com").phone("088776655").position("DevOps & Cloud Engineer").department("Infrastructure").salary(2300.0).status(Constants.STATUS_ACTIVE).build()
        );

        dummyEmployees.forEach(e -> {
            e.setCreateAt(new Date());
            e.setCreateBy(Constants.SYSTEM);
        });

        employeeRepository.saveAll(dummyEmployees);
        log.info("Seeded initial employees.");
    }

    private void seedProducts() {
        if (productRepository.count() > 0) {
            return;
        }

        ProductCategory electronics = productCategoryRepository.findAll().stream()
                .filter(c -> "Electronics".equalsIgnoreCase(c.getName()))
                .findFirst()
                .orElseGet(() -> {
                    ProductCategory cat = new ProductCategory();
                    cat.setName("Electronics");
                    cat.setStatus(Constants.STATUS_ACTIVE);
                    return productCategoryRepository.save(cat);
                });

        ProductCategory accessories = productCategoryRepository.findAll().stream()
                .filter(c -> "Accessories".equalsIgnoreCase(c.getName()))
                .findFirst()
                .orElseGet(() -> {
                    ProductCategory cat = new ProductCategory();
                    cat.setName("Accessories");
                    cat.setStatus(Constants.STATUS_ACTIVE);
                    return productCategoryRepository.save(cat);
                });

        Product p1 = new Product(0, "MacBook Pro 16\" M3", "PROD-MBP16-01", electronics, 2499.00, 1999.00, 25.0, "Apple M3 Pro chip, 18GB unified memory, 512GB SSD storage", Constants.STATUS_ACTIVE);
        Product p2 = new Product(0, "Dell XPS 15 OLED", "PROD-XPS15-02", electronics, 1899.00, 1450.00, 18.0, "Intel Core i9, 32GB RAM, 1TB SSD, 3.5K OLED touchscreen display", Constants.STATUS_ACTIVE);
        Product p3 = new Product(0, "iPhone 16 Pro Max 256GB", "PROD-IP16P-03", electronics, 1199.00, 950.00, 40.0, "Titanium design, A18 Pro chip, 48MP fusion camera system", Constants.STATUS_ACTIVE);
        Product p4 = new Product(0, "Logitech MX Master 3S", "PROD-MXM3S-04", accessories, 99.00, 65.00, 80.0, "Performance wireless ergonomic mouse with 8K DPI tracking", Constants.STATUS_ACTIVE);
        Product p5 = new Product(0, "Sony WH-1000XM5 Wireless Headphones", "PROD-WHXM5-05", accessories, 399.00, 280.00, 30.0, "Industry-leading noise cancelling with Auto NC Optimizer", Constants.STATUS_ACTIVE);
        Product p6 = new Product(0, "Keychron Q1 Pro Mechanical Keyboard", "PROD-KQ1P-06", accessories, 199.00, 130.00, 45.0, "Custom wireless mechanical keyboard with CNC aluminum body", Constants.STATUS_ACTIVE);

        List<Product> products = List.of(p1, p2, p3, p4, p5, p6);
        products.forEach(p -> {
            p.setCreateAt(new Date());
            p.setCreateBy(Constants.SYSTEM);
        });

        productRepository.saveAll(products);
        log.info("Seeded initial products.");
    }
}
