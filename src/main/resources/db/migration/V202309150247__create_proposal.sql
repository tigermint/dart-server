create table if not exists proposal
(
    proposal_id        bigint auto_increment
        primary key,
    created_time       datetime     null,
    last_modified_time datetime     null,
    proposal_status    varchar(255) null,
    requested_team_id  bigint       null,
    requesting_team_id bigint       null,
    constraint FK73cwxbfgo4vlf7qatx1q1nh2v
        foreign key (requested_team_id) references team (team_id),
    constraint FKm06ltyiocg4vgfdrjeolkelj2
        foreign key (requesting_team_id) references team (team_id)
);

