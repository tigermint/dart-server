create table if not exists proposal
(
    proposal_id        bigint auto_increment
        primary key,
    created_time       datetime     null,
    last_modified_time datetime     null,
    proposal_status    varchar(255) null,
    requested_team_id  bigint       null,
    requesting_team_id bigint       null
);

