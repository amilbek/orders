drop table orders cascade

drop sequence hibernate_sequence

create sequence hibernate_sequence start 1 increment 1

create table orders (
    id int8 not null,
    address_to varchar(255),
    costing numeric(19, 2),
    name_of_order varchar(255),
    order_state int4,
    quantity int4 not null,
    primary key (id)
);
