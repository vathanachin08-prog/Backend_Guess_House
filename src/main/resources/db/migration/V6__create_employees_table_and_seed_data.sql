-- ============================================================
-- V6: Create employees table and seed dummy employees & products
-- ============================================================

CREATE TABLE IF NOT EXISTS employees (
    id            BIGSERIAL PRIMARY KEY,
    name          VARCHAR(255) NOT NULL,
    email         VARCHAR(255) NOT NULL UNIQUE,
    phone         VARCHAR(50)  NOT NULL,
    position      VARCHAR(255) NOT NULL,
    department    VARCHAR(255) NOT NULL,
    salary        DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    status        VARCHAR(50)  NOT NULL DEFAULT 'ACT',
    profile_image TEXT,
    created_at    TIMESTAMP,
    created_by    VARCHAR(255),
    updated_at    TIMESTAMP,
    updated_by    VARCHAR(255)
);

-- Seed initial employees
INSERT INTO employees (name, email, phone, position, department, salary, status, created_at, created_by)
VALUES
('Sokha Chan', 'sokha.chan@company.com', '012345678', 'Senior Fullstack Engineer', 'Engineering', 2500.0, 'ACT', NOW(), 'system'),
('Bopha Keo', 'bopha.keo@company.com', '098765432', 'Lead UI/UX Designer', 'Design', 1800.0, 'ACT', NOW(), 'system'),
('Vicheka Heng', 'vicheka.heng@company.com', '089112233', 'Backend Developer', 'Engineering', 1600.0, 'ACT', NOW(), 'system'),
('Dara Sam', 'dara.sam@company.com', '077889900', 'Product Manager', 'Product', 2200.0, 'ACT', NOW(), 'system'),
('Leakhena Meas', 'leakhena.meas@company.com', '011223344', 'QA Automation Engineer', 'Quality Assurance', 1400.0, 'ACT', NOW(), 'system'),
('Rathana Kim', 'rathana.kim@company.com', '088776655', 'DevOps & Cloud Engineer', 'Infrastructure', 2300.0, 'ACT', NOW(), 'system')
ON CONFLICT (email) DO NOTHING;

-- Seed product categories if empty
INSERT INTO product_categories (name, created_at, created_by)
SELECT 'Electronics', NOW(), 'system'
WHERE NOT EXISTS (SELECT 1 FROM product_categories WHERE name = 'Electronics');

INSERT INTO product_categories (name, created_at, created_by)
SELECT 'Accessories', NOW(), 'system'
WHERE NOT EXISTS (SELECT 1 FROM product_categories WHERE name = 'Accessories');

INSERT INTO product_categories (name, created_at, created_by)
SELECT 'Office', NOW(), 'system'
WHERE NOT EXISTS (SELECT 1 FROM product_categories WHERE name = 'Office');

-- Seed products if empty
INSERT INTO products (name, product_code, category_id, price, cost, stock_quantity, description, status, created_at, created_by)
SELECT 'MacBook Pro 16" M3', 'PROD-MBP16-01', c.id, 2499.00, 1999.00, 25, 'Apple M3 Pro chip, 18GB unified memory, 512GB SSD storage', 'ACT', NOW(), 'system'
FROM product_categories c WHERE c.name = 'Electronics'
AND NOT EXISTS (SELECT 1 FROM products WHERE product_code = 'PROD-MBP16-01');

INSERT INTO products (name, product_code, category_id, price, cost, stock_quantity, description, status, created_at, created_by)
SELECT 'Dell XPS 15 OLED', 'PROD-XPS15-02', c.id, 1899.00, 1450.00, 18, 'Intel Core i9, 32GB RAM, 1TB SSD, 3.5K OLED touchscreen display', 'ACT', NOW(), 'system'
FROM product_categories c WHERE c.name = 'Electronics'
AND NOT EXISTS (SELECT 1 FROM products WHERE product_code = 'PROD-XPS15-02');

INSERT INTO products (name, product_code, category_id, price, cost, stock_quantity, description, status, created_at, created_by)
SELECT 'iPhone 16 Pro Max 256GB', 'PROD-IP16P-03', c.id, 1199.00, 950.00, 40, 'Titanium design, A18 Pro chip, 48MP fusion camera system', 'ACT', NOW(), 'system'
FROM product_categories c WHERE c.name = 'Electronics'
AND NOT EXISTS (SELECT 1 FROM products WHERE product_code = 'PROD-IP16P-03');

INSERT INTO products (name, product_code, category_id, price, cost, stock_quantity, description, status, created_at, created_by)
SELECT 'Logitech MX Master 3S', 'PROD-MXM3S-04', c.id, 99.00, 65.00, 80, 'Performance wireless ergonomic mouse with 8K DPI any-surface tracking', 'ACT', NOW(), 'system'
FROM product_categories c WHERE c.name = 'Accessories'
AND NOT EXISTS (SELECT 1 FROM products WHERE product_code = 'PROD-MXM3S-04');

INSERT INTO products (name, product_code, category_id, price, cost, stock_quantity, description, status, created_at, created_by)
SELECT 'Sony WH-1000XM5 Wireless Headphones', 'PROD-WHXM5-05', c.id, 399.00, 280.00, 30, 'Industry-leading noise cancelling with Auto NC Optimizer', 'ACT', NOW(), 'system'
FROM product_categories c WHERE c.name = 'Accessories'
AND NOT EXISTS (SELECT 1 FROM products WHERE product_code = 'PROD-WHXM5-05');

INSERT INTO products (name, product_code, category_id, price, cost, stock_quantity, description, status, created_at, created_by)
SELECT 'Keychron Q1 Pro Mechanical Keyboard', 'PROD-KQ1P-06', c.id, 199.00, 130.00, 45, 'Custom wireless mechanical keyboard with CNC aluminum body', 'ACT', NOW(), 'system'
FROM product_categories c WHERE c.name = 'Accessories'
AND NOT EXISTS (SELECT 1 FROM products WHERE product_code = 'PROD-KQ1P-06');

