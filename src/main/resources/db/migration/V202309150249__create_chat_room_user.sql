create table if not exists chat_room_user
(
    chat_room_user_id bigint auto_increment
        primary key,
    chat_room_id      bigint null,
    user_id           bigint null
);

