create table post(
	id serial primary key,
	name varchar(200),
	text text,
	link text unique,
	created timestamp
)