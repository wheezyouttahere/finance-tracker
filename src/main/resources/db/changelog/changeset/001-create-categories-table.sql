--changeset beks:1
CREATE TABLE categories (
                            id BIGSERIAL PRIMARY KEY,
                            name VARCHAR(255) NOT NULL,
                            type VARCHAR(50) NOT NULL
);