--liquibase formatted sql

--changeset arsenii:001-create-users-table
CREATE TABLE users (
                       id       UUID         NOT NULL DEFAULT gen_random_uuid(),
                       username VARCHAR(100) NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       role     VARCHAR(30)  NOT NULL,
                       CONSTRAINT pk_users PRIMARY KEY (id),
                       CONSTRAINT uk_users_username UNIQUE (username)
);
--rollback DROP TABLE users;