CREATE TABLE election_participations (
    election_participation_uuid UUID PRIMARY KEY,
    voter_uuid UUID NOT NULL,
    election_uuid UUID NOT NULL,
    voted_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_voter FOREIGN KEY (voter_uuid) REFERENCES eligible_voters(voter_uuid),
    CONSTRAINT fk_election FOREIGN KEY (election_uuid) REFERENCES elections(election_uuid)
);