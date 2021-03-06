-- CREATE TABLES
CREATE TABLE user_data
(
    username TEXT        NOT NULL PRIMARY KEY,
    password TEXT        NOT NULL,
    email    TEXT UNIQUE NOT NULL
);

CREATE TABLE role
(
    username    TEXT NOT NULL,
    role_name   TEXT NOT NULL,
    description TEXT,
    PRIMARY KEY (username, role_name),
    CONSTRAINT fk_role_to_user
        FOREIGN KEY (username)
            REFERENCES user_data (username)
);

CREATE UNIQUE INDEX idx_role_username ON role (username, role_name);

CREATE TABLE wallet
(
    username        TEXT  NOT NULL PRIMARY KEY,
    current_balance MONEY NOT NULL,
    CONSTRAINT fk_wallet_to_user
        FOREIGN KEY (username)
            REFERENCES user_data (username)
);

CREATE TABLE play_session
(
    session_id BIGSERIAL PRIMARY KEY,
    username   TEXT      NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time   TIMESTAMP,
    CONSTRAINT fk_session_to_user
        FOREIGN KEY (username)
            REFERENCES user_data (username)
);

CREATE TABLE transaction
(
    tx_id       BIGSERIAL PRIMARY KEY,
    tx_type     TEXT   NOT NULL,
    session_id  BIGINT NOT NULL,
    tx_time     TIMESTAMP,
    tx_amount   MONEY  NOT NULL,
    old_balance MONEY  NOT NULL,
    new_balance MONEY  NOT NULL,
    CONSTRAINT fk_tx_to_session
        FOREIGN KEY (session_id)
            REFERENCES play_session (session_id)

);


--INSERT DATA
INSERT INTO user_data (username, password, email) VALUES ('zlatko'::text, '{noop}password'::text, 'zlatko.sh@gmail.com'::text);
INSERT INTO role (username, role_name, description) VALUES ('zlatko', 'ADMIN', 'Admin role having all permissions.');