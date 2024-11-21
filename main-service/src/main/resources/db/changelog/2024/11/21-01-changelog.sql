-- liquibase formatted sql

-- changeset denismalinin:1732152896669-1
CREATE TABLE revoke_token
(
    token        VARCHAR(255)                NOT NULL,
    delete_after TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_revoke_token PRIMARY KEY (token)
);

