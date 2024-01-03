DROP TABLE IF EXISTS stats;

CREATE TABLE IF NOT EXISTS stats (
    stats_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    app VARCHAR(255),
    uri VARCHAR(255),
    ip VARCHAR(255),
    timestamp TIMESTAMP WITHOUT TIME ZONE NOT NULL
);