-- liquibase formatted sql

-- changeset denismalinin:1732152896669-1
CREATE TABLE revoke_token
(
    token        VARCHAR(255)                NOT NULL,
    delete_after TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_revoke_token PRIMARY KEY (token)
);

-- changeset denismalinin:1732152896669-2
ALTER TABLE users_authorities
    DROP CONSTRAINT uc_users_authorities_authorities;