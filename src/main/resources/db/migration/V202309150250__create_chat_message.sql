create table if not exists chat_message
(
    chat_message_id    bigint auto_increment
        primary key,
    created_time       datetime     null,
    last_modified_time datetime     null,
    chat_message_type  varchar(255) null,
    chat_content       varchar(255) null,
    sender_id          bigint       null,
    chat_room_id       bigint       null,
    constraint FKj52yap2xrm9u0721dct0tjor9
        foreign key (chat_room_id) references chat_room (chat_room_id)
);

