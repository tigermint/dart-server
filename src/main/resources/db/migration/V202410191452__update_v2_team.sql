create table if not exists image
(
    id                 bigserial
        primary key,
    created_time       timestamp with time zone,
    last_modified_time timestamp with time zone,
    data               varchar(255) not null,
    type               varchar(255) not null
);

create table if not exists team_image
(
    team_image_id      bigserial
        primary key,
    created_time       timestamp with time zone,
    last_modified_time timestamp with time zone,
    image_id           bigint,
    team_id            bigint
);

ALTER TABLE team ADD COLUMN description varchar(255);
ALTER TABLE team ADD COLUMN leader_user_id bigint;
ALTER TABLE team ADD CONSTRAINT fkil41lpcxlqo1je68trvkkl4va
    FOREIGN KEY (leader_user_id) REFERENCES users (user_id);
