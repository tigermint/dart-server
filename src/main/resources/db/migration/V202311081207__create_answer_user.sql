create table if not exists answer_user
(
    answer_user_id     bigint auto_increment
        primary key,
    answer_id          bigint   null,
    user_id            bigint   null,
    created_time       datetime null,
    last_modified_time datetime null
);

