-- liquibase formatted sql

-- changeset developer:004-1
CREATE TABLE IF NOT EXISTS users (
                                     id BIGSERIAL PRIMARY KEY,
                                     username VARCHAR(64) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(32) NOT NULL DEFAULT 'ROLE_USER',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
                             );

-- changeset developer:004-2
ALTER TABLE categories ADD COLUMN user_id BIGINT;
ALTER TABLE categories ADD CONSTRAINT fk_categories_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

-- changeset developer:004-3
ALTER TABLE transactions ADD COLUMN user_id BIGINT;
ALTER TABLE transactions ADD CONSTRAINT fk_transactions_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;