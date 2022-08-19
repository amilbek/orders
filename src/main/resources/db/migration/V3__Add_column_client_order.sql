alter table orders add customer varchar(255);

update orders set customer = 'Nurbolat Amilbek' where id = 1;
