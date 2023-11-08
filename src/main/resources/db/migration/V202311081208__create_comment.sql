create table if not exists comment
(
    comment_id         bigint auto_increment
        primary key,
    content            varchar(255) null,
    survey_id          bigint       null,
    user_id            bigint       null,
    created_time       datetime     null,
    last_modified_time datetime     null
);

