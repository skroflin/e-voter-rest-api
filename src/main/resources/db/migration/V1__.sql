CREATE TABLE admin_users
(
    admin_uuid    UUID NOT NULL,
    username      VARCHAR(255),
    password_hash VARCHAR(255),
    role          SMALLINT,
    created_at    TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_admin_users PRIMARY KEY (admin_uuid)
);

CREATE TABLE candidates
(
    candidate_uuid     UUID    NOT NULL,
    candidate_fullname VARCHAR NOT NULL,
    bio                TEXT    NOT NULL,
    election_uuid      UUID    NOT NULL,
    created_at         TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_candidates PRIMARY KEY (candidate_uuid)
);

CREATE TABLE elections
(
    election_uuid       UUID         NOT NULL,
    election_name       VARCHAR(255) NOT NULL,
    description         TEXT,
    election_start_time TIMESTAMP WITHOUT TIME ZONE,
    election_end_time   TIMESTAMP WITHOUT TIME ZONE,
    public_key          VARCHAR,
    private_key_enc     VARCHAR,
    election_status     SMALLINT,
    created_at          TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_elections PRIMARY KEY (election_uuid)
);

CREATE TABLE eligible_voters
(
    voter_uuid      UUID    NOT NULL,
    first_name      VARCHAR NOT NULL,
    last_name       VARCHAR NOT NULL,
    email           VARCHAR NOT NULL,
    is_enabled      BOOLEAN,
    username        VARCHAR NOT NULL,
    password_hash   VARCHAR NOT NULL,
    is_token_issued BOOLEAN,
    token_issued_at TIMESTAMP WITHOUT TIME ZONE,
    role            SMALLINT,
    created_at      TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_eligible_voters PRIMARY KEY (voter_uuid)
);

CREATE TABLE issued_tokens
(
    token_uuid UUID NOT NULL,
    voter_uuid UUID,
    issued_at  TIMESTAMP WITHOUT TIME ZONE,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_issued_tokens PRIMARY KEY (token_uuid)
);

CREATE TABLE used_tokens
(
    token_hash VARCHAR NOT NULL,
    used_at    TIMESTAMP WITHOUT TIME ZONE,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_used_tokens PRIMARY KEY (token_hash)
);

CREATE TABLE user_verification
(
    verification_user_uuid UUID         NOT NULL,
    voter_uuid             UUID,
    verification_code      VARCHAR(255) NOT NULL,
    expiry_date            TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    created_at             TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_user_verification PRIMARY KEY (verification_user_uuid)
);

CREATE TABLE votes
(
    vote_uuid      UUID    NOT NULL,
    token_hash     VARCHAR NOT NULL,
    election_uuid  UUID,
    candidate_uuid UUID,
    cast_at        TIMESTAMP WITHOUT TIME ZONE,
    created_at     TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_votes PRIMARY KEY (vote_uuid)
);

ALTER TABLE admin_users
    ADD CONSTRAINT uc_admin_users_username UNIQUE (username);

ALTER TABLE eligible_voters
    ADD CONSTRAINT uc_eligible_voters_email UNIQUE (email);

ALTER TABLE eligible_voters
    ADD CONSTRAINT uc_eligible_voters_username UNIQUE (username);

ALTER TABLE issued_tokens
    ADD CONSTRAINT uc_issued_tokens_voter_uuid UNIQUE (voter_uuid);

ALTER TABLE user_verification
    ADD CONSTRAINT uc_user_verification_voter_uuid UNIQUE (voter_uuid);

ALTER TABLE votes
    ADD CONSTRAINT uc_votes_token_hash UNIQUE (token_hash);

ALTER TABLE candidates
    ADD CONSTRAINT FK_CANDIDATES_ON_ELECTION_UUID FOREIGN KEY (election_uuid) REFERENCES elections (election_uuid);

ALTER TABLE issued_tokens
    ADD CONSTRAINT FK_ISSUED_TOKENS_ON_VOTER_UUID FOREIGN KEY (voter_uuid) REFERENCES eligible_voters (voter_uuid);

ALTER TABLE user_verification
    ADD CONSTRAINT FK_USER_VERIFICATION_ON_VOTER_UUID FOREIGN KEY (voter_uuid) REFERENCES eligible_voters (voter_uuid);

ALTER TABLE votes
    ADD CONSTRAINT FK_VOTES_ON_CANDIDATE_UUID FOREIGN KEY (candidate_uuid) REFERENCES candidates (candidate_uuid);

ALTER TABLE votes
    ADD CONSTRAINT FK_VOTES_ON_ELECTION_UUID FOREIGN KEY (election_uuid) REFERENCES elections (election_uuid);

ALTER TABLE votes
    ADD CONSTRAINT FK_VOTES_ON_TOKEN_HASH FOREIGN KEY (token_hash) REFERENCES used_tokens (token_hash);