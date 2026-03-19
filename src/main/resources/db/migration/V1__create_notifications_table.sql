-- ============================================================
-- V1: Create notifications table
-- Run this manually on new environments since Hibernate uses
-- ddl-auto: validate (no auto schema generation in prod)
-- ============================================================

CREATE TABLE IF NOT EXISTS notifications (
    notification_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    recipient_user_id UUID NOT NULL,
    type VARCHAR(50) NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT,
    resource_id VARCHAR(255),
    resource_type VARCHAR(100),
    performer_name VARCHAR(255),
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Performance indexes
CREATE INDEX IF NOT EXISTS idx_notifications_recipient
    ON notifications(recipient_user_id);

CREATE INDEX IF NOT EXISTS idx_notifications_recipient_read
    ON notifications(recipient_user_id, is_read);

CREATE INDEX IF NOT EXISTS idx_notifications_created
    ON notifications(created_at);
