--liquibase formatted sql

--changeset arsenii:003-create-api-data-table
CREATE TABLE api_data (
                          id         UUID      NOT NULL DEFAULT gen_random_uuid(),
                          created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          success    BOOLEAN   NOT NULL DEFAULT FALSE,
                          payload    TEXT,
                          CONSTRAINT pk_api_data PRIMARY KEY (id)
);
--rollback DROP TABLE api_data;