create table if not exists survey
(
    survey_id                 bigint auto_increment
        primary key,
    created_time       datetime     null,
    last_modified_time datetime     null,
    content            varchar(255) null,
    category_id        bigint       null
);

