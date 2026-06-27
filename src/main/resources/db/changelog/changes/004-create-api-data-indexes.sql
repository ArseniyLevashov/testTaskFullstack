--liquibase formatted sql

--changeset arsenii:004-create-api-data-index-created-at
CREATE INDEX idx_api_data_created_at ON api_data (created_at DESC);
--rollback DROP INDEX idx_api_data_created_at;