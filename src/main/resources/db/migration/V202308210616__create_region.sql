create table if not exists region
(
    region_id bigint       not null
        primary key,
    name      varchar(255) null
);

