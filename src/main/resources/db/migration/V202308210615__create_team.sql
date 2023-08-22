create table if not exists team
(
    team_id                       bigint       not null
        primary key,
    created_time                  datetime     null,
    last_modified_time            datetime     null,
    is_visible_to_same_university bit          null,
    name                          varchar(255) null,
    university_id                 bigint       null,
    team_users_combination_hash   varchar(255) null,
    constraint UK_8gpfc9cpx2usm7awyrc5pkhsp
        unique (team_users_combination_hash)
);

