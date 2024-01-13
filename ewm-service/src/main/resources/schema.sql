--drop table IF EXISTS categories, users, events, requests, compilation;

create TABLE IF NOT EXISTS categories (
category_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
category_name VARCHAR(50) NOT NULL,
UNIQUE (category_name)
);

create TABLE IF NOT EXISTS users (
user_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
user_email VARCHAR(255) NOT NULL,
user_name VARCHAR(255) NOT NULL,
UNIQUE (user_email)
);

create TABLE IF NOT EXISTS events (
event_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
annotation VARCHAR(255) NOT NULL,
category_id Integer NOT NULL,
confirmed_requests Integer,
created_on TIMESTAMP WITHOUT TIME ZONE,
description VARCHAR(255),
event_date TIMESTAMP WITHOUT TIME ZONE,
initiator_id Integer,
lat DOUBLE PRECISION,
lon DOUBLE PRECISION,
paid BOOLEAN,
participant_limit Integer,
published_on TIMESTAMP WITHOUT TIME ZONE,
request_moderation BOOLEAN,
state VARCHAR(50),
title VARCHAR(255),
FOREIGN KEY(category_id) REFERENCES categories(category_id) ON DELETE CASCADE,
FOREIGN KEY(initiator_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS requests (
request_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
created TIMESTAMP                         NOT NULL,
event_id BIGINT                           NOT NULL,
requester_id BIGINT                       NOT NULL,
status VARCHAR(15)                        NOT NULL,
FOREIGN KEY(event_id) REFERENCES events(event_id),
FOREIGN KEY(requester_id) REFERENCES users(user_id));


create TABLE IF NOT EXISTS compilation (
compilation_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
pinned BOOLEAN,
title VARCHAR(255)
);
