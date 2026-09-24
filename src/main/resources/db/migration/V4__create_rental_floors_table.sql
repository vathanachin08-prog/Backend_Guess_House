-- ============================================================
-- V4__create_rental_floors_table.sql
-- Rental Floors Management
-- ============================================================

CREATE TABLE IF NOT EXISTS floors (
    id          BIGSERIAL PRIMARY KEY,
    property_id BIGINT NOT NULL,
    name        VARCHAR(100) NOT NULL,
    floor_order INT DEFAULT 1,
    status      VARCHAR(50) DEFAULT 'ACTIVE',
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by  VARCHAR(255),
    updated_at  TIMESTAMP,
    updated_by  VARCHAR(255),
    CONSTRAINT fk_floor_property FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE CASCADE,
    CONSTRAINT uq_property_floor_name UNIQUE (property_id, name)
);

CREATE INDEX IF NOT EXISTS idx_floors_property ON floors(property_id);

-- Seed initial floors for any existing properties
INSERT INTO floors (property_id, name, floor_order, status)
SELECT id, 'ជាន់ទី ១', 1, 'ACTIVE' FROM properties
ON CONFLICT DO NOTHING;

INSERT INTO floors (property_id, name, floor_order, status)
SELECT id, 'ជាន់ទី ២', 2, 'ACTIVE' FROM properties
ON CONFLICT DO NOTHING;

INSERT INTO floors (property_id, name, floor_order, status)
SELECT id, 'ជាន់ទី ៣', 3, 'ACTIVE' FROM properties
ON CONFLICT DO NOTHING;
