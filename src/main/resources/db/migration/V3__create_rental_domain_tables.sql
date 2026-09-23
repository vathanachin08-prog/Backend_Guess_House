-- ============================================================
-- V3__create_rental_domain_tables.sql
-- Rental Marketplace Domain (Phase 1)
-- ============================================================

-- 1. Insert new roles if not exist
INSERT INTO roles (name) SELECT 'ROLE_OWNER'   WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ROLE_OWNER');
INSERT INTO roles (name) SELECT 'ROLE_STUDENT' WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ROLE_STUDENT');

-- 2. Facilities
CREATE TABLE IF NOT EXISTS facilities (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255),
    icon        VARCHAR(100)
);

-- Seed standard facilities
INSERT INTO facilities (name, description, icon) VALUES
('WIFI',               'High-speed Wi-Fi internet',      'wifi'),
('AIR_CONDITIONER',   'Air conditioning unit',          'ac_unit'),
('PRIVATE_BATHROOM',  'Attached private bathroom',      'bathtub'),
('PARKING',           'Motorcycle or car parking space','local_parking'),
('KITCHEN',           'Shared or private kitchen',      'kitchen'),
('WASHING_MACHINE',   'Laundry washing machine',        'local_laundry_service'),
('SECURITY',          '24/7 security or CCTV camera',   'security'),
('FURNITURE',         'Furnished with bed and wardrobe','single_bed')
ON CONFLICT (name) DO NOTHING;

-- 3. Properties
CREATE TABLE IF NOT EXISTS properties (
    id                  BIGSERIAL PRIMARY KEY,
    owner_id            INT NOT NULL,
    name                VARCHAR(255) NOT NULL,
    description         TEXT,
    property_type       VARCHAR(50) NOT NULL DEFAULT 'APARTMENT',
    address             VARCHAR(255),
    city                VARCHAR(100),
    district            VARCHAR(100),
    latitude            DOUBLE PRECISION,
    longitude           DOUBLE PRECISION,
    status              VARCHAR(50) NOT NULL DEFAULT 'DRAFT',
    verification_status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    main_image          VARCHAR(500),
    images              TEXT,
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by          VARCHAR(255),
    updated_at          TIMESTAMP,
    updated_by          VARCHAR(255),
    CONSTRAINT fk_property_owner FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_properties_owner ON properties(owner_id);
CREATE INDEX IF NOT EXISTS idx_properties_status ON properties(status, verification_status);
CREATE INDEX IF NOT EXISTS idx_properties_location ON properties(city, district);

-- 4. Rooms
CREATE TABLE IF NOT EXISTS rooms (
    id                  BIGSERIAL PRIMARY KEY,
    property_id         BIGINT NOT NULL,
    room_number         VARCHAR(50) NOT NULL,
    title               VARCHAR(255) NOT NULL,
    description         TEXT,
    price               NUMERIC(12,2) NOT NULL DEFAULT 0.00,
    room_type           VARCHAR(50) NOT NULL DEFAULT 'SINGLE',
    gender_preference   VARCHAR(50) NOT NULL DEFAULT 'ANY',
    available           BOOLEAN NOT NULL DEFAULT TRUE,
    floor               INT,
    area                DOUBLE PRECISION,
    images              TEXT,
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by          VARCHAR(255),
    updated_at          TIMESTAMP,
    updated_by          VARCHAR(255),
    CONSTRAINT fk_room_property FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE CASCADE,
    CONSTRAINT uq_property_room_number UNIQUE (property_id, room_number)
);

CREATE INDEX IF NOT EXISTS idx_rooms_property ON rooms(property_id);
CREATE INDEX IF NOT EXISTS idx_rooms_price_avail ON rooms(price, available);

-- 5. Room Facilities (Many-to-Many)
CREATE TABLE IF NOT EXISTS room_facilities (
    room_id     BIGINT NOT NULL,
    facility_id BIGINT NOT NULL,
    PRIMARY KEY (room_id, facility_id),
    CONSTRAINT fk_rf_room FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE CASCADE,
    CONSTRAINT fk_rf_facility FOREIGN KEY (facility_id) REFERENCES facilities(id) ON DELETE CASCADE
);

-- 6. Favorites
CREATE TABLE IF NOT EXISTS favorites (
    id          BIGSERIAL PRIMARY KEY,
    student_id  INT NOT NULL,
    property_id BIGINT,
    room_id     BIGINT,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_favorite_student FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_favorite_property FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE CASCADE,
    CONSTRAINT fk_favorite_room FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE CASCADE
);

CREATE UNIQUE INDEX IF NOT EXISTS uq_favorite_student_property ON favorites (student_id, property_id) WHERE property_id IS NOT NULL;
CREATE UNIQUE INDEX IF NOT EXISTS uq_favorite_student_room ON favorites (student_id, room_id) WHERE room_id IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_favorites_student ON favorites(student_id);

-- 7. Visit Requests
CREATE TABLE IF NOT EXISTS visit_requests (
    id              BIGSERIAL PRIMARY KEY,
    student_id      INT NOT NULL,
    property_id     BIGINT NOT NULL,
    room_id         BIGINT,
    requested_date  DATE NOT NULL,
    requested_time  VARCHAR(50),
    message         TEXT,
    status          VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by      VARCHAR(255),
    updated_at      TIMESTAMP,
    updated_by      VARCHAR(255),
    CONSTRAINT fk_vr_student FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_vr_property FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE CASCADE,
    CONSTRAINT fk_vr_room FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_visit_requests_student ON visit_requests(student_id);
CREATE INDEX IF NOT EXISTS idx_visit_requests_property ON visit_requests(property_id);
CREATE INDEX IF NOT EXISTS idx_visit_requests_status ON visit_requests(status);

-- 8. Reviews
CREATE TABLE IF NOT EXISTS reviews (
    id          BIGSERIAL PRIMARY KEY,
    student_id  INT NOT NULL,
    property_id BIGINT NOT NULL,
    rating      INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment     TEXT,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by  VARCHAR(255),
    updated_at  TIMESTAMP,
    updated_by  VARCHAR(255),
    CONSTRAINT fk_review_student FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_review_property FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE CASCADE,
    CONSTRAINT uq_review_student_property UNIQUE (student_id, property_id)
);

CREATE INDEX IF NOT EXISTS idx_reviews_property ON reviews(property_id);

-- 9. Reports
CREATE TABLE IF NOT EXISTS reports (
    id          BIGSERIAL PRIMARY KEY,
    reporter_id INT NOT NULL,
    property_id BIGINT NOT NULL,
    reason      VARCHAR(50) NOT NULL,
    description TEXT,
    status      VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by  VARCHAR(255),
    reviewed_at TIMESTAMP,
    reviewed_by VARCHAR(255),
    CONSTRAINT fk_report_reporter FOREIGN KEY (reporter_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_report_property FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_reports_property ON reports(property_id);
CREATE INDEX IF NOT EXISTS idx_reports_status ON reports(status);
