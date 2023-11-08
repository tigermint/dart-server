create table if not exists answer
(
    answer_id bigint auto_increment
        primary key,
    content   varchar(255) null,
    survey_id bigint       null
);

