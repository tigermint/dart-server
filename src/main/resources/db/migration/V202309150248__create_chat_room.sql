create table if not exists chat_room
(
    chat_room_id                bigint auto_increment
        primary key,
    latest_chat_message_content varchar(255) null,
    latest_chat_message_time    datetime     null,
    proposal_id                 bigint       null
);

