-- liquibase formatted sql

-- changeset denismalinin:1732053628495-1
CREATE SEQUENCE IF NOT EXISTS role_seq START WITH 1 INCREMENT BY 50;

-- changeset denismalinin:1732053628495-2
CREATE SEQUENCE IF NOT EXISTS users_seq START WITH 1 INCREMENT BY 50;

-- changeset denismalinin:1732053628495-3
CREATE TABLE role
(
    id   BIGINT NOT NULL,
    name VARCHAR(64),
    CONSTRAINT pk_role PRIMARY KEY (id)
);

-- changeset denismalinin:1732053628495-4
CREATE TABLE users
(
    id       BIGINT       NOT NULL,
    username VARCHAR(64) NOT NULL,
    password VARCHAR(255),
    CONSTRAINT pk_users PRIMARY KEY (id)
);

-- changeset denismalinin:1732053628495-5
CREATE TABLE users_authorities
(
    user_id        BIGINT NOT NULL,
    authorities_id BIGINT NOT NULL,
    CONSTRAINT pk_users_authorities PRIMARY KEY (user_id, authorities_id)
);

-- changeset denismalinin:1732053628495-6
ALTER TABLE users_authorities
    ADD CONSTRAINT uc_users_authorities_authorities UNIQUE (authorities_id);

-- changeset denismalinin:1732053628495-7
ALTER TABLE users
    ADD CONSTRAINT uc_users_username UNIQUE (username);

-- changeset denismalinin:1732053628495-8
ALTER TABLE users_authorities
    ADD CONSTRAINT fk_useaut_on_role FOREIGN KEY (authorities_id) REFERENCES role (id);

-- changeset denismalinin:1732053628495-9
ALTER TABLE users_authorities
    ADD CONSTRAINT fk_useaut_on_user FOREIGN KEY (user_id) REFERENCES users (id);

-- changeset denismalinin:1732053628495-10
INSERT INTO role (id, name) VALUES (1, 'ROLE_USER');
INSERT INTO role (id, name) VALUES (2, 'ROLE_ADMIN')

