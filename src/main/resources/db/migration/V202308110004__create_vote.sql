create table if not exists vote
(
    vote_id        bigint   not null
        primary key,
    first_user_id  bigint   null,
    fourth_user_id bigint   null,
    picked_time    datetime null,
    second_user_id bigint   null,
    third_user_id  bigint   null,
    picked_user_id bigint   null,
    question_id    bigint   null,
    user_id        bigint   null
);

