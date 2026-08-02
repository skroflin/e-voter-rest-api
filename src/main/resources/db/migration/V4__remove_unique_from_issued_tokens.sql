ALTER TABLE issued_tokens
DROP CONSTRAINT uc_issued_tokens_voter_uuid;

ALTER TABLE election_participations
    ADD CONSTRAINT uc_election_participation_voter_election
        UNIQUE (voter_uuid, election_uuid);