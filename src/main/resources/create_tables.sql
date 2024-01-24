create table person(
id int not null,
first_name varchar(200),
second_name varchar(200),
primary key (id)
);

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

create table contact(
id int not null,
value varchar(255),
address_id int not null ,
primary key (id),
foreign key (address_id) references address(id)
);

EXEC sp_RENAME 'contact.value' , 'contact_value', 'COLUMN'
