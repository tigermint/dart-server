create table if not exists team_region
(
    team_region_id bigint not null
        primary key,
    region_id      bigint null,
    team_id        bigint null
);

