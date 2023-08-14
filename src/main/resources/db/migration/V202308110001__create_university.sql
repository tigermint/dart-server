create table if not exists university
(
    university_id bigint auto_increment
        primary key,
    area          varchar(50) null,
    name          varchar(50) null,
    campus_type   varchar(50) null,
    department    varchar(50) null,
    state         varchar(50) null,
    div0          varchar(50) null,
    div1          varchar(50) null,
    div2          varchar(50) null,
    div3          varchar(50) null,
    years         varchar(50) null
);

