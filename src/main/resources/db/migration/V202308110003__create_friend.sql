create table if not exists friend
(
    friend_id      bigint not null
        primary key,
    user_id        bigint null,
    friend_user_id bigint null
);

