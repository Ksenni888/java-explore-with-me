drop table IF EXISTS categories, users, events, requests, compilations, compilation_event;

create TABLE IF NOT EXISTS categories (
category_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
category_name VARCHAR(50) NOT NULL,
UNIQUE (category_name)
);

create TABLE IF NOT EXISTS users (
user_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
user_email VARCHAR(254) NOT NULL,
user_name VARCHAR(250) NOT NULL,
UNIQUE (user_email)
);

create TABLE IF NOT EXISTS events (
event_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
annotation VARCHAR(2000)                        NOT NULL,
category_id BIGINT                              NOT NULL,
confirmed_requests Integer,
created_on TIMESTAMP WITHOUT TIME ZONE          NOT NULL,
description VARCHAR(7000)                       NOT NULL,
event_date TIMESTAMP WITHOUT TIME ZONE          NOT NULL,
initiator_id BIGINT                             NOT NULL,
lat DOUBLE PRECISION                            NOT NULL,
lon DOUBLE PRECISION                            NOT NULL,
paid BOOLEAN                                    NOT NULL,
participant_limit Integer                       NOT NULL,
published_on TIMESTAMP WITHOUT TIME ZONE,
request_moderation BOOLEAN                      NOT NULL,
state VARCHAR(50)                               NOT NULL,
title VARCHAR(120)                              NOT NULL,
FOREIGN KEY(category_id) REFERENCES categories(category_id) ON delete CASCADE,
FOREIGN KEY(initiator_id) REFERENCES users(user_id) ON delete CASCADE
);

create TABLE IF NOT EXISTS requests (
request_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
created TIMESTAMP                         NOT NULL,
event_id BIGINT                           NOT NULL,
requester_id BIGINT                       NOT NULL,
status VARCHAR(50)                        NOT NULL,
FOREIGN KEY(event_id) REFERENCES events(event_id),
FOREIGN KEY(requester_id) REFERENCES users(user_id));


create TABLE IF NOT EXISTS compilations (
compilation_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
pinned BOOLEAN NOT NULL,
title VARCHAR(50) NOT NULL

);

create TABLE IF NOT EXISTS compilation_event (
event_id BIGINT NOT NULL,
compilation_id BIGINT NOT NULL,
PRIMARY KEY (compilation_id, event_id),
FOREIGN KEY(event_id) REFERENCES events(event_id),-- ON delete CASCADE,
FOREIGN KEY(compilation_id) REFERENCES compilations(compilation_id)-- ON delete CASCADE
);
