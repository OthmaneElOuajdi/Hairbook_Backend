-- =============================================================================
-- 1. ENUMS - Custom data types for status fields
-- =============================================================================

-- Using native PostgreSQL enums for data integrity and performance.
CREATE TYPE e_role AS ENUM ('ROLE_VISITOR', 'ROLE_MEMBER', 'ROLE_ADMIN');
CREATE TYPE notification_status AS ENUM ('UNREAD', 'READ');
CREATE TYPE notification_type AS ENUM ('BOOKING_CONFIRMATION', 'BOOKING_CANCELLATION', 'BOOKING_REMINDER', 'PAYMENT_STATUS');
CREATE TYPE payment_status AS ENUM ('PENDING', 'COMPLETED', 'FAILED', 'REFUNDED');
CREATE TYPE reservation_status AS ENUM ('PENDING', 'CONFIRMED', 'CANCELLED', 'COMPLETED');

-- =============================================================================
-- 2. TABLES - Core application data structures
-- =============================================================================

-- Table for application roles
CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    name e_role NOT NULL UNIQUE
);

-- Table for users
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    last_login_at TIMESTAMP,
    loyalty_points INT NOT NULL DEFAULT 0
);

-- Join table for user roles (Many-to-Many)
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role_id BIGINT NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

-- Table for salon services
CREATE TABLE service_items (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    price DOUBLE PRECISION NOT NULL,
    loyalty_points_reward INT NOT NULL,
    description TEXT
);

-- Table for reservations
CREATE TABLE reservations (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    service_item_id BIGINT NOT NULL REFERENCES service_items(id) ON DELETE RESTRICT,
    start_time TIMESTAMP NOT NULL,
    status reservation_status NOT NULL DEFAULT 'PENDING',
    notes VARCHAR(1000),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- Table for payments
CREATE TABLE payments (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    reservation_id BIGINT NOT NULL UNIQUE REFERENCES reservations(id) ON DELETE CASCADE,
    amount DOUBLE PRECISION NOT NULL,
    status payment_status NOT NULL,
    message VARCHAR(500),
    payment_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    payment_method VARCHAR(50),
    transaction_id VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- Table for refresh tokens
CREATE TABLE refresh_tokens (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token VARCHAR(512) NOT NULL UNIQUE,
    expires_at TIMESTAMP NOT NULL,
    revoked BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Table for working hours
CREATE TABLE working_hours (
    id BIGSERIAL PRIMARY KEY,
    day_of_week VARCHAR(16) NOT NULL UNIQUE,
    start_time TIME,
    end_time TIME,
    closed BOOLEAN NOT NULL DEFAULT FALSE
);

-- Table for blocked slots
CREATE TABLE blocked_slots (
    id BIGSERIAL PRIMARY KEY,
    start_at TIMESTAMP NOT NULL,
    end_at TIMESTAMP NOT NULL,
    reason VARCHAR(255),
    admin_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Table for notifications
CREATE TABLE notifications (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    type notification_type NOT NULL,
    message VARCHAR(255) NOT NULL,
    status notification_status NOT NULL DEFAULT 'UNREAD',
    sent_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    read_at TIMESTAMP
);

-- Table for loyalty point transactions
CREATE TABLE loyalty_point_transactions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    points INT NOT NULL,
    description VARCHAR(255),
    transaction_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Table for audit logs
CREATE TABLE audit_logs (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
    entity_name VARCHAR(120) NOT NULL,
    entity_id BIGINT,
    action VARCHAR(60) NOT NULL,
    details TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- =============================================================================
-- 3. INDEXES - For performance optimization
-- =============================================================================

-- Reservations
CREATE INDEX idx_reservation_user ON reservations(user_id);
CREATE INDEX idx_reservation_start ON reservations(start_time);

-- Refresh Tokens
CREATE INDEX idx_refreshtoken_token ON refresh_tokens(token);

-- Blocked Slots
CREATE INDEX idx_blockedslot_range ON blocked_slots(start_at, end_at);

-- Loyalty Transactions
CREATE INDEX idx_loyalty_tx_user ON loyalty_point_transactions(user_id);
CREATE INDEX idx_loyalty_tx_date ON loyalty_point_transactions(transaction_date);

-- Audit Logs
CREATE INDEX idx_audit_entity ON audit_logs(entity_name, entity_id);
CREATE INDEX idx_audit_createdAt ON audit_logs(created_at);
