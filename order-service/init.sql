create table if not exists usr (
	id number primary key auto_increment,
	username varchar(255),
	phone varchar(255),
	email varchar(255)
);

create table if not exists delivery_order (
	id number primary key auto_increment,
	code varchar(255),
	created_date date,
	address varchar(255),
	receiver varchar(255),
	status varchar(10),
	user_id number
);
