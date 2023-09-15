create table if not exists chat_room_user
(
    chat_room_user_id bigint auto_increment
        primary key,
    chat_room_id      bigint null,
    user_id           bigint null,
    constraint FK368skiewasavvt4ltyep63dn8
        foreign key (user_id) references user (user_id),
    constraint FKn7wfsq1ii61la6vi9gigw4pk1
        foreign key (chat_room_id) references chat_room (chat_room_id)
);

