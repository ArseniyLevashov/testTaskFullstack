--liquibase formatted sql

--changeset arsenii:002-seed-users
INSERT INTO users (username, password, role) VALUES
                                                 ('admin', '$2b$10$XK7gNOr7H/vYiZZSe0cRFuPFbaCY3EcZ61uaO/CrMcbf1gmCmuJpe', 'ROLE_ADMIN'),
                                                 ('user',  '$2b$10$8UxUWfSs9WbQS4Wf2Ns5eecvTEnRXQzm52BhNYBeoIfWExeueg..6', 'ROLE_USER');
--rollback DELETE FROM users WHERE username IN ('admin', 'user');