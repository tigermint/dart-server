-- Create new vote table updated schema
CREATE TABLE if not exists vote_updated
(
    vote_id         bigint   not null
        primary key,
    picked_time     datetime null,
    question_id     bigint   null,
    picking_user_id bigint   null,
    picked_user_id  bigint   null
);



-- Insert data from existing vote table into candidate and vote_updated table
INSERT INTO candidate (user_id, vote_id)
SELECT first_user_id, vote_id FROM vote
WHERE first_user_id IS NOT NULL
UNION ALL
SELECT second_user_id, vote_id FROM vote
WHERE second_user_id IS NOT NULL
UNION ALL
SELECT third_user_id, vote_id FROM vote
WHERE third_user_id IS NOT NULL
UNION ALL
SELECT fourth_user_id, vote_id FROM vote
WHERE fourth_user_id IS NOT NULL;

INSERT INTO vote_updated (vote_id, picked_time, picked_user_id, question_id, picking_user_id)
SELECT vote_id, picked_time, picked_user_id, question_id, user_id
FROM vote;

-- Drop old vote table and rename vote_updated to vote
DROP TABLE vote;
ALTER TABLE vote_updated RENAME TO vote;
