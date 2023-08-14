create table if not exists question
(
    question_id bigint       not null
        primary key,
    content     varchar(255) null,
    icon        varchar(255) null
);

