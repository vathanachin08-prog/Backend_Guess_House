-- ============================================================
-- V5__create_rental_tenants_table.sql
-- Rental Tenants Management
-- ============================================================

CREATE TABLE IF NOT EXISTS tenants (
    id           BIGSERIAL PRIMARY KEY,
    property_id  BIGINT NOT NULL,
    room_id      BIGINT,
    name         VARCHAR(150) NOT NULL,
    room_number  VARCHAR(50) NOT NULL,
    floor        VARCHAR(100),
    phone_number VARCHAR(50),
    email        VARCHAR(150),
    status       VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    joined_date  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by   VARCHAR(255),
    updated_at   TIMESTAMP,
    updated_by   VARCHAR(255),
    CONSTRAINT fk_tenant_property FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE CASCADE,
    CONSTRAINT fk_tenant_room FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_tenants_property ON tenants(property_id);
CREATE INDEX IF NOT EXISTS idx_tenants_room ON tenants(room_number);
CREATE INDEX IF NOT EXISTS idx_tenants_status ON tenants(status);

