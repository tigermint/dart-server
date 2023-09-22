create table if not exists single_team_friend
(
    single_team_friend_profile_id bigint auto_increment
        primary key,
    birth_year                    int          null,
    nickname                      varchar(255) null,
    profile_image_url             varchar(255) null,
    team_id                       bigint       null,
    university_id                 bigint       null
);

