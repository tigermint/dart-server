create table if not exists candidate
(
    candidate_id bigint not null auto_increment
        primary key,
    user_id      bigint null,
    vote_id      bigint null
);

