create table if not exists answer
(
    answer_id bigserial
        constraint "idx_24578_PRIMARY"
            primary key,
    content   varchar(255),
    survey_id bigint
);

create table if not exists answer_user
(
    answer_user_id     bigserial
        constraint "idx_24583_PRIMARY"
            primary key,
    answer_id          bigint,
    user_id            bigint,
    created_time       timestamp with time zone,
    last_modified_time timestamp with time zone
);

create table if not exists candidate
(
    candidate_id bigserial
        constraint "idx_24588_PRIMARY"
            primary key,
    user_id      bigint,
    vote_id      bigint
);

create table if not exists category
(
    category_id bigserial
        constraint "idx_24593_PRIMARY"
            primary key,
    name        varchar(255)
);

create table if not exists chat_message
(
    chat_message_id    bigserial
        constraint "idx_24598_PRIMARY"
            primary key,
    created_time       timestamp with time zone,
    last_modified_time timestamp with time zone,
    chat_message_type  varchar(255),
    chat_content       varchar(255),
    sender_id          bigint,
    chat_room_id       bigint
);

create table if not exists chat_room
(
    chat_room_id                bigserial
        constraint "idx_24605_PRIMARY"
            primary key,
    latest_chat_message_content varchar(255),
    proposal_id                 bigint,
    created_time                timestamp with time zone,
    last_modified_time          timestamp with time zone
);

create table if not exists chat_room_user
(
    chat_room_user_id bigserial
        constraint "idx_24610_PRIMARY"
            primary key,
    chat_room_id      bigint,
    user_id           bigint
);

create table if not exists comment
(
    comment_id         bigserial
        constraint "idx_24615_PRIMARY"
            primary key,
    content            varchar(255),
    survey_id          bigint,
    user_id            bigint,
    created_time       timestamp with time zone,
    last_modified_time timestamp with time zone
);

create table if not exists comment_like
(
    comment_like_id    bigserial
        constraint "idx_24620_PRIMARY"
            primary key,
    created_time       timestamp with time zone,
    last_modified_time timestamp with time zone,
    comment_id         bigint,
    user_id            bigint
);

create table if not exists comment_report
(
    comment_report_id  bigserial
        constraint "idx_24625_PRIMARY"
            primary key,
    created_time       timestamp with time zone,
    last_modified_time timestamp with time zone,
    comment_id         bigint,
    user_id            bigint
);

create table if not exists friend
(
    friend_id      bigserial
        constraint "idx_24636_PRIMARY"
            primary key,
    user_id        bigint,
    friend_user_id bigint
);

create table if not exists profile_question
(
    profile_question_id bigserial
        constraint "idx_24641_PRIMARY"
            primary key,
    count               bigint,
    question_id         bigint,
    user_id             bigint
);

create table if not exists proposal
(
    proposal_id        bigserial
        constraint "idx_24646_PRIMARY"
            primary key,
    created_time       timestamp with time zone,
    last_modified_time timestamp with time zone,
    proposal_status    varchar(255),
    requested_team_id  bigint,
    requesting_team_id bigint
);

create index if not exists idx_24646_proposal_requested_team_id_index
    on proposal (requested_team_id);

create table if not exists question
(
    question_id bigserial
        constraint "idx_24651_PRIMARY"
            primary key,
    content     varchar(255),
    icon        varchar(255)
);

create table if not exists region
(
    region_id bigserial
        constraint "idx_24658_PRIMARY"
            primary key,
    name      varchar(255)
);

create table if not exists single_team_friend
(
    single_team_friend_profile_id bigserial
        constraint "idx_24663_PRIMARY"
            primary key,
    birth_year                    integer,
    nickname                      varchar(255),
    profile_image_url             varchar(255),
    team_id                       bigint,
    university_id                 bigint
);

create index if not exists idx_24663_single_team_friend_team_id_index
    on single_team_friend (team_id);

create table if not exists survey
(
    survey_id          bigserial
        constraint "idx_24670_PRIMARY"
            primary key,
    created_time       timestamp with time zone,
    last_modified_time timestamp with time zone,
    content            varchar(255),
    category_id        bigint
);

create table if not exists team
(
    team_id                       bigserial
        constraint "idx_24675_PRIMARY"
            primary key,
    created_time                  timestamp with time zone,
    last_modified_time            timestamp with time zone,
    is_visible_to_same_university boolean,
    name                          varchar(255),
    university_id                 bigint,
    team_users_combination_hash   varchar(255),
    view_count                    integer
);

create unique index if not exists "idx_24675_UK_8gpfc9cpx2usm7awyrc5pkhsp"
    on team (team_users_combination_hash);

create index if not exists idx_24675_team_is_visible_to_same_university_university_id_inde
    on team (is_visible_to_same_university, university_id);

create table if not exists team_region
(
    team_region_id bigserial
        constraint "idx_24682_PRIMARY"
            primary key,
    region_id      bigint,
    team_id        bigint
);

create index if not exists idx_24682_team_region_team_id_index
    on team_region (team_id);

create table if not exists team_user
(
    team_user_id bigserial
        constraint "idx_24687_PRIMARY"
            primary key,
    team_id      bigint,
    user_id      bigint
);

create index if not exists idx_24687_team_user_team_id_index
    on team_user (team_id);

create table if not exists university
(
    university_id bigserial
        constraint "idx_24692_PRIMARY"
            primary key,
    area          varchar(50),
    name          varchar(50),
    campus_type   varchar(50),
    department    varchar(50),
    state         varchar(50),
    div0          varchar(50),
    div1          varchar(50),
    div2          varchar(50),
    div3          varchar(50),
    years         varchar(50)
);

create table if not exists "user"
(
    user_id                             bigserial
        constraint "idx_24699_PRIMARY"
            primary key,
    created_time                        timestamp with time zone,
    last_modified_time                  timestamp with time zone,
    name                                varchar(255),
    password                            varchar(255),
    phone                               varchar(255),
    provider                            varchar(255),
    provider_id                         varchar(255),
    role                                varchar(255),
    username                            varchar(255),
    university_id                       bigint,
    recommendation_code                 varchar(255),
    admission_year                      integer,
    next_vote_available_date_time       timestamp with time zone,
    gender                              varchar(255) not null,
    birth_year                          integer,
    nickname                            varchar(255),
    profile_image_url                   varchar(255),
    point                               integer,
    student_id_card_image_url           varchar(255),
    student_id_card_verification_status varchar(255)
);

create index if not exists "idx_24699_UK_sb8bbouer5wak8vyiiy4pf2bx"
    on "user" (username);

create table if not exists vote
(
    vote_id         bigserial
        constraint "idx_24706_PRIMARY"
            primary key,
    picked_time     timestamp with time zone,
    question_id     bigint,
    picking_user_id bigint,
    picked_user_id  bigint
);