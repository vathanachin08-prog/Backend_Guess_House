-- ============================================================
-- V7: Delete dummy sample employees and products from database
-- ============================================================

DELETE FROM employees WHERE email IN (
    'sokha.chan@company.com',
    'bopha.keo@company.com',
    'vicheka.heng@company.com',
    'dara.sam@company.com',
    'leakhena.meas@company.com',
    'rathana.kim@company.com'
);

DELETE FROM products WHERE product_code IN (
    'PROD-MBP16-01',
    'PROD-XPS15-02',
    'PROD-IP16P-03',
    'PROD-MXM3S-04',
    'PROD-WHXM5-05',
    'PROD-KQ1P-06'
);

