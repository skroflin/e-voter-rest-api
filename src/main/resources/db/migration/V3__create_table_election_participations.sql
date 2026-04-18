CREATE TABLE election_participations (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    election_uuid UUID NOT NULL,
    voted_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_participation_user FOREIGN KEY (user_id) REFERENCES users(user_uuid),
    CONSTRAINT fk_participation_election FOREIGN KEY (election_uuid) REFERENCES elections(election_uuid),
    CONSTRAINT unique_user_election_participation UNIQUE (user_id, election_uuid)
);