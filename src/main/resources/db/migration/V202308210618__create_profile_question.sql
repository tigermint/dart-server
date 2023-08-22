create table if not exists profile_question
(
    profile_question_id bigint not null
        primary key,
    count               bigint null,
    question_id         bigint null,
    user_id             bigint null
);

