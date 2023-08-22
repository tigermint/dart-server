create table if not exists team_user
(
    team_user_id bigint not null
        primary key,
    team_id      bigint null,
    user_id      bigint null
);

