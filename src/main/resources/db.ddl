create table person(
id int not null,
first_name varchar(200),
second_name varchar(200),
primary key (id)
);

insert into person values (1, 'Lajos', 'Kovács');
insert into person values (2, 'Piroska', 'Teszt');
insert into person values (3, 'Barbara', 'Teszt');
insert into person values (4, 'Lajos', 'Bárány');

create table address(
id int not null,
address_type varchar(200),
city varchar(200),
street varchar(200),
person_id int not null,
primary key (id),
foreign key (person_id) references person(id),
constraint ac unique (address_type, person_id)
);

insert into address values (10, 'TEMPORARY', 'Vác', 'Fő út 1', 1);
insert into address values (11, 'PERMANENT', 'Vác', 'Fő út 2', 1);
insert into address values (20, 'PERMANENT', 'Vác', 'Malom utca 7', 2);

create table contact(
id int not null,
value varchar(255),
address_id int not null ,
primary key (id),
foreign key (address_id) references address(id)
);

insert into contact values (100, '+36205672324', 10);
insert into contact values (101, '06-22-345-235', 10);
insert into contact values (200, '06/20-456-57-58', 20);