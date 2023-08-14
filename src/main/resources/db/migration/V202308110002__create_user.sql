create table if not exists user
(
    user_id                       bigint       not null
        primary key,
    created_time                  datetime     null,
    last_modified_time            datetime     null,
    name                          varchar(255) null,
    password                      varchar(255) null,
    phone                         varchar(255) null,
    provider                      varchar(255) null,
    provider_id                   varchar(255) null,
    role                          varchar(255) null,
    username                      varchar(255) null,
    university_id                 bigint       null,
    recommendation_code           varchar(255) null,
    admission_year                int          null,
    next_vote_available_date_time datetime     null,
    gender                        varchar(255) null,
    birth_year                    int          null,
    constraint UK_sb8bbouer5wak8vyiiy4pf2bx
        unique (username)
);

